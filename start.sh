docker rm -f $(docker ps -aq)
docker rmi docker.crb.ch/showcase/showcase-keycloak:latest
mvnw clean package
docker build -t docker.crb.ch/showcase/showcase-keycloak:latest .
docker push robertbrem/animalbase-backend:latest
docker compose --profile infra --profile app up