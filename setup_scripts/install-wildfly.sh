export TMP_INSTALLATION_FOLDER=/tmp/download
export JBOSS_HOME=/tmp/wildfly

# Set the WILDFLY_VERSION env variable
WILDFLY_VERSION=26.1.2.Final
WILDFLY_SHA1=3dda0f3795c00cedf8b14c83f8c341244e7cad44
export JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh


# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
mkdir  $TMP_INSTALLATION_FOLDER
cd $TMP_INSTALLATION_FOLDER
curl -L -O https://github.com/wildfly/wildfly/releases/download/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz 
sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 
tar xf wildfly-$WILDFLY_VERSION.tar.gz 
mv $TMP_INSTALLATION_FOLDER/wildfly-$WILDFLY_VERSION $JBOSS_HOME
rm wildfly-$WILDFLY_VERSION.tar.gz 
#chmod -R g+rw ${JBOSS_HOME}

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
export LAUNCH_JBOSS_IN_BACKGROUND=true

# Appserver Config
export WILDFLY_USER=admin
export WILDFLY_PASS=adminPassword

# Deployment directory, used in child dockerfiles
export DEPLOYMENT_DIR=$JBOSS_HOME/standalone/deployments/

# MySQL Config
export MYSQL_VERSION=8.0.30
MYSQL_CONNECTOR_SHA1=5dce3e7f7544ddfddf9cd2f7a4457819b74be59d

# Setting up WildFly Admin Console
echo "=> Adding WildFly administrator"
$JBOSS_HOME/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

# Configure Wildfly server
echo "=> Starting WildFly server"
$JBOSS_HOME/bin/standalone.sh &
echo "=> Waiting for the server to boot"
until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done
echo "=> Downloading MySQL driver"
curl --location --output /tmp/mysql-connector-java-${MYSQL_VERSION}.jar --url http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar
echo "=> Adding MySQL module"
$JBOSS_CLI --connect --command="module add --name=com.mysql --resources=/tmp/mysql-connector-java-${MYSQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api"
echo "=> Adding MySQL driver"
$JBOSS_CLI --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.jdbc.Driver)"
echo "=> Creating a new datasource"
$JBOSS_CLI --connect --command="data-source add \
        --name={env.DS_NAME} \
        --jndi-name=java:jboss/datasources/{env.DS_NAME} \
        --user-name={env.DB_USER} \
        --password={env.DB_PASS} \
        --driver-name=mysql \
        --connection-url=jdbc:mysql://{env.DB_URI}/{env.DB_NAME} \
        --use-ccm=false \
        --max-pool-size=25 \
        --blocking-timeout-wait-millis=5000 \
        --enabled=true"
echo "=> Adding Microprofile Health"
$JBOSS_CLI --connect --command="/extension=org.wildfly.extension.microprofile.health-smallrye:add"
$JBOSS_CLI --connect --command="/subsystem=microprofile-health-smallrye:add(security-enabled=false)"
echo "=> Configuring Log Pattern"
$JBOSS_CLI --connect --command="/subsystem=logging/pattern-formatter=PATTERN:write-attribute(name=pattern, value=\"%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-4p --- [%15.15t] %-40.40c{1.} : %s%e%n\")"
$JBOSS_CLI --connect --command="/subsystem=logging/pattern-formatter=COLOR-PATTERN:write-attribute(name=pattern, value=\"%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-4p --- [%15.15t] %-40.40c{1.} : %s%e%n\")"

echo "=> Shutting down WildFly and Cleaning up"
  $JBOSS_CLI --connect --command=":shutdown"
  rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/*
  rm -f /tmp/*.jar

STANDALONE_PATH="${JBOSS_HOME}/standalone/configuration/standalone.xml"
# Patching / prefixxing  all the environment variables with a `$` sign in form of `{env.DS_NAME}` to `${env.DS_NAME}` since they are not added propely as expected
echo "=> Patching variables of Datasource in ${STANDALONE_PATH}"
sed -i 's/{env.D/${env.D/g' ${STANDALONE_PATH}