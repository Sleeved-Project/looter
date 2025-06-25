#!/bin/bash

# Vérifier si un job est spécifié
if [ -z "$1" ]; then
  echo "Usage: ./run-job.sh JOB_NAME [PROFILE]"
  echo "Available jobs: hashingCardImageJob, scrapingCardJob, scrapingPriceJob, scrapingPriceJobWithoutApi"
  echo "Default profile: local"
  exit 1
fi

JOB_NAME=$1
PROFILE=${2:-local}

echo "Running job: $JOB_NAME with profile: $PROFILE"

docker compose run --rm \
  -e BATCH_JOB_NAME=$JOB_NAME \
  -e SPRING_PROFILES_ACTIVE=$PROFILE \
  app