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
-- FROM version 1.16.x TO version 1.17.x.
-- No database name is set to allow flexible deployment.

-- 1
CREATE TABLE C_DOCUMENT (
  DOCUMENT_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  URI VARCHAR(512) NOT NULL,
  VERSION VARCHAR(25),
  SIZE INT NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_DOCUMENT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 2
CREATE TABLE C_SOL_REV_DOC_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  ACCESS_TYPE_CD CHAR(2) NOT NULL,
  DOCUMENT_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, ACCESS_TYPE_CD, DOCUMENT_ID),
  CONSTRAINT C_REV_DOC_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_REV_DOC_MAP_C_REV_DOC      FOREIGN KEY (DOCUMENT_ID) REFERENCES C_DOCUMENT (DOCUMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
