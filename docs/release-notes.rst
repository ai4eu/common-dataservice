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

=================================
Common Data Service Release Notes
=================================

The client and server are released together.  The client is available
as a jar file in the Acumos/LF Nexus repository. The server is
available as a Docker image in the Acumos/LF Docker registry.

Version 3.0.0, 13 Sep 2019
--------------------------
* Add license profile templates to database scripts (`ACUMOS-3435 <https://jira.acumos.org/browse/ACUMOS-3435>`_)

Version 3.0.0, 5 Sep 2019
-------------------------
* Add support for license profiles (`ACUMOS-3160 <https://jira.acumos.org/browse/ACUMOS-3160>`_)
* Split DDL/DML scripts for image size (`ACUMOS-3321 <https://jira.acumos.org/browse/ACUMOS-3321>`_)
* Define new DDL/DML scripts for creating a new, or upgrading to, version 3.0 database
* Compile and run with Java 11, but keep client library compliance level at Java 8.
* Add "acumos/" prefix to container image name
* Requires database schema version 3.0

Version 2.2.6, 7 Aug 2019
-------------------------
* Add query method to get peers for a restricted catalog (`ACUMOS-3298 <https://jira.acumos.org/browse/ACUMOS-3298>`_)
* Requires database schema version 2.2

Version 2.2.5, 16 July 2019
---------------------------
* Add headers to discover texts (`ACUMOS-2992 <https://jira.acumos.org/browse/ACUMOS-2992>`_)
* Add kernel type CC (`ACUMOS-3147 <https://jira.acumos.org/browse/ACUMOS-3147>`_)
* Add kernel type JS (`ACUMOS-3148 <https://jira.acumos.org/browse/ACUMOS-3148>`_)
* Update default catalog names (`ACUMOS-3209 <https://jira.acumos.org/browse/ACUMOS-3209>`_)
* Requires database schema version 2.2

Version 2.2.4, 23 May 2019
--------------------------
* Restore automatic modified-date updates (`ACUMOS-2949 <https://jira.acumos.org/browse/ACUMOS-2949>`_)
* Extend migration script for solutions with mixed access types (`ACUMOS-3110 <https://jira.acumos.org/browse/ACUMOS-3110>`_)
* Requires database schema version 2.2

Version 2.2.3, 15 May 2019
--------------------------
* Adjust logging for server FQDN (`ACUMOS-2456 <https://jira.acumos.org/browse/ACUMOS-2456>`_)
* Drop site content in database creation scripts (`ACUMOS-2723 <https://jira.acumos.org/browse/ACUMOS-2723>`_)
* Repair DML script for C_ROLE insert (`ACUMOS-2891 <https://jira.acumos.org/browse/ACUMOS-2891>`_)
* Repair federation subscription selector example (`ACUMOS-2899 <https://jira.acumos.org/browse/ACUMOS-2899>`_)
* Check for null key or value in query parameter map in search methods
* Requires database schema version 2.2

Version 2.2.2, 22 Apr 2019
--------------------------
* Add site content to database creation scripts (`ACUMOS-2420 <https://jira.acumos.org/browse/ACUMOS-2420>`_)
* Add portal home page discover-marketplace content (`ACUMOS-2738 <https://jira.acumos.org/browse/ACUMOS-2738>`_)
* Add method to fetch unique catalog publisher names (`ACUMOS-2758 <https://jira.acumos.org/browse/ACUMOS-2758>`_)
* Remove validation box from database creation script (`ACUMOS-2764 <https://jira.acumos.org/browse/ACUMOS-2764>`_)
* Restore method to get solutions by modified date (`ACUMOS-2783 <https://jira.acumos.org/browse/ACUMOS-2783>`_)
* Record DDL/DML script history (`ACUMOS-2843 <https://jira.acumos.org/browse/ACUMOS-2843>`_)
* Requires database schema version 2.2

Version 2.2.1, 12 Apr 2019
--------------------------
* Add method to fetch right-to-use objects by ref ID (`ACUMOS-2722 <https://jira.acumos.org/browse/ACUMOS-2722>`_)
* Add methods to check peer and user solution access (`ACUMOS-2735 <https://jira.acumos.org/browse/ACUMOS-2735>`_)
* Rename and repair findPublishedSolutions method (`ACUMOS-2736 <https://jira.acumos.org/browse/ACUMOS-2736>`_)
* Requires database schema version 2.2

