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
import java.net.URISyntaxException;
import java.util.Date;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the domain models that have complex mappings.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FOMAnnotationTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FOMTestService fomTestService;

	/*
	 * Tests for annotation errors in the FOM classes
	 */
	@Test
	public void testFOM() throws URISyntaxException {
		final String loginName = "user_" + Long.toString(new Date().getTime());
		MLPUser cu = new MLPUser(loginName, "fom@email.com", true);
		cu = userRepository.save(cu);
		Assert.assertNotNull(cu.getUserId());
		logger.info("Created user {}", cu);

		MLPSolution cs = new MLPSolution("sol name", cu.getUserId(), true);
		cs = solutionRepository.save(cs);
		Assert.assertNotNull("Solution ID", cs.getSolutionId());

		MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "version", cu.getUserId(), "PR");
		cr = revisionRepository.save(cr);
		Assert.assertNotNull("Revision ID", cr.getRevisionId());
		logger.info("Created solution revision {}", cr.getRevisionId());

		MLPArtifact ca = new MLPArtifact("version", "BP", "name", "uri", cu.getUserId(), 1);
		ca = artifactRepository.save(ca);
		Assert.assertNotNull(ca.getArtifactId());
		logger.info("Created artifact {}", ca);

		MLPSolRevArtMap map = new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId());
		solRevArtMapRepository.save(map);
		logger.info("Mapped artifact to revision {}", map);

		// This throws if any problems are found
		fomTestService.testFomAnnotations();

		solRevArtMapRepository.delete(map);
		artifactRepository.delete(ca);
		revisionRepository.delete(cr);
		solutionRepository.delete(cs);
		userRepository.delete(cu);

	}

}
