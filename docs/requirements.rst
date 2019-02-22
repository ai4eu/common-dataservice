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
Common Data Service Requirements
================================

This document presents the abstract data model implemented by the Acumos Common Data Service.
The data model is explained in terms of entities in the system, attributes of the entities,
and relationships among the entities.  These requirements are implemented in a relational
database, but this page does not define table names, column names, data types, lengths, etc.

Implications of Federation
--------------------------

The Acumos system is intended to be federated, meaning multiple systems share information
with each other:

* Multiple systems will be running in different organizations
* Information will be shared selectively across the systems
* A public "root" instance will be used to publish some information
* Users can publish their solutions for use by others.

This has implications for identifiers used in the system, because some must be usable globally.

Entity and Relationship Overview
--------------------------------

Entities in the system are the main items that users create and manipulate, including solutions,
solution revisions, solution artifacts. For the purpose of CDS a user is also an entity, to track
name, credentials and so on.  To name another example, federation peers are also entities.

Solutions and revisions are in a one-to-many relationship; a solution may be considered just a
collection of revisions. Similarly a revision is just a collection of artifacts. Users are in a
many-to-many relationship with most of the other entities in the system.

Entity and Attribute Details
----------------------------

All entities and attributes are listed below, grouped into three sections:

* Simple code-name entities (readonly pairs of values)
* Complex entities
* Relationship (mapping) entities


Enumerated Code-Name Sets
-------------------------

The code-name value sets listed below are the minimum that shall be provided as configuration.
These may be configured differently in a specific installation. The value sets cannot be changed by clients.

Access Type
^^^^^^^^^^^

| OR "Organization"
| PB "Public"
| PR "Private"

Artifact Type
^^^^^^^^^^^^^

| BP "Blueprint File"
| CD "CDUMP File"
| DI "Docker Image"
| DP "Docker Image Pre-dockerized"
| DS "Data Source"
| LG "Log File"
| LI "License"
| MD "Metadata"
| MH "Model H2O"
| MI "Model Image"
| MR "Model R"
| MS "Model Scikit"
| MT "Model Tensorflow"
| PJ "Protobuf File"
| TE "TOSCA Template"
| TG "TOSCA Generator Input File"
| TS "TOSCA Schema"
| TT "TOSCA Translate"

Deployment Status
^^^^^^^^^^^^^^^^^

| DP "Deployed"
| FA "Failed"
| IP "In Progress"
| ST "Started"

Kernel Type
^^^^^^^^^^^

Applies to workbench notebooks.

| PY "Python"
| RR "R"
| JA "Java"
| SC "Scala"

Login Provider
^^^^^^^^^^^^^^

| FB "Facebook"
| GH "GitHub"
| GP "Google Plus"
| LI "LinkedIn"

Message Severity
^^^^^^^^^^^^^^^^

| HI "High"
| ME "Medium"
| LO "Low"

Model Type
^^^^^^^^^^

| CL "Classification"
| DS "Data Sources"
| DT "Data Transformer"
| PR "Prediction"
| RG "Regression"

Notebook Type
^^^^^^^^^^^^^

Applies to workbench notebooks.

| JB "jupyter/base-notebook"
| JM "jupyter/minimal-notebook"
| JR "jupyter/r-notebook"
| JS "jupyter/scipy-notebook"
| JT "jupyter/tensorflow-notebook"
| JD "jupyter/datascience-notebook"
| JP "jupyter/pyspark-notebook"
| JA "jupyter/all-spark-notebook"

Peer Status
^^^^^^^^^^^

| AC "Active"
| DC "Declined"
| IN "Inactive"
| RN "Renounced"
| RQ "Requested"
| UK "Unknown"

Publish Request Status
^^^^^^^^^^^^^^^^^^^^^^

| AP "Approved"
| DC "Declined"
| PE "Pending"
| WD "Withdrawn"

Service Status
^^^^^^^^^^^^^^

