#!/bin/bash

echo "=== Scheduler Status ==="
source .env

for job in scrapingPriceJob scrapingCardJob hashingCardImageJob; do
  last_run=$(docker compose exec -T scrap-db mysql -uroot -p"$LOOTER_SCRAP_DB_PASSWORD" $LOOTER_SCRAP_DB_NAME -N -e \
    "SELECT MAX(bje.START_TIME) FROM BATCH_JOB_EXECUTION bje LEFT JOIN BATCH_JOB_INSTANCE bji ON bje.JOB_INSTANCE_ID = bji.JOB_INSTANCE_ID WHERE bji.JOB_NAME = '$job';")
  last_run=$(echo "$last_run" | cut -d. -f1)
  case $job in
    scrapingPriceJob)
      next_run=$(date -v+1d -jf "%Y-%m-%d %H:%M:%S" "${last_run:-$(date '+%Y-%m-%d 02:00:00')}" "+%Y-%m-%d 02:00:00")
      ;;
    scrapingCardJob)
      next_run=$(date -jf "%Y-%m-%d %H:%M:%S" "${last_run:-$(date '+%Y-%m-%d 03:00:00')}" "+%Y-$(printf '%02d' $(( (($(date +%m)-1)/3+1)*3%12+1 )))-01 03:00:00")
      ;;
    hashingCardImageJob)
      next_run=$(date -jf "%Y-%m-%d %H:%M:%S" "${last_run:-$(date '+%Y-%m-%d 04:00:00')}" "+%Y-$(printf '%02d' $(( (($(date +%m)-1)/3+1)*3%12+1 )))-01 04:00:00")
      ;;
  esac
  echo "- $job :"
  echo "    Last launch : ${last_run:-Aucun}"
  echo "    Next scheduled   : $next_run"
done