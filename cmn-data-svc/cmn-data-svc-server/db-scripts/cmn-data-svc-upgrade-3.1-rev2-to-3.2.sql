-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
-- FROM version 3.1-rev2 TO version 3.2
-- No database name is set to allow flexible deployment.

-- Reference to external resource
CREATE TABLE C_HYPERLINK (
  HYPERLINK_ID CHAR(36) NOT NULL,
  PRIMARY KEY (HYPERLINK_ID),
  NAME VARCHAR(100) NOT NULL,
  URI VARCHAR(512) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution_rev to hyperlink requires a map (join) table
CREATE TABLE C_SOL_REV_HYPERLINK_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  HYPERLINK_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, HYPERLINK_ID),
  CONSTRAINT C_SOL_REV_HYPERLINK_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_REV_HYPERLINK_MAP_C_HYPERLINK    FOREIGN KEY (HYPERLINK_ID) REFERENCES C_HYPERLINK (HYPERLINK_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

-- Many:many mapping of source_solution_rev to target_solution_rev requires a map (join) table
CREATE TABLE C_SOURCE_SOL_REV_TARGET_SOL_REV_MAP (
  SOURCE_ID CHAR(36) NOT NULL,
  TARGET_ID CHAR(36) NOT NULL,
  PRIMARY KEY (SOURCE_ID, TARGET_ID),
  CONSTRAINT C_SOURCE_SOL_REV_TARGET_SOL_REV_MAP_C_SOLUTION_REV FOREIGN KEY (SOURCE_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOURCE_SOL_REV_TARGET_SOL_REV_MAP_C_SOLUTION_REV FOREIGN KEY (TARGET_ID) REFERENCES C_SOLUTION_REV (REVISION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Record this action in the history
INSERT INTO C_HISTORY (COMMENT, CREATED_DATE) VALUES ('cmn-data-svc-upgrade-3.1-rev2-to-3.2-rev1', NOW());
