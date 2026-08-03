# Documento de Requisitos - Library API

## Sumário

- [1. Introdução](#1-introdução)
- [2. Atores do sistema](#2-atores-do-sistema)
- [3. Requisitos Funcionais (RF)](#3-requisitos-funcionais-rf)
- [4. Requisitos Não Funcionais (RNF)](#4-requisitos-não-funcionais-rnf)
- [5. Regras de Negócio (RN)](#5-regras-de-negócio-rn)
- [6. Casos de Uso](#6-casos-de-uso)
- [7. Histórias de Usuário](#7-histórias-de-usuário)
- [8. Matriz de rastreabilidade](#8-matriz-de-rastreabilidade)
- [9. Contrato de API (endpoints e respostas HTTP)](#9-contrato-de-api-endpoints-e-respostas-http)
- [10. Regras de validação de campos](#10-regras-de-validação-de-campos)

## 1. Introdução

Este documento descreve os requisitos funcionais, não funcionais, os casos de uso e as histórias de usuário do projeto Library API, um sistema de gerenciamento de biblioteca com controle de acervo, autores, categorias, usuários e empréstimos, protegido por autenticação e autorização baseadas em JWT.

## 2. Atores do sistema

| Ator          | Descrição                                                                                                                                  |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| Visitante     | Usuário não autenticado, que ainda não possui conta no sistema                                                                             |
| Cliente       | Usuário autenticado com perfil `CLIENT`, que consulta o acervo e gerencia seus próprios empréstimos                                        |
| Administrador | Usuário autenticado com perfil `ADMIN`, responsável pela gestão do acervo (livros, autores e categorias) e pela supervisão dos empréstimos |

## 3. Requisitos Funcionais (RF)

| Código | Descrição                                                                                                                                          |
|--------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| RF01   | O sistema deve permitir que um visitante se cadastre informando nome, e-mail e senha                                                               |
| RF02   | O sistema deve permitir que um usuário cadastrado realize login com e-mail e senha, recebendo um token de acesso                                   |
| RF03   | O sistema deve impedir o cadastro de um usuário com e-mail já existente                                                                            |
| RF04   | O sistema deve permitir que um administrador cadastre um novo livro                                                                                |
| RF05   | O sistema deve permitir que um administrador edite os dados de um livro existente                                                                  |
| RF06   | O sistema deve permitir que um administrador exclua um livro do acervo, desde que não existam empréstimos ativos (não devolvidos) associados a ele |
| RF07   | O sistema deve permitir que qualquer usuário autenticado consulte a lista de livros, com paginação                                                 |
| RF08   | O sistema deve permitir que qualquer usuário autenticado consulte os detalhes de um livro específico                                               |
| RF09   | O sistema deve permitir a busca de livros por título, autor e categoria                                                                            |
| RF10   | O sistema deve permitir que um administrador cadastre um novo autor                                                                                |
| RF11   | O sistema deve permitir que um administrador edite os dados de um autor existente                                                                  |
| RF12   | O sistema deve permitir que um administrador exclua um autor, desde que ele não possua livros vinculados                                           |
| RF13   | O sistema deve permitir que qualquer usuário autenticado consulte a lista de autores                                                               |
| RF14   | O sistema deve permitir que um administrador cadastre uma nova categoria                                                                           |
| RF15   | O sistema deve permitir que um administrador edite uma categoria existente                                                                         |
| RF16   | O sistema deve permitir que um administrador exclua uma categoria, desde que ela não possua livros vinculados                                      |
| RF17   | O sistema deve permitir que qualquer usuário autenticado consulte a lista de categorias                                                            |
| RF18   | O sistema deve permitir que um cliente registre o empréstimo de um livro disponível                                                                |
| RF19   | O sistema deve permitir que um cliente registre a devolução de um livro emprestado por ele                                                         |
| RF20   | O sistema deve permitir que um cliente consulte o próprio histórico de empréstimos                                                                 |
| RF21   | O sistema deve permitir que um administrador consulte o histórico de empréstimos de todos os usuários                                              |
| RF22   | O sistema deve atualizar automaticamente a quantidade de exemplares disponíveis de um livro a cada empréstimo ou devolução                         |
| RF23   | O sistema deve impedir o empréstimo de um livro sem exemplares disponíveis                                                                         |
| RF24   | O sistema deve calcular automaticamente a data prevista de devolução no momento do empréstimo                                                      |
| RF25   | O sistema deve identificar empréstimos em atraso, comparando a data prevista de devolução com a data atual                                         |
| RF26   | O sistema deve impedir que um cliente registre um novo empréstimo caso possua empréstimos em atraso                                                |
| RF27   | O sistema deve fornecer documentação interativa dos endpoints da API                                                                               |
| RF28   | O sistema deve retornar mensagens de erro padronizadas em caso de falha nas requisições                                                            |

## 4. Requisitos Não Funcionais (RNF)

| Código | Descrição                                                                                                                                                                                                                                                                                                                                                                     |
|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| RNF01  | O sistema deve ser desenvolvido em Java 21 utilizando o framework Spring Boot                                                                                                                                                                                                                                                                                                 |
| RNF02  | O sistema deve persistir os dados em um banco de dados relacional PostgreSQL                                                                                                                                                                                                                                                                                                  |
| RNF03  | O sistema deve utilizar Spring Data JPA e Hibernate como camada de persistência                                                                                                                                                                                                                                                                                               |
| RNF04  | O sistema deve utilizar tokens JWT para autenticação, sem manter estado de sessão no servidor                                                                                                                                                                                                                                                                                 |
| RNF05  | As senhas dos usuários devem ser armazenadas utilizando hash criptográfico (BCrypt), nunca em texto plano. **(sugestão)** Fator de custo (`strength`) padrão do `BCryptPasswordEncoder`: 10                                                                                                                                                                                   |
| RNF06  | O acesso aos endpoints de escrita deve ser restrito por perfil de usuário (`ADMIN` ou `CLIENT`)                                                                                                                                                                                                                                                                               |
| RNF07  | O token JWT deve possuir tempo de expiração configurável. **(sugestão)** Valor padrão: 1 hora (3600 segundos), definido pela propriedade `jwt.expiration-seconds`                                                                                                                                                                                                             |
| RNF08  | A API deve seguir os princípios REST, utilizando os verbos HTTP de forma semântica (GET, POST, PUT, DELETE)                                                                                                                                                                                                                                                                   |
| RNF09  | A API deve retornar códigos de status HTTP apropriados para cada situação. O mapeamento completo, endpoint a endpoint, está formalizado na [seção 9](#9-contrato-de-api-endpoints-e-respostas-http)                                                                                                                                                                           |
| RNF10  | O sistema deve validar os dados de entrada antes de processá-los, utilizando Bean Validation. As regras objetivas por campo estão formalizadas na [seção 10](#10-regras-de-validação-de-campos)                                                                                                                                                                               |
| RNF11  | O sistema deve possuir tratamento centralizado de exceções, evitando exposição de detalhes internos da aplicação                                                                                                                                                                                                                                                              |
| RNF12  | As listagens de recursos devem suportar paginação, evitando o retorno de grandes volumes de dados em uma única requisição. **(sugestão)** Parâmetros `page` (índice iniciando em 0) e `size`; tamanho padrão de página: 20 itens; tamanho máximo permitido: 100 itens (requisições acima do limite são truncadas para 100, não rejeitadas)                                    |
| RNF13  | O sistema deve possuir documentação de API gerada automaticamente via Swagger/OpenAPI                                                                                                                                                                                                                                                                                         |
| RNF14  | O código-fonte deve seguir uma arquitetura em camadas (Controller, Service, Repository, Entity, DTO)                                                                                                                                                                                                                                                                          |
| RNF15  | O sistema deve possuir testes automatizados cobrindo as principais regras de negócio. **(sugestão)** Cobertura mínima de linha nas classes de `Service`: 80%                                                                                                                                                                                                                  |
| RNF16  | O ambiente de banco de dados deve poder ser provisionado via Docker Compose, facilitando a execução local                                                                                                                                                                                                                                                                     |
| RNF17  | As respostas de erro da API devem seguir um formato padronizado, contendo timestamp, status, mensagem e caminho da requisição. O schema exato está formalizado na [seção 9](#9-contrato-de-api-endpoints-e-respostas-http)                                                                                                                                                    |
| RNF18  | O tempo de resposta das operações de consulta deve ser adequado para uso interativo, evitando consultas não otimizadas ao banco de dados. **(sugestão)** Meta de referência: p95 (percentil 95) das requisições de consulta (`GET`) respondendo em até 300 ms, medido localmente, sem fatores de rede externos, com o volume de dados de um ambiente de desenvolvimento/teste |

## 5. Regras de Negócio (RN)

| Código | Descrição                                                                                                                                                    | Relacionado a                                                                            |
|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| RN01   | Um livro só pode ser emprestado se possuir ao menos um exemplar disponível                                                                                   | UC05; RF18, RF23                                                                         |
| RN02   | O prazo padrão de empréstimo é de 14 dias corridos a partir da data do empréstimo                                                                            | UC05; RF24                                                                               |
| RN03   | Um empréstimo é considerado em atraso (`OVERDUE`) quando a data atual ultrapassa a data prevista de devolução e o livro ainda não foi devolvido              | UC07; RF25                                                                               |
| RN04   | Um cliente com empréstimos em atraso não pode realizar novos empréstimos até regularizar a pendência                                                         | UC05; RF26                                                                               |
| RN05   | Um autor não pode ser excluído caso possua ao menos um livro vinculado                                                                                       | UC08; RF12                                                                               |
| RN06   | Uma categoria não pode ser excluída caso possua ao menos um livro vinculado                                                                                  | UC08; RF16                                                                               |
| RN07   | O e-mail do usuário é único no sistema e utilizado como identificador de login                                                                               | UC01, UC02; RF01, RF02, RF03                                                             |
| RN08   | Apenas usuários com perfil `ADMIN` podem criar, editar ou excluir livros, autores e categorias                                                               | UC03, UC08, UC09, UC10, UC12, UC13; RF04, RF05, RF06, RF10, RF11, RF12, RF14, RF15, RF16 |
| RN09   | Um usuário só pode devolver um livro cujo empréstimo esteja associado ao seu próprio usuário, exceto o administrador, que pode gerenciar qualquer empréstimo | UC06; RF19                                                                               |
| RN10   | Um livro não pode ser excluído caso possua ao menos um empréstimo ativo (`ACTIVE`, ainda não devolvido) associado a ele                                      | UC13; RF06                                                                               |
| RN11   | O ISBN de um livro é único no acervo                                                                                                                         | UC03, UC12; RF04, RF05                                                                   |

## 6. Casos de Uso

### UC01 - Cadastrar usuário

- **Ator principal:** Visitante
- **Pré-condições:** Nenhuma
- **Pós-condições:** Usuário criado no sistema com perfil `CLIENT`
- **Requisitos relacionados:** RF01, RF03; RNF05, RNF09, RNF10, RNF17
- **Regras de negócio relacionadas:** RN07
- **Fluxo principal:**
  1. O visitante acessa o endpoint de cadastro
  2. O visitante informa nome, e-mail e senha
  3. O sistema valida os dados informados
  4. O sistema verifica se o e-mail já está cadastrado
  5. O sistema cria o usuário, armazenando a senha com hash
  6. O sistema retorna confirmação de cadastro
- **Fluxos alternativos:**
  - 4a. E-mail já cadastrado: o sistema retorna erro de conflito (409) e interrompe o fluxo
  - 3a. Dados inválidos: o sistema retorna erro de validação (400) com o detalhamento dos campos inválidos

### UC02 - Autenticar usuário

- **Ator principal:** Cliente ou Administrador
- **Pré-condições:** Usuário previamente cadastrado
- **Pós-condições:** Token JWT emitido para o usuário
- **Requisitos relacionados:** RF02; RNF04, RNF07, RNF09
- **Regras de negócio relacionadas:** RN07
- **Fluxo principal:**
  1. O usuário informa e-mail e senha
  2. O sistema valida as credenciais
  3. O sistema gera um token JWT contendo as informações do usuário e seu perfil
  4. O sistema retorna o token ao usuário
- **Fluxos alternativos:**
  - 2a. Credenciais inválidas: o sistema retorna erro de autenticação (401)

### UC03 - Cadastrar livro

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`; autor e categorias já cadastrados
- **Pós-condições:** Livro criado no acervo
- **Requisitos relacionados:** RF04; RNF06, RNF09, RNF10
- **Regras de negócio relacionadas:** RN08, RN11
- **Fluxo principal:**
  1. O administrador informa os dados do livro: título, ISBN, ano de publicação, quantidade de exemplares, autor e categorias
  2. O sistema valida os dados informados
  3. O sistema associa o livro ao autor e às categorias informadas
  4. O sistema define a quantidade disponível igual à quantidade de exemplares cadastrada
  5. O sistema persiste o livro e retorna confirmação
- **Fluxos alternativos:**
  - 2a. Dados inválidos, ISBN duplicado ou autor/categoria inexistente: o sistema retorna erro (400, 409 ou 404 — ver [seção 9](#9-contrato-de-api-endpoints-e-respostas-http))
  - Usuário sem perfil `ADMIN`: o sistema retorna erro de acesso negado (403)

### UC04 - Consultar acervo de livros

- **Ator principal:** Cliente ou Administrador
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Lista de livros retornada, paginada
- **Requisitos relacionados:** RF07, RF08, RF09; RNF09, RNF12, RNF18
- **Fluxo principal:**
  1. O usuário solicita a listagem de livros, podendo informar filtros de título, autor ou categoria
  2. O sistema consulta o banco de dados aplicando os filtros e a paginação
  3. O sistema retorna a lista de livros correspondente
- **Fluxos alternativos:**
  - 1a. Busca por título, autor ou categoria: a correspondência é parcial e não diferencia maiúsculas de minúsculas (equivalente a `containingIgnoreCase`), nunca exigindo o texto exato

### UC05 - Registrar empréstimo

- **Ator principal:** Cliente
- **Pré-condições:** Usuário autenticado com perfil `CLIENT`; livro cadastrado com exemplares disponíveis; cliente sem empréstimos em atraso
- **Pós-condições:** Empréstimo registrado; quantidade de exemplares disponíveis do livro reduzida em uma unidade
- **Requisitos relacionados:** RF18, RF22, RF23, RF24, RF25, RF26; RNF06, RNF09, RNF10
- **Regras de negócio relacionadas:** RN01, RN02, RN04
- **Fluxo principal:**
  1. O cliente seleciona o livro desejado
  2. O sistema verifica a disponibilidade de exemplares
  3. O sistema verifica se o cliente possui empréstimos em atraso
  4. O sistema registra o empréstimo, calculando a data prevista de devolução
  5. O sistema reduz a quantidade de exemplares disponíveis do livro
  6. O sistema retorna a confirmação do empréstimo
- **Fluxos alternativos:**
  - 2a. Livro sem exemplares disponíveis: o sistema retorna erro (409)
  - 3a. Cliente com empréstimos em atraso: o sistema retorna erro (409) informando a pendência

### UC06 - Registrar devolução

- **Ator principal:** Cliente
- **Pré-condições:** Empréstimo ativo (`ACTIVE`) associado ao cliente
- **Pós-condições:** Empréstimo marcado como devolvido (`RETURNED`); quantidade de exemplares disponíveis do livro aumentada em uma unidade
- **Requisitos relacionados:** RF19, RF22; RNF06, RNF09
- **Regras de negócio relacionadas:** RN09
- **Fluxo principal:**
  1. O cliente informa o empréstimo a ser devolvido
  2. O sistema verifica se o empréstimo pertence ao cliente autenticado e está ativo
  3. O sistema registra a data efetiva de devolução
  4. O sistema atualiza a situação do empréstimo para devolvido (`RETURNED`)
  5. O sistema aumenta a quantidade de exemplares disponíveis do livro
  6. O sistema retorna confirmação da devolução
- **Fluxos alternativos:**
  - 2a. Empréstimo não encontrado ou não pertencente ao cliente: o sistema retorna erro (404 ou 403)
  - 2b. Empréstimo já devolvido: o sistema retorna erro (409)

### UC07 - Consultar histórico de empréstimos

- **Ator principal:** Cliente ou Administrador
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Lista de empréstimos retornada
- **Requisitos relacionados:** RF20, RF21, RF25; RNF06, RNF09, RNF12, RNF18
- **Regras de negócio relacionadas:** RN03
- **Fluxo principal:**
  1. O cliente solicita o próprio histórico de empréstimos, ou o administrador solicita o histórico geral
  2. O sistema verifica o perfil do usuário autenticado
  3. O sistema retorna a lista de empréstimos correspondente ao escopo de acesso do usuário

### UC08 - Excluir autor ou categoria

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`; autor ou categoria cadastrados
- **Pós-condições:** Autor ou categoria removidos, quando não houver dependências
- **Requisitos relacionados:** RF12, RF16; RNF06, RNF09
- **Regras de negócio relacionadas:** RN05, RN06, RN08
- **Fluxo principal:**
  1. O administrador solicita a exclusão de um autor ou categoria
  2. O sistema verifica se existem livros vinculados ao registro
  3. O sistema remove o registro
  4. O sistema retorna confirmação da exclusão
- **Fluxos alternativos:**
  - 2a. Existem livros vinculados: o sistema retorna erro de conflito (409), impedindo a exclusão

### UC09 - Cadastrar autor ou categoria

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`
- **Pós-condições:** Autor ou categoria criados no sistema
- **Requisitos relacionados:** RF10, RF14; RNF06, RNF09, RNF10
- **Regras de negócio relacionadas:** RN08
- **Fluxo principal:**
  1. O administrador informa os dados do autor (nome) ou da categoria (nome)
  2. O sistema valida os dados informados
  3. O sistema persiste o novo registro
  4. O sistema retorna confirmação do cadastro
- **Fluxos alternativos:**
  - 2a. Dados inválidos: o sistema retorna erro de validação (400)
  - Usuário sem perfil `ADMIN`: o sistema retorna erro de acesso negado (403)

### UC10 - Editar autor ou categoria

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`; autor ou categoria previamente cadastrados
- **Pós-condições:** Dados do autor ou categoria atualizados
- **Requisitos relacionados:** RF11, RF15; RNF06, RNF09, RNF10
- **Regras de negócio relacionadas:** RN08
- **Fluxo principal:**
  1. O administrador informa o identificador e os novos dados do autor ou categoria
  2. O sistema verifica se o registro existe
  3. O sistema valida os dados informados
  4. O sistema atualiza o registro
  5. O sistema retorna confirmação da edição
- **Fluxos alternativos:**
  - 2a. Registro não encontrado: o sistema retorna erro (404)
  - 3a. Dados inválidos: o sistema retorna erro de validação (400)
  - Usuário sem perfil `ADMIN`: o sistema retorna erro de acesso negado (403)

### UC11 - Consultar autores e categorias

- **Ator principal:** Cliente ou Administrador
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Lista de autores ou categorias retornada
- **Requisitos relacionados:** RF13, RF17; RNF09, RNF12, RNF18
- **Fluxo principal:**
  1. O usuário solicita a listagem de autores ou de categorias
  2. O sistema consulta o banco de dados
  3. O sistema retorna a lista correspondente

### UC12 - Editar livro

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`; livro previamente cadastrado
- **Pós-condições:** Dados do livro atualizados
- **Requisitos relacionados:** RF05; RNF06, RNF09, RNF10
- **Regras de negócio relacionadas:** RN08, RN11
- **Fluxo principal:**
  1. O administrador informa o identificador do livro e os novos dados
  2. O sistema verifica se o livro existe
  3. O sistema valida os dados informados
  4. O sistema atualiza o livro
  5. O sistema retorna confirmação da edição
- **Fluxos alternativos:**
  - 2a. Livro não encontrado: o sistema retorna erro (404)
  - 3a. Dados inválidos, ISBN duplicado (pertencente a outro livro) ou autor/categoria inexistente: o sistema retorna erro (400, 409 ou 404)
  - Usuário sem perfil `ADMIN`: o sistema retorna erro de acesso negado (403)

### UC13 - Excluir livro

- **Ator principal:** Administrador
- **Pré-condições:** Usuário autenticado com perfil `ADMIN`; livro previamente cadastrado
- **Pós-condições:** Livro removido do acervo
- **Requisitos relacionados:** RF06; RNF06, RNF09
- **Regras de negócio relacionadas:** RN08, RN10
- **Fluxo principal:**
  1. O administrador solicita a exclusão de um livro
  2. O sistema verifica se o livro existe
  3. O sistema verifica se existem empréstimos ativos associados ao livro
  4. O sistema remove o livro
  5. O sistema retorna confirmação da exclusão
- **Fluxos alternativos:**
  - 2a. Livro não encontrado: o sistema retorna erro (404)
  - 3a. Existem empréstimos ativos associados ao livro: o sistema retorna erro de conflito (409), impedindo a exclusão
  - Usuário sem perfil `ADMIN`: o sistema retorna erro de acesso negado (403)

## 7. Histórias de Usuário

### Módulo: Conta e acesso

**US01**
Como visitante, quero me cadastrar no sistema informando meu nome, e-mail e senha, para que eu possa acessar o acervo da biblioteca.

*Caso de uso relacionado:* UC01

*Critérios de aceite:*
- O sistema deve rejeitar o cadastro se o e-mail já estiver em uso
- A senha deve ser armazenada de forma criptografada
- Após o cadastro, o usuário deve receber confirmação de sucesso

**US02**
Como usuário cadastrado, quero fazer login informando e-mail e senha, para que eu receba um token de acesso e possa utilizar a API.

*Caso de uso relacionado:* UC02

*Critérios de aceite:*
- O sistema deve validar as credenciais antes de emitir o token
- Credenciais inválidas devem retornar erro de autenticação, sem detalhar qual campo está incorreto
- O token emitido deve ter tempo de expiração definido

### Módulo: Acervo

**US03**
Como administrador, quero cadastrar novos livros com título, ISBN, ano de publicação, autor e categorias, para que o acervo esteja sempre atualizado.

*Caso de uso relacionado:* UC03

*Critérios de aceite:*
- O sistema deve exigir autor e ao menos uma categoria válidos
- A quantidade disponível deve ser inicializada igual à quantidade de exemplares
- Usuários sem perfil `ADMIN` não devem conseguir realizar esse cadastro

**US04**
Como cliente, quero consultar a lista de livros disponíveis, com filtros por título, autor e categoria, para que eu possa encontrar facilmente o livro que desejo.

*Caso de uso relacionado:* UC04

*Critérios de aceite:*
- A listagem deve ser paginada
- Os filtros devem poder ser combinados
- A busca não deve exigir correspondência exata de texto (correspondência parcial, sem diferenciar maiúsculas de minúsculas)

**US05**
Como administrador, quero editar ou excluir um livro do acervo, para que eu possa manter as informações corretas e atualizadas.

*Casos de uso relacionados:* UC12 (editar), UC13 (excluir)

*Critérios de aceite:*
- A edição deve validar os dados antes de salvar
- A exclusão de um livro com empréstimos ativos deve ser impedida (RN10), retornando erro de conflito (409)

**US06**
Como administrador, quero cadastrar, editar e excluir autores e categorias, para que eu possa organizar o acervo da biblioteca.

*Casos de uso relacionados:* UC09 (cadastrar), UC10 (editar), UC08 (excluir)

*Critérios de aceite:*
- A exclusão de um autor ou categoria vinculado a algum livro deve ser impedida
- Apenas administradores devem ter acesso a essas operações

**US13**
Como cliente ou administrador, quero consultar a lista de autores e a lista de categorias cadastradas, para que eu possa usá-las como referência ao cadastrar, editar ou filtrar livros.

*Caso de uso relacionado:* UC11

*Critérios de aceite:*
- A listagem de autores e a listagem de categorias são endpoints distintos (ver [seção 9](#9-contrato-de-api-endpoints-e-respostas-http))
- Ambas devem estar disponíveis para qualquer usuário autenticado (`CLIENT` ou `ADMIN`)

### Módulo: Empréstimos

**US07**
Como cliente, quero registrar o empréstimo de um livro disponível, para que eu possa retirá-lo da biblioteca.

*Caso de uso relacionado:* UC05

*Critérios de aceite:*
- O sistema deve impedir o empréstimo se não houver exemplares disponíveis
- O sistema deve impedir o empréstimo se o cliente tiver pendências em atraso
- A data prevista de devolução deve ser calculada automaticamente

**US08**
Como cliente, quero registrar a devolução de um livro que peguei emprestado, para que eu regularize minha pendência e o exemplar volte a ficar disponível.

*Caso de uso relacionado:* UC06

*Critérios de aceite:*
- Apenas o cliente responsável pelo empréstimo pode registrar a devolução, exceto o administrador
- A quantidade de exemplares disponíveis deve ser atualizada automaticamente
- Um empréstimo já devolvido não pode ser devolvido novamente

**US09**
Como cliente, quero consultar meu histórico de empréstimos, para que eu acompanhe quais livros já peguei e quais ainda preciso devolver.

*Caso de uso relacionado:* UC07

*Critérios de aceite:*
- A listagem deve mostrar a situação de cada empréstimo (`ACTIVE`, `RETURNED` ou `OVERDUE`)
- Um cliente não deve conseguir visualizar o histórico de outros usuários

**US10**
Como administrador, quero consultar o histórico geral de empréstimos de todos os usuários, para que eu possa supervisionar o uso do acervo e identificar atrasos.

*Caso de uso relacionado:* UC07

*Critérios de aceite:*
- A listagem deve ser paginada
- Deve ser possível identificar visualmente os empréstimos em atraso (`OVERDUE`)

### Módulo: Qualidade e documentação

**US11**
Como desenvolvedor que vai integrar com a API, quero acessar uma documentação interativa dos endpoints, para que eu entenda como consumir cada recurso sem precisar ler o código-fonte.

*Caso de uso relacionado:* Nenhum — requisito transversal, aplicável à API na totalidade (RF27)

*Critérios de aceite:*
- A documentação deve listar todos os endpoints, parâmetros e exemplos de resposta
- A documentação deve estar acessível via navegador, sem necessidade de ferramentas externas

**US12**
Como consumidor da API, quero receber mensagens de erro padronizadas e claras, para que eu possa entender rapidamente o motivo de uma falha na requisição.

*Caso de uso relacionado:* Nenhum — requisito transversal, aplicável a todos os fluxos alternativos de erro (RF28)

*Critérios de aceite:*
- As respostas de erro devem seguir sempre o mesmo formato
- O status HTTP retornado deve corresponder ao tipo de erro ocorrido

## 8. Matriz de rastreabilidade

| Caso de uso | Histórias de usuário relacionadas | RF relacionados                    | RNF relacionados           | RN relacionadas  |
|-------------|-----------------------------------|------------------------------------|----------------------------|------------------|
| UC01        | US01                              | RF01, RF03                         | RNF05, RNF09, RNF10, RNF17 | RN07             |
| UC02        | US02                              | RF02                               | RNF04, RNF07, RNF09        | RN07             |
| UC03        | US03                              | RF04                               | RNF06, RNF09, RNF10        | RN08, RN11       |
| UC04        | US04                              | RF07, RF08, RF09                   | RNF09, RNF12, RNF18        | —                |
| UC05        | US07                              | RF18, RF22, RF23, RF24, RF25, RF26 | RNF06, RNF09, RNF10        | RN01, RN02, RN04 |
| UC06        | US08                              | RF19, RF22                         | RNF06, RNF09               | RN09             |
| UC07        | US09, US10                        | RF20, RF21, RF25                   | RNF06, RNF09, RNF12, RNF18 | RN03             |
| UC08        | US06                              | RF12, RF16                         | RNF06, RNF09               | RN05, RN06, RN08 |
| UC09        | US06                              | RF10, RF14                         | RNF06, RNF09, RNF10        | RN08             |
| UC10        | US06                              | RF11, RF15                         | RNF06, RNF09, RNF10        | RN08             |
| UC11        | US13                              | RF13, RF17                         | RNF09, RNF12, RNF18        | —                |
| UC12        | US05                              | RF05                               | RNF06, RNF09, RNF10        | RN08, RN11       |
| UC13        | US05                              | RF06                               | RNF06, RNF09               | RN08, RN10       |

**Observações:**

- **RF27 e RF28** (documentação interativa e mensagens de erro padronizadas) são requisitos transversais: aplicam-se à API inteira, não se limitando a um caso de uso específico. Relacionam-se diretamente a US11 e US12, respectivamente.
- **RNF01, RNF02, RNF03, RNF08, RNF11, RNF13, RNF14, RNF15, RNF16** são requisitos de arquitetura/infraestrutura válidos para o sistema inteiro (stack tecnológica, padrão arquitetural, testes, etc.) e por isso não foram amarrados a casos de uso individuais na tabela acima.

## 9. Contrato de API (endpoints e respostas HTTP)

Esta seção formaliza RNF08 e RNF09: para cada Requisito Funcional que corresponde a um endpoint, define o verbo HTTP, o caminho, o perfil exigido e os possíveis retornos HTTP. Os caminhos, nomes de recursos e mensagens abaixo estão em inglês, para ficarem consistentes com o código-fonte.

| RF               | Método        | Endpoint                           | Perfil exigido                           | Sucesso        | Erros possíveis                                                        |
|------------------|---------------|------------------------------------|------------------------------------------|----------------|------------------------------------------------------------------------|
| RF01             | POST          | `/auth/register`                   | Público                                  | 201 Created    | 400 Bad Request, 409 Conflict                                          |
| RF02             | POST          | `/auth/login`                      | Público                                  | 200 OK         | 401 Unauthorized                                                       |
| RF04             | POST          | `/books`                           | `ADMIN`                                  | 201 Created    | 400, 401, 403, 404 (autor/categoria inexistente), 409 (ISBN duplicado) |
| RF05             | PUT           | `/books/{id}`                      | `ADMIN`                                  | 200 OK         | 400, 401, 403, 404, 409 (ISBN duplicado)                               |
| RF06             | DELETE        | `/books/{id}`                      | `ADMIN`                                  | 204 No Content | 401, 403, 404, 409 (empréstimo ativo)                                  |
| RF07, RF08, RF09 | GET           | `/books`, `/books/{id}`            | Qualquer autenticado                     | 200 OK         | 401, 404 (apenas em `/books/{id}`)                                     |
| RF10             | POST          | `/authors`                         | `ADMIN`                                  | 201 Created    | 400, 401, 403                                                          |
| RF11             | PUT           | `/authors/{id}`                    | `ADMIN`                                  | 200 OK         | 400, 401, 403, 404                                                     |
| RF12             | DELETE        | `/authors/{id}`                    | `ADMIN`                                  | 204 No Content | 401, 403, 404, 409 (livro vinculado)                                   |
| RF13             | GET           | `/authors`                         | Qualquer autenticado                     | 200 OK         | 401                                                                    |
| RF14             | POST          | `/categories`                      | `ADMIN`                                  | 201 Created    | 400, 401, 403                                                          |
| RF15             | PUT           | `/categories/{id}`                 | `ADMIN`                                  | 200 OK         | 400, 401, 403, 404                                                     |
| RF16             | DELETE        | `/categories/{id}`                 | `ADMIN`                                  | 204 No Content | 401, 403, 404, 409 (livro vinculado)                                   |
| RF17             | GET           | `/categories`                      | Qualquer autenticado                     | 200 OK         | 401                                                                    |
| RF18             | POST          | `/loans`                           | `CLIENT`                                 | 201 Created    | 400, 401, 403, 409 (sem exemplar disponível ou cliente em atraso)      |
| RF19             | PUT           | `/loans/{id}/return`               | `CLIENT` (dono do empréstimo) ou `ADMIN` | 200 OK         | 401, 403 (não é o dono nem `ADMIN`), 404, 409 (já devolvido)           |
| RF20             | GET           | `/loans/me`                        | `CLIENT`                                 | 200 OK         | 401                                                                    |
| RF21             | GET           | `/loans`                           | `ADMIN`                                  | 200 OK         | 401, 403                                                               |
| RF27             | GET           | `/swagger-ui.html`, `/v3/api-docs` | Público                                  | 200 OK         | —                                                                      |
| RF28             | (transversal) | Qualquer endpoint                  | —                                        | —              | Corpo de erro padronizado (ver abaixo) em toda resposta 4xx/5xx        |

**Observações sobre o contrato:**
- RF22, RF23, RF24, RF25 e RF26 não são endpoints próprios: são comportamentos internos aplicados dentro de `POST /loans` (RF18), `PUT /loans/{id}/return` (RF19) e das consultas de histórico (RF20/RF21).
- **500 Internal Server Error** é o retorno de fallback transversal para qualquer falha não tratada explicitamente, em qualquer endpoint (RNF11).
- **401 Unauthorized** é retornado por qualquer endpoint autenticado quando o token JWT estiver ausente, expirado ou inválido — não listado individualmente por RF para evitar repetição.

### Formato padronizado de erro (RF28 / RNF17)

Corpo de resposta para qualquer erro (4xx ou 5xx):

```json
{
  "timestamp": "2026-07-31T14:32:10Z",
  "status": 409,
  "error": "Conflict",
  "message": "The book has no available copies for loan.",
  "path": "/loans"
}
```

Para erros de validação (400), um campo adicional `errors` detalha cada campo inválido:

```json
{
  "timestamp": "2026-07-31T14:32:10Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for the given fields.",
  "path": "/books",
  "errors": [
    { "field": "title", "message": "must not be blank" },
    { "field": "publicationYear", "message": "must not be a future year" }
  ]
}
```

### Formato padronizado de listagem paginada (RF07 / RNF12)

```json
{
  "content": [ ],
  "page": 0,
  "size": 20,
  "totalElements": 57,
  "totalPages": 3
}
```

## 10. Regras de validação de campos

Esta seção formaliza RNF10: os campos e limites abaixo tornam objetivo o que os RF/UC anteriores descreviam apenas como "dados válidos". Os nomes de campo estão em inglês, consistentes com os DTOs de request/response. Limites de tamanho marcados como **(sugestão)** não vinham explícitos no texto original.

### User (RF01)

| Campo    | Regra                                                          |
|----------|----------------------------------------------------------------|
| name     | Obrigatório; **(sugestão)** 2 a 100 caracteres                 |
| email    | Obrigatório; formato de e-mail válido; único no sistema (RN07) |
| password | Obrigatório; **(sugestão)** mínimo de 8 caracteres             |

### Book (RF04, RF05)

| Campo           | Regra                                                                            |
|-----------------|----------------------------------------------------------------------------------|
| title           | Obrigatório; **(sugestão)** 1 a 200 caracteres                                   |
| isbn            | Obrigatório; formato ISBN-10 ou ISBN-13; único no acervo (RN11)                  |
| publicationYear | Obrigatório; não pode ser um ano futuro em relação à data atual                  |
| totalCopies     | Obrigatório; inteiro maior ou igual a 1                                          |
| authorId        | Obrigatório; deve referenciar um autor existente                                 |
| categoryIds     | Obrigatório; ao menos 1 categoria; todas devem referenciar categorias existentes |

### Author (RF10, RF11)

| Campo | Regra                                          |
|-------|------------------------------------------------|
| name  | Obrigatório; **(sugestão)** 2 a 150 caracteres |

### Category (RF14, RF15)

| Campo | Regra                                          |
|-------|------------------------------------------------|
| name  | Obrigatório; **(sugestão)** 2 a 100 caracteres |

### Loan (RF18, RF19)

| Campo      | Regra                                                                                                               |
|------------|---------------------------------------------------------------------------------------------------------------------|
| bookId     | Obrigatório (informado pelo cliente); deve referenciar um livro existente com ao menos 1 exemplar disponível (RN01) |
| dueDate    | Gerado pelo sistema: data do empréstimo + 14 dias corridos (RN02), não informado pelo usuário                       |
| returnDate | Gerado pelo sistema no momento da devolução (RF19), não informado pelo usuário                                      |