package com.votacao.controller;

import com.votacao.dto.RegistrarVotoRequest;
import com.votacao.dto.ResultadoVotacaoResponse;
import com.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}")
public class VotoController {
    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/votos")
    public ResponseEntity<Void> votar(@PathVariable Long pautaId,
                                      @Valid @RequestBody RegistrarVotoRequest request) {
        votoService.votar(pautaId, request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/resultado")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable Long pautaId) {
        return ResponseEntity.ok(votoService.resultado(pautaId));
    }
}
