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

import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProjPipelineMap;
import org.acumos.cds.domain.MLPProject;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProjPipelineMapRepository extends CrudRepository<MLPProjPipelineMap, MLPProjPipelineMap.ProjPlMapPK> {

	/**
	 * Gets all pipelines mapped to the specified project. Size is expected to be
	 * modest, so this does not use page.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return Iterable of MLPPipeline
	 */
	@Query(value = "SELECT p FROM MLPPipeline p, MLPProjPipelineMap m " //
			+ " WHERE p.pipelineId =  m.pipelineId AND m.projectId = :projectId")
	Iterable<MLPPipeline> findProjectPipelines(@Param("projectId") String projectId);

	/**
	 * Gets all projects to which the specified pipeline is mapped. Size is expected
	 * to be modest, so this does not use page.
	 * 
	 * @param pipelineId
	 *                       Pipeline ID
	 * @return Iterable of MLPProject
	 */
	@Query(value = "SELECT p FROM MLPProject p, MLPProjPipelineMap m " //
			+ " WHERE p.projectId =  m.projectId AND m.pipelineId = :pipelineId")
	Iterable<MLPProject> findPipelineProjects(@Param("pipelineId") String pipelineId);

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
	 * Deletes all entries for the specified pipeline ID.
	 * 
	 * @param pipelineId
	 *                       pipeline ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByPipelineId(@Param("pipelineId") String pipelineId);

}
