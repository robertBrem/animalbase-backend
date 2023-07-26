component-test:
	mvn clean test

artifact-build:
	mvn clean package

linting:

sonar-test: linting

linting:
	mvn verify sonar:sonar -Dsonar.host.url=${SONAR_URL} -Dsonar.token=${SONAR_LOGIN_TOKEN} -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.qualitygate.wait=true

vulnerability-scan:


image: image-build

image-build:
	docker build --no-cache --tag "$(IMAGE):$(IMAGE_TAG)" -f Dockerfile .

setup:
	./setup_scripts/install-wildfly.sh

run:
	./start.sh

end2end-test:

docker-scan:
	trivy image  "$(IMAGE):$(IMAGE_TAG)"