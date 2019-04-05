.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

==================================================
Developer Guide for the Common Data Service Server
==================================================

This microservice provides common data services to components in the Acumos machine-learning platform.
It is built using the Spring-Boot platform. This document primarily offers guidance for server developers.

Supported Methods and Objects
-----------------------------

The microservice endpoints and objects are documented using Swagger. A running server documents itself at a URL like the following, but
consult the server's configuration for the exact port number (e.g., "8000") and context root (e.g., "ccds") to use::

    http://localhost:8000/ccds/swagger-ui.html


Building and Packaging
----------------------

As of this writing the build (continuous integration) process is fully automated in the Linux Foundation system
using Gerrit and Jenkins.  This section describes how to perform local builds for development and testing.

Prerequisites
~~~~~~~~~~~~~

The build machine needs the following:

1. Java version 1.8
2. Maven version 3
3. Connectivity to Maven Central to download required jars

Use maven to build and package the service into a single "fat" jar using this command::

    mvn package

Development and Local Testing
-----------------------------

This section provides information for developing and testing the server locally.

Testing with an in-memory database
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The default test configuration for the server uses an in-memory Derby database, which is created at launch time.

Testing with an external database
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A properties file "application-mariadb.properties" is provided that configures the server to use an
external MariaDB database running on the local host at port 3306.  Direct Spring-Boot to use that
properties file during a test with this invocation::

    mvn -Dspring.config.name=application-mariadb test

The server can be configured to use a different external database as follows:

    1. Copy the local application.properties file to a new file "application-mydb.properties"
    2. Revise the new "mydb" properties file to have suitable server coordinates and credentials
    3. Ensure the newly configured database server is reachable at the expected port
    4. Check that the database tables have been created and populated
    5. Launch the test as usual, adding an extra argument to use the alternate properties file.

This is a sample invocation::

    mvn -Dspring.config.name=application-mydb test

Launching
~~~~~~~~~

Launch the server for development and testing like this::

     mvn clean spring-boot:run

Alternately, launch the microservice from Eclipse by starting this class::

    org.acumos.cds.CdsApplication.

Production Deployment
---------------------

This section provides information for running the server in a production environment,
assuming that the application is packaged into a docker container for deployment.

Prerequisites
~~~~~~~~~~~~~

    1. Java version 1.8 in the runtime environment; i.e., installed in the docker container
    2. A Mariadb or Mysql database with the required tables; instructions are shown below
    3. The username/password combination to access the database
    4. A valid configuration with database coordinates.

Configuring the system
~~~~~~~~~~~~~~~~~~~~~~

First the database must be created or upgraded, depending on the situation,
using scripts in the "db-scripts" directory.  Please note version numbers are
mostly written here as "M.N" because actual version numbers change regularly.

- cmn-data-svc-user-mysql.sql: This file is a TEMPLATE can be used to
  create a Mysql/MariaDB database, to create a user, and to grant the
  user permission on the database.  The values in CAPITALS shown in
  the file must be adjusted for each use.
- cmn-data-svc-ddl-dml-mysql-M-N.sql: This file has the data-definition and
  data-modeling language statements that create new tables and
  populate them.
- cds-mysql-upgrade-M.N-to-M.N+1.sql: If an existing system needs to be upgraded,
  these files have the required SQL statements to perform the upgrade.

Next, configuration parameters must be specified.  A template with
default values can be found in the top level of this project named
application.properties.template, and can be copied a file named
application.properties (but see below for the preferred method).

Details about the database configuration must be supplied in the following
required entries::

    spring.database.driver.classname=org.apache.derby.jdbc.EmbeddedDriver
    spring.datasource.url=jdbc:derby:memory:cdsdb;create=true
    spring.datasource.username = ccds_user
    spring.datasource.password = some-password
    spring.jpa.database-platform=org.hibernate.dialect.DerbyTenSevenDialect

The HTTP server's username and password are configured in the properties file.
Only one username/password is used to secure the REST endpoint.
The default entries for the server are shown here::

    security.user.name=ccds_client
    security.user.password=(encrypted)

