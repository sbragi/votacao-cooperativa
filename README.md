# Votação Cooperativa - Spring Boot

Aplicativo para gerenciar pautas, sessões de votação e votos de associados.

## Stack

- Java 21
- Spring Boot 3.4.5
- Spring Web
- Spring Validation
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger UI
- JUnit 5 / Mockito / Javcoco
- Testcontainers
- Docker / Docker Compose

## Regras de negócio

1. Uma pauta possui título e descrição.
2. Uma pauta pode ter uma única sessão de votação.
3. A duração da sessão é informada na abertura; se omitida, o padrão é 60 segundos.
4. Um voto só pode ser `SIM` ou `NAO`.
5. Um associado pode votar uma única vez por pauta.
6. O banco garante a regra de voto único com `UNIQUE (pauta_id, associado_id)`.
7. A sessão é considerada aberta quando `inicio <= agora < fim`.
8. Não existe scheduler para fechar a sessão: o prazo é validado no momento do voto.
9. O resultado é calculado a partir dos votos persistidos.

## Banco e concorrência

A regra "um voto por associado em uma pauta" é validada na aplicação e garantida pelo PostgreSQL:

CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)

Isso é importante em concorrência. Dois requests simultâneos podem passar pela consulta `exists`, mas somente uma inserção poderá ser persistida.

A aplicação captura `DataIntegrityViolationException` e devolve `409 CONFLICT`.

## Como executar localmente

### Opção 1 - PostgreSQL com Docker Compose

Suba apenas o banco:

```bash
docker compose up -d postgres
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
mvnw.cmd spring-boot:run
```

### Opção 2 - tudo em containers

Primeiro gere o JAR:

```bash
mvn clean package -DskipTests
```

Depois:

```bash
docker compose up --build
```

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Actuator health:

```text
http://localhost:8080/actuator/health
```

## Endpoints

### Criar pauta

```http
POST /api/v1/pautas
Content-Type: application/json
```

```json
{
  "titulo": "Aprovação do novo estatuto",
  "descricao": "Votação para aprovação do novo estatuto"
}
```

### Abrir sessão - 60 segundos default

```http
POST /api/v1/pautas/1/sessao
Content-Type: application/json
```

Sem body, a sessão dura 60 segundos.

### Abrir sessão - duração customizada

```http
POST /api/v1/pautas/1/sessao
Content-Type: application/json
```

```json
{
  "duracaoSegundos": 120
}
```

### Votar SIM

```http
POST /api/v1/pautas/1/votos
Content-Type: application/json
```

```json
{
  "associadoId": 1001,
  "voto": "SIM"
}
```

### Votar NAO

```json
{
  "associadoId": 1002,
  "voto": "NAO"
}
```

### Resultado

```http
GET /api/v1/pautas/1/resultado
```

Exemplo:

```json
{
  "pautaId": 1,
  "titulo": "Aprovação do novo estatuto",
  "totalVotos": 2,
  "votosSim": 1,
  "votosNao": 1,
  "resultado": "EMPATE"
}
```

## Exemplos com curl

```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H 'Content-Type: application/json' \
  -d '{"titulo":"Novo estatuto","descricao":"Aprovação do novo estatuto"}'
```

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao
```

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H 'Content-Type: application/json' \
  -d '{"associadoId":1001,"voto":"SIM"}'
```

```bash
curl http://localhost:8080/api/v1/pautas/1/resultado
```


## Estratégia de testes

Todos os **testes unitários** deste projeto são executados sem Docker, PostgreSQL, Flyway ou chamadas HTTP externas.

As dependências externas são mockadas com Mockito:

- Repositories -> `@Mock`
- Services -> `@Mock`
- `UserInfoClient` -> `@Mock`
- Controllers -> `MockMvcBuilders.standaloneSetup(...)`

Isso deixa os testes rápidos, determinísticos e independentes do ambiente Docker.


## Decisões arquiteturais

### Sessão sem scheduler

A sessão guarda `inicio` e `fim`. A aplicação não precisa executar um job para alterar um status. No momento do voto, verifica-se:

```text
inicio <= agora < fim
```

Essa abordagem é simples e funciona bem quando existem múltiplas instâncias da API.

### Banco como última linha de defesa

A consulta `existsByPautaIdAndAssociadoId` melhora a resposta funcional, mas não é suficiente para concorrência. A constraint única no PostgreSQL é que garante.

### API stateless

Nenhum estado de sessão é mantido em memória do pod. Isso permite colocar múltiplas instâncias atrás de um load balancer.


