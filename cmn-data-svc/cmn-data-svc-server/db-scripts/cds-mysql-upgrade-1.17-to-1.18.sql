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
-- FROM version 1.17.x TO version 1.18.x.
-- No database name is set to allow flexible deployment.

-- 1
CREATE TABLE C_USER_TAG_MAP (
  USER_ID CHAR(36) NOT NULL,
  TAG VARCHAR(32) NOT NULL,
  PRIMARY KEY (USER_ID, TAG),
  CONSTRAINT C_USER_TAG_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_USER_TAG_MAP_C_SOL_TAG FOREIGN KEY (TAG) REFERENCES C_SOLUTION_TAG (TAG)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 2
-- Tracks publish-to-public workflow requests
CREATE TABLE C_PUBLISH_REQUEST (
  REQUEST_ID INT PRIMARY KEY AUTO_INCREMENT,
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  REQ_USER_ID CHAR(36) NOT NULL,
  RVW_USER_ID CHAR(36),
  STATUS_CD CHAR(2) NOT NULL,
  COMMENT VARCHAR(8192),
  CONSTRAINT C_PUB_REQ_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_PUB_REQ_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_PUB_REQ_REQ_C_USER FOREIGN KEY (REQ_USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_PUB_REQ_APP_C_USER FOREIGN KEY (RVW_USER_ID) REFERENCES C_USER (USER_ID),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 3 
ALTER TABLE C_PEER_SUB CHANGE PROCESSED_DATE PROCESSED_DATE TIMESTAMP NULL DEFAULT 0;
-- 4 
ALTER TABLE C_USER_LOGIN_PROVIDER CHANGE EXPIRE_TIME EXPIRE_TIME TIMESTAMP NULL DEFAULT 0;
-- 5
ALTER TABLE C_SOLUTION_WEB CHANGE LAST_DOWNLOAD LAST_DOWNLOAD TIMESTAMP NULL DEFAULT 0;
-- 6
INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN, CREATED_DATE) VALUES ('9d961018-5464-5b0e-a9c2-11dcdfdb67a0', 'Publisher', 'Y', CURRENT_TIMESTAMP());