Version 2.2.0, 5 Apr 2019
-------------------------
* Add APIs for workbench data access (`ACUMOS-2509 <https://jira.acumos.org/browse/ACUMOS-2509>`_)
* Add project for workbench (`ACUMOS-2516 <https://jira.acumos.org/browse/ACUMOS-2516>`_)
* Define service-status values (`ACUMOS-2520 <https://jira.acumos.org/browse/ACUMOS-2520>`_)
* Add pipeline for workbench (`ACUMOS-2534 <https://jira.acumos.org/browse/ACUMOS-2534>`_)
* Add notebook for workbench (`ACUMOS-2535 <https://jira.acumos.org/browse/ACUMOS-2535>`_)
* Define notebook-type values (`ACUMOS-2560 <https://jira.acumos.org/browse/ACUMOS-2560>`_)
* Define kernel-type values (`ACUMOS-2561 <https://jira.acumos.org/browse/ACUMOS-2561>`_)
* Extend data and APIs for catalog features (`ACUMOS-2569 <https://jira.acumos.org/browse/ACUMOS-2569>`_)
* Remove access type code from solution revision (`ACUMOS-2569 <https://jira.acumos.org/browse/ACUMOS-2569>`_)
* Remove access and scope type from peer subscription (`ACUMOS-2569 <https://jira.acumos.org/browse/ACUMOS-2569>`_)
* Add self-publish flag to catalog (`ACUMOS-2569 <https://jira.acumos.org/browse/ACUMOS-2569>`_)
* Map descriptions and documents to catalogs (`ACUMOS-2569 <https://jira.acumos.org/browse/ACUMOS-2569>`_)
* Drop peer and solution groups (`ACUMOS-2590 <https://jira.acumos.org/browse/ACUMOS-2590>`_)
* Query project, notebook and pipeline (`ACUMOS-2595 <https://jira.acumos.org/browse/ACUMOS-2595>`_)
* Revise access type codes (`ACUMOS-2610 <https://jira.acumos.org/browse/ACUMOS-2610>`_)
* Add method to get RTU users (`ACUMOS-2666 <https://jira.acumos.org/browse/ACUMOS-2666>`_)
* Add Swagger library to display size constraints from JPA annotations in generated UI
* Upgrade to Spring-Boot version 2.1.4.RELEASE
* Requires database schema version 2.2

Version 2.1.2, 4 Mar 2019
-------------------------
* Add task type MS for micro service generation (`ACUMOS-2600 <https://jira.acumos.org/browse/ACUMOS-2600>`_)
* Requires database schema version 2.1

Version 2.1.1, 20 Feb 2019
--------------------------
* Add controller methods to get pages of configs and contents (`ACUMOS-2423 <https://jira.acumos.org/browse/ACUMOS-2423>`_)
* Add peer-status code UK (`ACUMOS-2513 <https://jira.acumos.org/browse/ACUMOS-2513>`_)
* Allow server to run as unprivileged user (`ACUMOS-2533 <https://jira.acumos.org/browse/ACUMOS-2533>`_)
* Requires database schema version 2.1

Version 2.1.0, 11 Feb 2019
--------------------------
* Add on-boarded timestamp to solution revision (`ACUMOS-1827 <https://jira.acumos.org/browse/ACUMOS-1827>`_)
* Add MLPRightToUse for License Management (`ACUMOS-2355 <https://jira.acumos.org/browse/ACUMOS-2355>`_)
* Add MLPTask and revise MLPTaskStepResult for on-boarding status (`ACUMOS-2387 <https://jira.acumos.org/browse/ACUMOS-2387>`_)
* Requires database schema version 2.1

Version 2.0.7, 6 Feb 2019
-------------------------
* Restore self as peer search parameter (`ACUMOS-2470 <https://jira.acumos.org/browse/ACUMOS-2470>`_)
* Requires database schema version 2.0

Version 2.0.6, 1 Feb 2019
-------------------------
* Revise client domain model setUri methods (`ACUMOS-2443 <https://jira.acumos.org/browse/ACUMOS-2443>`_)
* Requires database schema version 2.0

Version 2.0.5, 30 Jan 2019
--------------------------
* Extend delete methods to cascade (`ACUMOS-2417 <https://jira.acumos.org/browse/ACUMOS-2417>`_)
* Revise annotations on web stats for concurrency safety (`ACUMOS-2432 <https://jira.acumos.org/browse/ACUMOS-2432>`_)
* Requires database schema version 2.0

Version 2.0.4, 24 Jan 2019
--------------------------
* Repair createUser method behavior on apiToken field (`ACUMOS-2413 <https://jira.acumos.org/browse/ACUMOS-2413>`_)
* Add JSR310 library to deserialize Instant values (`ACUMOS-2416 <https://jira.acumos.org/browse/ACUMOS-2416>`_)
* Requires database schema version 2.0

