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
package org.acumos.cds.test;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRevCatDocMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.CatSolMapRepository;
import org.acumos.cds.repository.CatalogRepository;
import org.acumos.cds.repository.DocumentRepository;
import org.acumos.cds.repository.RevCatDescriptionRepository;
import org.acumos.cds.repository.RevCatDocMapRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionFOMRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.service.SolutionSearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the domain models that have complex mappings.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FOMRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private CatalogRepository catalogRepository;
	@Autowired
	private CatSolMapRepository catSolMapRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private RevCatDescriptionRepository revCatDescRepository;
	@Autowired
	private RevCatDocMapRepository revCatDocMapRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionFOMRepository solutionFOMRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private SolutionSearchService solutionSearchService;
	@Autowired
	private UserRepository userRepository;

	@Test
	public void testEntities() throws Exception {

		// Can query existing items in db
		boolean setupTeardown = true;

		MLPUser cu = null;
		MLPUser cu2 = null;
		MLPCatalog cc = null;
		MLPSolution cs = null;
		MLPSolutionRevision cr = null;
		MLPArtifact ca = null;
		MLPRevCatDescription rd = null;
		MLPDocument cd = null;
		MLPCatSolMap catSolMap = null;
		MLPSolRevArtMap revArtMap = null;
		MLPRevCatDocMap revDocMap = null;
		MLPSolUserAccMap accMap = null;
		final String name = "name";

		if (setupTeardown) {
			// Create entities for query

			cu = new MLPUser("entity_user1", "entitytestuser@abc.com", true);
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());
			logger.info("Created user {}", cu);

			cu2 = new MLPUser("entity_user2", "entityuser2@abc.com", true);
			cu2 = userRepository.save(cu2);
			Assert.assertNotNull(cu2.getUserId());
			logger.info("Created user {}", cu2);

			cc = new MLPCatalog("PB", true, "catalog name", "publisher", "http://pub.io");
			cc = catalogRepository.save(cc);
			Assert.assertNotNull("Catalog ID", cc.getCatalogId());

			cs = new MLPSolution("some solution " + name, cu.getUserId(), true);
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());

			cr = new MLPSolutionRevision(cs.getSolutionId(), "version", cu.getUserId());
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Created solution revision {}", cr.getRevisionId());

			ca = new MLPArtifact("version", "BP", "name", "uri", cu.getUserId(), 1);
			ca = artifactRepository.save(ca);
			Assert.assertNotNull(ca.getArtifactId());
			logger.info("Created artifact {}", ca);

			catSolMap = new MLPCatSolMap(cc.getCatalogId(), cs.getSolutionId());
			catSolMap = catSolMapRepository.save(catSolMap);
			logger.info("Created cat-sol map {}", catSolMap);

			revArtMap = new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId());
			revArtMap = solRevArtMapRepository.save(revArtMap);
			logger.info("Created sol-rev-art map {}", revArtMap);

			rd = new MLPRevCatDescription(cr.getRevisionId(), cc.getCatalogId(), "Some description");
			rd = revCatDescRepository.save(rd);
			Assert.assertNotNull(rd.getCreated());
			logger.info("Created description {}", rd);

			cd = new MLPDocument("docName", "http://uri", 123, cu.getUserId());
			cd = documentRepository.save(cd);
			Assert.assertNotNull(cd.getDocumentId());
			logger.info("Created document {}", cd);

			revDocMap = new MLPRevCatDocMap(cr.getRevisionId(), cc.getCatalogId(), cd.getDocumentId());
			revDocMap = revCatDocMapRepository.save(revDocMap);
			logger.info("Created sol-rev-doc map {}", revDocMap);

			accMap = new MLPSolUserAccMap(cs.getSolutionId(), cu2.getUserId());
			accMap = solUserAccMapRepository.save(accMap);
			logger.info("Created sol-user-acc map {}", accMap);
		}

		// Find all via Spring repository
		logger.info("Querying for SolFOM via repo findAll method");
		List<MLPSolutionFOM> foms = solutionFOMRepository.findAll();
		Assert.assertNotNull(foms);
		Assert.assertNotEquals(0, foms.size());
		logger.info("Found FOM row count {}", foms.size());

		// Find by modified date
		String[] empty = new String[0];
		String[] nameKw = new String[] { name }; // substring of solution name

		// Via Hibernate constraint
		logger.info("Querying for FOM via search services");
		Pageable pageable = PageRequest.of(0, 6);

		logger.info("Querying for FOM via findPortalSolutions method");
		Page<MLPSolution> byName = solutionSearchService.findPortalSolutions(nameKw, empty, true, empty, empty, empty,
				empty, empty, pageable);
		Assert.assertNotNull(byName);
		Assert.assertNotEquals(0, byName.getNumberOfElements());
		logger.info("Found sols by name via criteria: size {}", byName.getContent().size());

		// Find by user and Hibernate constraint - user2 owns no solutions but has
		// access
		Page<MLPSolution> byUser = solutionSearchService.findUserSolutions(true, true, cu2.getUserId(), nameKw, empty,
				empty, empty, pageable);
		Assert.assertNotNull(byUser);
		Assert.assertNotEquals(0, byUser.getNumberOfElements());
		logger.info("Found sols by user via criteria: size {}", byUser.getContent().size());

		if (setupTeardown) {
			// Clean up
			solUserAccMapRepository.delete(accMap);
			revCatDocMapRepository.delete(revDocMap);
			solRevArtMapRepository.delete(revArtMap);
			catSolMapRepository.delete(catSolMap);
			documentRepository.delete(cd);
			revCatDescRepository.delete(rd);
			artifactRepository.delete(ca);
			revisionRepository.delete(cr);
			solutionRepository.delete(cs);
			catalogRepository.delete(cc);
			userRepository.delete(cu2);
			userRepository.delete(cu);
		}
	}

}
