# 3. Isolar anotações do OpenAPI/Swagger em interfaces ControllerDocs

* **Status:** Aceito
* **Data:** 2026-07-22

## Contexto
À medida que a API cresceu, as anotações do Swagger (`@Operation`, `@ApiResponse`, `@Tag`) no nível dos `@RestController` poluíram a leitura do código da camada de apresentação, misturando lógica de rotas com metadados de documentação.

## Decisão
Decidimos separar a documentação do OpenAPI em interfaces dedicadas (ex: `CinemaControllerDocs`) e fazer com que as classes de controller implementem essas interfaces (`implements`).

## Consequências

### Positivas:
* Separação clara de responsabilidades: Controllers focam em tratar requisições e a interface foca na documentação.
* Melhora substancial na legibilidade das classes de Controller.

### Negativas:
* Criação de mais arquivos no projeto (uma interface de documentação para cada controller).