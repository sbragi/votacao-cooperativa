package com.votacao.dto;

import com.votacao.entity.TipoVoto;
import jakarta.validation.constraints.NotNull;

public record RegistrarVotoRequest(@NotNull Long associadoId, @NotNull TipoVoto voto) {}
