![Performance Architecture](https://img.shields.io/badge/PERFORMANCE_ARCHITECTURE-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/STATUS-Aceito-brightgreen?style=for-the-badge)

# ADR-0007: Adoção do Redis como Camada de Cache Distribuído

**Projeto:** movie-booking-api | **Data:** 2026-07-24 | **Escopo:** Performance, Escalabilidade & Cache

## Contexto

A `movie-booking-api` possui diversos endpoints de consulta responsáveis por disponibilizar informações sobre cinemas, sessões e assentos. Essas operações são executadas com maior frequência do que as operações de escrita e tendem a gerar consultas repetitivas ao banco de dados.

À medida que a aplicação evolui, consultas recorrentes podem aumentar a carga sobre o banco de dados, elevar a latência das respostas e comprometer a escalabilidade da API.

Além disso, durante o fluxo de reservas, a disponibilidade dos assentos precisa refletir rapidamente as alterações realizadas pelas operações de criação e cancelamento de reservas, exigindo uma estratégia que concilie desempenho e consistência dos dados.

## Decisão

Decidimos adotar o **Redis** como mecanismo de cache distribuído da aplicação, utilizando a abstração do **Spring Cache**.

O cache será aplicado exclusivamente às operações de leitura (`@Cacheable`), reduzindo consultas repetidas ao banco de dados para recursos frequentemente acessados, como:

- Cinemas
- Sessões
- Assentos

As operações de escrita (`create`, `update`, `delete` e alterações de reservas) utilizarão `@CacheEvict` para invalidar automaticamente os registros em cache sempre que houver modificações nos dados persistidos.

A configuração adotada utiliza:

- Redis como provedor oficial de cache (`spring.cache.type=redis`);
- Configuração de conexão por variáveis de ambiente (`REDIS_HOST` e `REDIS_PORT`);
- Tempo de expiração (TTL) de **10 minutos** para os dados armazenados em cache.

Essa estratégia permite reduzir a carga sobre o banco de dados sem comprometer a consistência das informações disponibilizadas pela API.

## Consequências

### Positivas:

* **Melhoria de desempenho:** Reduz significativamente consultas repetitivas ao banco de dados.
* **Menor latência:** Endpoints de leitura respondem mais rapidamente utilizando dados armazenados em memória.
* **Escalabilidade:** O Redis atua como cache distribuído, permitindo que múltiplas instâncias da aplicação compartilhem o mesmo cache.
* **Integração com Spring Cache:** A utilização das anotações `@Cacheable` e `@CacheEvict` simplifica a implementação e reduz o acoplamento da aplicação ao mecanismo de cache.
* **Consistência dos dados:** A invalidação automática do cache garante que alterações em cinemas, sessões, reservas e disponibilidade de assentos sejam refletidas nas próximas consultas.
* **Flexibilidade de configuração:** A utilização de variáveis de ambiente facilita a execução em ambientes de desenvolvimento, homologação e produção.

### Negativas:

* Introduz uma dependência adicional de infraestrutura, exigindo uma instância Redis disponível.
* O gerenciamento do cache adiciona complexidade operacional ao ambiente da aplicação.
* Caso as estratégias de invalidação não sejam corretamente implementadas, existe o risco de disponibilizar dados temporariamente desatualizados.
* O armazenamento em memória aumenta o consumo de recursos do ambiente onde o Redis estiver sendo executado.