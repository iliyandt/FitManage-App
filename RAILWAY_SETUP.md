# Railway MySQL Database Connection Guide

This guide explains how to connect your Spring Boot application to a MySQL database on Railway.

## Configuration Files

### 1. `application-railway.properties`

This file contains the configuration for connecting to Railway's MySQL database:

```properties
spring.datasource.url=${MYSQL_URL}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 2. `application.yml`

The main configuration file includes profile settings:

```yaml
spring:
  profiles:
    include: secrets
    group:
      railway: railway,secrets
```

### 3. `Procfile`

This file tells Railway how to run your application:

```
web: java -Dspring.profiles.active=railway -jar target/FitManage-0.0.1-SNAPSHOT.jar
```

## Railway Setup Instructions

1. **Create a MySQL Database in Railway**:
   - In your Railway project, add a MySQL database service

2. **Link Your Application to the Database**:
   - In your Railway dashboard, go to your application service
   - Navigate to the "Variables" tab
   - Add a new variable named `MYSQL_URL` with the value:
     ```
     ${{ MySQL.MYSQL_URL }}
     ```

3. **Deploy Your Application**:
   - Push your code to your repository
   - Railway will automatically deploy your application
   - The `Procfile` will ensure the `railway` profile is activated

4. **Verify the Connection**:
   - After deployment, check your application logs in Railway
   - Look for successful Hibernate initialization messages
   - If there are connection errors, verify that the `MYSQL_URL` variable is correctly set

## Troubleshooting

If your application still cannot connect to the database:

1. **Check Environment Variables**:
   - Verify that the `MYSQL_URL` variable is correctly set in Railway
   - The value should be `${{ MySQL.MYSQL_URL }}`

2. **Verify Database Service**:
   - Make sure your MySQL service is running in Railway
   - Check that your application service is linked to the MySQL service

3. **Check Application Logs**:
   - Look for specific error messages in the logs
   - Common issues include authentication failures or network connectivity problems

4. **Manual Connection Test**:
   - You can try connecting to the database using the MySQL CLI from Railway's shell
   - This can help determine if the issue is with the database service or your application

## How It Works

When your application is deployed to Railway:

1. Railway sets the `MYSQL_URL` environment variable with the connection string to your MySQL database
2. The `Procfile` activates the `railway` profile
3. Spring Boot loads `application-railway.properties`, which uses the `MYSQL_URL` environment variable
4. Your application connects to the Railway MySQL database using this connection string

This configuration allows your application to use local database settings during development and automatically switch to Railway's MySQL database when deployed.
