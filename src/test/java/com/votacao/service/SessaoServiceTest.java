package com.votacao.service;

import com.votacao.dto.AbrirSessaoRequest;
import com.votacao.dto.SessaoResponse;
import com.votacao.entity.Pauta;
import com.votacao.entity.Sessao;
import com.votacao.exception.ConflictException;
import com.votacao.repository.SessaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {
    @Mock SessaoRepository sessaoRepository;
    @Mock PautaService pautaService;

    @Test
    void shouldUseDefaultDurationOfOneMinute() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-10T15:00:00Z"), ZoneOffset.UTC);
        SessaoService service = new SessaoService(sessaoRepository, pautaService, clock);
        Pauta pauta = new Pauta(); pauta.setId(1L);
        when(pautaService.buscar(1L)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoRepository.saveAndFlush(any())).thenAnswer(inv -> {
            Sessao s = inv.getArgument(0); s.setId(10L); return s;
        });

        SessaoResponse response = service.abrir(1L, null);

        assertEquals(60, java.time.Duration.between(response.inicio(), response.fim()).getSeconds());
    }

    @Test
    void shouldRejectSecondSession() {
        Clock clock = Clock.systemUTC();
        SessaoService service = new SessaoService(sessaoRepository, pautaService, clock);
        Pauta pauta = new Pauta(); pauta.setId(1L);
        when(pautaService.buscar(1L)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.abrir(1L, new AbrirSessaoRequest(60L)));
    }
}
