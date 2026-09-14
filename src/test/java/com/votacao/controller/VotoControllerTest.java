package com.votacao.controller;

import com.votacao.dto.RegistrarVotoRequest;
import com.votacao.dto.ResultadoVotacaoResponse;
import com.votacao.entity.TipoVoto;
import com.votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VotoControllerTest {
    @Mock VotoService votoService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VotoController(votoService)).build();
    }

    @Test
    void shouldRegisterVote() throws Exception {
        RegistrarVotoRequest request = new RegistrarVotoRequest(1l, TipoVoto.SIM);

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(votoService).votar(eq(1L), any(RegistrarVotoRequest.class));
    }

    @Test
    void shouldReturnVotingResult() throws Exception {
        when(votoService.resultado(1L))
                .thenReturn(new ResultadoVotacaoResponse(1L, "Pauta", 10L, 7L, 3L, "APROVADA"));

        mockMvc.perform(get("/api/v1/pautas/1/resultado"))
                .andExpect(status().isOk());

        verify(votoService).resultado(1L);
    }
}
