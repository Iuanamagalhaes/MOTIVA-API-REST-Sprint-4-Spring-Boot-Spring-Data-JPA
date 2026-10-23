# MOTIVA — API REST (Sprint 4: Spring Boot + Spring Data JPA)

Evolução do sistema MOTIVA (monitoramento e manutenção de vegetação em rodovias).
Na Sprint 3 o sistema persistia no Oracle com **JDBC puro + DAO**; agora ele é uma
**API REST** com **Spring Boot 4.1, Spring Data JPA e Oracle**, reaproveitando as
mesmas tabelas (`sql/script-criacao.sql`).

## Estrutura

```
sprint-04/
├── pom.xml
├── sql/                          # scripts da Sprint 3 (não mudaram)
│   ├── script-criacao.sql
│   └── script-dados.sql
└── src/
    ├── main/java/br/com/motiva/
    │   ├── MotivaApplication.java        # @SpringBootApplication
    │   ├── model/                        # @Entity (TrechoRodovia, EquipeManutencao,
    │   │                                 #   IntervencaoOperacional + 3 subclasses, RelatorioPrioridade)
    │   ├── repository/                   # interfaces JpaRepository + derived queries (SEM SQL)
    │   ├── service/                      # @Service: regras de negócio (MotorPrioridade, validações, GeradorRelatorio)
    │   ├── controller/                   # @RestController: apenas HTTP
    │   ├── dto/                          # records de entrada/saída (contrato da API) 
    │   └── exception/                    # exceções de negócio + @RestControllerAdvice 
    ├── main/resources/application.properties
    └── test/                             # @SpringBootTest + MockMvc, H2 em memória   
```

Fluxo: **Controller → Service → Repository → Banco** 

## Como executar

Pré-requisitos: **Java 17** e **Maven**; acesso ao Oracle da FIAP.
1. Crie as tabelas e dados (uma vez), executando `sql/script-criacao.sql` e `sql/script-dados.sql`
   no Oracle (as tabelas são as mesmas da Sprint 3).
2. Informe suas credenciais **sem escrevê-las no código** — escolha uma opção:

   ```bash
   # Opção A: variáveis de ambiente
   export DB_USER=rm123456
   export DB_PASSWORD=sua_senha
   # (opcional) export DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
   ```
   ```properties
   # Opção B: arquivo ./application-local.properties na raiz (já está no .gitignore)
   spring.datasource.username=rm123456
   spring.datasource.password=sua_senha
   ```
3. Suba a API:

   ```bash
   mvn spring-boot:run
   ```
   A API fica em `http://localhost:8080`. O SQL gerado pelo Spring Data aparece no console
   (`spring.jpa.show-sql=true`).
4. Testes (usam H2 em memória, **não** tocam no Oracle): `mvn test`


## Regras do motor de prioridade (Sprint 2/3)

| Nível de vegetação | Prioridade | Intervenção sugerida |
|---|---|---|
| ≥ 40 cm | CRITICO | MECANIZADA (roçada mecanizada) |
| ≥ 20 cm | ATENCAO | MANUAL (roçada manual) |
| ≥ 10 cm | ALERTA | PULVERIZACAO |
| < 10 cm | NORMAL | nenhuma |

Vivem em `service/MotorPrioridade`.

## Endpoints

### Trechos — `/api/trechos`

| Método | Rota | Ação | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/trechos` | Lista todos (filtros opcionais `?nivelMinimo=20`, `?tipoAmbiente=UMIDO`) | 200 | 400 (filtro inválido) |
| GET | `/api/trechos/{id}` | Busca por ID | 200 | 404 |
| POST | `/api/trechos` | Cria | 201 + `Location` + corpo | 400 |
| PUT | `/api/trechos/{id}` | Atualiza | 200 | 400 / 404 |
| DELETE | `/api/trechos/{id}` | Remove | 204 | 404 / 409 (tem intervenções) |

### Equipes — `/api/equipes`

| Método | Rota | Ação | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/equipes` | Lista (filtro opcional `?nome=norte`) | 200 | — |
| GET | `/api/equipes/{id}` | Busca por ID | 200 | 404 |
| POST | `/api/equipes` | Cria | 201 | 400 |
| PUT | `/api/equipes/{id}` | Atualiza | 200 | 400 / 404 |
| DELETE | `/api/equipes/{id}` | Remove | 204 | 404 |

### Intervenções — `/api/intervencoes`

| Método | Rota | Ação | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/intervencoes` | Lista (filtros `?tipo=MANUAL`, `?trechoId=1`) | 200 | 400 |
| GET | `/api/intervencoes/{id}` | Busca por ID | 200 | 404 |
| POST | `/api/intervencoes` | Cria (campos conforme o tipo) | 201 | 400 |
| PUT | `/api/intervencoes/{id}` | Atualiza (não troca o tipo) | 200 | 400 / 404 |
| DELETE | `/api/intervencoes/{id}` | Remove | 204 | 404 |

Campos por tipo: `MECANIZADA` → `tipoEquipamento` · `MANUAL` → `numeroPessoas` ·
`PULVERIZACAO` → `produtoQuimico` + `volumeLitros`.

### Relatórios — `/api/relatorios`

| Método | Rota | Ação | Sucesso | Erros |
|---|---|---|---|---|
| POST | `/api/relatorios` | Gera o relatório com os trechos atuais **e grava no histórico** | 201 | 400 (sem trechos) |
| GET | `/api/relatorios` | Lista o histórico (mais recentes primeiro) | 200 | — |
| GET | `/api/relatorios/periodo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` | Histórico por período | 200 | 400 |
| GET | `/api/relatorios/{id}` | Busca um relatório | 200 | 404 |

Erros seguem um formato único (`@RestControllerAdvice`):

```json
{
  "timestamp": "2026-10-18T10:15:30",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados da requisição inválidos.",
  "caminho": "/api/trechos",
  "detalhes": ["nivelVegetacao: nivelVegetacao não pode ser negativo"]
}
```

## Exemplos cURL

```bash
# 1) Criar trecho  -> 201
curl -i -X POST http://localhost:8080/api/trechos -H "Content-Type: application/json" \
  -d '{"quilometroInicial":40.0,"quilometroFinal":45.0,"nivelVegetacao":52.5,"tipoAmbiente":"TROPICAL","possuiSensorIoT":true}'

