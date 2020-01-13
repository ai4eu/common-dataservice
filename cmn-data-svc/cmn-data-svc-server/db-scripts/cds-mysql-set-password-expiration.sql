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

-- Script to set a password expiration date of one month from now
-- to all users that have no expiration date.
-- A non-null value enables the Portal's password expiration feature.
-- Also see https://jira.acumos.org/browse/ACUMOS-3605
-- No database name is set to allow flexible deployment.

UPDATE c_user
    SET login_pass_expire_date = (SELECT DATE_ADD(NOW(), INTERVAL 1 MONTH))
    WHERE login_pass_expire_date IS NULL ;
