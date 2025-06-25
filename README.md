# 🚀 Looter - Spring Batch Project

Batch processor application developed with **Spring Boot 3.4.7**, **Java 17**, and **MySQL**. All the job used in this application are principaly ETL for Extract Transform Load workflow.

## 📦 First things first

- Java 17 installé
- Maven 3.8+ installé
- IDE recommandé : IntelliJ IDEA / VSCode
- Docker
- Docker Compose
- Taskfile [https://taskfile.dev](https://taskfile.dev)
- Git LFS

## Clone reposotory

```bash
git clone https://github.com/ton-utilisateur/looter.git
cd looter
```

## Git LFS

Large data files are send on github with Git LFS. Git Large File Storage (LFS) replaces large files such as audio samples, videos, datasets, and graphics with text pointers inside Git, while storing the file contents on a remote server like GitHub.com or GitHub Enterprise.

If you want push large file data run **before push**

```bash
git lfs push --all origin
```

## 🛠️ Project setup

### Update external databases

Looter will connected with other external sleeved services. In this cas it's more reliatable to keep thoose databases updated beafore runing any jobs.

- [sleeved_db_v4.sql](https://drive.google.com/file/d/1Msb3Zav87DGnFoVezrPWZswzGLd9NpLW/view?usp=drive_link)
- [iris_db_v1.sql](https://drive.google.com/file/d/1yaEqMBymH0Mb5EyUzaxOH3NGCbAR-VPa/view?usp=drive_link)

Where dumps are dowloaded, import them localy or in the docker container, for this read the suitable service documentation.

### Configurate application

`src/main/resources/application.yml` file contain main configuration, here an exemple :

```yaml
spring:
  datasource:
    hikari:
      jdbc-url: ${LOOTER_SCRAP_JDBC_URL}
      username: ${LOOTER_SCRAP_DB_USERNAME}
      password: ${LOOTER_SCRAP_DB_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver
```

Where you work localy, copy-paste `.env.exemple` into `.env` and fill all the necessary variables.

Mac / Linux :

```sh
cp .env.exemple .env
```

Windows :

```sh
Copy-Item -Path .env.exemple -Destination .env
```

Make sure that never commit this file on Git. Exclusion of this file is already handle into the `.gitignore`.

### Compile and packaging

Before runing application run this command

```bash
task setup
```

This command **build all images** and up **only the internal databases**

💡 The new JAR `version` will be generated into `target/looter-<version>-SNAPSHOT.jar`.

### Update internal databases

After compiling you need to import the internal database dump. Dump files are available at the root project to initialize the staging and batch databases.

📥 Import the dataset with this command

```bash
task scrap-db:import
task staging-db:import
```

These files **must be updated after each batch** to ensure data persistence.

```bash
task scrap-db:export
task staging-db:export
```

💡 The dump export will be extract into `./scrap_db_dump.sql` and `./sctaging_db_dump.sql`.

‼️ Don't forget to send this dumps on github with git lfs after runing job **completed**.

## ▶️ Runing batch

Run a job with name and proofile arguments

```bash
task run-job JOB_NAME [PROFILE]
```

Exemple :

```bash
task run-job job=scrapingCardJob profile=local
```

⚠️ No hot reload id necessary for this project but if you modify source code don't forget to compile again git

```bash
task build
```

## 🧰 Variables available

### Profiles

- `local` : Configuration for local developpement (default)
- `prod` : Configuration for production environment
- `test` : Configuration for tests

### Jobs

Jobs are wokflow that will be ran during batch. You can choose the workflow to execute among this list :

- `scrapingCardJob` : Get cards base informations from TcgAPI
- `hashingCardImageJob` : Generate image card hashes from `sleeved_db` to `iris_db`
- `scrapingPriceJob` : Get card prices informations from TcgAPI (default)
- `scrapingPriceJobWithoutApi` : Get card prices informations already staged in `staging_db`

## 💎 Code quality

This project use **Husky**. This permete to configure Git pre-comit by check java linter using **Checkstyle**.

Install Husky dependencies :

```bash
npm install
```

Befor every commit, new and updated files will be verified by Checkstyle. If you want to add so new setp in pre-commit, configuration can be updated into `.husky/pre-commit`.

You can also run manualy code quality :

```bash
mvn checkstyle:check
```

💡Checkstyle policies are defined into `resources/checkstyle.xml` and can be adapted according to you needs.

## 🧪 Run tests

You can run test localy with

```bash
mvn clean test
```

Or with docker (recomanded)

```bash
task test
```

💡 We are using AssertJ fro unit and integration test. Tests are in `src/test`.

## 🗂 Structure du projet

```text
com.sleeved.looter
├── batch
│   ├── config           → Spring Batch & DataSources configurations
│   ├── job              → Jobs and steps definition
│   ├── listener         → Listeners definition
│   ├── processor        → Main processors used in jobs (mapping, validation...)
│   ├── reader           → Main readers used in jobs
│   ├── tasklet          → Tasklets definition (Loop API scraping calls)
│   └── writer           → Main writtes used in jobs
├── domain
│   ├── entity           → Entities definition (Card, Rarity, Artist…)
│   ├── repository       → Repositories definition
│   ├── enums            → Domain enums definition (TypeEnums, SubtypeEnums...)
│   └── service          → Domain services definition
├── infra
│   ├── dto              → Input and output data transfer objects definition
│   ├── mapper           → Mappers definition
│   ├── processor        → Reusable processors definition, used for complex mapping
│   └── service          → Infra external services definition (API calls)
└── common
    ├── util             → Utils methodes
    ├── exception        → Exceptions definition
    └── cache            → Caches utils
```

## 🔧 Ajouter un nouveau job

- Declare a bean `@Bean(name = "newJobName")` in a Spring Batch configuration class.
- Update the `Taskfile.yml` to display the available jobs
- Document the usage in this README.

## 📚 Usefull ressources

- [Spring Batch Guide](https://spring.io/guides/gs/batch-processing)

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)

- [MySQL avec Spring](http://spring.io/guides/gs/accessing-data-mysql)

- [Confluence looter](https://sleeved.atlassian.net/wiki/spaces/SleevedConception/folder/3735556?atlOrigin=eyJpIjoiODU2YzEwNDA3ZTQ0NDQxMWE4YTE1NzI3ZWJmZDY2NTQiLCJwIjoiYyJ9)
