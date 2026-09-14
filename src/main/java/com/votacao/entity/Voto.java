package com.votacao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voto", uniqueConstraints = @UniqueConstraint(
        name = "uk_voto_pauta_associado", columnNames = {"pauta_id", "associado_id"}))
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(name = "associado_id", nullable = false)
    private Long associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoVoto voto;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Pauta getPauta() { return pauta; }
    public Sessao getSessao() { return sessao; }
    public Long getAssociadoId() { return associadoId; }
    public TipoVoto getVoto() { return voto; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setPauta(Pauta pauta) { this.pauta = pauta; }
    public void setSessao(Sessao sessao) { this.sessao = sessao; }
    public void setAssociadoId(Long associadoId) { this.associadoId = associadoId; }
    public void setVoto(TipoVoto voto) { this.voto = voto; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
