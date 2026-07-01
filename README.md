# 🎬 Movie Booking API

API REST para reserva de ingressos de cinema, desenvolvida com **Spring Boot 3.3**, **Spring Security + JWT**, **PostgreSQL** e documentada com **Swagger/OpenAPI**.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Funcionalidades](#-funcionalidades)
- [Fluxo de Negócio](#-fluxo-de-negócio)
- [Como Rodar](#-como-rodar)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Endpoints](#-endpoints)
- [Autenticação](#-autenticação)
- [Regras de Negócio](#-regras-de-negócio)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Banco de Dados](#-banco-de-dados)
- [O que Falta](#-o-que-falta)
- [Melhorias Futuras](#-melhorias-futuras)

---

## 🎯 Sobre o Projeto

O **Movie Booking API** é um sistema backend completo para gerenciamento e reserva de ingressos de cinema. A API permite que administradores gerenciem o catálogo de filmes, salas, cinemas e sessões, enquanto clientes podem consultar a programação, escolher assentos e realizar reservas com expiração automática.

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.3.12 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.3.x | Persistência de dados |
| PostgreSQL | 42.7.3 | Banco de dados relacional |
| JWT (jjwt) | 0.11.5 | Geração e validação de tokens |
| Lombok | Latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |
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

---

## ✅ Funcionalidades

### Autenticação
- Registro de usuário (role `CUSTOMER` automático)
- Login com geração de token JWT
- Autorização baseada em roles (`CUSTOMER` / `ADMIN`)

### Cinemas
- CRUD completo (somente `ADMIN`)
- Validação de CNPJ e dados de endereço

### Salas
- CRUD completo (somente `ADMIN`)
- Vinculação obrigatória a um Cinema existente
- Configuração de capacidade e assentos por fileira

### Filmes
- CRUD completo (somente `ADMIN`)
- Suporte a gênero (`ACTION`, `COMEDY`, `DRAMA`, `HORROR`, `ROMANCE`, `SCI_FI`, `ANIMATION`)
- Classificação etária (`FREE`, `TEN`, `TWELVE`, `FOURTEEN`, `SIXTEEN`, `EIGHTEEN`)

### Sessões
- Criação de sessão vinculando Filme + Sala + Horário
- Cálculo automático de horário de término baseado na duração do filme
- Validação de conflito de horário na mesma sala
- Geração automática de assentos ao criar uma sessão

### Assentos
- Consulta de assentos por sessão com status de disponibilidade
- Identificação por fileira (A, B, C...) e número (1, 2, 3...)

### Reservas
- Criação de reserva com múltiplos assentos
- Validação de disponibilidade e pertencimento à sessão
- Status: `PENDING` → `CONFIRMED` / `CANCELLED` / `EXPIRED`
- Expiração automática em 10 minutos (scheduler rodando a cada 1 minuto)
- Liberação automática de assentos ao cancelar ou expirar
- Controle de ownership (usuário só acessa suas próprias reservas)

---

## 🔄 Fluxo de Negócio

```
1. ADMIN cadastra Cinema
2. ADMIN cadastra Room (vinculada ao Cinema)
3. ADMIN cadastra Movie
4. ADMIN cria Session (vincula Movie + Room + horário)
   └── Assentos são gerados automaticamente
5. CUSTOMER se registra / faz login
6. CUSTOMER consulta sessões disponíveis
7. CUSTOMER consulta assentos da sessão (GET /seats?sessionId=X)
8. CUSTOMER cria reserva escolhendo os assentos
   └── Status: PENDING | Expira em 10 minutos
9. CUSTOMER confirma a reserva
   └── Status: CONFIRMED
10. (Opcional) CUSTOMER cancela a reserva
    └── Status: CANCELLED | Assentos liberados
```

---

## ⚙️ Como Rodar

### Pré-requisitos

- Java 21+
- Maven 3.x
- PostgreSQL 14+

### Passos

**1. Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/movie-booking-api.git
cd movie-booking-api
```

**2. Crie o banco de dados no PostgreSQL:**
```sql
CREATE DATABASE cinema_booking_db;
```

**3. Configure as variáveis de ambiente** (veja a seção abaixo).

**4. Rode o projeto:**
```bash
mvn spring-boot:run
```

**5. Acesse a documentação Swagger:**
```
http://localhost:8082/swagger-ui.html
```

---

## 🔐 Variáveis de Ambiente

Configure o arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cinema_booking_db
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

server.port=8082

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

> ⚠️ **Atenção**: a `SECRET_KEY` do JWT atualmente está hardcoded no `JwtService`. Em produção, mova para uma variável de ambiente.

---

## 📡 Endpoints

### Autenticação — `/api/v1/auth`

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/register` | Registrar novo usuário | Público |
| `POST` | `/login` | Login e geração de JWT | Público |

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

---

## 🔑 Autenticação

A API usa **JWT Bearer Token**. Para acessar endpoints protegidos:

**1. Registre-se:**
```json
POST /api/v1/auth/register
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "123456"
}
```

**2. Faça login e guarde o token:**
```json
POST /api/v1/auth/login
{
  "email": "joao@email.com",
  "password": "123456"
}
```

**3. Use o token no header de todas as requisições protegidas:**
```
Authorization: Bearer {seu_token_aqui}
```

> O token expira em **10 horas**. Após isso, faça login novamente.

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
│   └── SessionController.java
├── dto/
│   ├── auth/
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
│   └── User.java
├── enums/
│   ├── AgeRating.java
│   ├── BookingStatus.java
│   ├── Genre.java
│   ├── Role.java
│   ├── RoomType.java
│   └── State.java
├── exception/
│   ├── BusinessException.java
│   ├── ErrorResponseDTO.java
│   ├── GlobalExceptionHandler.java
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
│   └── SecurityConfig.java
└── service/
    ├── impl/
    │   ├── BookingServiceImpl.java
    │   ├── CinemaServiceImpl.java
    │   ├── CustomUserDetailsService.java
    │   ├── MovieServiceImpl.java
    │   ├── RoomServiceImpl.java
    │   ├── SeatServiceImpl.java
    │   └── SessionServiceImpl.java
    ├── BookingService.java
    ├── CinemaService.java
    ├── MovieService.java
    ├── RoomService.java
    ├── SeatService.java
    └── SessionService.java
```

---

## 🗄️ Banco de Dados

### Diagrama de Relacionamentos

```
cinemas (1) ──── (N) rooms (1) ──── (N) sessions (1) ──── (N) seats
                                         │
                                    movies (N)
                                         │
                                    bookings (N) ──── (N) seats
                                         │
                                    users (N)
```

### Tabelas

| Tabela | Descrição |
|---|---|
| `cinemas` | Dados do cinema (nome, CNPJ, endereço) |
| `rooms` | Salas vinculadas a um cinema |
| `movies` | Catálogo de filmes |
| `sessions` | Sessões (filme + sala + horário) |
| `seats` | Assentos gerados por sessão |
| `users` | Usuários cadastrados |
| `bookings` | Reservas de ingressos |
| `booking_seats` | Tabela associativa reserva ↔ assento |

---

## 🚧 O que Falta

Funcionalidades importantes que ainda não foram implementadas:

- [ ] **Endpoint para criar ADMIN** — atualmente a promoção para ADMIN é feita manualmente via SQL. Falta um endpoint protegido (`POST /api/v1/admin/users/{id}/promote`) ou um `DataSeeder` que crie um admin padrão na inicialização.
- [ ] **Tratamento específico de `BadCredentialsException`** — login com senha errada retorna 500 em vez de 401. Falta adicionar um `@ExceptionHandler` específico no `GlobalExceptionHandler`.
- [ ] **Testes automatizados** — nenhum teste unitário ou de integração foi implementado ainda.
- [ ] **Filtro de sessões por filme** — não existe endpoint `GET /sessions?movieId=X` para buscar sessões de um filme específico.
- [ ] **Paginação** — endpoints `findAll()` retornam todos os registros sem paginação, o que pode ser problemático com grandes volumes de dados.
- [ ] **Refresh Token** — o token JWT expira em 10 horas e não há mecanismo de renovação sem novo login.
- [ ] **SECRET_KEY do JWT em variável de ambiente** — atualmente hardcoded no código.

---

## 🔮 Melhorias Futuras

Evoluções planejadas para versões futuras:

- [ ] **Pagamento** — integração com gateway de pagamento (ex: Stripe, PagSeguro) para processar o pagamento da reserva antes de confirmá-la.
- [ ] **Notificações por email** — enviar email de confirmação de reserva, lembrete da sessão e aviso de expiração usando Spring Mail.
- [ ] **Upload de pôster do filme** — armazenar imagens dos filmes (ex: AWS S3 ou Cloudinary).
- [ ] **Role `MANAGER`** — um nível intermediário entre `CUSTOMER` e `ADMIN`, responsável por gerenciar as sessões de um cinema específico.
- [ ] **Avaliações de filmes** — permitir que usuários avaliem filmes após assistir.
- [ ] **Histórico de preços** — registrar variações de preço por sessão/horário (matinê, noturno, etc.).
- [ ] **Tipos de assento** — diferenciar assentos comuns, VIP e para pessoas com deficiência.
- [ ] **Deploy** — containerizar com Docker e fazer deploy em nuvem (Railway, Render, AWS, etc.).
- [ ] **Cache** — usar Redis para cachear listas de filmes e sessões, reduzindo carga no banco.
- [ ] **Rate Limiting** — limitar o número de requisições por IP para evitar abusos.
- [ ] **Logs estruturados** — usar Logback/SLF4J com logs em JSON para facilitar monitoramento em produção.


> 📖 Documentação interativa disponível em: `http://localhost:8082/swagger-ui.html`