At runtime in production deployments, in addition to using a configuration file,
environment-specific configuration properties should be supplied using a block of
JSON in an environment variable called SPRING\_APPLICATION\_JSON. This can easily
be done in a docker-compose configuration file.  For example::

      SPRING_APPLICATION_JSON: '{
          "server" : {
              "port" : 8002
          },
          "security" : {
              "user" : {
                  "name"     : "ccds_client",
                  "password" : "ENC(encrypted-string-here)"
              }
          },
          "spring" : {
              "database" : {
                  "driver" : {
                      "classname" : "org.mariadb.jdbc.Driver"
                  }
              },
              "datasource" : {
                  "url"      : "jdbc:mariadb://hostname-db:3306/cds?useSSL=false",
                  "username" : "cds",
                  "password" : "ENC(encrypted-string-here)"
              },
              "jpa" : {
                  "database-platform" : "org.hibernate.dialect.MySQLDialect",
                  "hibernate" : {
                      "ddl-auto" : "validate"
                  }
              }
          }
     }'

Defining Code-Name Value Sets
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The application properties file defines all restricted value sets, which are code-name pairs.
For example, the access type for a catalog may take on the value "PB" (public).

These value sets can be changed by modifying the properties file.  Each entry has a code and
an associated name.  Continuing with the same example, the complete access type value set
is defined by the following configuration entries::

    code-name.access-type.PB=Public
    code-name.access-type.RS=Restricted

Perform these steps to define a new value set:

    1. Extend the Java class CodeNameType in the client project
    2. Extend the Java class CodeNameProperties in the server project
    3. Add appropriate entries to the default properties file.

Generating Encrypted Passwords
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Clear-text passwords are prohibited in many deployment environments.
Use the following commands to generate an encrypted password for the database and the service.

1. Download the jar, for example using wget::

    wget http://central.maven.org/maven2/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar

2. Use the Jasypt jar to generate the password. Note that the input 'YourPasswordHere' is the actual database password.  The confusingly named password parameter is used to encrypt the input::

    java -cp jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI algorithm=PBEWithMD5AndDES input='YourPasswordHere' password='EncryptionKey'

Using Encrypted Passwords
~~~~~~~~~~~~~~~~~~~~~~~~~

The same "password" parameter used to encrypt the passwords must be supplied at run time, in any of the following ways:

1. In the application.properties file using the key jasypt.encryptor.password.  For example::

    jasypt.encryptor.password=EncryptionKey

2. Alternately, the password can be supplied on the command line with a JVM argument "-Djasypt.encryptor.password"::

    java -jar cmn-data-svc-server-N.N.jar -Djasypt.encryptor.password=EncryptionKey

Launch Instructions
~~~~~~~~~~~~~~~~~~~

Once the configuration is provided either in an application.properties file or in an environment variable,
start the application with the following command::

    java -Xms128m -Xmx1024m -Djava.security.egd=file:/dev/./urandom -jar common-dataservice-N.N.N.jar

Quickstart Version Upgrade
~~~~~~~~~~~~~~~~~~~~~~~~~~

This documents the steps required to upgrade an installation to a new(er) version.

1. Create a new database. If needed, create a new user and grant access to the database for the new user.  Example commands to do this are in script "cmn-data-svc-basemysql.sql" and are something like this::

    % sudo mysql
    > create database cds118;
    > create user 'CDS_USER'@'%' identified by 'CDS_PASS';
    > grant all on cds118.* to 'CDS_USER'@'%';

2. Migrate the old database to the new database.  For example, if working on the Mysql/Mariadb database server the command is something like the following, depending on system configuration and user privileges::

    sudo mysqldump cds117 | sudo mysql cds118

3. Upgrade the new database to the latest structure by running the appropriate upgrade script.  For example, the command sequence may be something like this::

    % sudo mysql
    > use cds118;
    > source cds-mysql-upgrade-1-17-to-1-18.sql;

4. Configure the docker image for the new version.  Assuming that the docker compose is being used, revise the appropriate docker-compose file to have an entry for the new version, using an available network port.

5. Use an appropriate docker-compose start script (varies by environment) to start the new image, for example::

    docker-compose up -d common-dataservice-1181

Troubleshooting
---------------

Spring-Boot throws a confusing exception if the database connection fails, something like this::

    Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException:
    Error creating bean with name 'artifactController': Unsatisfied dependency expressed through field 'artifactService'; nested exception is
    org.springframework.beans.factory.UnsatisfiedDependencyException:
    Error creating bean with name 'artifactService': Unsatisfied dependency expressed through field 'sessionFactory'; nested exception is
    org.springframework.beans.factory.BeanCreationException:
    Error creating bean with name 'sessionFactory' defined in class path resource [.../ccds/hibernate/HibernateConfiguration.class : Invocation of init method failed;
    nested exception is org.hibernate.service.spi.ServiceException:
    Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]

If you see this exception, first check the database configuration carefully.
