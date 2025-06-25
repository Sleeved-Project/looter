# 🚀 Looter - Spring Batch Project

Projet de traitement batch développé avec **Spring Boot 3.4.7**, **Java 17**, et **MySQL** comme base de données.

## 📦 Prérequis

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

### Mettre à jour les bases externe :

L'application de batch communique avec les base des autres service. Il est préférable d'avoir ses bases à jour avant de lancer un batch.

- [sleeved_db_v4.sql](https://drive.google.com/file/d/1Msb3Zav87DGnFoVezrPWZswzGLd9NpLW/view?usp=drive_link)
- [iris_db_v1.sql](https://drive.google.com/file/d/1yaEqMBymH0Mb5EyUzaxOH3NGCbAR-VPa/view?usp=drive_link)

Une fois téléchargés, importer les dump en local ou dans les container des sevrices associés

### Configuration de l'application

Le fichier `src/main/resources/application.yml` contient les configuration principales :

```yaml
# Exemple de configuration
spring:
  datasource:
    hikari:
      jdbc-url: ${LOOTER_SCRAP_JDBC_URL}
      username: ${LOOTER_SCRAP_DB_USERNAME}
      password: ${LOOTER_SCRAP_DB_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver
```

💡 Pour le développement local, copier-coller le `.env.exemple` dans un `.env` et renseigner les variables.

Mac / Linux :

```sh
cp .env.exemple .env
```

Windows :

```sh
Copy-Item -Path .env.exemple -Destination .env
```

⚠️ Assurez-vous de ne jamais commiter ce fichier sur Git. Il est déjà configuré dans le .gitignore.

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

## 🧰 Paramètres disponibles

### Profils

- `local` : Configuration pour développement local (par défaut)
- `prod` : Configuration pour environnement de production
- `test` : Configuration pour les tests

### Jobs

Le projet dispose de plusieurs jobs batch :

- `scrapingCardJob` : Récupère les informations des cartes depuis les sources externes
- `hashingCardImageJob` : Génère les hash pour les images de cartes
- `scrapingPriceJob` : Récupère les prix des cartes
- `scrapingPriceJobWithoutApi` : Récupère les prix des cartes sans utiliser d'API externe

## 💎 Qualité du code

Ce projet utilise Husky pour les hooks Git et Checkstyle pour le linting Java.

Installez les dépendances NPM si ce n'est pas encore fait :

```bash
npm install
```

💡Avant chaque commit, les fichiers modifiés seront vérifiés via checkstyle grace au fichier `.husky/pre-commit`

Lancer manuellement Checkstyle

```bash
mvn checkstyle:check
```

💡Les règles sont définies dans `resources/checkstyle.xml`. Vous pouvez les adapter selon vos standards d'équipe.

## 🧪 Lancer les tests

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
│   ├── config           → Configuration Spring Batch & DataSources
│   ├── job              → Définition des jobs et des steps
│   ├── listener         → Définition des listener
│   ├── processor        → ItemProcessor (mapping, validation…)
│   ├── reader           → ItemReader personnalisés
│   ├── tasklet          → Tasklets (par exemple pour l'appel API)
│   └── writer           → ItemWriter
├── domain
│   ├── entity           → Entités JPA (Card, Rarity, Artist…)
│   ├── repository       → Repositories Spring Data
│   ├── enums            → Enums utilisés dans le domaine (par exemple, type de carte)
│   └── service          → Service (logique de traitement domaine base de données)
├── infra
│   ├── dto              → Objets de transfert issus du JSON
│   ├── mapper           → Mapping entre DTOs et entités
│   ├── processor        → Process entre DTOs et entités
│   └── service          → Services (logique de traitement métier, API calls, etc.)
└── common
    ├── util             → Méthodes utilitaires diverses
    ├── exception        → Exceptions customisées
    └── cache            → Services de cache mémoire
```

## 🔧 Ajouter un nouveau job

- Declare a bean `@Bean(name = "newJobName")` in a Spring Batch configuration class.
- Update the `Taskfile.yml` to display the available jobs
- Document the usage in this README.

## 📚 Ressources utiles

- [Spring Batch Guide](https://spring.io/guides/gs/batch-processing)

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)

- [MySQL avec Spring](http://spring.io/guides/gs/accessing-data-mysql)

- [Confluence looter](https://sleeved.atlassian.net/wiki/spaces/SleevedConception/folder/3735556?atlOrigin=eyJpIjoiODU2YzEwNDA3ZTQ0NDQxMWE4YTE1NzI3ZWJmZDY2NTQiLCJwIjoiYyJ9)
