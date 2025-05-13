#!/bin/bash
# Startup script for Spring Boot application with SQLite database
# This script checks for database existence and initializes if needed

# Check if database file exists
if [ ! -f /data/bibliodb.sqlite ]; then
  echo "Database file not found. Creating new database..."
  
  # Create an empty SQLite database
  sqlite3 /data/bibliodb.sqlite "PRAGMA journal_mode=WAL;"
  
  echo "Database created successfully."
fi

# Make sure permissions are correct
chmod 777 /data
chmod 666 /data/bibliodb.sqlite
chmod -R 777 /usr/local/tomcat/uploads

# Print environment info
echo "Starting Tomcat with the following environment:"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "JAVA_OPTS: $JAVA_OPTS"
echo "Database location: /data/bibliodb.sqlite"

# Start Tomcat
exec catalina.sh run