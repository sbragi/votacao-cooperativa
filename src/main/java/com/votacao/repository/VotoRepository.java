package com.votacao.repository;

import com.votacao.entity.TipoVoto;
import com.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByPautaIdAndAssociadoId(Long pautaId, Long associadoId);

    @Query("select count(v) from Voto v where v.pauta.id = :pautaId")
    long countByPautaId(@Param("pautaId") Long pautaId);

    @Query("select count(v) from Voto v where v.pauta.id = :pautaId and v.voto = :tipo")
    long countByPautaIdAndTipo(@Param("pautaId") Long pautaId, @Param("tipo") TipoVoto tipo);
}
