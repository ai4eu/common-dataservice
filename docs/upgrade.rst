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

================================
CDS Data Migrations and Upgrades
================================

This section explains data-migration and data-upgrade tools and
scripts that apply to the Common Data Service (CDS).


User and Author Data Upgrade for CDS 1.18.x
-------------------------------------------

This database script populates authorship details in models so that
they appear as expected in Portal-Markeplace verison 1.16.5 and later.
The script copies user first name, last name and email from the user
table to any solution revision that has no author details.

Prerequisites
~~~~~~~~~~~~~

This migration tool requires a Acumos Common Data Service database at
version 1.18.x.

Script Source
~~~~~~~~~~~~~

The text of the SQL script is available from the CDS gerrit
repository::

    git clone https://gerrit.acumos.org/r/common-dataservice

In this file::

    cmn-data-svc-server/db-scripts/cds-mysql-copy-user-author-1.18.sql

Run Instructions
~~~~~~~~~~~~~~~~

A database administrator should run this script in any affected
database using any appropriate administration tool.


CMS Admin and User Data Migration
---------------------------------

This utility migrates all data from the Hippo-CMS system
to the Common Data Service (version 2.0.x or later) and a Nexus
repository.  An early feature of Acumos stored admin and user
data in CMS, but later versions use CDS.  The following data items
are affected:

#. Solution picture: a user can add a picture to a solution.
#. Revision descriptions: a user can add a description appropriate for
   the COMPANY access level and another description appropriate for the
   PUBLIC access level of a single revision. In other words, every
   revision can have zero, one or two descriptions.
#. Revision supporting documents: a user can upload many supporting
   documents for a revision, one set visible at the COMPANY access
   level and another set of documents visible at the PUBLIC access
   level. In other words, every revision can have an arbitrary number
   of supporting documents, divided into two sets.
#. Carousel images and infographics: the rotating pictures at the top
   of the main landing page.
#. Co-brand logo: a small image at the top left of the main landing page.
#. Footer contact details: contact details shown at the bottom right.
#. Footer terms and conditions: shown in the page footer.


Prerequisites
~~~~~~~~~~~~~

This migration tool requires Acumos Common Data Service at version 1.17.0 or later,
credentials to read from the CMS instance, credentials to write to the CDS instance,
and also credentials to write to the Nexus instance (3 sets of username/password pairs).


Tool Source
~~~~~~~~~~~

The migration tool is available from the CDS gerrit repository::

    git clone https://gerrit.acumos.org/r/common-dataservice

In the following subdirectory::

    migrate-cms-to-cds/


Build Instructions
~~~~~~~~~~~~~~~~~~

Clone the Git repository and build the tool as follows::

    git clone https://gerrit.acumos.org/r/common-dataservice
    cd common-dataservice/migrate-cms-to-cds
    mvn clean package


Configuration
~~~~~~~~~~~~~

After obtaining valid URLs and appropriate user names and passwords for all three systems,
enter them in a file named "migrate.properties" using the following structure::

    migrate.data.type = admin

    cds.url = http://cdshost.myproject.org:8001/ccds
    cds.user =
    cds.pass =

    cms.url = http://cmshost.myproject.org:8085/site
    cms.user =
    cms.pass =

    nexus.url = http://nexushost.myproject.org:8081/repository/repo_name
    nexus.user =
    nexus.pass =
    # this is the group prefix; a UUID compnent will be added
    nexus.prefix = org.acumos


Run Instructions
~~~~~~~~~~~~~~~~

Run the migration tool as below, after replacing "x" with the current version number::

    java -jar target/migrate-cms-to-cds-2.0.x-SNAPSHOT-spring-boot.jar

The tool expects to find file "migrate.properties" in the current directory.
It will write a log file to the current directory.

The migration tool discovers the list of solutions by querying CDS, checks the content
of each solution by querying CMS, and migrates content to CDS and Nexus as needed.

In case of error, the tool can be run repeatedly on the same source and target.
It will not re-migrate data to CDS nor Nexus for any item.

When the tool is finished it reports statistics in this format::

    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Migration statistics:
    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Solutions checked: 1485
    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Revisions checked: 2578
    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Pictures migrated: 2 success, 0 fail
    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Descriptions migrated: 0 success, 0 fail
    2018-09-13T11:03:00.986Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Documents migrated: 0 success, 4 fail


Troubleshooting
~~~~~~~~~~~~~~~

The migration tool requires every document to have a file suffix that indicates the type of document;
e.g., ".doc" or ".xlsx".  A document without any suffix cannot be migrated.  Add a suffix to the document
name to fix this problem, then re-run the migration process.
