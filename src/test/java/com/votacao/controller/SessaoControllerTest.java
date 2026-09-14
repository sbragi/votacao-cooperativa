package com.votacao.controller;

import com.votacao.dto.AbrirSessaoRequest;
import com.votacao.dto.SessaoResponse;
import com.votacao.service.SessaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessaoControllerTest {
    @Mock SessaoService sessaoService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SessaoController(sessaoService)).build();
    }

    @Test
    void shouldOpenSession() throws Exception {
        when(sessaoService.abrir(eq(1L), any(AbrirSessaoRequest.class)))
                .thenReturn(new SessaoResponse(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)));

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AbrirSessaoRequest(60L))))
                .andExpect(status().isCreated());
    }
}
