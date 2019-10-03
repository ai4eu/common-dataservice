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
-- FROM version 2.2 TO version 3.0
-- No database name is set to allow flexible deployment.

CREATE TABLE C_LICENSE_PROFILE_TEMPLATE (
  TEMPLATE_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  TEMPLATE_NAME VARCHAR(50) NOT NULL,
  TEMPLATE VARCHAR(8192) NOT NULL CHECK (JSON_VALID(TEMPLATE)),
  PRIORITY INT NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_LICENSE_PROFILE_TEMPLATE_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- This requires an admin user; change the IDs as needed
INSERT INTO C_LICENSE_PROFILE_TEMPLATE (TEMPLATE_NAME, PRIORITY, USER_ID, CREATED_DATE, MODIFIED_DATE, TEMPLATE) VALUES
 ('Apache-2.0', 0, '12345678-abcd-90ab-cdef-1234567890ab', NOW(), NOW(), '{ "keyword": "Apache-2.0", "licenseName": "Apache License 2.0", "copyright": { "year": 2019, "company": "Company A", "suffix": "All Rights Reserved" }, "softwareType": "Machine Learning Model", "companyName": "Company A", "contact": { "name": "Company A Team Member", "URL": "http://companya.com", "email": "support@companya.com" }}'),
 ('Vendor-A-OSS', 1, '12345678-abcd-90ab-cdef-1234567890ab', NOW(), NOW(), '{ "keyword": "Vendor-A-OSS", "licenseName": "Vendor A Open Source Software License", "copyright": { "year": 2019, "company": "Vendor A", "suffix": "All Rights Reserved" }, "softwareType": "Machine Learning Model", "companyName": "Vendor A", "contact": { "name": "Vendor A Team", "URL": "http://Vendor-A.com", "email": "support@Vendor-A.com" }, "additionalInfo": "http://Vendor-A.com/licenses/Vendor-A-OSS"}'),
 ('Company-B-Proprietary', 2, '12345678-abcd-90ab-cdef-1234567890ab', NOW(), NOW(), '{ "keyword": "Company-B-Proprietary", "licenseName": "Company B Proprietary License", "copyright": { "year": 2019, "company": "Company B", "suffix": "All Rights Reserved" }, "softwareType": "Machine Learning Model", "companyName": "Company B", "contact": { "name": "Company B Team Member", "URL": "http://Company-B.com", "email": "support@Company-B.com" }, "additionalInfo": "http://Company-B.com/licenses/Company-B-Proprietary"}');

-- Record this action in the history
INSERT INTO C_HISTORY (COMMENT, CREATED_DATE) VALUES ('cmn-data-svc-upgrade-2.2-to-3.0-rev2', NOW());
