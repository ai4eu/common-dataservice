-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
-- FROM version 1.18.x TO version 2.0.x.
-- No database name is set to allow flexible deployment.

DROP TABLE C_SOLUTION_VALIDATION;
DROP TABLE C_SOL_VAL_SEQ;
ALTER TABLE C_PEER DROP COLUMN VALIDATION_STATUS_CD;
ALTER TABLE C_SOLUTION_REV DROP COLUMN VALIDATION_STATUS_CD;
ALTER TABLE C_SOLUTION ADD COLUMN VIEW_COUNT INT;
ALTER TABLE C_SOLUTION ADD COLUMN DOWNLOAD_COUNT INT;
ALTER TABLE C_SOLUTION ADD COLUMN LAST_DOWNLOAD TIMESTAMP NULL DEFAULT 0;
ALTER TABLE C_SOLUTION ADD COLUMN RATING_AVG_TENTHS INT;
ALTER TABLE C_SOLUTION ADD COLUMN RATING_COUNT INT;
ALTER TABLE C_SOLUTION ADD COLUMN FEATURED_YN CHAR(1);
UPDATE C_SOLUTION AS s
  INNER JOIN C_SOLUTION_WEB AS w 
  ON s.SOLUTION_ID = w.SOLUTION_ID
  SET s.VIEW_COUNT = w.VIEW_COUNT, 
      s.DOWNLOAD_COUNT = w.DOWNLOAD_COUNT,
      s.LAST_DOWNLOAD = w.LAST_DOWNLOAD,
      s.RATING_AVG_TENTHS = w.RATING_AVG_TENTHS,
      s.RATING_COUNT = w.RATING_COUNT,
      s.FEATURED_YN = w.FEATURED_YN;
DROP TABLE C_SOLUTION_WEB;
ALTER TABLE C_USER MODIFY COLUMN LOGIN_PASS_EXPIRE_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN LAST_LOGIN_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN LOGIN_FAIL_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN VERIFY_EXPIRE_DATE TIMESTAMP;
ALTER TABLE C_NOTIFICATION MODIFY COLUMN START_DATE TIMESTAMP;
ALTER TABLE C_NOTIFICATION MODIFY COLUMN END_DATE TIMESTAMP;
ALTER TABLE C_NOTIF_USER_MAP MODIFY COLUMN VIEWED_DATE TIMESTAMP;
