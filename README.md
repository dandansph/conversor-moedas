# Conversor de Moedas (Spring Boot)

API REST que converte valores entre moedas consumindo a API pública e gratuita
[Frankfurter](https://frankfurter.dev) (sem chave de acesso) com `RestClient`.

**Stack:** Java 17+, Spring Boot 3.3, Spring Web, Bean Validation, Maven.

## Como rodar

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Endpoints

### Converter um valor

```
GET /api/converter?de=USD&para=BRL&valor=100
```

Exemplo de resposta:

```json
{
  "de": "USD",
  "para": "BRL",
  "valorOriginal": 100,
  "taxa": 5.40,
  "valorConvertido": 540.00,
  "dataCotacao": "2026-10-13"
}
```

### Listar moedas suportadas

```
GET /api/moedas
```

## Testando rápido

```bash
curl "http://localhost:8080/api/converter?de=USD&para=BRL&valor=100"
curl "http://localhost:8080/api/converter?de=EUR&para=JPY&valor=25.50"
curl "http://localhost:8080/api/moedas"
```

## Erros

Os erros seguem o formato `ProblemDetail` (RFC 9457):

| Situação                                    | Status |
|---------------------------------------------|--------|
| Parâmetro ausente, código inválido, valor ≤ 0 | 400    |
| Moeda que a API não conhece                 | 404    |
| API de câmbio fora do ar                    | 502    |

## Testes

```bash
mvn test
```

Os testes não dependem de internet: o cliente HTTP é substituído por um mock.

## Estrutura

```
src/main/java/com/example/conversor
├── ConversorApplication.java       # classe main
├── controller/ConversorController  # endpoints + validação dos parâmetros
├── service/ConversorService        # regra de negócio (cálculo e arredondamento)
├── client/FrankfurterClient        # chamada HTTP à API externa (RestClient)
├── dto/                            # records de entrada/saída
└── exception/                      # exceções + tratamento global de erros
```

## Ideias para evoluir

- Cache da cotação (`spring-boot-starter-cache` + Caffeine) para não chamar a API a cada request.
- Endpoint de histórico (`/v1/{data}` e séries temporais da Frankfurter).
- Documentação automática com springdoc-openapi (Swagger UI).