Applies to projects, notebooks and pipelines in the workbench.

| AC "Active"
| CO "Completed"
| ER "Error"
| EX "Exception"
| FA "Failed"
| IN "Inactive"
| IP "In progress"

Task Step Status
^^^^^^^^^^^^^^^^

| ST "Started"
| SU "Succeeded"
| FA "Failed"

Task Type
^^^^^^^^^

| OB "Onboarding"
| SV "Security-Verification"

Toolkit Type
^^^^^^^^^^^^

This attribute was intended to characterize the technology used in a model.
Over time this has been used for other purposes, for example to identify special
features of the Design Studio. With experience it also became clear that a single
attribute value is not sufficient to characterize some models.  For these reasons,
the toolit-type code may be removed entirely.

| BR "Data Broker"
| CP "Composite Solution"
| DS "Design Studio"
| H2 "H2O"
| ON "ONAP"
| PB "Probe"
| RC "R"
| SK "Scikit-Learn"
| TF "TensorFlow"
| TC "Training Client"

Verified License
^^^^^^^^^^^^^^^^

| SU "Success"
| FA "Failed"
| IP "In progress"
| UR "Unrequested"

Verified Vulnerability
^^^^^^^^^^^^^^^^^^^^^^

| SU "Success"
| FA "Failed"
| IP "In progress"
| UR "Unrequested"

Entities
--------

The system entities are presented below in alphabetical order.

Catalog
^^^^^^^

A catalog is a collection of solutions to assist with federation.

Attributes:

* Catalog ID
* Access type code
* Name (intended to be globally unique)
* Description
* Origin (the peer that provided it, in case of a mirror)
* Publisher (name)
* URL (the peer that publishes the catalog)


Comment
^^^^^^^

This stores a user comment within a thread of comments.

Attributes:

* Comment ID
* Thread ID
* Parent ID (identifies the comment ID for which this comment is a reply; optional)
* User ID
* Text (the comment content)


Composite Solution
^^^^^^^^^^^^^^^^^^

A composite solution is composed by a user in the Design Studio and consists of other
simple and composite solutions.

Attributes:

* Child solutions


Document
^^^^^^^^

This stores a supplementary document for a revision as provided by a user.

Attributes:

* Document ID
* Name
* Size
* User ID


Notebook
^^^^^^^^

A notebook, part of the workbench, is a virtual computing environment used for literate programming.

Attributes:

* Notebook ID (UUID)
* Notebook type (value from restricted value set Notebook Type)
* Kernel type (value from restricted value set Kernel Type)
* Service status (value from restricted value set Service Status)
* Active status (true/false)
* Name (string)
* Version (string)
* Description (long string)
* Repository URL
* Service URL
* User (ID of creator)

Notebooks are mapped to several other entities in many:many relationships, as documented below.

Notification
^^^^^^^^^^^^

A notification is a message for a user about an event, for example that a solution previously downloaded has been updated.

Attributes:

* Notification ID
* Title (like an email subject)
* Message (like an email body)
* URL (a link)
* Start (earliest date/time when the notification is active)
* End (latest date/time when the notification is active)

Notifications are mapped to users in a many:many relationship.  That relationship must track which notifications have been viewed by the user.


Peer
^^^^

Registered and authorized external instances of the platform that communicate with this instance.
The registration is intended to be controlled by any user with admin roles.
This model is used to support the federated architecture.

Attributes:

* Unique ID for peer
* Site name
* Subject name

     -  For an X.509 certificate.  Must be unique among all peers.

* Site URL(s)

     -   How many interfaces will be required by federation?
     -   For now we are considering 2 types of urls: API url and web url.

* Description
* IsActive
* IsSelf
* Contacts (a pair, one as primary and another as backup)
* Created timestamp
* Modified timestamp


Peer Group
^^^^^^^^^^^

Defines a group that may be assigned to peers to facilitate access control. Only seen locally, not federated.

