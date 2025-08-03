#!/bin/bash
set -e

echo "Running database initialization script..."

# Apply all SQL migrations
for file in /docker-entrypoint-initdb.d/*.sql; do
  if [ -f "$file" ]; then
    echo "Applying migration: $file"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -f "$file"
    echo "Migration $file applied successfully"
  fi
done

echo "Database initialization completed successfully"
