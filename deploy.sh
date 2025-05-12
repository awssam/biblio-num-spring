#!/bin/bash
set -e

# Navigate to project directory
PROJECT_DIR="/Users/awtsoft/Documents/GitHub/FSK Courses/spring/biblio-num-spring"
cd "$PROJECT_DIR"

echo "Building project..."
./mvnw clean package -DskipTests

echo "Deploying to Render..."
render blueprint apply

echo "Deployment initiated. You can check the status in the Render dashboard."
