package com.votacao.repository;

import com.votacao.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    Optional<Sessao> findByPautaId(Long pautaId);

    @Query("select v from Sessao v where v.pauta.id = :pautaId and v.fim  >= :dataHoraAtual")
    public Optional<Sessao> findByPautaIdAndDateTime(Long pautaId, LocalDateTime dataHoraAtual);
    
    boolean existsByPautaId(Long pautaId);
}
