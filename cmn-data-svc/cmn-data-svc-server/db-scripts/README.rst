.. ===============LICENSE_START=======================================================
.. Acumos
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..  
..      http://creativecommons.org/licenses/by/4.0
..  
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

==================================
Acumos Common Data Service Scripts
==================================

This directory has scripts for creating and populating a database used by the Common Data Service.
The simple semantic versioning system for scripts uses only 2 digits. So for example, database 
scripts at version 1.3 can support service version 1.3.0, version 1.3.1 and so on.  That last
digit refers to bug fixes and other changes that do not break the database/field contracts.

Install Instructions
--------------------

To create a new database, run the script appropriate for the version.  For example:

::

    mysql> create database cds1130;
    mysql> use cds1130;
    mysql> source cmn-data-svc-ddl-dml-mysql-1-13.sql;

Upgrade Instructions
--------------------

To upgrade a database, copy the data into a new database then run the upgrade script(s) appropriate for the version. For example:

::

    mysql> create database cds1130;
    $ mysqldump cds1120 | mysql cds1130
    mysql> source cds-mysql-upgrade-1-12-to-1-13.sql;

Please note this skips details of logging in to the database etc.
