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

### 2. Importer la base de données

Un fichier dump est disponible pour initialiser la base sleeved_db, y compris son schéma et des données de test.

🔗 Télécharger le dump de la base

- [sleeved_db_v1.sql](https://drive.google.com/file/d/1nXXpMkDlnbhaw-m4T0ovjEp1evoT20-0/view?usp=sharing)

📥 Importer dans MySQL

```bash
mysql -u root -p < sleeved_db_v1.sql
```

💡 Assurez-vous que l’utilisateur MySQL dispose des droits nécessaires. Le dump contient déjà la commande CREATE DATABASE.

---

## ⚙️ Configuration de l'application

Le fichier src/main/resources/application.yml contient :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/looter_scrap_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

💡 Assurez-vous de remplacer les identifiant de connexion à votre base de données.

---

## 🔨 Compilation et packaging

```bash
mvn clean install
```

Le JAR sera généré dans target/looter-0.0.1-SNAPSHOT.jar.

---

## ▶️ Exécution du batch

Depuis une IDE

- Ouvrir le projet dans votre IDE préféré.
- Lancer la classe LooterApplication.java qui contient public static void main.

Depuis le Spring Boot Dashboard

- Ouvrez la vue Spring Boot Dashboard (menu View > Tool Windows > Spring Boot Dashboard).
- Sélectionnez l’application looter dans la liste des projets Spring Boot.
- Cliquez sur l’icône Run (▶️) ou Debug (🐞) à côté de l’entrée pour démarrer l’application.
- Dans la section Jobs du dashboard, vous verrez la liste des jobs disponibles ; cliquez sur le nom d’un job pour le démarrer.
- Pour relancer un job, cliquez à nouveau sur son nom ou utilisez les options de redémarrage fournies.

En ligne de commande

```bash
java -jar target/looter-0.0.1-SNAPSHOT.jar
```

💡 Pour lancer un job spécifique

```bash
java -jar target/looter-0.0.1-SNAPSHOT.jar --spring.batch.job.name=nomDuJob param1=value1
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
mvn test
```

Les tests unitaires et d’intégration sont situés dans src/test.

---

## 🗂 Structure du projet

```text
com.sleeved.looter
├── batch
│   ├── config           → Configuration Spring Batch & DataSources
│   ├── job              → Définition des jobs et des steps
│   ├── tasklet          → Tasklets (appel API)
│   ├── reader           → ItemReader personnalisés
│   ├── processor        → ItemProcessor (mapping, validation…)
│   └── writer           → ItemWriter
├── domain
│   ├── entity           → Entités JPA
│   ├── repository       → Repositories Spring Data
│   └── enums            → Enums utilisés dans le domaine
├── infrastructure
│   ├── dto              → Objets de transfert issus du JSON
│   ├── mapper           → Mapping entre DTOs et entités
│   └── service          → Services traitement métier et api
└── common
    ├── util             → Méthodes utilitaires diverses
    ├── exception        → Exceptions customisées
    └── cache            → Services de cache mémoire
```

---

### 📚 Ressources utiles

- [Spring Batch Guide](https://spring.io/guides/gs/batch-processing)

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)

- [MySQL avec Spring](http://spring.io/guides/gs/accessing-data-mysql)
