CREATE TABLE pauta (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE sessao (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL UNIQUE,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    CONSTRAINT fk_sessao_pauta FOREIGN KEY (pauta_id) REFERENCES pauta(id)
);

CREATE TABLE voto (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL,
    sessao_id BIGINT NOT NULL,
    associado_id BIGINT NOT NULL,
    voto VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES pauta(id),
    CONSTRAINT fk_voto_sessao FOREIGN KEY (sessao_id) REFERENCES sessao(id),
    CONSTRAINT ck_voto_tipo CHECK (voto IN ('SIM', 'NAO')),
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)
);

CREATE INDEX idx_voto_pauta ON voto(pauta_id);
CREATE INDEX idx_voto_associado ON voto(associado_id);
