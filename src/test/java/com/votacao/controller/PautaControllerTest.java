package com.votacao.controller;

import com.votacao.dto.CriarPautaRequest;
import com.votacao.dto.PautaResponse;
import com.votacao.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PautaControllerTest {
    @Mock PautaService pautaService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PautaController(pautaService)).build();
    }

    @Test
    void shouldCreatePauta() throws Exception {
        when(pautaService.criar(any(CriarPautaRequest.class)))
                .thenReturn(new PautaResponse(1L, "Pauta", "Descricao", LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CriarPautaRequest("Pauta", "Descricao"))))
                .andExpect(status().isCreated());
    }
}
