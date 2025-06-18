#!/bin/bash

# Vérifier si un job est spécifié
if [ -z "$BATCH_JOB_NAME" ]; then
  echo "Error: BATCH_JOB_NAME environment variable is required"
  echo "Available jobs: hashingCardImageJob, scrapingCardJob, scrapingPriceJob, scrapingPriceJobWithoutApi"
  exit 1
fi

# Vérifier si un profil est spécifié, sinon utiliser 'local' par défaut
PROFILE=${SPRING_PROFILES_ACTIVE:-local}

echo "Starting job: $BATCH_JOB_NAME with profile: $PROFILE"

# Lancer l'application Spring Boot avec les arguments appropriés
exec java -jar app.jar \
  --spring.profiles.active=$PROFILE \
  --spring.batch.job.name=$BATCH_JOB_NAME