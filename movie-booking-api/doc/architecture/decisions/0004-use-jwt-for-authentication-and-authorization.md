# 4. Uso de JSON Web Tokens (JWT) para Autenticação e Autorização

* **Status:** Aceito
* **Data:** 2026-07-22

## Contexto

A `movie-booking-api` necessita de um mecanismo de autenticação e autorização para proteger endpoints sensíveis (como criação de reservas, gerenciamento de sessões, salas e filmes).

Como a API foi projetada para ser consumida por diferentes clientes (front-end web, aplicativos móveis), a solução de segurança precisava atender aos seguintes requisitos:
* Ser completamente *stateless* (sem dependência de sessão HTTP no servidor) para facilitar a escalabilidade horizontal.
* Permitir a identificação do usuário e suas permissões (*roles*) em cada requisição de forma performática.
* Integrar-se perfeitamente ao ecossistema do **Spring Security**.

## Decisão

Decidimos utilizar **JSON Web Tokens (JWT)** combinados com o **Spring Security** para o gerenciamento de autenticação e autorização.

* O usuário autentica-se no endpoint `/auth/login` enviando suas credenciais. Se válidas, a aplicação gera e retorna um JWT assinado usando o algoritmo HMAC256.
* O cliente deve enviar esse token no cabeçalho HTTP `Authorization: Bearer <token>` em todas as requisições protegidas.
* Um filtro customizado (`JwtAuthenticationFilter`) intercepta as requisições, valida a assinatura e expiração do token, e carrega os detalhes do usuário no `SecurityContextHolder` do Spring Security.

## Consequências

### Positivas:
* **Arquitetura Stateless:** O servidor não precisa armazenar sessões em memória nem consultar o banco de dados a cada requisição para validar a identidade do usuário, reduzindo a carga no PostgreSQL.
* **Flexibilidade e Decoupling:** Ideal para consumo por múltiplos clientes distintos (web, mobile, serviços de terceiros).
* **Segurança e Granularidade:** Permite embutir claims customizadas (ex: ID do usuário, e-mail, perfis `ROLE_USER` / `ROLE_ADMIN`) no próprio payload do token assinado.

### Negativas:
* **Invalidação e Revogação:** Como o JWT é autocontido, revogar um token válido antes da sua data de expiração exige mecanismos adicionais (como uma *blocklist* em memória/Redis ou uso de tokens de curta duração com *refresh tokens*).
* **Gestão do Segredo:** Exige o armazenamento seguro da chave secreta de assinatura (`jwt.secret`) em variáveis de ambiente, nunca hardcoded no código.