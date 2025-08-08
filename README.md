# KeyFort

An oAuth2.0 authorization server REST API application.


## Config
- Setup a PostgreSQL Database
- Create a user
  ```sql
  CREATE USER kfuser WITH ENCRYPTED PASSWORD 'kfpassword';
  ```
- Create a database
  ```sql
  CREATE DATABASE kfdb OWNER kfuser;
  ```
- Grant Permissions to user
  ```sql
  GRANT ALL PRIVILEGES ON DATABASE kfdb TO kfuser;
  ```
- Add following environment variable `PGSQL_URL`, `PGSQL_DB`, `PGSQL_SCHEMA`, `PGSQL_USER` and `PGSQL_PASS`.