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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactFOM;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionRevisionFOM;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides a method to exercise the full object model classes.
 */
@Service("fomTestService")
@Transactional
public class FOMTestService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private EntityManager entityManager;

	/**
	 * Queries for solutions, revisions and artifacts using the plain and full
	 * object mapping classes. Results must match. If not, there is an annotation
	 * error.
	 */
	public void testFomAnnotations() {

		logger.info("testFomAnnotations: querying solutions");
		CriteriaQuery<MLPSolution> queryDefSol = entityManager.getCriteriaBuilder().createQuery(MLPSolution.class);
		Root<MLPSolution> fromSol = queryDefSol.from(MLPSolution.class);
		queryDefSol.select(fromSol);
		TypedQuery<MLPSolution> querySol = entityManager.createQuery(queryDefSol);
		List<MLPSolution> listSol = querySol.getResultList();
		CriteriaQuery<MLPSolutionFOM> queryDefSolFom = entityManager.getCriteriaBuilder()
				.createQuery(MLPSolutionFOM.class);
		Root<MLPSolutionFOM> fromSolFom = queryDefSolFom.from(MLPSolutionFOM.class);
		queryDefSolFom.select(fromSolFom);
		TypedQuery<MLPSolutionFOM> querySolFom = entityManager.createQuery(queryDefSolFom);
		List<MLPSolutionFOM> listSolFom = querySolFom.getResultList();
		Assert.assertEquals("solutions", listSol.size(), listSolFom.size());

		logger.info("testFomAnnotations: querying revisions");
		CriteriaQuery<MLPSolutionRevision> queryDefRev = entityManager.getCriteriaBuilder()
				.createQuery(MLPSolutionRevision.class);
		Root<MLPSolutionRevision> fromRev = queryDefRev.from(MLPSolutionRevision.class);
		queryDefRev.select(fromRev);
		TypedQuery<MLPSolutionRevision> queryRev = entityManager.createQuery(queryDefRev);
		List<MLPSolutionRevision> listRev = queryRev.getResultList();
		CriteriaQuery<MLPSolutionRevisionFOM> queryDefRevFom = entityManager.getCriteriaBuilder()
				.createQuery(MLPSolutionRevisionFOM.class);
		Root<MLPSolutionRevisionFOM> fromRevFom = queryDefRevFom.from(MLPSolutionRevisionFOM.class);
		queryDefRevFom.select(fromRevFom);
		TypedQuery<MLPSolutionRevisionFOM> queryRevFom = entityManager.createQuery(queryDefRevFom);
		List<MLPSolutionRevisionFOM> listRevFom = queryRevFom.getResultList();
		Assert.assertEquals("revisions", listRev.size(), listRevFom.size());

		logger.info("testFomAnnotations: querying artifacts");
		CriteriaQuery<MLPArtifact> queryDefArt = entityManager.getCriteriaBuilder().createQuery(MLPArtifact.class);
		Root<MLPArtifact> fromArt = queryDefArt.from(MLPArtifact.class);
		queryDefArt.select(fromArt);
		TypedQuery<MLPArtifact> queryArt = entityManager.createQuery(queryDefArt);
		List<MLPArtifact> listArt = queryArt.getResultList();
		CriteriaQuery<MLPArtifactFOM> queryDefArtFom = entityManager.getCriteriaBuilder()
				.createQuery(MLPArtifactFOM.class);
		Root<MLPArtifactFOM> fromArtFom = queryDefArtFom.from(MLPArtifactFOM.class);
		queryDefArtFom.select(fromArtFom);
		TypedQuery<MLPArtifactFOM> queryArtFom = entityManager.createQuery(queryDefArtFom);
		List<MLPArtifactFOM> listArtFom = queryArtFom.getResultList();
		Assert.assertEquals("artifacts", listArt.size(), listArtFom.size());

	}
}