Attributes:

* Group ID
* Name (must be unique among all peer groups)
* Description (additional textual information about this group)


Pipeline
^^^^^^^^

A pipeline, part of the workbench, is an assembly of runnable components.

Attributes:

* Pipeline ID (UUID)
* Active status (true/false)
* Service status (value from restricted value set Service Status)
* Name (string)
* Version (string)
* Description (long string)
* Repository URL
* Service URL
* User (ID of creator)

Pipelines are mapped to several other entities in many:many relationships, as documented below.


Project
^^^^^^^

A project, part of the workbench, groups notebooks and pipelines.

Attributes:

* Project ID (UUID)
* Active status (true/false)
* Service status (value from restricted value set Service Status)
* Name (string)
* Version (string)
* Description (long string)
* Repository URL
* User (ID of creator)

Projects are mapped to several other entities in many:many relationships, as documented below.


Right to Use
^^^^^^^^^^^^

Grants permissions to use a solution.  Only seen locally, not federated.

Attributes:

* Row ID
* Solution ID
* Boolean indicator whether the RTU applies to the site; i.e., to all users in the Acumos instance.
* List of right-to-use reference IDs.  Each is a GUID that is generated by an external system.


Role for Users
^^^^^^^^^^^^^^

Roles are named like "designer" or "administrator" and are used to assign privilege levels to users,
in terms of the functions those users may perform; i.e., the system features they are authorized to use.

Attributes:

* Unique ID
* Name (must be unique among all roles)
* Active (yes/no)


Role Function
^^^^^^^^^^^^^

A role function is a name for an action that may be performed by a user within a specific role, such as createModel.
The software system may grant access to specific features based on whether the user role function is assigned to the
user making a request. Role functions are related to roles in a many:mnany relationship.
So for example, a "designer" role may have many functions such as "read", "create", "update" and "delete" while
an "operator" role may have only the function "read".

Attributes:

* Unique ID
* Role ID
* Function name (must be unique among all role functions)


Site Configuration
^^^^^^^^^^^^^^^^^^

This stores administrative details for management of the system.

Attributes:

* Config key
* Config value, which is required to be a JSON block
* User ID, the last person who updated the entry; optional to allow creation of initial row without a user ID
* Created timestamp
* Modified timestamp


Site Content
^^^^^^^^^^^^

This stores data such as plain text, HTML or images to show on the web site.
Provided to store content that was previously held in a content management system (CMS) database.

Attributes:

* Content key
* Content value, which is a binary long object (BLOB)
* Mime type, a description of the content
* Created timestamp
* Modified timestamp


Solution
^^^^^^^^

* A solution is on-boarded by a client library or via the web
* A solution consists of a collection of solution revisions; which in turn consist of artifacts.
* May be generated by the system from an on-boarded trained statistical model.
* The primary element of the Catalog that is displayed to users
* Supports versioning - a solution may have many solution revisions

The metadata listed here describes the solution as a whole.

Attributes:

* Unique ID for system use
* Name (as chosen by user. This name is not required to be unique)
* Description (free-text description of what the solution does)
* User ID (creator of the solution, automatically assigned to the person who uploaded the machine-learning model artifact)
* List of authorized users (to facilitate review and collaborative work with a team)
* Provider (name of organization that sponsored and/or supports the solution)
* Peer (ID of Acumos peer where the solution was first on-boarded)
* Toolkit aka implementation technology code (underlying ML technology; e.g., Scikit, RCloud, Composite solution)
* Model type code (underlying ML category; valid values include CLASSIFICATION and PREDICTION)
* Proposed attribute: System ID where created (supports federation, exchange of solutions among peer systems)
* Create time (time when the solution was created; i.e., upload time)
* Modification time (the time when the solution was updated)
* Usage statistics: number of views, number of downloads, number of ratings, average rating (may be derived from other entities)


