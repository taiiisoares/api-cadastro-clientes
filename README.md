# API de Cadastro de Clientes com Upload de Foto 📸

Uma API RESTful desenvolvida em Java com Spring Boot para gerenciar o cadastro de clientes, incluindo o upload de fotos de perfil. As fotos são armazenadas no AWS S3 e os metadados dos clientes no PostgreSQL.

## 🌟 Visão Geral

Este projeto tem como objetivo principal o aprendizado prático de tecnologias de backend amplamente utilizadas no mercado, como Spring Boot, Spring Data JPA, PostgreSQL e serviços da AWS (S3, RDS, Elastic Beanstalk).

## 🛠️ Tecnologias Utilizadas

* **Backend:** Java 21, Spring Boot 3.x
* **Persistência de Dados:** Spring Data JPA, Hibernate
* **Banco de Dados:** PostgreSQL (para desenvolvimento local e AWS RDS para produção)
* **Armazenamento de Arquivos:** AWS S3 (para fotos dos clientes)
* **Deploy:** AWS Elastic Beanstalk (planejado)
* **Segurança:** Spring Security (autenticação básica, opcional/futuro)
* **Build e Gerenciamento de Dependências:** Apache Maven
* **Controle de Versão:** Git & GitHub
* **Testes de API:** Postman / Insomnia

## ✨ Funcionalidades

* **CRUD de Clientes:**
    * Criar novo cliente (nome, e-mail, CPF, foto).
    * Listar todos os clientes.
    * Buscar cliente por ID.
    * Buscar cliente por e-mail.
    * Buscar cliente por CPF.
    * Atualizar dados de um cliente.
    * Excluir um cliente.
* **Upload de Foto:**
    * Receber imagem do cliente.
    * Salvar imagem no bucket AWS S3.
    * Salvar a URL da imagem no registro do cliente no banco de dados.

## 📋 Pré-requisitos

Antes de rodar a aplicação localmente, certifique-se de ter instalado:

* Java JDK 21 ou superior
* Apache Maven 3.6+
* PostgreSQL (servidor rodando localmente)
* Git
* Uma IDE de sua preferência (ex: VS Code com extensões Java/Spring Boot)
* Postman ou Insomnia para testar os endpoints.

## 🚀 Como Rodar Localmente

1.  **Clone o repositório (se estiver baixando de outro lugar):**
    ```bash
    git clone [https://github.com/seu-usuario/api-cadastro-clientes.git](https://github.com/seu-usuario/api-cadastro-clientes.git)
    cd api-cadastro-clientes
    ```

2.  **Crie o Banco de Dados PostgreSQL:**
    * Certifique-se de que seu servidor PostgreSQL está rodando.
    * Crie um banco de dados local para a aplicação (ex: `api_clientes_db`).
    * Configure as credenciais do banco (URL, usuário, senha) no arquivo `src/main/resources/application.properties`:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_seu_banco
        spring.datasource.username=seu_usuario_postgres
        spring.datasource.password=sua_senha_postgres
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        ```

3.  **Compile e Rode a Aplicação Spring Boot:**
    * Você pode rodar diretamente pela sua IDE (procurando a classe principal `ApiCadastroClientesApplication.java` e rodando como Aplicação Java) ou via Maven:
        ```bash
        mvn spring-boot:run
        ```
    * A API estará disponível em `http://localhost:8080/api/clientes`.

## 📡 Endpoints da API (Planejados/Implementados)

* `POST /api/clientes`: Cria um novo cliente.
* `GET /api/clientes`: Lista todos os clientes.
* `GET /api/clientes/{id}`: Busca um cliente pelo ID.
* `GET /api/clientes/buscar_por_email?email={email}`: Busca um cliente pelo e-mail.
* `GET /api/clientes/buscar_por_cpf?cpf={cpf}`: Busca um cliente pelo CPF.
* `PUT /api/clientes/{id}`: Atualiza um cliente existente.
* `DELETE /api/clientes/{id}`: Deleta um cliente.

## 🚧 Status do Projeto

Em Desenvolvimento 🏗️

## 🗺️ Próximos Passos (Roadmap do Projeto Original)

* [ ] Configurar banco de dados PostgreSQL no AWS RDS.
* [ ] Integrar com AWS S3 para upload e armazenamento de fotos.
* [ ] Implementar a lógica de upload de arquivos no `ClienteService` e `ClienteController`.
* [ ] Fazer o deploy da aplicação na AWS Elastic Beanstalk.
* [ ] (Opcional) Adicionar autenticação básica com Spring Security.

---
