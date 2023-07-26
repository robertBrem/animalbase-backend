## Securing an application deployed to WildFly with OpenID Connect (OIDC)

This example demonstrates how to secure an application deployed to WildFly with OpenID Connect
(OIDC) without needing to use the Keycloak client adapter.

The OIDC configuration in this example is part of the deployment itself and 
Environment Variables are used to bootstrap the Application at Runtime with the needed properties.

## Config
### web application context rot
- web context root is set to `/` via the `WEB-INF/jboss-web.xml`

### OIDC
- OIDC configration is bootstrapped at runtime through the environment variables defined in `WEB/INF/oidc.json` 
- auth-method is set to `OIDC` and url pattern `/secured` is marked as secured in `WEB-INF/web.xml`



### Access Keyloak Admin Interface
We can access the keycloak administration interface using http://localhost:8080/auth

### Access the webapp
We can access our application using http://localhost:8081/

Click on "Access Secured Servlet".

Now, you'll be redirected to Keycloak to log in. Log in with one of the test users mentioned further below in the docs.
Next, you'll be redirected back to our application and you should see the "Secured Servlet" page.

We were able to successfully log in to our application via the Keycloak OpenID provider!

# Setup
## Keycloak Setup Notes
- H2 In memory is used

- Client authentication is activated
- Credentials added

### Realm Config Import
- The Realm is imported through the realm-export.json during the startup of the keycloak server and includes the following clients and roles:

# Realm Configuration
The Realm `crb-developer` is imported through the realm-export.json during the startup of the keycloak server and includes the following clients and roles:

## Clients
|Type           | Client ID  | Client Secret |
|---------------|------------|---------------|
| Confidential  | app-be     | the#secret    |
| Public        | app-fe     |               |


## Users
| User             | Password  | Roles                            |
|------------------|-----------|----------------------------------|
| crb-user@crb.ch  | test      | crb-role-user                    |
| crb-admin@crb.ch | test      | crb-role-admin, crb-role-user    |

 
Redirections are allowed for any url and configured with a wildcard  `*`.

# Building and Deploying the web app

# Config SSO with Keycloak
The following environment variables are used to setup the keycloak connection:
```
      - OIDC_PROVIDER_URL=http://keycloak:8080 # TODO hostname keycloak 
      - OIDC_REALM=crb-developer
      - OIDC_CLIENT=app-be
      - OIDC_CLIENT_SECRET: "the#secret"    
```
Example on how to access the logged in principal is included in the code.

# Config Database
The following environment variables are used to setup the database connection through the wildfly datasource:
```
      - DS_NAME=DataSourceName
      - DB_NAME=databaseName
      - DB_USER=mysql
      - DB_PASS=mysql
      - DB_URI=mariadb:3306 # TODO hostname db 
```
Example on how to use the datasource is included in the code.

#### Building the showcase app locally
In order to create the web application deployment file, run the following command:
```
mvn clean install
```

This creates the artifact `target/showcase-keycloak.war`

#### Docker Image Build 
```
docker build -t showcase-keycloak .
docker image tag showcase-keycloak:latest docker-push.crb.ch/showcase/showcase-keycloak:latest
docker image push docker-push.crb.ch/showcase/showcase-keycloak:latest
```
#### Docker Image Build on Apple M1
```
docker build --platform=linux/amd64 -t showcase-keycloak .
```

#### Running all containers locally
```
docker compose --profile infra --profile app up       
```

#### Running only infrastructure systems (db, keycloak, maipit, crb sap mock, etc.) and use a local app server for the actual system
```
docker compose --profile infra up       

```

*Important* The following host entry needs to be added to make the authentication flow work from you local machine connecting to the keycloak docker container.
The URL in the showcase app can not be configured to localhost, since the communication from the backend to the keycloak is container to container.

add hosts:
```
127.0.0.1	keycloak
```

