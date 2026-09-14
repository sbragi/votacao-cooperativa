package com.votacao.dto;

import java.time.LocalDateTime;

public record SessaoResponse(Long sessaoId, Long pautaId, LocalDateTime inicio, LocalDateTime fim) {}
