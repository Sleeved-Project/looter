#!/bin/bash

echo "Lancement des tests via Docker..."

# Option pour reconstruire l'image
if [ "$1" == "--build" ]; then
  echo "Construction de l'image Docker..."
  docker compose -f docker-compose.test.yml build
fi

# Exécution des tests avec Maven dans le conteneur
docker compose -f docker-compose.test.yml run --rm test

echo "Tests terminés."