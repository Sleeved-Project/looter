# 🚀 Looter - Spring Batch Project

Projet de traitement batch développé avec **Spring Boot 3.4.5**, **Java 17**, et **MySQL** comme base de données.

## 📦 Prérequis

- Java 17 installé
- Maven 3.8+ installé
- IDE recommandé : IntelliJ IDEA / VSCode
- Docker
- Docker Compose

## 🛠️ Installation du projet

### Cloner le dépôt

```bash
git clone https://github.com/ton-utilisateur/looter.git
cd looter
```

### Gestion des bases internes

Des fichier dump sont dipsonible à la racine du projet pour initialiser les base de données de staging et de batch. Ces fichiers devront être mis à jour à chaque batch pour garantir une persistance des données.

### Mettre à jour les bases externe :

L'application de batch communique avec les base des autres service. Il est préférable d'avoir ses bases à jour avant de lancer un batch.

- [sleeved_db_v4.sql](https://drive.google.com/file/d/1Msb3Zav87DGnFoVezrPWZswzGLd9NpLW/view?usp=drive_link)
- [iris_db_v1.sql](https://drive.google.com/file/d/1yaEqMBymH0Mb5EyUzaxOH3NGCbAR-VPa/view?usp=drive_link)

Une fois téléchargés, importer les dump en local ou dans les container des sevrices associés

## ⚙️ Configuration de l'application

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

## 🔨 Compilation et packaging

```bash
docker compose build
```

💡 La nouvelle `version` du JAR sera généré dans `target/looter-<version>-SNAPSHOT.jar`.

## ▶️ Exécution du batch

Le script `run-job.sh` simplifie l'exécution des jobs :

Rendez le script exécutable (nécessaire uniquement la première fois)

```bash
chmod +x run-job.sh
```

Exécutez un job en spécifiant son nom et optionnellement le profil

```bash
./run-job.sh NOM_DU_JOB [PROFIL]
```

Exemple :

```bash
./run-job.sh scrapingCardJob local
```

⚠️ Si vous avez modifié le code source, n'oubliez pas de compiler à nouveau

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

Vous pouvez lancer les test soit en local

```bash
mvn clean test
```

Ou via le container docker

```bash
./run-tests.sh
```

Passer l'option build permet de construire le container si il n'existe pas

```bash
./run-tests.sh --build
```

💡 Nous utilisons AsserJ pour les test, les tests unitaires et d’intégration sont situés dans `src/test`.

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

- Déclarez un bean `@Bean(name = "nomDuNouveauJob")` dans une classe de configuration Spring Batch.
- Mettre à jour le `run-job.sh` pour afficher les job disponible
- Documentez l’usage dans ce README.

## 📚 Ressources utiles

- [Spring Batch Guide](https://spring.io/guides/gs/batch-processing)

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)

- [MySQL avec Spring](http://spring.io/guides/gs/accessing-data-mysql)

- [Confluence looter](https://sleeved.atlassian.net/wiki/spaces/SleevedConception/folder/3735556?atlOrigin=eyJpIjoiODU2YzEwNDA3ZTQ0NDQxMWE4YTE1NzI3ZWJmZDY2NTQiLCJwIjoiYyJ9)
