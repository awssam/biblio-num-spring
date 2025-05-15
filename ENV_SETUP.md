# Environment Variables Setup for BiblioNum

This guide explains how to configure environment variables for the BiblioNum application to secure database credentials.

## Using .env File (Development Environment)

1. Create a `.env` file in your project root directory with the following content:

```
DB_URL=jdbc:postgresql://localhost:5432/bibliotheque
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

2. Replace `your_username` and `your_password` with your actual PostgreSQL credentials.

3. The application will automatically load these environment variables during startup thanks to the `EnvConfig` class.

## Using System Environment Variables (Production Environment)

For production environments, set these variables at the system or container level:

### Linux/macOS:

```bash
export DB_URL=jdbc:postgresql://your-production-db:5432/bibliotheque
export DB_USERNAME=production_user
export DB_PASSWORD=secure_password
```

### Windows:

```cmd
set DB_URL=jdbc:postgresql://your-production-db:5432/bibliotheque
set DB_USERNAME=production_user
set DB_PASSWORD=secure_password
```

### Docker:

If using Docker, add these variables to your Dockerfile or docker-compose.yml:

```yaml
environment:
  - DB_URL=jdbc:postgresql://db:5432/bibliotheque
  - DB_USERNAME=db_user
  - DB_PASSWORD=db_password
```

## Security Considerations

1. Never commit the `.env` file to version control
2. Use different credentials for development and production
3. Consider using a secret management service for production credentials
4. Regularly rotate passwords in production environments
