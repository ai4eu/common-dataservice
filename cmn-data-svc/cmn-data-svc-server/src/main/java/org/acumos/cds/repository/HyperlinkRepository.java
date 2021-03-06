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

import org.acumos.cds.domain.MLPHyperlink;
import org.acumos.cds.domain.MLPSolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Defines methods to process queries on specific fields and yield paginated
 * results.
 */
public interface HyperlinkRepository extends JpaRepository<MLPHyperlink, String>, JpaSpecificationExecutor<MLPSolution> {

	/**
	 * Finds hyperlinks using a LIKE query on the text column NAME.
	 * 
	 * @param searchTerm
	 *                        fragment to find in text columns
	 * @param pageRequest
	 *                        Page and sort criteria
	 * @return Page of MLPHyperlink
	 */
	@Query("SELECT h FROM MLPHyperlink h " //
			+ " WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<MLPHyperlink> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageRequest);

	/**
	 * Gets all hyperlinks associated with the specified solution revision.
	 * 
	 * This does not accept a pageable parameter because the number of hyperlinks for
	 * a single revision is expected to be modest.
	 *
	 * @param revisionId
	 *                       solution revision ID
	 * @return Iterable of MLPHyperlink
	 */
	@Query(value = "select a from MLPHyperlink a, MLPSolRevHyperlinkMap m " //
			+ " where a.hyperlinkId =  m.hyperlinkId " //
			+ " and m.revisionId = :revisionId")
	Iterable<MLPHyperlink> findByRevisionId(@Param("revisionId") String revisionId);
}
