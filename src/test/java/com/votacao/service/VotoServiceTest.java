package com.votacao.service;

import com.votacao.dto.RegistrarVotoRequest;
import com.votacao.entity.Pauta;
import com.votacao.entity.Sessao;
import com.votacao.entity.TipoVoto;
import com.votacao.exception.BusinessException;
import com.votacao.exception.ConflictException;
import com.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {
    @Mock VotoRepository votoRepository;
    @Mock PautaService pautaService;
    @Mock SessaoService sessaoService;

    private VotoService service;
    private Pauta pauta;
    private Sessao sessao;

    @BeforeEach
    void setup() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-10T15:00:00Z"), ZoneOffset.UTC);
        service = new VotoService(votoRepository, pautaService, sessaoService, clock);
        pauta = new Pauta(); pauta.setId(1L); pauta.setTitulo("Pauta"); pauta.setDescricao("Descrição");
        sessao = new Sessao(); sessao.setId(1L); sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.of(2026, 9, 10, 14, 59));
        sessao.setFim(LocalDateTime.of(2026, 9, 10, 15, 1));
        when(pautaService.buscar(1L)).thenReturn(pauta);
        when(sessaoService.buscarPorPauta(1L)).thenReturn(sessao);
    }

    @Test
    void shouldRegisterVote() {
        when(votoRepository.existsByPautaIdAndAssociadoId(1l, 1l)).thenReturn(false);

        service.votar(1L, new RegistrarVotoRequest(1l, TipoVoto.SIM));

        verify(votoRepository).saveAndFlush(any());
    }

    @Test
    void shouldRejectSecondVote() {
        when(votoRepository.existsByPautaIdAndAssociadoId(1l, 1l)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.votar(1L, new RegistrarVotoRequest(1l, TipoVoto.SIM)));
        verify(votoRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldRejectVoteAfterSessionClosed() {
        sessao.setFim(LocalDateTime.of(2026, 9, 10, 14, 59, 59));

        assertThrows(BusinessException.class,
                () -> service.votar(1L, new RegistrarVotoRequest(1l, TipoVoto.NAO)));
        verify(votoRepository, never()).saveAndFlush(any());
    }
}