Solution Artifact
^^^^^^^^^^^^^^^^^

* An artifact is a component of a solution revision.
* Example: a Docker image with one micro service that exposes one trained statistical model
* Example: a TOSCA model for deploying a solution revision
* Example: a trained statistical model
* The output of a machine-learning algorithm created by a data scientist using training data and on-boarded to the system; e.g., Python pickle or R binary object

Attributes:

*    The file image, treated as an opaque byte stream

     -  Very likely to be stored as a binary file in a Nexus repository, so the URL to the file can be stored as an attribute.

*    Unique ID for system use, a generated UUID to be globally unique

*    Type

     -   An artifact type can be either a statistical model, metadata, docker image or TOSCA file.

*    Descriptive name

     -   Chosen by user. This name may not be unique.

*    URL

     -   Using this, the artifact image can be retrieved from a Nexus repository

*    Owner ID

     -    The person's ID who created the artifact and is the owner of it.

*    Created timestamp

     -   Date and time when this row was created

*    Modified timestamp

     -   Date and time when this row was last modified

*    Description

     -   Describes what the artifact does

*    Size

     -   Represents the size of the artifact in KB

Below are detailed descriptions of some artifact types:

Trained statistical model

A trained statistical model is the output of a machine-learning algorithm.  The model is an opaque byte array, probably stored as a binary file in a Nexus repository.

Docker Image

A docker image is generated by the system, containing a microservice which in turn makes the trained statistical model usable.
TOSCA Model

A TOSCA model is used to deploy a solution to a specific hosted environment; e.g., Rackspace. Multiple TOSCA models can be defined for each solution. TOSCA models may be shared with other users.


Solution Deployment
^^^^^^^^^^^^^^^^^^^

This captures information about deployment of a specific revision of a solution to a target environment.

Attributes:

* Deployment ID - generated
* Solution ID - required
* Revision ID - required
* User ID - required
* Target deployment environment
* Deployment status. This uses the Deployment Status Code defined above.


Solution Group
^^^^^^^^^^^^^^

Defines a group that gathers solutions to facilitate access control. Only seen locally, not federated.

Attributes:

* Group ID
* Name (unique among all solution groups)
* Description (additional textual information about this group)


Solution Revision
^^^^^^^^^^^^^^^^^

* A revision is a particular version of a solution
* Represents a collection of artifacts that implement the solution in that version
* E.g., revision "1.0-alpha" is a consistent set of artifacts

A solution revision consists of a collection of solution artifacts. The metadata listed here describes the collection.

Attributes:

* Unique Revision ID

     -  A globally unique ID for this specific revision

* Solution ID

     -   Represents the solution, allows multiple revisions per solution

* Access type code

     - This refers to the visibility of the revision. It uses values defined by Access Type Code (above).

* Validation status code

     - This refers to the validation result for the revision. It uses values defined by Validation Status Code (above).

* Version

     -   Chosen by the user. This serves as the solution's child revision entry identifier. This needs to be unique for any solution revision within the same solution.

* Onboarded timestamp

     -   Date and time when this revision of the solution was on-boarded

* Created timestamp

     -   Date and time when this row was created

* Modified timestamp

     -   Date and time when this row was last modified

* Creator

     -   The person who created the revision of the solution (reference to the user table)

Task
^^^^

This tracks the status of processing a request made by some actor or process on an Acumos instance.
For example, a user requests on-boarding of a model.  A task carries some identification details
and carries 0..n step-result records that carry details of individual steps. A task does not have
a free-text result attribute; that is in the step result record.

Attributes:

* Task ID - generated

     -   A unique record identifier

* Name - required

     -   A descriptive name to benefit the user

* Status Code - required

     -   Represents the state of the task. Available values include "started", "succeeded" and "failed".

* Task type code - required

     -   Represents the type of action being tracked, for example on-boarding a ML model or verifying a ML model.

