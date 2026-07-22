# 🎬 Movie Booking API

API REST para reserva de ingressos de cinema, desenvolvida com **Spring Boot 3.4**, **Spring Security + JWT**, **PostgreSQL**, **Redis**, observabilidade com **Prometheus/Grafana** e documentada com **Swagger/OpenAPI**.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Funcionalidades](#-funcionalidades)
- [Segurança](#-segurança)
- [Observabilidade](#-observabilidade)
- [Fluxo de Negócio](#-fluxo-de-negócio)
- [Como Rodar](#-como-rodar)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Endpoints](#-endpoints)
- [Autenticação](#-autenticação)
- [Regras de Negócio](#-regras-de-negócio)
- [Testes](#-testes)
- [CI/CD](#-cicd)
- [Testes de Segurança (Pentest)](#-testes-de-segurança-pentest)
- [Decisões de Arquitetura (ADR)](#-decisões-de-arquitetura-adr)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Banco de Dados](#-banco-de-dados)
- [O que Falta](#-o-que-falta)
- [Melhorias Futuras](#-melhorias-futuras)

---

## 🎯 Sobre o Projeto

O **Movie Booking API** é um sistema backend completo para gerenciamento e reserva de ingressos de cinema. A API permite que administradores gerenciem o catálogo de filmes, salas, cinemas e sessões, enquanto clientes podem consultar a programação, escolher assentos e realizar reservas com expiração automática. O projeto também inclui verificação de conta por e-mail, recuperação de senha, cache distribuído, rate limiting, observabilidade com métricas e uma suíte própria de testes de segurança.

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.2 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.4.x | Persistência de dados |
| PostgreSQL | 42.7.13 | Banco de dados relacional |
| Redis | Latest | Cache distribuído |
| JWT (jjwt) | 0.11.5 | Geração e validação de tokens |
| Bucket4j | 8.19.0 | Rate limiting (algoritmo token bucket) |
| Spring Mail + Thymeleaf | — | Envio de e-mails transacionais (templates HTML) |
| Spring Boot Actuator | — | Health checks e métricas da aplicação |
| Micrometer + Prometheus | — | Coleta e exposição de métricas |
| Grafana | Latest | Dashboards de observabilidade |
| Lombok | Latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.7.0 | Documentação Swagger |
| JUnit 5 + Mockito | — | Testes unitários e de integração |
| H2 Database | — | Banco em memória para testes |
| Docker + Docker Compose | — | Containerização de toda a stack |
| Maven | 3.x | Gerenciamento de dependências |

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
Controller  →  Service (Interface + Impl)  →  Repository  →  Database
                     ↕
                   Mapper
                     ↕
                   DTOs
```

Cada módulo segue o padrão:
- **Entity** — mapeamento JPA para o banco de dados
- **DTO** — objetos de transferência de dados (Request/Response separados)
- **Mapper** — conversão entre Entity e DTO
- **Repository** — acesso ao banco de dados
- **Service** — regras de negócio
- **Controller** — endpoints REST

A documentação Swagger é isolada em interfaces `*ControllerDocs` (pacote `controller/docs/`), separando as anotações do OpenAPI da lógica dos controllers (ver [ADR 0003](#-decisões-de-arquitetura-adr)).

---

## ✅ Funcionalidades

### Autenticação e Contas
- Registro de usuário (role `CUSTOMER` automático) com **verificação de e-mail obrigatória** por código de 6 dígitos
- Reenvio de código de verificação (`/resend-code`)
- Login com geração de token JWT (bloqueado até o e-mail ser verificado)
- **Recuperação de senha** via código enviado por e-mail (`/forgot-password` + `/reset-password`), com mensagem genérica para não vazar quais e-mails estão cadastrados
- E-mails transacionais em HTML via Thymeleaf (código de verificação e redefinição de senha)
- Autorização baseada em roles (`CUSTOMER` / `ADMIN`)

### Cinemas
- CRUD completo (somente `ADMIN`)
- Validação de CNPJ e dados de endereço
- Cache em Redis das consultas

### Salas
- CRUD completo (somente `ADMIN`)
- Vinculação obrigatória a um Cinema existente
- Configuração de capacidade e assentos por fileira
- Cache em Redis das consultas

### Filmes
- CRUD completo (somente `ADMIN`)
- Suporte a gênero (`ACTION`, `COMEDY`, `DRAMA`, `HORROR`, `ROMANCE`, `SCI_FI`, `ANIMATION`)
- Classificação etária (`FREE`, `TEN`, `TWELVE`, `FOURTEEN`, `SIXTEEN`, `EIGHTEEN`)
- Cache em Redis das consultas

### Sessões
- Criação de sessão vinculando Filme + Sala + Horário
- Cálculo automático de horário de término baseado na duração do filme
- Validação de conflito de horário na mesma sala
- Geração automática de assentos ao criar uma sessão
- Cache em Redis das consultas

### Assentos
- Consulta de assentos por sessão com status de disponibilidade
- Identificação por fileira (A, B, C...) e número (1, 2, 3...)
- Cache em Redis das consultas

### Reservas
- Criação de reserva com múltiplos assentos
- Validação de disponibilidade e pertencimento à sessão
- Status: `PENDING` → `CONFIRMED` / `CANCELLED` / `EXPIRED`
- Expiração automática em 10 minutos (scheduler rodando a cada 1 minuto via `@Scheduled`)
- Liberação automática de assentos ao cancelar ou expirar
- Controle de ownership (usuário só acessa suas próprias reservas)
- Cache em Redis das consultas

---

## 🔐 Segurança

- **JWT** externalizado via `application.properties` / variáveis de ambiente (`JWT_SECRET`, `JWT_EXPIRATION`) — não é mais hardcoded no código
- **Rate Limiting** com Bucket4j, aplicado por IP em 3 perfis diferentes:
    - Login (`/auth/login`): 5 requisições/minuto
    - Endpoints sensíveis (`/auth/register`, `/auth/forgot-password`, `/auth/resend-code`): 3 requisições/hora
    - Demais endpoints: 60 requisições/minuto
    - Excedido o limite, a API responde `429 Too Many Requests`
- **Tratamento correto de credenciais inválidas**: `AuthenticationException` (incluindo `BadCredentialsException`) é capturada pelo `GlobalExceptionHandler` e retorna `401 Unauthorized` (em vez de `500`)
- **Logging estruturado com contexto de usuário**: `UserLoggingFilter` injeta o usuário autenticado (ou `anonymous`) no MDC do SLF4J/Logback, aparecendo em todas as linhas de log da requisição
- **Verificação de e-mail obrigatória** antes do primeiro login
- Senhas sempre armazenadas com hash (BCrypt via `PasswordEncoder`)

---

## 📊 Observabilidade

- **Spring Boot Actuator** expõe `health`, `info`, `metrics` e `prometheus`
- **Micrometer + Prometheus** coletam métricas da aplicação (JVM, HTTP, pool de conexões, etc.)
- **Grafana** disponível via Docker Compose para visualização de dashboards
- **Logs estruturados** via SLF4J/Logback, com padrão customizado incluindo o usuário autenticado: `[User: username]`

---

## 🔄 Fluxo de Negócio

```
1. ADMIN cadastra Cinema
2. ADMIN cadastra Room (vinculada ao Cinema)
3. ADMIN cadastra Movie
4. ADMIN cria Session (vincula Movie + Room + horário)
   └── Assentos são gerados automaticamente
5. CUSTOMER se registra
   └── Recebe código de verificação por e-mail
6. CUSTOMER verifica o e-mail (POST /auth/verify)
   └── Conta ativada + token JWT emitido
7. CUSTOMER faz login
8. CUSTOMER consulta sessões disponíveis
9. CUSTOMER consulta assentos da sessão (GET /seats?sessionId=X)
10. CUSTOMER cria reserva escolhendo os assentos
    └── Status: PENDING | Expira em 10 minutos
11. CUSTOMER confirma a reserva
    └── Status: CONFIRMED
12. (Opcional) CUSTOMER cancela a reserva
    └── Status: CANCELLED | Assentos liberados
13. (Opcional) CUSTOMER esqueceu a senha
    └── Solicita código por e-mail e redefine a senha
```

---

## ⚙️ Como Rodar

### Opção 1 — Docker Compose (recomendado)

Sobe API, PostgreSQL, Redis, Prometheus e Grafana de uma vez.

**Pré-requisitos:** Docker e Docker Compose.

**1. Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/movie-booking-api.git
cd movie-booking-api
```

**2. Crie um arquivo `.env`** na raiz do projeto (veja a seção [Variáveis de Ambiente](#-variáveis-de-ambiente)).

**3. Suba os containers:**
```bash
docker compose up --build
```

Isso inicia:
| Serviço | Porta |
|---|---|
| API | `${SERVER_PORT}` (padrão `8082`) |
| PostgreSQL | `5432` |
| Redis | `6379` |
| Prometheus | `9090` |
| Grafana | `3000` |

### Opção 2 — Local (sem Docker)

**Pré-requisitos:**
- Java 21+
- Maven 3.x
- PostgreSQL 14+
- Redis (necessário, pois o cache está configurado como `spring.cache.type=redis`)

**1. Crie o banco de dados no PostgreSQL:**
```sql
CREATE DATABASE cinema_booking_db;
```

**2. Configure as variáveis de ambiente** (veja a seção abaixo).

**3. Rode o projeto:**
```bash
mvn spring-boot:run
```

**4. Acesse a documentação Swagger:**
```
http://localhost:8082/swagger-ui.html
```

---

## 🔐 Variáveis de Ambiente

O projeto usa 100% de configuração externalizada via variáveis de ambiente (arquivo `.env`, não versionado):

```properties
# Banco de dados
DB_URL=jdbc:postgresql://localhost:5432/cinema_booking_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

# Servidor
SERVER_PORT=8082

# JWT
JWT_SECRET=uma_chave_secreta_forte
JWT_EXPIRATION=36000000

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# E-mail (Gmail SMTP)
GMAIL_USERNAME=seu_email@gmail.com
GMAIL_APP_PASSWORD=sua_senha_de_app
```

> ✅ A `SECRET_KEY` do JWT **não está mais hardcoded** — é lida via `${JWT_SECRET}` em `application.properties`.
>
> ⚠️ Para o envio de e-mails funcionar, é necessário gerar uma **senha de app** do Gmail (não a senha normal da conta).

---

## 📡 Endpoints

### Autenticação — `/api/v1/auth`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/register` | Registrar novo usuário (envia código de verificação) | Público |
| `POST` | `/verify` | Verificar e-mail com código recebido | Público |
| `POST` | `/resend-code` | Reenviar código de verificação | Público |
| `POST` | `/login` | Login e geração de JWT (requer e-mail verificado) | Público |
| `POST` | `/forgot-password` | Solicitar código de redefinição de senha | Público |
| `POST` | `/reset-password` | Redefinir senha com o código recebido | Público |

### Cinemas — `/api/v1/cinemas`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/` | Cadastrar cinema | ADMIN |
| `GET` | `/` | Listar cinemas | Público |
| `GET` | `/{id}` | Buscar cinema por ID | Público |
| `PUT` | `/{id}` | Atualizar cinema | ADMIN |
| `DELETE` | `/{id}` | Deletar cinema | ADMIN |

### Salas — `/api/v1/rooms`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/` | Cadastrar sala | ADMIN |
| `GET` | `/` | Listar salas | Público |
| `GET` | `/{id}` | Buscar sala por ID | Público |
| `PUT` | `/{id}` | Atualizar sala | ADMIN |
| `DELETE` | `/{id}` | Deletar sala | ADMIN |

### Filmes — `/api/v1/movies`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/` | Cadastrar filme | ADMIN |
| `GET` | `/` | Listar filmes | Público |
| `GET` | `/{id}` | Buscar filme por ID | Público |
| `PUT` | `/{id}` | Atualizar filme | ADMIN |
| `DELETE` | `/{id}` | Deletar filme | ADMIN |

### Sessões — `/api/v1/sessions`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/` | Criar sessão | ADMIN |
| `GET` | `/` | Listar sessões | Público |
| `GET` | `/{id}` | Buscar sessão por ID | Público |
| `DELETE` | `/{id}` | Deletar sessão | ADMIN |

### Assentos — `/api/v1/seats`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/?sessionId={id}` | Listar assentos de uma sessão | Público |

### Reservas — `/api/v1/bookings`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/` | Criar reserva | Autenticado |
| `POST` | `/{id}/confirm` | Confirmar reserva | Autenticado |
| `POST` | `/{id}/cancel` | Cancelar reserva | Autenticado |
| `GET` | `/{id}` | Buscar reserva por ID | Autenticado |
| `GET` | `/me` | Minhas reservas | Autenticado |

### Observabilidade — `/actuator`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/actuator/health` | Status da aplicação | Público |
| `GET` | `/actuator/info` | Informações da aplicação | Público |
| `GET` | `/actuator/metrics` | Métricas gerais | Público |
| `GET` | `/actuator/prometheus` | Métricas no formato Prometheus | Público |

> ⚠️ Todos os endpoints (exceto os públicos listados acima) estão sujeitos a **rate limiting por IP** (ver seção [Segurança](#-segurança)).

---

## 🔑 Autenticação

A API usa **JWT Bearer Token**, com verificação de e-mail obrigatória antes do primeiro login.

**1. Registre-se:**
```json
POST /api/v1/auth/register
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "123456"
}
```
Você receberá um código de 6 dígitos por e-mail (válido por 15 minutos).

**2. Verifique seu e-mail:**
```json
POST /api/v1/auth/verify
{
  "email": "joao@email.com",
  "code": "123456"
}
```
A resposta já inclui um token JWT.

**3. Faça login (a partir daí) e guarde o token:**
```json
POST /api/v1/auth/login
{
  "email": "joao@email.com",
  "password": "123456"
}
```

**4. Use o token no header de todas as requisições protegidas:**
```
Authorization: Bearer {seu_token_aqui}
```

> O token expira conforme `JWT_EXPIRATION` (padrão: 10 horas). Após isso, faça login novamente.

**Esqueceu a senha?**
```json
POST /api/v1/auth/forgot-password
{ "email": "joao@email.com" }
```
```json
POST /api/v1/auth/reset-password
{
  "email": "joao@email.com",
  "code": "123456",
  "newPassword": "novaSenha123"
}
```

---

## 📏 Regras de Negócio

- Uma `Room` só pode ser criada se o `Cinema` existir no banco.
- Uma `Session` só pode ser criada se o `Movie` e a `Room` existirem.
- Não é possível criar duas sessões na **mesma sala** com **horários sobrepostos**.
- O `endTime` da sessão é calculado automaticamente: `startTime + movie.duration`.
- Os assentos são gerados automaticamente ao criar uma sessão (fileiras A, B, C...).
- Uma reserva só pode ser feita com assentos `available = true` e pertencentes à sessão informada.
- Reservas com status `PENDING` expiram em **10 minutos** automaticamente.
- Ao cancelar ou expirar uma reserva, os assentos são liberados automaticamente.
- Um usuário só pode confirmar/cancelar suas próprias reservas (exceto `ADMIN`).
- O registro sempre cria usuários com role `CUSTOMER`. A promoção para `ADMIN` é feita diretamente no banco.
- O usuário **precisa verificar o e-mail** antes de conseguir fazer login.
- Códigos de verificação e de redefinição de senha expiram (15 minutos e 10 minutos, respectivamente) e são de uso único.
- A recuperação de senha sempre retorna a mesma mensagem genérica, verificado o e-mail existindo ou não, para não vazar quais contas estão cadastradas.

---

## 🧪 Testes

O projeto conta com uma suíte de testes automatizados usando **JUnit 5**, **Mockito** e **H2** (banco em memória):

- **Testes de unidade dos Services** (`service/impl/*Test.java`): `BookingServiceImplTest`, `CinemaServiceImplTest`, `MovieServiceImplTest`, `RoomServiceImplTest`, `SeatServiceImplTest`, `SessionServiceImplTest` — cobrindo regras de negócio, validações e cenários de erro.
- **Testes de Controller** (`@WebMvcTest` + `MockMvc`): `CinemaControllerTest`, `MovieControllerTest` — validando contrato HTTP, status codes, serialização e segurança de endpoints.

Para rodar os testes:
```bash
mvn test
```

---

## 🔁 CI/CD

Pipeline configurado em `.github/workflows/ci.yml` (GitHub Actions):
- Disparado em `push` e `pull_request` para a branch `main`
- Configura Java 21 (Temurin) com cache do Maven
- Executa `mvn test`
- Publica os relatórios de teste (`surefire-reports`) como artefato do workflow

---

## 🛡️ Testes de Segurança (Pentest)

A pasta `pentest/` contém scripts e relatórios de testes de segurança ofensivos rodados contra a própria API, em ambiente local, seguindo práticas de Red Team:

- `pentest/scripts/brute_force.py` — script Python que simula um ataque de força bruta contra `/api/v1/auth/login`
- `pentest/reports/brute_force_report.md` — relatório com objetivo, metodologia e resultado do teste (validando o Rate Limiting implementado)

> ⚠️ Uso exclusivo contra a própria API em ambiente local de desenvolvimento — ver aviso legal no `pentest/README.md`.

---

## 📄 Decisões de Arquitetura (ADR)

Decisões técnicas relevantes documentadas em `doc/architecture/decisions/`:

| ADR | Título |
|---|---|
| 0001 | Registrar Decisões de Arquitetura (ADR) |
| 0002 | Escolha do Banco de Dados Relacional (PostgreSQL) |
| 0003 | Isolar anotações do OpenAPI/Swagger em interfaces `ControllerDocs` |
| 0004 | Uso de JSON Web Tokens (JWT) para Autenticação e Autorização |

O diagrama de entidade-relacionamento também está documentado em `doc/architecture/database-der.md`.

---

## 📁 Estrutura do Projeto

```
src/main/java/com/gabriel/moviebooking/
├── config/
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── CinemaController.java
│   ├── MovieController.java
│   ├── RoomController.java
│   ├── SeatController.java
│   ├── SessionController.java
│   └── docs/                     # Interfaces com anotações Swagger/OpenAPI
│       ├── AuthControllerDocs.java
│       ├── BookingControllerDocs.java
│       ├── CinemaControllerDocs.java
│       ├── MovieControllerDocs.java
│       ├── RoomControllerDocs.java
│       ├── SeatControllerDocs.java
│       └── SessionControllerDocs.java
├── dto/
│   ├── auth/                     # inclui verify, resend-code, forgot/reset password
│   ├── booking/
│   ├── cinema/
│   ├── movie/
│   ├── room/
│   ├── seat/
│   └── session/
├── entity/
│   ├── Booking.java
│   ├── Cinema.java
│   ├── Movie.java
│   ├── Room.java
│   ├── Seat.java
│   ├── Session.java
│   └── User.java                 # inclui campos de verificação e reset de senha
├── enums/
│   ├── AgeRating.java
│   ├── BookingStatus.java
│   ├── Genre.java
│   ├── Role.java
│   ├── RoomType.java
│   └── State.java
├── exception/
│   ├── BusinessException.java
│   ├── CinemaNotFoundException.java
│   ├── ErrorResponseDTO.java
│   ├── GlobalExceptionHandler.java  # trata AuthenticationException/BadCredentials
│   └── ResourceNotFoundException.java
├── factory/
│   └── SeatGenerator.java
├── mapper/
│   ├── BookingMapper.java
│   ├── CinemaMapper.java
│   ├── MovieMapper.java
│   ├── RoomMapper.java
│   ├── SeatMapper.java
│   └── SessionMapper.java
├── repository/
│   ├── BookingRepository.java
│   ├── CinemaRepository.java
│   ├── MovieRepository.java
│   ├── RoomRepository.java
│   ├── SeatRepository.java
│   ├── SessionRepository.java
│   └── UserRepository.java
├── scheduler/
│   └── BookingExpirationScheduler.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   ├── RateLimitingFilter.java    # Bucket4j
│   ├── SecurityConfig.java
│   └── UserLoggingFilter.java     # injeta usuário no MDC de log
└── service/
    ├── impl/
    │   ├── AuthServiceImpl.java
    │   ├── BookingServiceImpl.java
    │   ├── CinemaServiceImpl.java
    │   ├── CustomUserDetailsService.java
    │   ├── EmailService.java       # Thymeleaf + Spring Mail
    │   ├── MovieServiceImpl.java
    │   ├── RoomServiceImpl.java
    │   ├── SeatServiceImpl.java
    │   └── SessionServiceImpl.java
    ├── AuthService.java
    ├── BookingService.java
    ├── CinemaService.java
    ├── MovieService.java
    ├── RoomService.java
    ├── SeatService.java
    └── SessionService.java

src/main/resources/
├── application.properties
└── templates/email/
    ├── reset-password.html
    └── verification-code.html

src/test/java/com/gabriel/moviebooking/
├── controller/
│   ├── CinemaControllerTest.java
│   └── MovieControllerTest.java
└── service/impl/
    ├── BookingServiceImplTest.java
    ├── CinemaServiceImplTest.java
    ├── MovieServiceImplTest.java
    ├── RoomServiceImplTest.java
    ├── SeatServiceImplTest.java
    └── SessionServiceImplTest.java

doc/architecture/
├── database-der.md
└── decisions/                    # ADRs

pentest/
├── README.md
├── scripts/brute_force.py
└── reports/brute_force_report.md

.github/workflows/ci.yml
docker-compose.yml
Dockerfile
```

---

## 🗄️ Banco de Dados

### Diagrama de Relacionamentos

```mermaid
    erDiagram
    CINEMAS ||--|{ ROOMS : "possui"
    ROOMS ||--|{ SESSIONS : "sedia"
    MOVIES ||--|{ SESSIONS : "exibido em"
    USERS ||--|{ BOOKINGS : "faz"
    SESSIONS ||--|{ BOOKINGS : "tem"
    SEATS }|--|| BOOKINGS : "reservados em"

    CINEMAS {
        Long id PK
        String name
        String location
    }
    ROOMS {
        Long id PK
        String name
        Integer capacity
    }
    SESSIONS {
        Long id PK
        LocalDateTime startTime
        BigDecimal price
    }
    MOVIES {
        Long id PK
        String title
        Integer duration
    }
    BOOKINGS {
        Long id PK
        LocalDateTime createdAt
        String status
    }
    SEATS {
        Long id PK
        String seatNumber
    }
 ```
### Tabelas

| Tabela | Descrição |
|---|---|
| `cinemas` | Dados do cinema (nome, CNPJ, endereço) |
| `rooms` | Salas vinculadas a um cinema |
| `movies` | Catálogo de filmes |
| `sessions` | Sessões (filme + sala + horário) |
| `seats` | Assentos gerados por sessão |
| `users` | Usuários cadastrados (inclui campos de verificação de e-mail e reset de senha) |
| `bookings` | Reservas de ingressos |
| `booking_seats` | Tabela associativa reserva ↔ assento |

Veja também o diagrama detalhado em `doc/architecture/database-der.md`.

---

## 🚧 O que Falta

Funcionalidades importantes que ainda não foram implementadas:

- [ ] **Endpoint para criar ADMIN** — a promoção para ADMIN ainda é feita manualmente via SQL. Falta um endpoint protegido (`POST /api/v1/admin/users/{id}/promote`) ou um `DataSeeder` que crie um admin padrão na inicialização.
- [ ] **Cobertura de testes ainda incompleta** — existem testes de unidade para a maioria dos Services e testes de Controller apenas para `Cinema` e `Movie`. Faltam testes de Controller para `Room`, `Session`, `Seat`, `Booking` e `Auth`, além de testes de integração ponta a ponta.
- [ ] **Filtro de sessões por filme** — não existe endpoint `GET /sessions?movieId=X` para buscar sessões de um filme específico.
- [ ] **Paginação** — endpoints `findAll()` retornam todos os registros sem paginação, o que pode ser problemático com grandes volumes de dados.
- [ ] **Refresh Token** — o token JWT expira (padrão 10 horas) e não há mecanismo de renovação sem novo login.
- [ ] **Invalidação de cache** — o cache Redis é usado em consultas (`@Cacheable`), mas é preciso revisar se todas as operações de escrita (`create`/`update`/`delete`) possuem `@CacheEvict` correspondente para evitar dados desatualizados.
- [ ] **Geração de PDF do ingresso** — funcionalidade em estudo/planejamento: gerar um PDF do ingresso ao confirmar a reserva (ex: usando OpenPDF ou JasperReports).

---

## 🔮 Melhorias Futuras

Evoluções planejadas para versões futuras:

- [ ] **Pagamento** — integração com gateway de pagamento (ex: Stripe, PagSeguro) para processar o pagamento da reserva antes de confirmá-la.
- [ ] **Upload de pôster do filme** — armazenar imagens dos filmes (ex: AWS S3 ou Cloudinary).
- [ ] **Role `MANAGER`** — um nível intermediário entre `CUSTOMER` e `ADMIN`, responsável por gerenciar as sessões de um cinema específico.
- [ ] **Avaliações de filmes** — permitir que usuários avaliem filmes após assistir.
- [ ] **Histórico de preços** — registrar variações de preço por sessão/horário (matinê, noturno, etc.).
- [ ] **Tipos de assento** — diferenciar assentos comuns, VIP e para pessoas com deficiência.
- [ ] **Deploy em nuvem** — fazer deploy da stack Docker em um provedor cloud (Railway, Render, AWS, etc.).
- [ ] **Dashboards Grafana prontos** — hoje o Grafana já sobe via Docker Compose, mas ainda sem dashboards pré-configurados para as métricas do Prometheus.
- [ ] **Mais cenários de pentest** — expandir a pasta `pentest/` com outros testes (ex: enumeração de usuários, fuzzing de payloads, testes de autorização entre roles).

---

> 📖 Documentação interativa disponível em: `http://localhost:8082/swagger-ui.html`
> 📊 Métricas Prometheus em: `http://localhost:8082/actuator/prometheus`