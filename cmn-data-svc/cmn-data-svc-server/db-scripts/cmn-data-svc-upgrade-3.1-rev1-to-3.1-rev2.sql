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
-- FROM version 3.1-rev1 TO version 3.1-rev2
-- No database name is set to allow flexible deployment.

-- Create license admin role
INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN, CREATED_DATE, MODIFIED_DATE) VALUES ('a84919d9-1d94-4502-8b83-8d92bb57cf6a', 'License Admin', 'Y', NOW(), NOW());

-- Add license admin role to well-known admin user
INSERT INTO C_USER_ROLE_MAP (USER_ID, ROLE_ID) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', 'a84919d9-1d94-4502-8b83-8d92bb57cf6a');

-- Record this action in the history
INSERT INTO C_HISTORY (COMMENT, CREATED_DATE) VALUES ('cmn-data-svc-upgrade-3.1-rev1-to-3.1-rev2', NOW());
