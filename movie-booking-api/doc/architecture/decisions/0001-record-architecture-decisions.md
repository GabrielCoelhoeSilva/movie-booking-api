# 1. Registrar Decisões de Arquitetura (ADR)

* **Status:** Aceito
* **Data:** 2026-07-22

## Contexto

À medida que o projeto `movie-booking-api` evolui e cresce, decisões arquiteturais, técnicas e de design são tomadas constantemente. Sem um registro histórico centralizado, o motivo por trás de certas escolhas de engenharia se perde com o tempo, dificultando a entrada de novos desenvolvedores na equipe e a manutenção contínua do sistema.

Precisamos de uma forma leve, versionada e padronizada de documentar essas decisões de arquitetura diretamente no ecossistema do projeto.

## Decisão

Decidimos adotar os **Architecture Decision Records (ADRs)** para registrar todas as decisões arquiteturais significativas tomadas ao longo do desenvolvimento da API.

* Os registros serão armazenados em arquivos Markdown (`.md`) dentro do repositório em `/doc/architecture/decisions/`.
* Cada ADR seguirá a convenção de nomenclatura sequencial: `0000-titulo-da-decisao.md`.
* O formato básico de cada ADR conterá as seções: **Status**, **Data**, **Contexto**, **Decisão** e **Consequências** (Positivas e Negativas).

## Consequências

### Positivas:
* **Rastreabilidade:** Histórico claro dos motivos e trade-offs de cada escolha arquitetural.
* **Onboarding facilitado:** Novos membros ou colaboradores entendem rapidamente a evolução técnica da aplicação.
* **Centralização:** A documentação vive junto do código-fonte e é versionada via Git.

### Negativas:
* Requer a disciplina de criar e atualizar arquivos Markdown sempre que uma mudança relevante de decisão for tomada.