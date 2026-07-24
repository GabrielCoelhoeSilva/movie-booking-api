![Test Architecture](https://img.shields.io/badge/TEST_ARCHITECTURE-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/STATUS-Aceito-brightgreen?style=for-the-badge)

# ADR-0006: Organização Modular dos Testes de Integração

**Projeto:** movie-booking-api | **Data:** 2026-07-24 | **Escopo:** Test Architecture & Maintainability

## Contexto

A `movie-booking-api` possui uma suíte de testes de integração responsável por validar o comportamento completo da aplicação, cobrindo cenários de sucesso, validação, autenticação, autorização e tratamento de erros para cada endpoint exposto pela API.

À medida que novas funcionalidades são implementadas, concentrar todos os testes de um controller em um único arquivo faz com que as classes cresçam rapidamente, dificultando a navegação, a manutenção e a identificação dos cenários relacionados a cada endpoint.

Além disso, arquivos muito extensos aumentam a complexidade durante revisões de código, dificultam a leitura e tornam mais trabalhosa a evolução da suíte de testes ao longo do projeto.

## Decisão

Decidimos organizar os testes de integração em arquivos independentes, agrupando-os por endpoint (operação HTTP) de cada controller.

A estrutura seguirá o seguinte padrão:

```text
src/test/java
└── integration
    └── room
        ├── RoomCreateIT.java
        ├── RoomUpdateIT.java
        ├── RoomFindByIdIT.java
        ├── RoomFindAllIT.java
        └── RoomDeleteIT.java
```

Cada classe será responsável exclusivamente pelos testes relacionados ao endpoint correspondente, contendo apenas os cenários necessários para aquela operação.

Essa organização será adotada como padrão para novos controllers e aplicada gradualmente aos testes já existentes na aplicação.

## Consequências

### Positivas:

* **Modularização:** Cada classe possui uma única responsabilidade, facilitando sua manutenção.
* **Legibilidade:** Arquivos menores tornam os testes mais fáceis de compreender e revisar.
* **Facilidade de navegação:** Desenvolvedores conseguem localizar rapidamente os testes referentes a um endpoint específico.
* **Escalabilidade:** Novos cenários podem ser adicionados sem transformar um único arquivo em uma classe extensa.
* **Padronização:** Todos os controllers passam a seguir a mesma organização para seus testes de integração.
* **Melhor experiência durante Code Review:** Alterações ficam menores e mais objetivas, facilitando a revisão das Pull Requests.

### Negativas:

* O número de arquivos de teste aumenta conforme novos endpoints são implementados.
* Configurações compartilhadas poderão ser repetidas entre as classes caso não seja adotada uma classe base para reutilização.
* Para visualizar todos os cenários de um controller, pode ser necessário navegar entre múltiplos arquivos.