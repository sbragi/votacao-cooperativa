package com.votacao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    public Long getId() { return id; }
    public Pauta getPauta() { return pauta; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setId(Long id) { this.id = id; }
    public void setPauta(Pauta pauta) { this.pauta = pauta; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }

    public boolean estaAberta(LocalDateTime agora) {
        return !agora.isBefore(inicio) && agora.isBefore(fim);
    }
}
