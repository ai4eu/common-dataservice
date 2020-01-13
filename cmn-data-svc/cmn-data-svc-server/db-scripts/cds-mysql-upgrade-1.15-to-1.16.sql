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
-- FROM version 1.15.x TO version 1.16.x.
-- No database name is set to allow flexible deployment.

-- 1
ALTER TABLE C_SOLUTION DROP COLUMN PROVIDER;
-- 2
ALTER TABLE C_SOLUTION ADD COLUMN PICTURE LONGBLOB;
-- 3
ALTER TABLE C_SOLUTION CHANGE OWNER_ID USER_ID CHAR(36) NOT NULL;
-- 4
ALTER TABLE C_ARTIFACT CHANGE OWNER_ID USER_ID CHAR(36) NOT NULL;
-- 5
ALTER TABLE C_PEER_SUB CHANGE OWNER_ID USER_ID CHAR(36) NOT NULL;
-- 6
ALTER TABLE C_SOLUTION_REV CHANGE OWNER_ID USER_ID CHAR(36) NOT NULL;
-- 7
ALTER TABLE C_SOLUTION_REV ADD COLUMN AUTHORS VARCHAR(1024);
-- 8
ALTER TABLE C_SOLUTION_REV ADD COLUMN PUBLISHER VARCHAR(64);
-- 9
ALTER TABLE C_USER ADD COLUMN LOGIN_FAIL_COUNT SMALLINT NULL;
-- 10
ALTER TABLE C_USER ADD COLUMN LOGIN_FAIL_DATE DATETIME NULL DEFAULT NULL;
-- 11
ALTER TABLE C_USER ADD COLUMN API_TOKEN VARCHAR(64);
-- 12
ALTER TABLE C_USER ADD COLUMN VERIFY_TOKEN_HASH VARCHAR(64);
-- 13
ALTER TABLE C_USER ADD COLUMN VERIFY_EXPIRE_DATE DATETIME NULL DEFAULT NULL;
-- 14
CREATE TABLE C_REVISION_DESC (
  REVISION_ID CHAR(36) NOT NULL,
  ACCESS_TYPE_CD CHAR(2) NOT NULL,
  DESCRIPTION LONGTEXT NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (REVISION_ID, ACCESS_TYPE_CD),
  CONSTRAINT C_REV_DESC_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
