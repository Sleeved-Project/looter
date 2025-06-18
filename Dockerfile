FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copie du pom.xml et téléchargement des dépendances
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copie des sources et build
COPY src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copie du jar construit
COPY --from=build /app/target/*.jar app.jar

# Création d'un répertoire pour les fichiers de configuration
RUN mkdir -p /app/config

# Copie du script d'entrée
COPY docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

ENTRYPOINT ["/app/docker-entrypoint.sh"]