Version 2.0.3, 23 Jan 2019
--------------------------
* Revise base docker image to OpenJDK (`ACUMOS-2409 <https://jira.acumos.org/browse/ACUMOS-2409>`_)
* Requires database schema version 2.0

Version 2.0.2, 7 Jan 2019
-------------------------
* Use header X-ACUMOS-Request-Id (`ACUMOS-1801 <https://jira.acumos.org/browse/ACUMOS-1801>`_)
* Use Acumos logging standard (`ACUMOS-2328 <https://jira.acumos.org/browse/ACUMOS-2328>`_)
* Add artifact-type code 'SR' (`ACUMOS-2362 <https://jira.acumos.org/browse/ACUMOS-2362>`_)
* Requires database schema version 2.0

Version 2.0.1, 2 Jan 2019
-------------------------

* Upgrade to Spring-Boot version 2.1 (`ACUMOS-1926 <https://jira.acumos.org/browse/ACUMOS-1926>`_) which requires runtime configuration changes:
    * Drop property 'spring.database.driver.classname'
    * Change property key 'spring.datasource.url' to 'spring.datasource.jdbc-url'
    * Extend property 'spring.datasource.jdbc-url' with connection parameter 'useLegacyDatetimeCode=false'
    * Change property 'spring.jpa.database-platform' to value 'org.hibernate.dialect.MariaDB102Dialect'
    * Add prefix 'spring' to property keys 'security.user.name' and 'security.user.password'
* Add catalog support for solutions (`ACUMOS-2284 <https://jira.acumos.org/browse/ACUMOS-2284>`_)
* Silence warnings from Springfox dependencies (`ACUMOS-2316 <https://jira.acumos.org/browse/ACUMOS-2316>`_)
* Use constants etc. to silence Sonar warnings
* Requires database schema version 2.0

Version 2.0.0, 19 Dec 2018
--------------------------

* Extend table c_solution_rev with columns for security verification (`ACUMOS-1376 <https://jira.acumos.org/browse/ACUMOS-1376>`_)
* Move download, rating and view fields to MLPSolution entity (`ACUMOS-1675 <https://jira.acumos.org/browse/ACUMOS-1675>`_)
* Change column types to TIMESTAMP and modify Mariadb connection configuration (`ACUMOS-1691 <https://jira.acumos.org/browse/ACUMOS-1691>`_)
* Revise controllers to reduce query time and log warnings on bad requests (`ACUMOS-1697 <https://jira.acumos.org/browse/ACUMOS-1697>`_)
* Use tab-separated MDC-style log output format (`ACUMOS-1752 <https://jira.acumos.org/browse/ACUMOS-1752>`_)
* Add method to get user unread notification count (`ACUMOS-1883 <https://jira.acumos.org/browse/ACUMOS-1883>`_)
* Validate URI parameters in Artifact and Document (`ACUMOS-1927 <https://jira.acumos.org/browse/ACUMOS-1927>`_)
* Drop unused validation entities and features (`ACUMOS-1931 <https://jira.acumos.org/browse/ACUMOS-1931>`_)
* Support site content key-value pairs (`ACUMOS-1936 <https://jira.acumos.org/browse/ACUMOS-1936>`_)
* Add method to get peer subscription count (`ACUMOS-2006 <https://jira.acumos.org/browse/ACUMOS-2006>`_)
* Remove picture from MLPSolution, add get and save methods (`ACUMOS-2088 <https://jira.acumos.org/browse/ACUMOS-2088>`_)
* Add artifact-type code and toolkit-type codes (`ACUMOS-2248 <https://jira.acumos.org/browse/ACUMOS-2248>`_)
* Drop deprecated get-enum methods (`ACUMOS-2252 <https://jira.acumos.org/browse/ACUMOS-2252>`_)
* Remove description from MLPSolution and MLPSolutionRevision (`ACUMOS-2261 <https://jira.acumos.org/browse/ACUMOS-2261>`_)
* Add artifact-type code LI for license (`ACUMOS-2307 <https://jira.acumos.org/browse/ACUMOS-2307>`_)
* Revise restricted-federation query method to use page parameter
* Requires database schema version 2.0

Versions 2.* require configuration data in an environment variable SPRING_APPLICATION_JSON
with the following structure.  All values in upper case must be replaced::

    SPRING_APPLICATION_JSON: '{
        "server" : {
            "port" : 8000
        },
        "spring" : {
            "datasource" : {
                "jdbc-url" : "jdbc:mariadb://HOST-NAME:3306/DB-NAME?useLegacyDatetimeCode=false&useSSL=false",
                "username" : "DB_USERNAME",
                "password" : "DB_PASSWORD"
            },
            "jpa" : {
                "database-platform" : "org.hibernate.dialect.MariaDB102Dialect",
                "hibernate" : {
                    "ddl-auto" : "validate"
                },
                "show-sql" : false
            },
            "security" : {
                "user" : {
                    "name"     : "CLIENT_USERNAME",
                    "password" : "CLIENT_PASSWORD"
                }
            }
        }
    }'

