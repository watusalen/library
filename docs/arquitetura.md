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

Este documento descreve a organização de pastas e pacotes do projeto Library API, complementando o [Documento de Requisitos](requisitos.md), o [DER](der.puml) e o [Diagrama de Classes](class-diagram.puml) já produzidos. Enquanto aqueles documentos descrevem *o quê* o sistema faz e *como* os dados se relacionam, este documento descreve o *lugar* que cada parte do código deve viver e *por quê*.

O pacote raiz do projeto é `com.matusalenalves.library`, conforme já gerado pelo Spring Initializr e confirmado pela classe principal `LibraryApplication`.

## 2. Princípios arquiteturais

A organização de pastas aqui definida implementa diretamente a **RNF14** do documento de requisitos, que exige uma arquitetura em camadas (Controller, Service, Repository, Entity, DTO). Além disso, seguem-se três princípios adicionais de Engenharia de Software:

- **Separação por responsabilidade, não por tipo de dado.** Cada camada tem um pacote próprio, evitando misturar regra de negócio com acesso a dados ou com lógica de apresentação HTTP.
- **Domínio rico.** Conforme já estabelecido no diagrama de classes, entidades como `Book` e `Loan` carregam métodos de negócio (`isAvailable()`, `isOverdue()`), não apenas getters e setters. A pasta `entity` reflete isso.
- **Dependência de fora para dentro.** Controllers dependem de Services, Services dependem de Repositories — nunca o inverso (detalhado na [seção 5](#5-regra-de-dependência-entre-camadas)).

## 3. Estrutura de pacotes

```
src/main/java/com/matusalenalves/library/
│
├── LibraryApplication.java
│
├── config/
│   ├── OpenApiConfig.java
│   └── PaginationConfig.java
│
├── controller/
│   ├── AuthController.java
│   ├── BookController.java
│   ├── AuthorController.java
│   ├── CategoryController.java
│   ├── LoanController.java
│   └── exception/
│       └── GlobalExceptionHandler.java
│
├── service/
│   ├── AuthService.java
│   ├── BookService.java
│   ├── AuthorService.java
│   ├── CategoryService.java
│   ├── LoanService.java
│   └── exception/
│       ├── ResourceNotFoundException.java
│       ├── BusinessRuleException.java
│       └── EmailAlreadyExistsException.java
│
├── repository/
│   ├── UserRepository.java
│   ├── BookRepository.java
│   ├── AuthorRepository.java
│   ├── CategoryRepository.java
│   └── LoanRepository.java
│
├── entity/
│   ├── User.java
│   ├── Author.java
│   ├── Category.java
│   ├── Book.java
│   └── Loan.java
│
├── enums/
│   ├── Role.java
│   └── LoanStatus.java
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
    ├── JwtService.java
    ├── JwtAuthenticationFilter.java
    └── CustomUserDetailsService.java
```

## 4. Responsabilidade de cada pacote

| Pacote                 | Responsabilidade                                                                                                                                                                                                                                                                           | Requisitos relacionados                                 |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| `config`               | Classes de configuração transversal do Spring: documentação Swagger/OpenAPI (RF27, RNF13), configuração padrão de paginação (RNF12)                                                                                                                                                        | RF27, RNF12, RNF13                                      |
| `controller`           | Recebe requisições HTTP, aplica `@Valid` nos DTOs de entrada e delega para a camada `service`. Não contém regra de negócio                                                                                                                                                                 | RNF08, RNF09, RNF14; seção 9 do documento de requisitos |
| `controller/exception` | `GlobalExceptionHandler` (`@ControllerAdvice`): intercepta as exceções lançadas por qualquer camada e as converte no formato padronizado de erro (`ErrorResponse`). Fica em `controller` porque sua responsabilidade é especificamente traduzir uma exceção Java em resposta HTTP          | RF28; RNF11, RNF17                                      |
| `service`              | Concentra as regras de negócio (RN01 a RN11). É aqui que ficam as verificações como "livro sem exemplares disponíveis" ou "cliente com empréstimo em atraso"                                                                                                                               | RN01–RN11; RF18–RF26                                    |
| `service/exception`    | Exceções de domínio, lançadas pelos `Service` quando uma regra de negócio é violada: `ResourceNotFoundException` (404), `BusinessRuleException` (409, ex.: RN01, RN04, RN10) e `EmailAlreadyExistsException` (409, RN07). Ficam junto do `service` porque é ali que a violação é detectada | RN01, RN04, RN05, RN06, RN07, RN10, RN11                |
| `repository`           | Interfaces `JpaRepository`, responsáveis apenas por consultas ao banco. Consultas customizadas (ex.: busca por título/autor/categoria, RF09) ficam aqui como *query methods* ou `@Query`                                                                                                   | RF07, RF09, RF13, RF17, RF20, RF21; RNF03               |
| `entity`               | Classes `@Entity`, mapeadas a partir do [DER](der.puml). Contêm os métodos de domínio descritos no [diagrama de classes](class-diagram.puml) (`isAvailable()`, `decreaseAvailableCopies()`, `isOverdue()`, `markAsReturned()`, `isAdmin()`)                                                | Todas as entidades do DER                               |
| `enums`                | `Role` (`ADMIN`, `CLIENT`) e `LoanStatus` (`ACTIVE`, `RETURNED`, `OVERDUE`), conforme definidos no diagrama de classes                                                                                                                                                                     | RN08; RN03                                              |
| `dto/request`          | Objetos de entrada da API, validados com Bean Validation conforme a seção 10 do documento de requisitos                                                                                                                                                                                    | RNF10; seção 10 do documento de requisitos              |
| `dto/response`         | Objetos de saída da API, incluindo `ErrorResponse` (formato padronizado de erro, RF28/RNF17) e `PageResponse` (formato padronizado de paginação, RNF12)                                                                                                                                    | RF28; RNF12, RNF17; seção 9 do documento de requisitos  |
| `mapper`               | Conversão entre `entity` e `dto`, isolando a representação interna do contrato público da API                                                                                                                                                                                              | RNF14                                                   |
| `security`             | Autenticação e autorização via JWT: filtro de requisição, geração/validação de token, configuração de acesso por perfil                                                                                                                                                                    | RF02; RNF04, RNF06, RNF07; RN08, RN09                   |

## 5. Regra de dependência entre camadas

A direção de dependência entre pacotes segue sempre o mesmo sentido, nunca o inverso:

```
controller  -->  service  -->  repository  -->  entity
     |              |
     v              v
    dto           mapper

controller/exception  --(intercepta)-->  service/exception
```

- Um `Controller` nunca deve injetar um `Repository` diretamente — sempre passa pelo `Service` correspondente.
- Um `Service` nunca deve retornar uma `Entity` diretamente para o `Controller` — a conversão para `dto/response` acontece por meio do `mapper`, mantendo a entidade JPA isolada da camada HTTP.
- `entity` não depende de nenhuma outra camada do projeto (nem de `dto`, nem de `service`) — é a camada mais interna do domínio, coerente com o princípio de domínio rico adotado no diagrama de classes.
- `service/exception` só depende de `service` — são classes lançadas de dentro da própria regra de negócio, sem conhecer a camada HTTP.
- `controller/exception` é a única classe que "escuta" as exceções de `service/exception` para convertê-las em `ErrorResponse`, mantendo o `Service` sem nenhum conhecimento de HTTP (nenhum `Service` deve importar `HttpStatus` ou qualquer classe de `controller`).

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
├── service/
│   ├── BookServiceTest.java
│   └── LoanServiceTest.java
└── controller/
    ├── BookControllerIntegrationTest.java
    └── LoanControllerIntegrationTest.java
```

Os testes de `service` são testes unitários (com Mockito simulando os `Repository`), enquanto os testes de `controller` são testes de integração, utilizando o banco de dados H2 em memória, conforme já definido no README do projeto. Isso atende à **RNF15**.

## 8. Arquivos de configuração fora do código-fonte

Nem todo arquivo do projeto pertence à árvore `src/main/java`. Os seguintes já foram produzidos e ficam na raiz do projeto ou em `src/main/resources`:

| Arquivo                         | Local                                    | Descrição                             |
|---------------------------------|------------------------------------------|---------------------------------------|
| `docker-compose.yml`            | Raiz do projeto                          | Provisiona o PostgreSQL local (RNF16) |
| `application.properties`        | `src/main/resources/`                    | Configuração de datasource, JPA e JWT |
| `requisitos.md`                 | Raiz do repositório (ex.: pasta `docs/`) | Documento de requisitos completo      |
| `der.puml`                      | Raiz do repositório (ex.: pasta `docs/`) | Diagrama entidade-relacionamento      |
| `class-diagram.puml`            | Raiz do repositório (ex.: pasta `docs/`) | Diagrama de classes UML               |
| `arquitetura.md` (este arquivo) | Raiz do repositório (ex.: pasta `docs/`) | Documento de arquitetura de pastas    |

## 9. Referências

- [Documento de Requisitos](requisitos.md) — RF, RNF, RN, casos de uso, histórias de usuário, contrato de API e regras de validação
- [DER](der.puml) — modelo de dados relacional
- [Diagrama de Classes](class-diagram.puml) — modelo de domínio orientado a objetos
- `docker-compose.yml` e `application.properties` — configuração de ambiente