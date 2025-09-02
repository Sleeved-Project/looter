#!/bin/bash

echo "=== Scheduler Status ==="
source .env

DATE_FMT="%Y-%m-%d %H:%M:%S"

get_next_quarter_date() {
  local hour="$1"
  local current_month=$(date +%m | sed 's/^0*//')
  local current_year=$(date +%Y)

  local next_quarter_month=$(( (( (current_month - 1) / 3 + 1 ) * 3 + 1) ))
  if [ "$next_quarter_month" -gt 12 ]; then
    next_quarter_month=1
    current_year=$((current_year + 1))
  fi

  printf "%04d-%02d-01 %s\n" "$current_year" "$next_quarter_month" "$hour"
}

for job in scrapingPriceJob scrapingCardJob hashingCardImageJob; do
  last_run=$(docker compose exec -T scrap-db mysql -uroot -p"$LOOTER_SCRAP_DB_ROOT_PASSWORD" "$LOOTER_SCRAP_DB_NAME" -N -e "
    SELECT MAX(bje.START_TIME)
    FROM BATCH_JOB_EXECUTION bje
    LEFT JOIN BATCH_JOB_INSTANCE bji ON bje.JOB_INSTANCE_ID = bji.JOB_INSTANCE_ID
    WHERE bji.JOB_NAME = '$job';
  ")

  last_run=$(echo "$last_run" | cut -d. -f1)
  [ "$last_run" = "NULL" ] && last_run=""

  if date --version >/dev/null 2>&1; then
    # GNU date (Linux)
    add_day() { date -d "$1 +1 day" "+%Y-%m-%d 02:00:00"; }
  else
    # BSD date (macOS)
    add_day() { date -v+1d -jf "$DATE_FMT" "$1" "+%Y-%m-%d 02:00:00"; }
  fi

  case $job in
    scrapingPriceJob)
      if [ -z "$last_run" ]; then
        next_run=$(date "+%Y-%m-%d 02:00:00")
      else
        next_run=$(add_day "$last_run")
      fi
      ;;
    scrapingCardJob)
      next_run=$(get_next_quarter_date "03:00:00")
      ;;
    hashingCardImageJob)
      next_run=$(get_next_quarter_date "04:00:00")
      ;;
  esac

  echo "- $job :"
  echo "    Last launch     : ${last_run:-Aucun}"
  echo "    Next scheduled  : $next_run"
done
