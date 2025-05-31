# 🚀 Looter - Spring Batch Project

Projet de traitement batch développé avec **Spring Boot 3.4.5**, **Java 17**, et **MySQL** comme base de données.

---

## 📦 Prérequis

- Java 17 installé
- Maven 3.8+ installé
- MySQL (local ou distant)
- IDE recommandé : IntelliJ IDEA / VSCode

---

## 🛠️ Installation du projet

### 1. Cloner le dépôt

```bash
git clone https://github.com/ton-utilisateur/looter.git
cd looter
```

### 2. Importer les bases de données

Un fichier dump est disponible pour initialiser la base sleeved_db, y compris son schéma et des données de test.

🔗 Télécharger les dump des base

- [sleeved_db_v1.sql](https://drive.google.com/file/d/1nXXpMkDlnbhaw-m4T0ovjEp1evoT20-0/view?usp=sharing)
- [looter_scrap_db_v1.sql](https://drive.google.com/file/d/1HhoiVYRUHn_G675nqpZW4p_o5A1IpXDY/view?usp=drive_link)
- [looter_staging_db_v1.sql](https://drive.google.com/file/d/1B19d7DsSOqzCbThkF1eNNbbTYKx-oHh9/view?usp=drive_link)

📥 Importer les base grace à cette commande

```bash
mysql -u root -p < databsename.sql
```

💡 Assurez-vous que l’utilisateur MySQL dispose des droits nécessaires. Le dump contient déjà la commande CREATE DATABASE.

---

## ⚙️ Configuration de l'application

Le fichier `src/main/resources/application.yml` contient :

```yaml
spring:
  datasource:
    hikari:
      jdbc-url: jdbc:mysql://localhost:3306/looter_scrap_db
      username: ${LOOTER_DB_USERNAME}
      password: ${LOOTER_DB_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver
```

💡 Pour le développement local, créez un fichier `src/main/resources/application-local.yml` qui contiendra vos identifiants :

```yaml
spring:
  datasource:
    hikari:
      username: root
      password: password
```

⚠️ Assurez-vous de ne jamais commiter ce fichier sur Git. Il est déjà configuré dans le .gitignore.

---

## 🔨 Compilation et packaging

```bash
mvn clean install
```

La nouvelle `version` du JAR sera généré dans `target/looter-<version>-SNAPSHOT.jar`.

---

## ▶️ Exécution du batch

**Depuis VsCode :**

- Ouvrez le projet dans VS Code.
- Ouvrez le panneau (▶️) Run and (🐞) Debug `(Ctrl+Shift+D)`.
- Sélectionnez la configuration `Run Spring Batch Job`.
- Choisissez le profil Spring `local`.
- Choisissez le nom du job à exécuter.

💡 Cette configuration est définie dans .vscode/launch.json et est partagée dans le repo.

**Depuis la ligne de commande :**

```bash
java -Dspring.profiles.active=<profile> -jar target/<nom-du-jar>.jar --spring.batch.job.name=<nom-du-job>
```

---

## 💎 Qualité du code

Ce projet utilise Husky pour les hooks Git et Checkstyle pour le linting Java.

Mise en place automatique

- Installez les dépendances NPM si ce n'est pas encore fait :

```bash
npm install
```

💡Avant chaque commit, les fichiers modifiés seront vérifiés via checkstyle grace au fichier `.husky/pre-commit`

Lancer manuellement Checkstyle

```bash
mvn checkstyle:check
```

💡Les règles sont définies dans `resources/checkstyle.xml`. Vous pouvez les adapter selon vos standards d'équipe.

---

## 🧪 Lancer les tests

```bash
mvn clean test
```

💡 Nous utilisons AsserJ pour les test, les tests unitaires et d’intégration sont situés dans `src/test`.

---

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

---

## 🔧 Ajouter un nouveau job

- Déclarez un bean `@Bean(name = "nomDuNouveauJob")` dans une classe de configuration Spring Batch.
- Ajoutez le nom du job dans .vscode/launch.json > inputs > jobName (optionnel mais recommandé pour VS Code).
- Documentez l’usage dans ce README.

---

### 📚 Ressources utiles

- [Spring Batch Guide](https://spring.io/guides/gs/batch-processing)

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)

- [MySQL avec Spring](http://spring.io/guides/gs/accessing-data-mysql)

- [Confluence looter](https://sleeved.atlassian.net/wiki/spaces/SleevedConception/folder/3735556?atlOrigin=eyJpIjoiODU2YzEwNDA3ZTQ0NDQxMWE4YTE1NzI3ZWJmZDY2NTQiLCJwIjoiYyJ9)
