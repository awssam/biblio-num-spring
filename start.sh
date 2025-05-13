#!/bin/bash
# Startup script for Spring Boot application with SQLite database
# This script checks for database existence and initializes if needed

# Check if database file exists
if [ ! -f /data/bibliodb.sqlite ]; then
  echo "Database file not found. Creating new database..."
  
  # Create an empty SQLite database
  sqlite3 /data/bibliodb.sqlite "PRAGMA journal_mode=WAL;"
  
  echo "Database created successfully."
  
  # Set admin password for first run
  # Generate a secure random password if ADMIN_PASSWORD is not set
  if [ -z "$ADMIN_PASSWORD" ]; then
    ADMIN_PASSWORD=$(tr -dc 'A-Za-z0-9!@#$%^&*' < /dev/urandom | head -c 12)
    echo "Generated random admin password: $ADMIN_PASSWORD"
  fi
  
  # Add admin password to Java options
  export JAVA_OPTS="$JAVA_OPTS -Dadmin.password=$ADMIN_PASSWORD"
  echo "Admin user will be created with the specified or generated password."
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