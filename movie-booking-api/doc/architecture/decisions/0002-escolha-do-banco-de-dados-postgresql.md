# 2. Escolha do Banco de Dados Relacional (PostgreSQL)

* **Status:** Aceito
* **Data:** 2026-07-22

## Contexto

A aplicação de reserva de ingressos de cinema (`movie-booking-api`) requer persistência de dados com forte integridade transacional. O modelo de domínio exige relacionamentos complexos e consistência rígida entre entidades como Filmes, Salas, Assentos, Sessões e Reservas (evitando, por exemplo, reservas duplicadas do mesmo assento).

Precisávamos escolher um Sistema Gerenciador de Banco de Dados (SGBD) confiável, de código aberto e amplamente suportado pelo ecossistema Spring Boot / Spring Data JPA.

## Decisão

Decidimos utilizar o **PostgreSQL** como o banco de dados relacional principal da aplicação.

* O PostgreSQL será executado em ambiente de desenvolvimento via container Docker.
* A integração com a aplicação será feita através do Spring Data JPA / Hibernate com o driver oficial `org.postgresql:postgresql`.

## Consequências

### Positivas:
* **Confiabilidade e ACID:** Suporte completo a transações ACID com alto nível de isolamento, essencial para concorrência na compra de ingressos.
* **Recursos Avançados:** Suporte nativo a tipos de dados avançados (como `JSONB` e tipos temporais/locais), úteis para futuras expansões de catálogo.
* **Compatibilidade e Comunidade:** Excelente integração com o Spring Boot/Hibernate e amplo ecossistema de ferramentas de administração (pgAdmin, DBeaver).
* **Open Source:** Gratuito e sem risco de *lock-in* de licença para ambientes de produção.

### Negativas:
* Requer a infraestrutura adicional de um servidor de banco de dados rodando (local via Docker ou em nuvem).
* Exige atenção extra ao modelar esquemas e migrações para evitar gargalos de *lock* em tabelas com alta taxa de concorrência (ex: tabela `seats`).