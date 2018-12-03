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

-- Script to copy data from C_USER to C_SOLUTION_REV in the database 
-- used by the Acumos Common Data Service version 1.18.x.
-- Also see https://jira.acumos.org/browse/ACUMOS-2109
-- No database name is set to allow flexible deployment.

update c_solution_rev r 
  inner join c_user u on r.user_id = u.user_id 
  set r.authors = concat (u.first_name, ' ', u.last_name, '\t', u.email ) 
  where (r.AUTHORS is null or r.AUTHORS='') and u.last_name is not null and u.email is not null;