# 2) Listar / filtrar derived query  -> 200
curl http://localhost:8080/api/trechos
curl "http://localhost:8080/api/trechos?nivelMinimo=40"

# 3) Buscar por ID  -> 200 (ou 404)
curl -i http://localhost:8080/api/trechos/1
curl -i http://localhost:8080/api/trechos/9999

# 4) Atualizar  -> 200
curl -i -X PUT http://localhost:8080/api/trechos/1 -H "Content-Type: application/json" \
  -d '{"quilometroInicial":10.0,"quilometroFinal":15.0,"nivelVegetacao":8.0,"tipoAmbiente":"SECO","possuiSensorIoT":false}'

# 5) Remover  -> 204 (409 se houver intervenções)
curl -i -X DELETE http://localhost:8080/api/trechos/5

# 6) Dado inválido  -> 400
curl -i -X POST http://localhost:8080/api/trechos -H "Content-Type: application/json" \
  -d '{"quilometroInicial":20.0,"quilometroFinal":10.0,"nivelVegetacao":5.0,"tipoAmbiente":"PADRAO","possuiSensorIoT":false}'

# Equipes
curl -i -X POST http://localhost:8080/api/equipes -H "Content-Type: application/json" \
  -d '{"nome":"Equipe Leste","quantidadeIntegrantes":6}'

# Intervenções (uma de cada tipo)
curl -i -X POST http://localhost:8080/api/intervencoes -H "Content-Type: application/json" \
  -d '{"descricao":"Rocada mecanizada","idTrechoAlvo":1,"tipoIntervencao":"MECANIZADA","tipoEquipamento":"Trator TR-200"}'
curl -i -X POST http://localhost:8080/api/intervencoes -H "Content-Type: application/json" \
  -d '{"descricao":"Rocada manual","idTrechoAlvo":2,"tipoIntervencao":"MANUAL","numeroPessoas":4}'
curl -i -X POST http://localhost:8080/api/intervencoes -H "Content-Type: application/json" \
  -d '{"descricao":"Pulverizacao","idTrechoAlvo":3,"tipoIntervencao":"PULVERIZACAO","produtoQuimico":"Herbicida X1","volumeLitros":12.5}'
curl "http://localhost:8080/api/intervencoes?tipo=MANUAL"

# Relatórios
curl -i -X POST http://localhost:8080/api/relatorios        # gera + persiste -> 201
curl http://localhost:8080/api/relatorios                    # histórico
curl "http://localhost:8080/api/relatorios/periodo?inicio=2026-10-01&fim=2026-10-31"
```

## Evidências (prints das requisições)


## Perguntas de reflexão

## 1. Por que o Repository é uma interface e não uma classe? Quem escreve a implementação e quando?

Porque a gente só precisa definir quais operações quer fazer no banco, como salvar, buscar e filtrar, sem precisar escrever todo o código por trás disso. Quem cuida da implementação é o Spring Data, que cria essa implementação quando a aplicação inicia. Assim, não precisamos escrever manualmente os métodos básicos de acesso ao banco. Até consultas como `findByTipoAmbiente` são criadas automaticamente pelo Spring com base no nome do método.

## 2. O pattern DAO da Sprint 3 "morreu" na migração ou só mudou de forma?

Ele só mudou de forma. A ideia continua a mesma: separar o acesso ao banco do restante do sistema. Na Sprint 3, a gente precisava escrever o SQL e cuidar de coisas como `PreparedStatement` e `ResultSet`. Agora, o Spring Data faz boa parte desse trabalho por meio dos Repositories. Com isso, o Service continua sem precisar saber exatamente como os dados são consultados ou salvos no banco.

## 3. Por que a validação de `nivelVegetacao >= 0` (Sprint 1) deve ficar no Service e não no Controller?

Porque essa validação faz parte das regras de negócio do sistema, e não apenas da comunicação com a API. Se ela ficasse somente no Controller, outro recurso que utilizasse o Service, como um teste ou uma futura integração com sensores IoT, poderia cadastrar um valor inválido. Deixando a regra no Service, garantimos que ela seja aplicada independentemente de onde a operação for chamada. O Controller fica responsável por receber as requisições e retornar as respostas HTTP, enquanto o Bean Validation no DTO ajuda a verificar os dados recebidos antes de processá-los.

## 4. No JDBC puro vocês escreviam SQL. Onde está o SQL do `findByTipo()`? Quem o gerou?

Dessa vez, a gente não precisa escrever o SQL diretamente no código. O Spring Data identifica o nome do método `findByTipo()` e usa a propriedade correspondente da entidade para montar a consulta automaticamente. Depois, o Hibernate transforma essa consulta em SQL compatível com o banco utilizado. Por exemplo, ele pode gerar um `SELECT` na tabela de intervenções, filtrando pelo tipo informado. Se quisermos conferir o SQL gerado durante a execução, podemos habilitar a configuração `spring.jpa.show-sql=true`.