* Tracking ID - optional

     -  This represents a workflow execution instance. For example it may represent on-boarding of a ML model.

* Solution ID - optional
* Revision ID - optional

* User ID - required

     -  The user who made the request


Task Step Result
^^^^^^^^^^^^^^^^

This tracks the status of a single step within a task. For example, the on-boarding feature can store information
about the status and outcome of every step during the task of on-boarding a model.

Attributes:

* Step Result ID - generated
* Name - required

     -   Represents the specific step involved in the workflow. For example in an on-boarding workflow, the step name could be "Solution ID creation".

* Status Code - required

     -   Represents the state of the step. Available values include "started", "succeeded" and "failed".

* Result - optional

     -    Text information for a workflow step progress, for debugging purposes.

* Start Date - required

     -   Date/time when a step starts

* End Date - optional

     -   Date/time when a step ends


User Notification Preference
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This stores the delivery mechanism and message priority preferences by the user for receiving notifications

Attributes:

* User ID (notification recipient)
* Notification type (email/text/web)
* Message Severity code. This uses the Message Severity Code value set defined above.


Tag for Solution
^^^^^^^^^^^^^^^^

Keywords applied to solutions. Attributes:

* Tag name

Mapped many:many to solutions.


Thread
^^^^^^

This stores the general topic of discussion to which a comment is associated

Attributes:

* Thread ID
* Thread Title (optional)
* Solution ID
* Revision ID


User
^^^^

* Authorized users of the system must be recognized and authenticated.
* May be authenticated using a social identity provider; e.g., LinkedIn

Attributes:

* Unique ID for system use
* User's organization name
* Login name (must be unique among all users)
* Login password
* Password expiration date/time
* First, middle, last names
* Email address (must be unique among all users)
* Phone number(s)
* Profile picture (subject to some size limit)
* Authentication mechanism (possibly Facebook, Github, Linked-in)
* Authentication token

     -   For example, JSON Web Token, which should be short (hundreds of bytes) but may be large (thousand of bytes). This will be used to Secure APIs after logging in.

* Levels of access

     -   For example, users might be modelers (data scientists) who upload models; integrators who build solutions in the design studio; or consumers who download and run solutions only.
     -   As one possible implementation, the EP-SDK represents privileges using roles and role functions.  A user is assigned one or more roles.  Each role is associated with one or more functions.  A function is a specific feature in the system. Still TBD if an external authentication system will deliver privileges like roles, or if all must be stored locally.

Users are related to user roles in a 1:many relationship; in other words, multiple roles may be assigned to a single user.


User Social Login Provider Account
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Describes the details of a user's account at a social identity provider.  One user may use multiple login providers; e.g., Facebook, Google, LinkedIn, Github; further a user may use multiple accounts with a single provider.

Attributes:

* User ID
* Login provider code
* User's login name at the provider
* Rank (which provider to prefer)
* Display name
* Profile URL
* Image URL
* Secret
* Access token
* Refresh token
* Expiration time


Entity Mapping Relationships
----------------------------

This section documents the relationships among entities that are managed in separate mapping tables.
The extra tables allow many-many relationships using entity ID values.
These standalone relationship tables do not define new entities, but may store information about the
relationship, such as the time when it was created.

Please note this section does not document simple relationships managed within entities, which includes
one-to-one and many-to-one relationships.  For example, every comment has the ID of the containing thread,
so a separate table is not required to manage that relationship.

Relationship Catalog - Solution
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures solution membership in a catalog.

Attributes:

* Catalog ID
* Solution ID


Relationship Revision - Artifact
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the many:many relationship of an artifact to a revision.
A separate mapping entity is required here.

Attributes:

* Revision ID
* Artifact ID


Relationship Right To Use - Reference ID
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This maps a right-to-use record to an ID generated by an external system. The remote system tracks right-to-use details.

Attributes:

* Right to Use ID
* Reference ID (a GUID)


