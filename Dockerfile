FROM docker.crb.ch/crb-wildfly:26

# MySQL Configuration
# Part of Docker Compose or Helm Charts
# ENV DB_NAME                   ebkpassistant
# ENV DS_NAME                   EbkpAssistant
# ENV DB_USER                   mysql
# ENV DB_PASS                   mysql
# ENV DB_URI                    db:3306

# Keycloak Configuration
# Part of Docker Compose or Helm Charts
# ENV OIDC_PROVIDER_URL         http://host.docker.internal:8080
# ENV OIDC_CLIENT               crb-developer
# ENV OIDC_REALM                app-be

# TODO move to ebkpassistant Dockerfile (just here for testing purpose)
# Special Project Dependencies / Configuration
USER root
#RUN echo "=> installing dependencies for wkhtmltopdf and flyway cli" && \
#   yum install -y which fontconfig libXrender libXext xorg-x11-fonts-Type1 xorg-x11-fonts-75dpi freetype libpng zlib libjpeg-turbo openssl wget && \
#   yum clean all && wget -q https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.5/wkhtmltox-0.12.5-1.centos7.x86_64.rpm && \
#   rpm -Uvh wkhtmltox-0.12.5-1.centos7.x86_64.rpm && mkdir /ebkptemp && chown -R jboss /ebkptemp \
#   && wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.8.1/flyway-commandline-9.8.1-linux-x64.tar.gz | tar xvz && ln -s `pwd`/flyway-9.8.1/flyway /usr/local/bin && chown -R jboss `pwd`/flyway-9.8.1
#USER jboss

# Add Deployment file
ADD target/showcase-keycloak.war $DEPLOYMENT_DIR
