package com.votacao.controller;

import com.votacao.dto.AbrirSessaoRequest;
import com.votacao.dto.SessaoResponse;
import com.votacao.service.SessaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoController {
    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping
    public ResponseEntity<SessaoResponse> abrir(
            @PathVariable Long pautaId,
            @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoService.abrir(pautaId, request));
    }
}