------------

Version 1.18.4, 23 Oct 2018
---------------------------

* Detect description and document modifications (`ACUMOS-1902 <https://jira.acumos.org/browse/ACUMOS-1902>`_)
* Add SQL script to create default first author in revisions (`ACUMOS-2109 <https://jira.acumos.org/browse/ACUMOS-2109>`_)
* Add missing API model annotations to benefit Swagger
* Add test case for find-by-modified-date feature
* Requires database schema version 1.18

Version 1.18.3, 9 Oct 2018
--------------------------

* Add site-config data in 1.18 SQL script to silence 400 errors (`ACUMOS-857 <https://jira.acumos.org/browse/ACUMOS-857>`_)
* Restore exception logging in ONAP/Athena logging output pattern (`ACUMOS-1752 <https://jira.acumos.org/browse/ACUMOS-1752>`_)
* Add configuration to stop dropping file suffixes in path variables (`ACUMOS-1836 <https://jira.acumos.org/browse/ACUMOS-1836>`_)
* Requires database schema version 1.18

Version 1.18.2, 27 Sep 2018
---------------------------

* Add peer status code RM=Removed (`ACUMOS-1596 <https://jira.acumos.org/browse/ACUMOS-1596>`_)
* Use ONAP/Athena logging output pattern (`ACUMOS-1752 <https://jira.acumos.org/browse/ACUMOS-1752>`_)
* Upgrade Spring-Boot to 1.15.16.RELEASE (`ACUMOS-1754 <https://jira.acumos.org/browse/ACUMOS-1754>`_)
* New client method to search solutions with all/any tags (`ACUMOS-1763 <https://jira.acumos.org/browse/ACUMOS-1763>`_)
* Use N/Y instead of 0/1 in table C_ROLE column ACTIVE_YN (`ACUMOS-1788 <https://jira.acumos.org/browse/ACUMOS-1788>`_)
* Disable Build-for-ONAP/DCAE by default (`ACUMOS-1812 <https://jira.acumos.org/browse/ACUMOS-1812>`_)
* Limit console log output to level WARN and above
* Refactor additional search controller annotations for Swagger web UI
* Requires database schema version 1.18

Version 1.18.1, 11 Sep 2018
---------------------------

* New client method to find restricted solutions (`ACUMOS-1611 <https://jira.acumos.org/browse/ACUMOS-1611>`_)
* Add role "Publisher" to base 1.18 DML/DDL script (`ACUMOS-1642 <https://jira.acumos.org/browse/ACUMOS-1642>`_)
* Defend against null arguments (`ACUMOS-1696 <https://jira.acumos.org/browse/ACUMOS-1696>`_)
* Add default value in database scripts for timestamp-type columns (`ACUMOS-1703 <https://jira.acumos.org/browse/ACUMOS-1703>`_)
* Fix bug in method that builds criteria for querying on value list (`ACUMOS-1707 <https://jira.acumos.org/browse/ACUMOS-1707>`_)
* Extend migration tool for special characters in file names (`ACUMOS-1733 <https://jira.acumos.org/browse/ACUMOS-1733>`_)
* Refactor artifact-search controller annotations for Swagger web UI
* Add example federation selector to Swagger annotation
* Move some methods from user to notification controller
* Increase default memory limit to 1GB
* Upgrade Spring-Boot from 1.15.14.RELEASE to 1.15.15.RELEASE.
* Requires database schema version 1.18

Version 1.18.0, 31 Aug 2018
---------------------------

* Add publish request entity with supporting methods (`ACUMOS-1642 <https://jira.acumos.org/browse/ACUMOS-1642>`_)
* Add tags to User entity (`ACUMOS-1643 <https://jira.acumos.org/browse/ACUMOS-1643>`_)
* Refactor find-solution methods to enable Swagger UI
* Requires database schema version 1.18

Version 1.17.3, 31 Aug 2018
---------------------------

* Added new API endpoint to get Dataset info, right now it is just prototype (`ACUMOS-1182 <https://jira.acumos.org/browse/ACUMOS-1182>`_)

Version 1.17.2, 28 Aug 2018
---------------------------

