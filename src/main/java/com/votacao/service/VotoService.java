package com.votacao.service;

import com.votacao.dto.RegistrarVotoRequest;
import com.votacao.dto.ResultadoVotacaoResponse;
import com.votacao.entity.Pauta;
import com.votacao.entity.Sessao;
import com.votacao.entity.TipoVoto;
import com.votacao.entity.Voto;
import com.votacao.exception.BusinessException;
import com.votacao.exception.ConflictException;
import com.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class VotoService {
    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final Clock clock;

    public VotoService(VotoRepository votoRepository, PautaService pautaService,
                       SessaoService sessaoService, Clock clock) {
        this.votoRepository = votoRepository;
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.clock = clock;
    }

    @Transactional
    public void votar(Long pautaId, RegistrarVotoRequest request) {
        log.info("Iniciando registro de voto: pautaId={}, tipoVoto={}", pautaId, request.voto());

        Pauta pauta = pautaService.buscar(pautaId);
        Sessao sessao = sessaoService.buscarPorPauta(pautaId);
        LocalDateTime agora = LocalDateTime.now(clock);

        if (!sessao.estaAberta(agora)) {
            log.warn("Tentativa de voto em sessão encerrada: pautaId={}, sessaoId={}", pautaId, sessao.getId());
            throw new BusinessException("A sessão de votação está encerrada.");
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, request.associadoId())) {
            log.warn("Voto duplicado rejeitado: pautaId={}, associadoId={}", pautaId, request.associadoId());
            throw new ConflictException("O associado " + request.associadoId() + " já votou nesta pauta.");
        }

        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setSessao(sessao);
        voto.setAssociadoId(request.associadoId());
        voto.setVoto(request.voto());

        try {
            votoRepository.saveAndFlush(voto);
            log.info("Voto registrado com sucesso: pautaId={}, associadoId={}, tipoVoto={}",
                    pautaId, request.associadoId(), request.voto());
        } catch (DataIntegrityViolationException ex) {
            // Proteção contra duas requisições concorrentes para o mesmo associado/pauta.
            log.warn("Conflito de concorrência ao registrar voto: pautaId={}, associadoId={}",
                    pautaId, request.associadoId());
            throw new ConflictException("O associado " + request.associadoId() + " já votou nesta pauta.");
        }
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse resultado(Long pautaId) {
        log.info("Calculando resultado da votação: pautaId={}", pautaId);

        Pauta pauta = pautaService.buscar(pautaId);
        long sim = votoRepository.countByPautaIdAndTipo(pautaId, TipoVoto.SIM);
        long nao = votoRepository.countByPautaIdAndTipo(pautaId, TipoVoto.NAO);
        long total = sim + nao;

        String resultado = sim > nao ? "APROVADA" : sim < nao ? "REJEITADA" : "EMPATE";
        log.info("Resultado calculado: pautaId={}, total={}, sim={}, nao={}, resultado={}",
                pautaId, total, sim, nao, resultado);

        return new ResultadoVotacaoResponse(pauta.getId(), pauta.getTitulo(), total, sim, nao, resultado);
    }
}
