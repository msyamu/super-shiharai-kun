#!/bin/bash

# Load environment variables from .env file
export $(grep -v '^#' .env | xargs)

# Start docker-compose for database
echo "Starting database..."
docker-compose up -d

# Wait for database to be ready
echo "Waiting for database to be ready..."
sleep 3

# Run the application
echo "Starting application..."
./gradlew run