* Revise logging for ONAP recommended output pattern (`ACUMOS-625 <https://jira.acumos.org/browse/ACUMOS-625>`_)
* Include solution and revision ID fields in search (`ACUMOS-1576 <https://jira.acumos.org/browse/ACUMOS-1576>`_)
* Revise message sent when user is locked out temporarily (`ACUMOS-1597 <https://jira.acumos.org/browse/ACUMOS-1597>`_)
* Filter search results using all tags not any tag (`ACUMOS-1601 <https://jira.acumos.org/browse/ACUMOS-1601>`_)
* Document Pageable request parameters in Swagger (`ACUMOS-1608 <https://jira.acumos.org/browse/ACUMOS-1608>`_)
* Enhance search for revision description (`ACUMOS-1614 <https://jira.acumos.org/browse/ACUMOS-1614>`_)
* Add API to count top-level comments on a revision (`ACUMOS-1644 <https://jira.acumos.org/browse/ACUMOS-1644>`_)
* Requires database schema version 1.17

Version 1.17.1, 10 Aug 2018
---------------------------

* Create tags automatically when creating or updating a solution (`ACUMOS-1546 <https://jira.acumos.org/browse/ACUMOS-1546>`_)
* Change data type of picture fields from Byte[] to byte [] (`ACUMOS-1557 <https://jira.acumos.org/browse/ACUMOS-1557>`_)
* Requires database schema version 1.17

Version 1.17.0, 9 Aug 2018
--------------------------

* Store metadata for user documents in Nexus (`ACUMOS-1235 <https://jira.acumos.org/browse/ACUMOS-1235>`_)
* Load capitalized role name "Admin" not "admin" (`ACUMOS-1526 <https://jira.acumos.org/browse/ACUMOS-1526>`_)
* Requires database schema version 1.17

Version 1.16.1, 2 Aug 2018
--------------------------

* Store API token encrypted, not hashed (`ACUMOS-1487 <https://jira.acumos.org/browse/ACUMOS-1487>`_)
* Requires database schema version 1.16

Version 1.16.0, 24 July 2018
----------------------------

* Add column for solution picture; add table for revision description (`ACUMOS-1235 <https://jira.acumos.org/browse/ACUMOS-1235>`_)
* Rename ownerId to userId in solution, revision, artifact, peer subscription (`ACUMOS-1359 <https://jira.acumos.org/browse/ACUMOS-1359>`_)
* Add authors; move provider in solution to publisher in revision (`ACUMOS-1359 <https://jira.acumos.org/browse/ACUMOS-1359>`_)
* Extend findPortalSolutions to search authors and publisher (`ACUMOS-1359 <https://jira.acumos.org/browse/ACUMOS-1359>`_)
* Add verification token and expiration date (`ACUMOS-1386 <https://jira.acumos.org/browse/ACUMOS-1386>`_)
* Add API token attribute to user entity, add loginApi method (`ACUMOS-1424 <https://jira.acumos.org/browse/ACUMOS-1424>`_)
* Add attributes to track failed login attempts (`ACUMOS-1442 <https://jira.acumos.org/browse/ACUMOS-1442>`_)
* Enable the admin role in newly created database (`ACUMOS-1446 <https://jira.acumos.org/browse/ACUMOS-1446>`_)
* Add support for proxy when getting a CDS client instance
* Requires database schema version 1.16

Version 1.15.4, 6 July 2018
---------------------------

* Add method to get solution revision comment count (`ACUMOS-1270 <https://jira.acumos.org/browse/ACUMOS-1270>`_)
* Show exception details in log files (`ACUMOS-1328 <https://jira.acumos.org/browse/ACUMOS-1328>`_)
* Requires database schema version 1.15

Version 1.15.3, 27 June 2018
----------------------------

* Add search method to find user's co-owned solutions (`ACUMOS-1257 <https://jira.acumos.org/browse/ACUMOS-1257>`_)
* Add methods to manage composite solution parent-child membership
* Update Spring library versions to address vulnerabilities flagged by CLM
* Requires database schema version 1.15

Version 1.15.2, 6 June 2018
---------------------------

* Add toolkit type codes CO, SP (`ACUMOS-1013 <https://jira.acumos.org/browse/ACUMOS-1013>`_)
* Repair findPortalSolutions API behavior on untagged solutions (`ACUMOS-1045 <https://jira.acumos.org/browse/ACUMOS-1045>`_)
* Requires database schema version 1.15

Version 1.15.1, 18 May 2018
---------------------------

* Call audit logger in controller methods (`ACUMOS-625 <https://jira.acumos.org/browse/ACUMOS-625>`_)
* Add artifact type code LG - Log File (`ACUMOS-765 <https://jira.acumos.org/browse/ACUMOS-765>`_)
* Define C_USER table PICTURE column as type LONGBLOB (`ACUMOS-888 <https://jira.acumos.org/browse/ACUMOS-888>`_)
* Requires database schema version 1.15

