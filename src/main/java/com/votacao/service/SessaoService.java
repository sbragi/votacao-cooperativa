package com.votacao.service;

import com.votacao.dto.AbrirSessaoRequest;
import com.votacao.dto.SessaoResponse;
import com.votacao.entity.Pauta;
import com.votacao.entity.Sessao;
import com.votacao.exception.ConflictException;
import com.votacao.exception.ResourceNotFoundException;
import com.votacao.repository.SessaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class SessaoService {
    private static final Logger log = LoggerFactory.getLogger(SessaoService.class);
    private static final long DURACAO_PADRAO_SEGUNDOS = 60L;

    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;
    private final Clock clock;

    public SessaoService(SessaoRepository sessaoRepository, PautaService pautaService, Clock clock) {
        this.sessaoRepository = sessaoRepository;
        this.pautaService = pautaService;
        this.clock = clock;
    }

    @Transactional
    public SessaoResponse abrir(Long pautaId, AbrirSessaoRequest request) {
        log.info("Iniciando abertura de sessão: pautaId={}", pautaId);

        Pauta pauta = pautaService.buscar(pautaId);
        if (sessaoRepository.existsByPautaId(pautaId)) {
            log.warn("Tentativa de abrir segunda sessão para a pauta: pautaId={}", pautaId);
            throw new ConflictException("Já existe uma sessão para a pauta " + pautaId);
        }

        long duracao = request == null || request.duracaoSegundos() == null
                ? DURACAO_PADRAO_SEGUNDOS : request.duracaoSegundos();
        LocalDateTime inicio = LocalDateTime.now(clock);

        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(inicio);
        sessao.setFim(inicio.plusSeconds(duracao));

        try {
            Sessao saved = sessaoRepository.saveAndFlush(sessao);
            log.info("Sessão aberta com sucesso: sessaoId={}, pautaId={}, duracaoSegundos={}",
                    saved.getId(), pautaId, duracao);
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Conflito ao abrir sessão, provavelmente concorrência: pautaId={}", pautaId);
            throw new ConflictException("Já existe uma sessão para a pauta " + pautaId);
        }
    }

    @Transactional(readOnly = true)
    public Sessao buscarPorPauta(Long pautaId) {
        log.debug("Buscando sessão da pauta: pautaId={}", pautaId);
        return sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para a pauta: pautaId={}", pautaId);
                    return new ResourceNotFoundException("Sessão não encontrada para a pauta " + pautaId);
                });
    }

    private SessaoResponse toResponse(Sessao sessao) {
        return new SessaoResponse(sessao.getId(), sessao.getPauta().getId(), sessao.getInicio(), sessao.getFim());
    }
}
