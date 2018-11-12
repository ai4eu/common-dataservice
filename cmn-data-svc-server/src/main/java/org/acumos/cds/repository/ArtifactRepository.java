/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.cds.domain.MLPArtifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepository extends PagingAndSortingRepository<MLPArtifact, String> {

	/**
	 * Gets all artifacts associated with the specified solution revision.
	 * 
	 * This does not accept a pageable parameter because the number of artifacts for
	 * a single revision is expected to be modest.
	 *
	 * @param revisionId
	 *                       solution revision ID
	 * @return Iterable of MLPArtifact
	 */
	@Query(value = "select a from MLPArtifact a, MLPSolRevArtMap m " //
			+ " where a.artifactId =  m.artifactId " //
			+ " and m.revisionId = :revisionId")
	Iterable<MLPArtifact> findByRevision(@Param("revisionId") String revisionId);

	/**
	 * Finds artifacts using a LIKE query on the text columns NAME and DESCRIPTION.
	 * 
	 * @param searchTerm
	 *                        fragment to find in text columns
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Iterable of MLPArtifact
	 */
	@Query("SELECT s FROM MLPArtifact s " //
			+ " WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" //
			+ " OR LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<MLPArtifact> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageRequest);

}