Relationship Right To Use - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This represents a right-to-use grant on a solution for a specific user. For example, two users may be entitled to deploy a solution.

Attributes:

* Right to Use ID
* User ID (a GUID)


Relationship Solution - Solution for Composite Solutions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a parent-child relationship of a composite solution; i.e., a solution that reuses other solutions.

Attributes:

* Parent solution ID
* Child solution ID


Relationship Solution - Revision - Task for Validation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This relationship stores details of validating a solution revision against specific criteria such as a license check.

Attributes:

* Solution ID
* Revision ID
* Task ID (validation job identifier)
* Validation type
* Validation status (pass, fail, ..)
* Details of validation results


Relationship Solution - Tag
^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the assignment of tags to solutions.

Attributes:

* Solution ID
* Tag value


Relationship Solution - User for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This represents an access grant on a solution for a specific user. For example, a solution may be shared by a solution creator with a reviewer.

Attributes:

* Solution ID
* User ID


Relationship Solution - Artifact - User for Download
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a download of a solution artifact by a user.

Attributes:

* Solution ID
* Artifact ID
* User ID
* Download date and time

Descriptive statistics are derived from individual records; for example total number of downloads and last download time. The statistics must be cached and updated on changes to reduce the time needed to fetch information.  For example, update the cached number of downloads and last-download time each time an artifact is downloaded.


Relationship Solution - User for Favorite
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures an action by a user to specify that a solution is a favorite

Attributes:

* Solution ID
* User ID


Relationship Solution - User for Rating
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a rating, text review and other feedback contributed by users about a solution. In keeping with other application stores, the rating is modeled at the solution level (not revision).

Attributes:

* Solution ID
* User ID

     -  Identifier of the user who rated that solution through the web user interface.

* Rating

     -  A numerical rating scale, for example 1-5

* Text of review
* Created timestamp

     -   The date and time when the solution rating was created by the user

* Modified timestamp

     -   The date and time when the rating gets updated

Descriptive statistics are derived from individual solution ratings; for example average rating. The statistics may be cached and updated on change to reduce the time needed to fetch information about a solution. For example, update the cached number of reviews and average rating each time a solution is reviewed.


Relationship User - Role
^^^^^^^^^^^^^^^^^^^^^^^^

This captures the assignment of a role to a user.

Attributes:

* User ID
* Role ID


Relationship Peer - Subscription
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Describes which solution(s) available on a remote peer should be tracked and/or replicated.

Attributes:

* Subscription ID
* Peer ID
* Selector

     - What solutions should be selected

* Refresh interval

     -  How often to poll the remote system

* Create timestamp
* Modified timestamp


Relationship Notification - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the relationship between a notification and a user; i.e., specifies which users should see which notifications.

Attributes:

* Notification ID
* User ID
* Viewed date and time


Relationship Peer - Peer Group for Membership
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents the membership of peers in a peer access group.

Attributes:

* Peer Group ID
* Peer ID
* Create timestamp


Relationship Solution - Solution Group for Membership
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents the membership of solutions in a solution access group.

Attributes:

* Solution Group ID
* Solution ID
* Create timestamp


Relationship Solution Group - Peer Group for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents granting of access to all solutions in the solution group by peers in the peer group.

Attributes:

* Solution Group ID
* Peer Group ID
* Active flag (yes/no)
* Create timestamp


Relationship Peer Group - Peer Group for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents granting of access to resource peers for principal peers.

Attributes:

* Principal peer group ID
* Resource peer group ID
* Create timestamp


Relationship Project - Notebook
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The workbench Project entity is in a many-to-many relationship with notebooks.

Attributes:

* Project ID
* Notebook ID


Relationship Project - Pipeline
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The workbench Project entity is in a many-to-many relationship with pipelines.

Attributes:

* Project ID
* Pipeline ID


Relationship Project - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The workbench Project entity is in a many-to-many relationship with users.

