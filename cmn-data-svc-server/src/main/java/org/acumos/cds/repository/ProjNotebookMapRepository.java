/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.repository;

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPProjNotebookMap;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProjNotebookMapRepository extends CrudRepository<MLPProjNotebookMap, MLPProjNotebookMap.ProjNbMapPK> {

	/**
	 * Gets all notebooks mapped to the specified project. Size is expected to be
	 * modest, so this does not use page.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return Iterable of MLPNotebook
	 */
	@Query(value = "SELECT p FROM MLPNotebook p, MLPProjNotebookMap m " //
			+ " WHERE p.notebookId =  m.notebookId AND m.projectId = :projectId")
	Iterable<MLPNotebook> findProjectNotebooks(@Param("projectId") String projectId);

	/**
	 * Deletes all entries for the specified project ID.
	 * 
	 * @param projectId
	 *                      project ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByProjectId(@Param("projectId") String projectId);

	/**
	 * Deletes all entries for the specified notebook ID.
	 * 
	 * @param notebookId
	 *                       notebook ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByNotebookId(@Param("notebookId") String notebookId);

}
