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

================================================
CDS Database Upgrade Scripts and Migration Tools
================================================

This section explains upgrade scripts and a data-migration tool for
managing databases used by the Common Data Service (CDS).


Database Upgrade Scripts
------------------------

Upgrade scripts are provided for every CDS version that requires a
schema change. The database schema changes at major and minor version
changes but not at patch version changes. For example, the schema
changed when moving from version 1.18 to 2.0 and also when moving from
version 2.0 to 2.1, but the schema did not change when moving from
version 2.1.0 to 2.1.1. All SQL scripts are published in the CDS
gerrit repository at this URL::

    https://gerrit.acumos.org/r/gitweb?p=common-dataservice.git;a=tree;f=cmn-data-svc-server/db-scripts

Run Instructions
~~~~~~~~~~~~~~~~

A database administrator can run an upgrade script on any affected
database using any appropriate administration tool, including the
command line. The DBA is strongly advised to check the header of the
upgrade script for instructions specific to that script.

User and Author Data Adjustment for CDS 1.18.x
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Unlike the upgrade scripts mentioned above, this script is used to
modify an existing database to improve system behavior. This script
populates authorship details in models so that they appear as expected
in Portal-Markeplace verison 1.16.5 and later.  The script copies user
first name, last name and email from the user table to any solution
revision that has no author details. This script requires a Acumos
Common Data Service database at version 1.18.x. The SQL script is
available from the CDS gerrit repository at this URL::

    https://gerrit.acumos.org/r/gitweb?p=common-dataservice.git;a=blob;f=cmn-data-svc-server/db-scripts/cds-mysql-copy-user-author-1.18.sql


CMS Admin and User Data Migration Tool for CDS 2.0.x
----------------------------------------------------

This tool migrates all administrator and user data from the Hippo-CMS
system to the Common Data Service (version 2.0.x or later) and a Nexus
repository.  An early feature of Acumos stored admin and user data in
CMS, but later versions use CDS.  The following data items are
affected:

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
#. Co-brand logo: a small image at the top left of the main landing page,
   which is chosen by the administrator.
#. Carousel images and infographics: the rotating pictures at the top
   of the main landing page, which are configured by the administrator.
#. Text that decorates the "Discover Acumos" graphic on the Marketplace,
   which can be changed by an administrator.
#. Footer contact details: contact details shown at the bottom right,
   which can be changed by an administrator.
#. Footer terms and conditions: shown in the page footer,
   which can be changed by an administrator.

Prerequisites
~~~~~~~~~~~~~

Using this migration tool requires the following prerequisites:

#. A running docker daemon
#. Network connectivity to the public Acumos docker registry, nexus3.acumos.org
#. Network access to the local Acumos Common Data Service instance, version 1.17.0 or later
#. Network access to the local Acumos Hippo-CMS instance
#. Network access to the local Acumos Nexus repository
#. Credentials to read from the local CMS instance
#. Credentials to write to the local CDS instance
#. Credentials to write to the local Nexus repository.

Migration Preparation
~~~~~~~~~~~~~~~~~~~~~

Choose whether to migrate user data or admin data.  The user data
migration is required when upgrading from CDS version 1.17 to 1.18.
The admin data migration is required when upgrading from CDS version
2.0 to 2.1.  Set the type in the configuration file as described next.

After obtaining valid URLs, user names and passwords for all three
systems, enter them in a file named "migrate.properties" using the
following structure::

    # one of: admin, user
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

Migration Instructions
~~~~~~~~~~~~~~~~~~~~~~

For user data, the migration tool discovers the list of solutions by
querying CDS, checks the content of each solution by querying CMS, and
migrates content to CDS and Nexus as needed.  For admin data, the
migration tool discovers the data by querying CMS, and migrates
content to CDS as needed.

The tool expects to be invoked with a file named "migration.properties"
in the current working directory. The tool logs details of actions and
results on the standard output.

Run the migration tool using the released Docker image as shown below::

    docker run --rm -v ${PWD}/migrate.properties:/maven/migrate.properties \
           nexus3.acumos.org:10002/migrate-cms-to-cds:2.0.0

Note that port 10002 in the registry URL refers to the docker
"releases" registry. If the migration-tool image is not found there,
it may be necessary to pull a staged-for-release Docker image from the
staging registry by using the following URL instead::

    nexus3.acumos.org:10004

When the tool is finished it reports statistics in this format::

    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Migration statistics:
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Solutions checked: 474
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Revisions checked: 0
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Pictures migrated: 0 success, 0 fail
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Descriptions migrated: 0 success, 0 fail
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Documents migrated: 0 success, 0 fail
    2019-05-02T18:49:26.101Z [main] INFO  o.a.cds.migrate.MigrateCmsToCdsApp - Global items migrated: 2 success, 0 fail

Troubleshooting
~~~~~~~~~~~~~~~

In case of error, the tool can be run repeatedly on the same source
and target.  It will not re-migrate data to CDS nor Nexus for any
item.

The migration tool requires every document to have a file suffix that
indicates the type of document; e.g., ".doc" or ".xlsx".  A document
without any suffix cannot be migrated.  Add a suffix to the document
name to fix this problem, then re-run the migration process.