Version 1.15.0, 6 April 2018
----------------------------

* Move the acccess-type and validation-status attributes from Solution to Solution Revision entity (`ACUMOS-196 <https://jira.acumos.org/browse/ACUMOS-196>`_)
* Revise field labels in site_config table entry (`ACUMOS-346 <https://jira.acumos.org/browse/ACUMOS-346>`_)
* Add unique constraints for name columns; e.g., role name (`ACUMOS-435 <https://jira.acumos.org/browse/ACUMOS-435>`_)
* Add fields to site_config table entry (`ACUMOS-486 <https://jira.acumos.org/browse/ACUMOS-486>`_)
* Search solutions returns unexpectedly few results (`ACUMOS-529 <https://jira.acumos.org/browse/ACUMOS-529>`_)
* User email attribute should not accept null (`ACUMOS-603 <https://jira.acumos.org/browse/ACUMOS-603>`_)
* Write details about security-related events to the audit log (`ACUMOS-618 <https://jira.acumos.org/browse/ACUMOS-618>`_)
* Check user (in)active status in login and change password methods (`ACUMOS-639 <https://jira.acumos.org/browse/ACUMOS-639>`_)
* Define admin user with well-known username and password for all-in-one install (`ACUMOS-388 <https://jira.acumos.org/browse/ACUMOS-388>`_)
* Requires database schema version 1.15

Version 1.14.5, 6 June 2018
---------------------------

* Add toolkit type codes CO, SP (`ACUMOS-1013 <https://jira.acumos.org/browse/ACUMOS-1013>`_)
* Requires database schema version 1.14

Version 1.14.4, 3 May 2018
--------------------------

* Add artifact type code LG - Log File (`ACUMOS-765 <https://jira.acumos.org/browse/ACUMOS-765>`_)
* Requires database schema version 1.14

Version 1.14.3, 26 March 2018
-----------------------------

* Search solutions returns unexpectedly few results (`ACUMOS-529 <https://jira.acumos.org/browse/ACUMOS-529>`_)
* Requires database schema version 1.14

Version 1.14.2, 15 March 2018
-----------------------------

* Refactor code-name value sets to use properties (`ACUMOS-376 <https://jira.acumos.org/browse/ACUMOS-376>`_)
* Add Swagger annotations to required fields in domain models (`ACUMOS-399 <https://jira.acumos.org/browse/ACUMOS-399>`_)
* Requires database schema version 1.14

Version 1.14.1, 9 March 2018
----------------------------

* Define created-date columns as "DEFAULT 0" to stop Mariadb from setting to now() on update (`ACUMOS-243 <https://jira.acumos.org/browse/ACUMOS-243>`_)
* Cascade solution delete to associated step results (`ACUMOS-328 <https://jira.acumos.org/browse/ACUMOS-328>`_)
* Drop unneeded queries in server-side repository methods (`ACUMOS-344 <https://jira.acumos.org/browse/ACUMOS-344>`_)
* Add copy constructors to all domain POJO classes (`ACUMOS-345 <https://jira.acumos.org/browse/ACUMOS-345>`_)
* Requires database schema version 1.14

Version 1.14.0, 1 March 2018
----------------------------

* Add search-by-date method for federation subscription update (`ACUMOS-61 <https://jira.acumos.org/browse/ACUMOS-61>`_)
* Add peer group, solution group and mapping features for federation access control (`ACUMOS-62 <https://jira.acumos.org/browse/ACUMOS-62>`_)
* Refactor to drop code-name database tables (`ACUMOS-144 <https://jira.acumos.org/browse/ACUMOS-144>`_)
* Add feature for user notification preference and user notification (`ACUMOS-166 <https://jira.acumos.org/browse/ACUMOS-166>`_)
* Assert unique constraint on peer subjectName attribute (`ACUMOS-214 <https://jira.acumos.org/browse/ACUMOS-214>`_)
* Revise peer status code/name value set (`ACUMOS-215 <https://jira.acumos.org/browse/ACUMOS-215>`_)
* Add new toolkit type code for ONAP (`ACUMOS-232 <https://jira.acumos.org/browse/ACUMOS-232>`_)
* Add license headers to sql files (`ACUMOS-275 <https://jira.acumos.org/browse/ACUMOS-275>`_)
* Apply distinct transformer to avoid duplicate search results (`ACUMOS-298 <https://jira.acumos.org/browse/ACUMOS-298>`_)
* Report consistent error message when an item is not found
* Requires database schema version 1.14

