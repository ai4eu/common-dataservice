# Acumos Common Data Service

This repository holds the server and client components of the Common Data Service
for the Acumos machine-learning platform.  The server provides a storage and query
layer between system components and a relational database and exposes a REST interface.
The client allows Java developers to access the REST endpoints easily.

Please see the documentation in the "docs" folder.

## Running

For development and testing purposes, working from this source tree you can start an
instance of the CDS server that uses an in-memory database like this:

    cd cmn-data-svc-client
    mvn install
    cd ../cmn-data-svc-server
    mvn spring-boot:run

You can configure the server to use a MariaDB or Mysql database by entering the URL,
username and password in the file 'cmn-data-svc-server/application-mariadb.properties'
then launch the server using the following modified command much like above:

    mvn -Dspring.config.name=application-mariadb spring-boot:run

## Developers

Eclipse and Spring Tool Suite IDE users should install the plugin "m2e-apt" from the
Eclipe Marketplace.

## License

Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
Acumos is distributed by AT&T and Tech Mahindra under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.  See the License for the specific language governing permissions and limitations
under the License.
