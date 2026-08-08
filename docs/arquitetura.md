# Documento de Arquitetura - Library API

## Sumário

- [1. Introdução](#1-introdução)
- [2. Princípios arquiteturais](#2-princípios-arquiteturais)
- [3. Estrutura de pacotes](#3-estrutura-de-pacotes)
- [4. Responsabilidade de cada pacote](#4-responsabilidade-de-cada-pacote)
- [5. Regra de dependência entre camadas](#5-regra-de-dependência-entre-camadas)
- [6. Convenção de nomenclatura](#6-convenção-de-nomenclatura)
- [7. Estrutura de testes](#7-estrutura-de-testes)
- [8. Arquivos de configuração fora do código-fonte](#8-arquivos-de-configuração-fora-do-código-fonte)
- [9. Referências](#9-referências)

## 1. Introdução

Este documento descreve a organização de pastas e pacotes do projeto Library API, complementando o [Documento de Requisitos](requisitos.md), o [DER](der.puml) e o [Diagrama de Classes](class-diagram.puml) já produzidos. Enquanto aqueles documentos descrevem *o quê* o sistema faz e *como* os dados se relacionam, este documento descreve *em que lugar* cada parte do código deve viver e *por quê*.

O pacote raiz do projeto é `com.matusalenalves.library`, conforme já gerado pelo Spring Initializr e confirmado pela classe principal `LibraryApplication`.

## 2. Princípios arquiteturais

A organização de pastas aqui definida implementa diretamente a **RNF14** do documento de requisitos, que exige uma arquitetura em camadas (Controller, Service, Repositories, Entity, DTO). Além disso, seguem-se três princípios adicionais de Engenharia de Software:

- **Separação por responsabilidade, não por tipo de dado.** Cada camada tem um pacote próprio, evitando misturar regra de negócio com acesso a dados ou com lógica de apresentação HTTP.
- **Domínio rico.** Conforme já estabelecido no diagrama de classes, entidades como `Book` e `Loan` carregam métodos de negócio (`isAvailable()`, `isOverdue()`), não apenas getters e setters. A pasta `entities` reflete isso.
- **Dependência de fora para dentro.** Controllers dependem de Services, Services dependem de Repositories — nunca o inverso (detalhado na [seção 5](#5-regra-de-dependência-entre-camadas)).

## 3. Estrutura de pacotes

Visão geral da raiz do repositório:

```
library-api/
├── docs/
│   ├── requisitos.md
│   ├── der.puml
│   ├── class-diagram.puml
│   └── arquitetura.md
├── src/
│   ├── main/
│   │   ├── java/com/matusalenalves/library/   (ver árvore detalhada abaixo)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/matusalenalves/library/
├── .gitignore
├── docker-compose.yml
├── pom.xml
└── README.md
```

Árvore detalhada de `src/main/java/com/matusalenalves/library/`:

```
src/main/java/com/matusalenalves/library/
│
├── LibraryApplication.java
│
├── config/
│   ├── OpenApiConfig.java
│   ├── PaginationConfig.java
│   └── TestConfig.java
│
├── controller/
│   ├── AuthController.java
│   ├── BookController.java
│   ├── AuthorController.java
│   ├── CategoryController.java
│   ├── LoanController.java
│   └── exceptions/
│       └── GlobalExceptionHandler.java
│
├── services/
│   ├── AuthService.java
│   ├── BookService.java
│   ├── AuthorService.java
│   ├── CategoryService.java
│   ├── LoanService.java
│   └── exceptions/
│       ├── ResourceNotFoundException.java
│       ├── BusinessRuleException.java
│       ├── EmailAlreadyExistsException.java
│       ├── LoanAccessDeniedException.java
│       └── DataBaseException.java
│
├── repositories/
│   ├── UserRepository.java
│   ├── BookRepository.java
│   ├── AuthorRepository.java
│   ├── CategoryRepository.java
│   └── LoanRepository.java
│
├── entities/
│   ├── User.java
│   ├── Author.java
│   ├── Category.java
│   ├── Book.java
│   ├── Loan.java
│   └── enums/
│       ├── Role.java
│       └── LoanStatus.java
│
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── BookRequest.java
│   │   ├── AuthorRequest.java
│   │   ├── CategoryRequest.java
│   │   └── LoanRequest.java
│   └── response/
│       ├── TokenResponse.java
│       ├── BookResponse.java
│       ├── AuthorResponse.java
│       ├── CategoryResponse.java
│       ├── LoanResponse.java
│       ├── PageResponse.java
│       └── ErrorResponse.java
│
├── mapper/
│   ├── BookMapper.java
│   ├── AuthorMapper.java
│   ├── CategoryMapper.java
│   └── LoanMapper.java
│
└── security/
    ├── SecurityConfig.java
    ├── jwt/
    │   ├── JwtService.java
    │   └── JwtAuthenticationFilter.java
    ├── userdetails/
    │   ├── CustomUserDetailsService.java
    │   └── CustomUserDetails.java
    └── exceptions/
        ├── RestAuthenticationEntryPoint.java
        └── RestAccessDeniedHandler.java
```

## 4. Responsabilidade de cada pacote

| Pacote                  | Responsabilidade                                                                                                                                                                                                                                                                                                                                                                                                                              | Requisitos relacionados                                 |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| `config`                | Classes de configuração transversal do Spring: documentação Swagger/OpenAPI (RF27, RNF13), configuração padrão de paginação (RNF12). Inclui também `TestConfig`, um `CommandLineRunner` que popula o banco com dados de exemplo em ambiente de desenvolvimento local — não corresponde a nenhum RF/RNF e não deve rodar em produção                                                                                                           | RF27, RNF12, RNF13                                      |
| `controller`            | Recebe requisições HTTP, aplica `@Valid` nos DTOs de entrada e delega para a camada `services`. Não contém regra de negócio                                                                                                                                                                                                                                                                                                                   | RNF08, RNF09, RNF14; seção 9 do documento de requisitos |
| `controller/exceptions` | `GlobalExceptionHandler` (`@RestControllerAdvice`): intercepta as exceções lançadas por qualquer camada e as converte no formato padronizado de erro (`ErrorResponse`, em `dto/response`). Fica em `controller` porque sua responsabilidade é especificamente traduzir uma exceção Java em resposta HTTP                                                                                                                                     | RF28; RNF11, RNF17                                      |
| `services`              | Concentra as regras de negócio (RN01 a RN11). É aqui que ficam as verificações como "livro sem exemplares disponíveis" ou "cliente com empréstimo em atraso"                                                                                                                                                                                                                                                                                  | RN01–RN11; RF18–RF26                                    |
| `services/exceptions`   | Exceções de domínio, lançadas pelos `Service` quando uma regra de negócio é violada: `ResourceNotFoundException` (404), `BusinessRuleException` (409, ex.: RN01, RN04, RN10), `EmailAlreadyExistsException` (409, RN07), `LoanAccessDeniedException` (403, RN09 — cliente tentando devolver empréstimo que não é seu) e `DataBaseException` (409, rede de segurança contra condição de corrida quando o banco recusa a operação por violação de integridade referencial). Ficam junto do `services` porque é ali que a violação é detectada | RN01, RN04, RN05, RN06, RN07, RN09, RN10, RN11          |
| `repositories`          | Interfaces `JpaRepository`, responsáveis apenas por consultas ao banco. Consultas customizadas (ex.: busca por título/autor/categoria, RF09) ficam aqui como *query methods* ou `@Query`                                                                                                                                                                                                                                                      | RF07, RF09, RF13, RF17, RF20, RF21; RNF03               |
| `entities`              | Classes `@Entity`, mapeadas a partir do [DER](der.puml). Contêm os métodos de domínio descritos no [diagrama de classes](class-diagram.puml) (`isAvailable()`, `decreaseAvailableCopies()`, `increaseAvailableCopies()`, `isOverdue()`, `markAsReturned()`, `isAdmin()`). A tabela de `User` é `tb_user`, não `user`, pois `user` é palavra reservada no PostgreSQL                                                                           | Todas as entidades do DER                               |
| `entities/enums`        | `Role` (`ADMIN`, `CLIENT`) e `LoanStatus` (`ACTIVE`, `RETURNED`, `OVERDUE`), conforme definidos no diagrama de classes. Aninhado dentro de `entities` por serem tipos usados exclusivamente como atributo de entidade (`@Enumerated(EnumType.STRING)`)                                                                                                                                                                                        | RN08; RN03                                              |
| `dto/request`           | Objetos de entrada da API, validados com Bean Validation conforme a seção 10 do documento de requisitos                                                                                                                                                                                                                                                                                                                                       | RNF10; seção 10 do documento de requisitos              |
| `dto/response`          | Objetos de saída da API: `PageResponse` (formato padronizado de paginação, RNF12) e `ErrorResponse` — com o registro aninhado `ErrorResponse.FieldErrorResponse` — para o formato padronizado de erro (RF28/RNF17 — `errors` só aparece no JSON quando há falha de validação, ver seção 9 do documento de requisitos)                                                                                                                         | RF28; RNF12, RNF17; seção 9 do documento de requisitos  |
| `mapper`                | Conversão entre `entities` e `dto`, isolando a representação interna do contrato público da API                                                                                                                                                                                                                                                                                                                                               | RNF14                                                   |
| `security`              | `SecurityConfig`: configuração central do Spring Security, ligando os subpacotes abaixo (autenticação stateless, autorização por rota/perfil)                                                                                                                                                                                                                                                                                                 | RF02; RNF04, RNF06, RNF07; RN08, RN09                   |
| `security/jwt`          | Geração/validação de token (`JwtService`) e o filtro que popula o `SecurityContext` a partir do header `Authorization` a cada requisição (`JwtAuthenticationFilter`)                                                                                                                                                                                                                                                                          | RF02; RNF04, RNF07                                      |
| `security/userdetails`  | Adaptação do usuário para o contrato do Spring Security (`CustomUserDetails`/`CustomUserDetailsService`), usada tanto no login quanto na validação do token                                                                                                                                                                                                                                                                                   | RF02; RN07                                              |
| `security/exceptions`   | `RestAuthenticationEntryPoint` (401) e `RestAccessDeniedHandler` (403): traduzem as rejeições feitas dentro da própria cadeia de filtros do Spring Security (token ausente/inválido, ou perfil sem permissão) para o mesmo formato padronizado de erro usado pelo `GlobalExceptionHandler` — não são cobertas por ele porque acontecem antes do `DispatcherServlet`                                                                           | RF28; RNF06, RNF17; RN08, RN09                          |

## 5. Regra de dependência entre camadas

A direção de dependência entre pacotes segue sempre o mesmo sentido, nunca o inverso:

```
controller  -->  services  -->  repositories  -->  entities
     |              |
     v              v
    dto           mapper

controller/exceptions  --(intercepta)-->  services/exceptions
```

- Um `Controller` nunca deve injetar um `Repository` diretamente — sempre passa pelo `Service` correspondente.
- Um `Service` nunca deve retornar uma `Entity` diretamente para o `Controller` — a conversão para `dto/response` acontece por meio do `mapper`, mantendo a entidade JPA isolada da camada HTTP.
- `entities` não depende de nenhuma outra camada do projeto (nem de `dto`, nem de `services`) — é a camada mais interna do domínio, coerente com o princípio de domínio rico adotado no diagrama de classes.
- `services/exceptions` só depende de `services` — são classes lançadas de dentro da própria regra de negócio, sem conhecer a camada HTTP.
- `controller/exceptions` é a única classe que "escuta" as exceções de `services/exceptions` para convertê-las em `ErrorResponse` (`dto/response`), mantendo o `Service` sem nenhum conhecimento de HTTP (nenhum `Service` deve importar `HttpStatus` ou qualquer classe de `controller`). O mesmo `ErrorResponse` também é usado, fora dessa cadeia, por `security/exceptions` (`RestAuthenticationEntryPoint`/`RestAccessDeniedHandler`) para os 401/403 que nunca chegam ao `Controller`.

## 6. Convenção de nomenclatura

| Tipo de classe         | Sufixo       | Exemplo                 |
|------------------------|--------------|-------------------------|
| Controller             | `Controller` | `BookController`        |
| Service                | `Service`    | `BookService`           |
| Repository             | `Repository` | `BookRepository`        |
| DTO de entrada         | `Request`    | `BookRequest`           |
| DTO de saída           | `Response`   | `BookResponse`          |
| Exceção de negócio     | `Exception`  | `BusinessRuleException` |
| Conversor entidade/DTO | `Mapper`     | `BookMapper`            |

## 7. Estrutura de testes

A estrutura de `src/test/java` espelha exatamente a estrutura de `src/main/java`, prática recomendada para manter a localização dos testes previsível:

```
src/test/java/com/matusalenalves/library/
├── services/
│   ├── BookServiceTest.java
│   └── LoanServiceTest.java
└── controller/
    ├── BookControllerIntegrationTest.java
    └── LoanControllerIntegrationTest.java
```

Os testes de `services` são testes unitários (com Mockito simulando os `Repository`), enquanto os testes de `controller` são testes de integração, utilizando o banco H2 em memória, conforme já definido no README do projeto. Isso atende à **RNF15**.

## 8. Arquivos de configuração fora do código-fonte

Nem todo arquivo do projeto pertence à árvore `src/main/java`. Os seguintes já foram produzidos e ficam na raiz do projeto ou em `src/main/resources`:

| Arquivo                         | Local                 | Descrição                                                                                        |
|---------------------------------|-----------------------|--------------------------------------------------------------------------------------------------|
| `README.md`                     | Raiz do projeto       | Apresentação do projeto: descrição, tecnologias, arquitetura, endpoints e instruções de execução |
| `docker-compose.yml`            | Raiz do projeto       | Provisiona o PostgreSQL local (RNF16)                                                            |
| `.gitignore`                    | Raiz do projeto       | Exclui `target/`, `.idea/` e arquivos de segredo do controle de versão                           |
| `application.properties`        | `src/main/resources/` | Configuração de datasource, JPA e JWT                                                            |
| `requisitos.md`                 | `docs/`               | Documento de requisitos completo                                                                 |
| `der.puml`                      | `docs/`               | Diagrama entidade-relacionamento                                                                 |
| `class-diagram.puml`            | `docs/`               | Diagrama de classes UML                                                                          |
| `arquitetura.md` (este arquivo) | `docs/`               | Documento de arquitetura de pastas                                                               |

A pasta `docs/` fica na raiz do repositório, no mesmo nível de `src/`, `pom.xml` e `docker-compose.yml` — nunca dentro de `src/main` ou `src/test`, já que não é código-fonte nem recurso da aplicação em tempo de execução.

## 9. Referências

- [Documento de Requisitos](requisitos.md) — RF, RNF, RN, casos de uso, histórias de usuário, contrato de API e regras de validação
- [DER](der.puml) — modelo de dados relacional
- [Diagrama de Classes](class-diagram.puml) — modelo de domínio orientado a objetos
- `docker-compose.yml` e `application.properties` — configuração de ambiente