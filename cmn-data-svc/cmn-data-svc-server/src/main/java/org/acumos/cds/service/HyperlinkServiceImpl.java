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
package org.acumos.cds.service;

import org.acumos.cds.repository.HyperlinkRepository;
import org.acumos.cds.repository.SolRevHyperlinkMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hyperlinkService")
public class HyperlinkServiceImpl implements HyperlinkService {

	@Autowired
	private HyperlinkRepository hyperlinkRepository;
	@Autowired
	private SolRevHyperlinkMapRepository solRevHyperlinkMapRepository;

	@Override
	public void deleteHyperlink(String hyperlinkId) {
		solRevHyperlinkMapRepository.deleteByHyperlinkId(hyperlinkId);
		hyperlinkRepository.deleteById(hyperlinkId);
	}

	@Override
	public void deleteOrphanHyperlink(String hyperlinkId) {
		if (!solRevHyperlinkMapRepository.findByHyperlinkId(hyperlinkId).iterator().hasNext()) {
			hyperlinkRepository.deleteById(hyperlinkId);
		}
		// Later also check if other EntityHyperlinkMapRepository.findByHyperlinkId is not empty before deleting the hyperlink
	}
}
