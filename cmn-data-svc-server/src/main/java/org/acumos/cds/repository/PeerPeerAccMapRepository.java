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

import java.util.List;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerPeerAccMap;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PeerPeerAccMapRepository
		extends PagingAndSortingRepository<MLPPeerPeerAccMap, MLPPeerPeerAccMap.PeerPeerAccMapPK> {

	/**
	 * Gets the list of peers accessible to the specified peer.
	 * 
	 * There must be a better way than this nested query.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return List of accessible peers
	 */
	@Query("SELECT p FROM MLPPeer p WHERE p.peerId IN  "//
			+ " ( SELECT pg.peerId FROM MLPPeerGrpMemMap pg WHERE pg.groupId IN " //
			+ "    ( SELECT pp.resourcePeerGroupId FROM MLPPeerPeerAccMap pp WHERE pp.principalPeerGroupId IN " //
			+ "       ( SELECT pg2.groupId FROM MLPPeerGrpMemMap pg2 WHERE pg2.peerId = :peerId ) " //
			+ "    ) " //
			+ " ) ")
	List<MLPPeer> findAccessPeers(@Param("peerId") String peerId);

}