Attributes:

* Project ID
* User ID


Relationship Notebook - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The workbench Notebook entity is in a many-to-many relationship with users.

Attributes:

* Notebook ID
* User ID


Relationship Pipeline - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The workbench Pipeline entity is in a many-to-many relationship with users.

Attributes:

* Pipeline ID
* User ID


Required Operations
-------------------

This section lists the required operations that shall be supported by the Common Data Micro Service. The list serves as a requirements document for both the client and server, in support of the entities and attributes identified above.

Metadata operations
^^^^^^^^^^^^^^^^^^^

These read-only actions provide access to value sets that may change over time:

* Get access types
* Get artifact types
* Get login providers
* Get model types
* Get toolkit types
* Get validation status values

CRUD operations
^^^^^^^^^^^^^^^

To keep the rest of this document brief, the standard "CRUD" operation definitions are repeated here:

* (C)reate an entity; a REST POST operation that requires new content. If the entity ID field is not supplied, this operation generates a unique ID; otherwise the supplied ID is used.
* (R)etrieve an enity; a REST GET operation that requires the entity ID
* (U)pdate an entity; a REST PUT operation that requires the entity ID and the new content
* (D)elete an entity; a REST DELETE operation that requires the entity ID

Operations on artifacts
^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of artifacts from the complete set, optionally sorted on one or more attributes
* Get a page of artifacts using partial ("like") value match on the name and description attributes, optionally sorted on one or more attributes
* Search for artifacts using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
* Get all the artifacts for a particular solution revision
* Add an artifact to a solution revision
* Delete an artifact from a solution revision.

Operations on catalogs
^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply plus the following:

* Get the collection of catalogs
* Get a page of solutions in the catalog, optionally sorted on one or more attributes


Operations on solutions
^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of solutions from the complete set, optionally sorted on one or more attributes
* Get a page of solutions using partial ("like") value match on the name and description attributes, optionally sorted on one or more attributes
* Search for solutions using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
* Get a page of solutions that use a specified toolkit type
* Tags

  - Get all tags assigned to a solution
  - Add a tag to a solution
  - Drop a tag from a solution
  - Get a page of solutions that have a specified tag

*  Authorized users

   - Get all authorized users assigned to a solution
   - Add a user to a solution
   - Drop a user from a solution

Operations on solution revisions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all revisions for a specific solution
* Get all revisions for multiple solutions
* Get a solution revision for a particular solution id and revision id.
* Get all the solution revisions for a particular artifact.

(Also see operations on artifacts, which are associated with solution revisions)

Operations on solution downloads
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* Standard CRUD operations plus the following:
* Get all downloads for a specific solution
* Get the count of downloads for a specific solution

Operations on solution ratings
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all ratings for a specific solution
* Get the average rating for a specific solution

Operations on tags
^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.

Operations on users
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of users from the complete set, optionally sorted on one or more attributes
* Get a page of users using partial ("like") value match on the first, middle, last or login name attributes, optionally sorted on one or more attributes
* Search for users using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
* Check user credentials - the login operation. Match login name/email address as user, password as password. Returns user object if found and active; signals bad request if user is not found, user is not active or password does not match.
* Change user password -  find user by ID and update password if user is active and old password matches. Signals bad request if user is not found, user is not active or old password does not match.

Operations on user login providers
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all login providers for the specified user

Operations on roles
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all roles for the specified user
* Search for roles using exact value match on one or more attributes

Operations on role functions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all role functions for the specified role

Operations on peers
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of peers from the complete set, optionally sorted on one or more attributes
* Search for peers using exact value match on one or more attributes

Operations on peer subscriptions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of peer subscriptions from the complete set, optionally sorted on one or more attributes

Operations on notifications
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Add a user as a notification recipient
* Update that a user has viewed a notification
* Drop a user as a notification recipient
* Get all notifications for a user

Operations on workflow step result
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.