Version 1.13.1, 9 February 2018
-------------------------------

* Limit memory use in server JVM to max 512MB
* Correct search method usage of response page wrapper class
* Requires database schema version 1.13

Version 1.13.0, 7 February 2018
-------------------------------

* Add workflow step feature for onboarding and validation result persistence (`ACUMOS-56 <https://jira.acumos.org/browse/ACUMOS-56>`_)
* Add origin attribute to solution and revision entities (`ACUMOS-59 <https://jira.acumos.org/browse/ACUMOS-59>`_)
* Revise search methods to return a page of results
* Revise peer and peer subscription attributes (`ACUMOS-60 <https://jira.acumos.org/browse/ACUMOS-60>`_, `ACUMOS-167 <https://jira.acumos.org/browse/ACUMOS-167>`_)
* Add toolkit type PB - Probe (`ACUMOS-168
  <https://jira.acumos.org/browse/ACUMOS-168>`_)
* Requires database schema version 1.13

Version 1.12.1, 26 January 2018
-------------------------------

* Repair findPortalSolutions endpoint to process multiple values correctly
* Requires database schema version 1.12

Version 1.12.0, 23 January 2018
-------------------------------

* Extend MLPPeerSubscription with required ownerId attribute with user ID
* Extend MLPSolution with optional sourceId attribute with peer ID
* Add alternate client constructor that accepts RestTemplate
* Extend search methods to accept value arrays
* Add two toolkit-type codes, BR and TC
* Add client mock implementation
* Extend enums to have names, not just codes
* Address code-quality issues identified by LF Sonar
* Requires database schema version 1.12

Version 1.11.0, 3 January 2018
------------------------------

* Revise MLPSiteConfig to make userId optional
* Revise MLPThread to add solutionId and revisionId; drop url
* Revise MLPComment to drop url
* Revise MLPPeer to add trustLevel
* Add methods to query for threads and comments using solution and revision IDs
* Requires database schema version 1.11

Version 1.10.2, 20 December 2017
--------------------------------

* Extend MLPSolution with tags and solution web statistics via unidirectional annotations
* Extend the find-solutions method for Portal/Marketplace dynamic search
* Requires database schema version 1.10

Version 1.10.1, 12 December 2017
--------------------------------

* Revert search-solutions method to version of 1.9.0
* New find-solutions method for Portal/Marketplace dynamic search
* Requires database schema version 1.10

Version 1.10.0, 6 December 2017
-------------------------------

* Increase size of details column for solution validation
* Support threads and comments
* Requires database schema version 1.10

Version 1.9.1, 30 November 2017
-------------------------------

* Add method to get rating by key fields solution ID and user ID
* Revise searchSolutions method to accept complex query criteria
* Requires database schema version 1.9

Version 1.9.0, 16 November 2017
-------------------------------

* Add methods to get role count, users-in-role count
* Add methods for bulk update of users in roles
* Add "options" attribute to Peer Subscription
* Requires database schema version 1.9

Version 1.8.0, 9 November 2017
------------------------------

* Add artifact ID to the solution download record
* Add last-download date to the solution web record
* Requires database schema version 1.8

Version 1.7.0, 3 November 2017
------------------------------

* Add support to fetch, create and delete solution deployments
* Add support to fetch, create and delete site configurations
* Add solution web metadata such as featured status
* Change all classes to use package prefix org.acumos
* Revise get-count methods to return long (not CountTransport)
* Revise "RCloud" name to just "R"
* Revise database schema to drop Mysql-specific column types like TINYINT
* Move tests that depend on a deployed instance to the test subproject
* Change default properties to a Derby in-memory database
* Add unit tests for client and server
* Address code-quality issues identified by Sonar
* Requires database schema version 1.7

Version 1.6.1, 18 October 2017
------------------------------

* Repair defect in updateSolutionRating feature
* Revise get-user-notification feature to include viewed status
* Requires database schema version 1.6

Version 1.6.0, 13 October 2017
------------------------------

* Add support for fetching, creating and deleting solution favorites
* Add support for fetching, creating, updating and deleting solution validations
* Add support for fetching, creating and deleting validation sequences
* Store hashes of user passwords using BCrypt algorithm
* Add artifact type codes "BP" and "DS"
* Add model type code "DS"
* Requires database schema version 1.6

Version 1.5.3, 26 September 2017
--------------------------------

* Revise signature of update-password client method
* Add method to get all solutions accessible to specified user
* Rename method to getSolutionAccessUsers (was getSolutionUserAccess)
* Implement server method to fetch role function
* Extend get-user-notification client method to accept page parameter
* Drop unused parameter peerId from several peer-subscription methods
* Use MariaDB client library as JDBC connector
* Requires database schema version 1.5

