package com.votacao.dto;

public record ResultadoVotacaoResponse(Long pautaId, String titulo, long totalVotos,
                                       long votosSim, long votosNao, String resultado) {}
