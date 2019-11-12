-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
-- FROM version 3.0 TO version 3.1
-- No database name is set to allow flexible deployment.

DROP TABLE C_RTU_USER_MAP;
DROP TABLE C_RTU_REF_MAP;
DROP TABLE C_RTU_REF;
DROP TABLE C_RIGHT_TO_USE;

CREATE TABLE C_CAT_ROLE_MAP (
  CATALOG_ID CHAR(36) NOT NULL,
  ROLE_ID CHAR(36) NOT NULL,
  PRIMARY KEY (CATALOG_ID, ROLE_ID),
  CONSTRAINT FK_C_CAT_ROLE_MAP_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID),
  CONSTRAINT FK_C_CAT_ROLE_MAP_C_ROLE FOREIGN KEY (ROLE_ID) REFERENCES C_ROLE (ROLE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Record this action in the history
INSERT INTO C_HISTORY (COMMENT, CREATED_DATE) VALUES ('cmn-data-svc-upgrade-3.0-to-3.1-rev1', NOW());
