#!/bin/bash

echo "Running health check script with URL: $1"
URL=$1
for i in {1..10}; do
  echo "Attempt $i: Checking service health"
  response=$(curl -o /dev/null -s -w "%{http_code}" "$URL")
  if [ "$response" -eq 200 ]; then
    echo "Service is UP!"
    exit 0
  fi
  sleep 10
done

echo "Service did not respond in time. Health check failed."
exit 1