Version 1.5.2, 20 September 2017
--------------------------------

* Add protobuf as an artifact type with code "PJ"
* Requires database schema version 1.5

Version 1.5.1, 14 September 2017
--------------------------------

* Add update password end point and method
* Add methods to get page of notifications and notification count
* Requires database schema version 1.5

Version 1.5.0, 5 September 2017
-------------------------------

* Change peer entity to have unstructured contact information
* Requires database schema version 1.5

Version 1.4.1, 29 August 2017
-----------------------------

* Add methods to add, drop roles for a user
* Extend user controller to cascade delete to login providers, notifications, roles
* Validate schema on startup
* Requires database schema version 1.4

Version 1.4.0, 23 August 2017
-----------------------------

* Add picture attribute to user entity
* Add statistics for solutions: view count
* Add simple user access control list for solutions
* Fix CD-765, count methods always return zero
* Cache solution download and rating statistics
* Requires database schema version 1.4

Version 1.3.1 update, 15 August 2017
------------------------------------

* Accept valid UUID as ID when creating artifact, solution and other entities
* Requires database schema version 1.3

Version 1.3.1, 9 August 2017
----------------------------

* Add org name attribute to user entity
* Add methods to find solutions by tag, toolkit type
* Extend search methods to select AND/OR conditions
* Use HQL for all queries, no native SQL
* Requires database schema version 1.3

Version 1.3.0, 7 August 2017
----------------------------

* Add solution download feature: get/create/delete items to track downloads and get count
* Add solution rating feature: get/create/update/delete reviews and get average rating
* Add solution tag feature: get/create/delete individual tags, get/add/drop tags on solutions
* Add notification feature: get/create/delete notifications; add/update/drop users as recipients
* Add password-expiration field to user entity
* Match email address when checking login credentials
* Requires database schema version 1.3

Version 1.2.3, 31 July 2017
---------------------------

* Repair client bug in RestPageResponse implementation so iterator returns content
* Repair server-side bug in getSolutionRevisions feature
* Add client methods getHealth(), getVersion(), getRevisionsForArtifact()
* Requires database schema version 1.2

Version 1.2.2, 28 July 2017
---------------------------

* Extend partial-match methods to accept page requests and return paged results
* Stop requiring HTTP authentication on swagger documentation pages
* Requires database schema version 1.2

Version 1.2.1, 27 July 2017
---------------------------

* Add find methods that perform partial matches (like queries)
* Add user web token and social login provider support
* Drop C(r)UD support for artifact type, model type values
* Requires database schema version 1.2

Version 1.2.0, 26 July 2017
---------------------------

* Add entity Peer Subscription and methods for CRUD operations
* Remove collections within models to stop eager fetching of data; e.g., the revisions for a solution
* Revise get-all methods to support pagination: accept max, page and sort parameters
* Add new methods so clients can fetch data lazily; e.g., the revisions for a solution
* Refactor to use Spring repositories instead of custom database query methods
* Requires database schema version 1.2

Version 1.1.3, 21 July 2017
---------------------------

* Repair bugs in client update methods not passing along IDs
* Add methods for CRUD operations on model type; user login.

Version 1.1.2, 18 July 2017
---------------------------

* Extend with Peer and new attributes on Solution.

Version 1.1.1, 5 July 2017
--------------------------

* Extend for solution revisions, which are collections of artifacts.

Version 1.1.0, 30 June 2017
---------------------------

* Adds solution revisions, UUID values as IDs and more.

Version 1.0.0, 15 June 2017
---------------------------

* Supports solutions, artifacts and users.

Versions 1.* require configuration data in an environment variable SPRING_APPLICATION_JSON
with the following structure.  All values in upper case must be replaced::

    SPRING_APPLICATION_JSON: '{
        "server" : {
            "port" : 8000
        },
        "security" : {
            "user" : {
                "name"     : "CLIENT_USERNAME",
                "password" : "CLIENT_PASSWORD"
            }
        },
        "spring" : {
            "database" : {
                "driver" : {
                    "classname" : "org.mariadb.jdbc.Driver"
                }
            },
            "datasource" : {
                "url" : "jdbc:mysql://HOST-NAME.DOMAIN-NAME:3306/DATABASE-NAME?useSSL=false",
                "username" : "DATABASE_USERNAME",
                "password" : "DATABASE_PASSWORD"
            },
            "jpa" : {
                "database-platform" : "org.hibernate.dialect.MySQLDialect",
                "hibernate" : {
                    "ddl-auto" : "validate"
                },
                "show-sql" : false
            }
        }
    }'
