package com.votacao.service;

import com.votacao.dto.CriarPautaRequest;
import com.votacao.dto.PautaResponse;
import com.votacao.entity.Pauta;
import com.votacao.exception.ResourceNotFoundException;
import com.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PautaService {
    private static final Logger log = LoggerFactory.getLogger(PautaService.class);
    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public PautaResponse criar(CriarPautaRequest request) {
   	   log.info("Iniciando criação de pauta");

        Pauta pauta = new Pauta();
        pauta.setTitulo(request.titulo().trim());
        pauta.setDescricao(request.descricao().trim());
        Pauta saved = pautaRepository.save(pauta);

        log.info("Pauta criada com sucesso: pautaId={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Pauta buscar(Long id) {
    	log.debug("Buscando pauta: pautaId={}", id);
    	    
        return pautaRepository.findById(id)
        		 .orElseThrow(() -> {
                     log.warn("Pauta não encontrada: pautaId={}", id);
                     return new ResourceNotFoundException("Pauta não encontrada: " + id);
                 });
               
    }

    private PautaResponse toResponse(Pauta pauta) {
        return new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.getCreatedAt());
    }
}
