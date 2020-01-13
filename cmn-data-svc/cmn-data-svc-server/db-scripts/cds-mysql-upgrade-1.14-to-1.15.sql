-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
-- ===================================================================================
-- This Acumos software file is distributed by AT&T and Tech Mahindra
-- under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- This file is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ===============LICENSE_END=========================================================

-- Script to upgrade database used by the Common Data Service
-- FROM version 1.14.x TO version 1.15.x.
-- No database name is set to allow flexible deployment.

-- WARNING!!  THIS SCRIPT ADDS UNIQUE CONSTRAINTS TO C_ROLE, C_PEER_GROUP and C_SOLUTION_GROUP. 
-- IF ANY OF THE FOLLOWING SELECTS RETURNS A RECORD THEN THE UNIQUE CONSTRAINT CREATION WILL FAIL, 
-- FAILING THE SCRIPT. IN THAT CASE MODIFY THE RECORDS IN THE DATABASE WITH DUPLICATE ROLE NAME
-- AND/OR PEER GROUP NAME AND/OR SOLUTION GROUP NAME UNTIL THE NAMES ARE UNIQUE, THEN RERUN.
-- Also notice that if the select query on C_USER returns more than 1 record, then update
-- the emails of all those records to be distinct non-null strings so that addition of
-- non-null constraint on the unique EMAIL field during upgrade succeeds.    
-- 0
SELECT NAME, count(*) AS c FROM C_ROLE           GROUP BY NAME HAVING c > 1;
SELECT NAME, count(*) AS c FROM C_PEER_GROUP     GROUP BY NAME HAVING c > 1;
SELECT NAME, count(*) AS c FROM C_SOLUTION_GROUP GROUP BY NAME HAVING c > 1;
SELECT * FROM C_USER WHERE EMAIL IS NULL; 
-- 1
ALTER TABLE C_SOLUTION_REV
  ADD COLUMN ACCESS_TYPE_CD CHAR(2) NOT NULL DEFAULT 'PR';
-- 2
UPDATE C_SOLUTION_REV REV
  SET ACCESS_TYPE_CD = CASE
  WHEN (SELECT COUNT(*) FROM C_SOLUTION SOL
                        WHERE SOL.SOLUTION_ID = REV.SOLUTION_ID AND SOL.ACCESS_TYPE_CD IS NOT NULL) > 0
  THEN (SELECT ACCESS_TYPE_CD FROM C_SOLUTION SOL
                        WHERE SOL.SOLUTION_ID = REV.SOLUTION_ID)
  ELSE 'PR'
END;
-- 3
ALTER TABLE C_SOLUTION
  DROP COLUMN ACCESS_TYPE_CD;
-- 4
ALTER TABLE C_SOLUTION_REV
  ADD COLUMN VALIDATION_STATUS_CD CHAR(2) NOT NULL DEFAULT 'NV';
-- 5
UPDATE C_SOLUTION_REV REV
  SET VALIDATION_STATUS_CD = CASE
  WHEN (SELECT COUNT(*) FROM C_SOLUTION SOL
                        WHERE SOL.SOLUTION_ID = REV.SOLUTION_ID AND SOL.VALIDATION_STATUS_CD IS NOT NULL) > 0
  THEN (SELECT VALIDATION_STATUS_CD FROM C_SOLUTION SOL
                        WHERE SOL.SOLUTION_ID = REV.SOLUTION_ID)
  ELSE 'NV'
END;
-- 6
ALTER TABLE C_SOLUTION
  DROP COLUMN VALIDATION_STATUS_CD;
-- 7
UPDATE C_SITE_CONFIG set CONFIG_VAL = 
  '{"fields":[{"type":"text","name":"siteInstanceName","label":"Site Instance Name","required":"true","data":"Acumos"}, {"type":"file","name":"headerLogo","label":"Header Logo","data":{"lastModified":1510831880727,"lastModifiedDate":"2017-11-16T11:31:20.727Z","name":"acumos_logo_white.png","size":3657,"type":"image/png"}},{"type":"file","name":"footerLogo","label":"Footer Logo","data":{"lastModified":1510831874776,"lastModifiedDate":"2017-11-16T11:31:14.776Z","name":"footer_logo.png","size":3127,"type":"image/png"}},{"type":"heading","name":"ConnectionConfig","label":"Connection Configuration","required":"true","subFields":[{"type":"text","name":"socketTimeout","label":"Socket Timeout","required":"true","data":"300"},{"type":"text","name":"connectionTimeout","label":"Connection Timeout","required":"true","data":"10"}]},{"type":"select","name":"enableOnBoarding","label":"Enable On-Boarding","options":[{"name":"Enabled"},{"name":"Disabled"}],"required":true,"data":{"name":"Enabled"}},{"type":"textarea","name":"validationText","label":"Model Validation Keyword Scan Entries (CSV)","required":"false","data":"test"},{"type":"select","name":"EnableDCAE","label":"Enable DCAE","options":[{"name":"Enabled"},{"name":"Disabled"}],"required":true,"data":{"name":"Enabled"}}]}'
  WHERE CONFIG_KEY='site_config';
-- 8
CREATE UNIQUE INDEX C_ROLE_C_NAME ON C_ROLE (NAME);
-- 9
CREATE UNIQUE INDEX C_PEER_GROUP_C_NAME ON C_PEER_GROUP (NAME);
-- 10
CREATE UNIQUE INDEX C_SOLUTION_GROUP_C_NAME ON C_SOLUTION_GROUP (NAME);
-- 11
ALTER TABLE C_USER MODIFY COLUMN EMAIL VARCHAR(100) NOT NULL;
-- 12
ALTER TABLE C_USER MODIFY COLUMN PICTURE LONGBLOB;
-- 13
CREATE TABLE C_HISTORY (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  COMMENT VARCHAR(100) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 14
INSERT INTO C_HISTORY (COMMENT) VALUES ('upgrade-1.14-to-1.15');
