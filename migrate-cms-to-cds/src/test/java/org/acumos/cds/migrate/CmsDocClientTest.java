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

package org.acumos.cds.migrate;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.migrate.client.CMSDiscoverAcumos;
import org.acumos.cds.migrate.client.CMSReaderClient;
import org.acumos.cds.migrate.client.CMSWorkspace;
import org.acumos.cds.migrate.domain.CMSDescription;
import org.acumos.cds.migrate.domain.CMSNameList;
import org.acumos.cds.migrate.domain.CMSRevisionDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Crude test that exercises all CMS getter methods. Depends on existing data,
 * does not create test data.
 */
public class CmsDocClientTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	String solutionId = "0074ddeb-d13d-4f9f-89fa-5418859d9d59";
	String revisionId = "dae46be9-a2f6-461f-b09b-fcb17e30c13d";
	String workspace = CMSWorkspace.ORG.getCmsKey();

	// This only works in our development enviroment
	public void testGetDocumentNames() throws Exception {
		try {
			MigrateProperties props = new MigrateProperties();

			CMSReaderClient client = new CMSReaderClient(props.getProperty(MigrateProperties.CMS_URL),
					props.getProperty(MigrateProperties.CMS_USER), props.getProperty(MigrateProperties.CMS_PASS));

			CMSNameList solImageList = client.getSolutionImageName(solutionId);
			logger.info("solution image name: {}", solImageList);

			for (String imageName : solImageList.getResponse_body()) {
				byte[] image = client.getSolutionImage(solutionId, imageName);
				logger.info("solution image length: {}", image.length);
			}

			CMSRevisionDescription revDesc = client.getRevisionDescription(solutionId, revisionId, workspace);
			logger.info("revision description: {}", revDesc);

			CMSNameList revDocList = client.getRevisionDocumentNames(solutionId, revisionId, workspace);
			logger.info("document name: {}", revDocList);

			for (String docName : revDocList.getResponse_body()) {
				byte[] doc = client.getRevisionDocument(solutionId, revisionId, workspace, docName);
				logger.info("doc length: {}", doc.length);
			}

			byte[] coBrandLogo = client.getCoBrandLogo();
			logger.info("Co-brand logo length {}", coBrandLogo == null ? 0 : coBrandLogo.length);

			CMSDescription footerContact = client.getFooterContactInfo();
			logger.info("Footer contact length {}",
					footerContact == null ? 0 : footerContact.getDescription().length());

			CMSDescription footerTc = client.getFooterTermsConditions();
			logger.info("Footer T&C length {}", footerTc == null ? 0 : footerTc.getDescription().length());

			for (CMSDiscoverAcumos discoverKey : CMSDiscoverAcumos.values()) {
				CMSDescription desc = client.getDiscoverText(discoverKey.getCmsKey());
				logger.info("Discover text for key {} has length {}", discoverKey.getCmsKey(),
						desc.getDescription().length());
			}

		} catch (HttpStatusCodeException ex) {
			// Helpful to see what the server replies when things go south
			logger.error("Failed {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}

}
