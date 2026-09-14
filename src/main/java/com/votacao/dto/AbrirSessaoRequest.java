package com.votacao.dto;

import jakarta.validation.constraints.Min;

public record AbrirSessaoRequest(@Min(1) Long duracaoSegundos) {}
