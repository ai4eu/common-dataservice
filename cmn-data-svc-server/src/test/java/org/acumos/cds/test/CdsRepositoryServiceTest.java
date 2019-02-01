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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPCompSolMap;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeerPeerAccMap;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuRefMap;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPRtuUserMap;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolGrpMemMap;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolRevDocMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRating.SolutionRatingPK;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.CatSolMapRepository;
import org.acumos.cds.repository.CatalogRepository;
import org.acumos.cds.repository.CommentRepository;
import org.acumos.cds.repository.CompSolMapRepository;
import org.acumos.cds.repository.DocumentRepository;
import org.acumos.cds.repository.NotifUserMapRepository;
import org.acumos.cds.repository.NotificationRepository;
import org.acumos.cds.repository.PeerGroupRepository;
import org.acumos.cds.repository.PeerGrpMemMapRepository;
import org.acumos.cds.repository.PeerPeerAccMapRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.PeerSolAccMapRepository;
import org.acumos.cds.repository.PeerSubscriptionRepository;
import org.acumos.cds.repository.PublishRequestRepository;
import org.acumos.cds.repository.RevisionDescriptionRepository;
import org.acumos.cds.repository.RightToUseRepository;
import org.acumos.cds.repository.RoleFunctionRepository;
import org.acumos.cds.repository.RoleRepository;
import org.acumos.cds.repository.RtuRefMapRepository;
import org.acumos.cds.repository.RtuReferenceRepository;
import org.acumos.cds.repository.RtuUserMapRepository;
import org.acumos.cds.repository.SiteConfigRepository;
import org.acumos.cds.repository.SolGrpMemMapRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolRevDocMapRepository;
import org.acumos.cds.repository.SolTagMapRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionDownloadRepository;
import org.acumos.cds.repository.SolutionGroupRepository;
import org.acumos.cds.repository.SolutionRatingRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.TagRepository;
import org.acumos.cds.repository.TaskRepository;
import org.acumos.cds.repository.TaskStepResultRepository;
import org.acumos.cds.repository.ThreadRepository;
import org.acumos.cds.repository.UserLoginProviderRepository;
import org.acumos.cds.repository.UserNotificationPreferenceRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.repository.UserRoleMapRepository;
import org.acumos.cds.service.ArtifactSearchService;
import org.acumos.cds.service.CodeNameService;
import org.acumos.cds.service.PeerSearchService;
import org.acumos.cds.service.PublishRequestSearchService;
import org.acumos.cds.service.RoleSearchService;
import org.acumos.cds.service.SolutionSearchService;
import org.acumos.cds.service.StepResultSearchService;
import org.acumos.cds.service.UserSearchService;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.SolutionRatingStats;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;

/**
 * Tests the repository and service classes that provide access to the database.
 * Relies on the provided application.properties with Derby configuration.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CdsRepositoryServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private CatalogRepository catalogRepository;
	@Autowired
	private CatSolMapRepository catSolMapRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CompSolMapRepository compSolMapRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private NotifUserMapRepository notifUserMapRepository;
	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private PeerSubscriptionRepository peerSubscriptionRepository;
	@Autowired
	private RoleFunctionRepository roleFunctionRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private SiteConfigRepository siteConfigRepository;
	@Autowired
	private SolutionDownloadRepository solutionDownloadRepository;
	@Autowired
	private SolutionRatingRepository solutionRatingRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolTagMapRepository solTagMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private UserLoginProviderRepository userLoginProviderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleMapRepository userRoleMapRepository;
	@Autowired
	private ArtifactSearchService artifactSearchService;
	@Autowired
	private PeerSearchService peerSearchService;
	@Autowired
	private RoleSearchService roleSearchService;
	@Autowired
	private SolutionSearchService solutionSearchService;
	@Autowired
	private UserSearchService userSearchService;
	@Autowired
	private TaskStepResultRepository stepResultRepository;
	@Autowired
	private StepResultSearchService stepResultSearchService;
	@Autowired
	private UserNotificationPreferenceRepository usrNotifPrefRepository;
	@Autowired
	private PeerGroupRepository peerGroupRepository;
	@Autowired
	private SolutionGroupRepository solutionGroupRepository;
	@Autowired
	private PeerGrpMemMapRepository peerGroupMemMapRepository;
	@Autowired
	private SolGrpMemMapRepository solGroupMemMapRepository;
	@Autowired
	private PeerSolAccMapRepository peerSolAccMapRepository;
	@Autowired
	private PeerPeerAccMapRepository peerPeerAccMapRepository;
	@Autowired
	private CodeNameService codeNameService;
	@Autowired
	private RevisionDescriptionRepository revisionDescRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private SolRevDocMapRepository solRevDocMapRepository;
	@Autowired
	private PublishRequestRepository publishRequestRepository;
	@Autowired
	private PublishRequestSearchService publishRequestSearchService;
	@Autowired
	private RightToUseRepository rtuRepository;
	@Autowired
	private RtuReferenceRepository rtuRefRepository;
	@Autowired
	private RtuRefMapRepository rtuRefMapRepository;
	@Autowired
	private RtuUserMapRepository rtuUserMapRepository;

	@Test
	public void testRepositories() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			Instant lastLogin = Instant.now().minusSeconds(60);
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			final String firstName = "FirstTestRepo";
			final String lastName = "TestLast";
			final String loginName = "login_name_test_repo";
			final String loginPass = "test_pass3";
			cu.setEmail("testRepouser1@abc.com");
			cu.setFirstName(firstName);
			cu.setLastName(lastName);
			cu.setLoginName(loginName);
			cu.setLoginFailCount((short) 0);
			cu.setLoginFailDate(lastLogin);
			cu.setLoginHash(loginPass);
			cu.setLoginPassExpire(Instant.now());
			cu = userRepository.save(cu);
			Instant originalCreated = cu.getCreated();
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			// Created and modified timestamps might overlap a second boundary
			Assert.assertTrue(Math.abs(cu.getCreated().getEpochSecond() - cu.getModified().getEpochSecond()) < 2);
			logger.info("Created user {}", cu);
			cu.setAuthToken("JWT is Greek to me");
			cu.setLastLogin(lastLogin);
			cu = userRepository.save(cu);
			// Check hibernate behavior on timestamps
			Assert.assertEquals(originalCreated, cu.getCreated());
			Assert.assertNotEquals(cu.getCreated(), cu.getModified());
			Assert.assertEquals(lastLogin, cu.getLastLogin());
			// The created-date field accepts updates, unfortunately
			Instant changedCreate = Instant.now().minusSeconds(1);
			cu.setCreated(changedCreate);
			cu = userRepository.save(cu);
			Assert.assertEquals(changedCreate, cu.getCreated());

			// Fetch it back
			Page<MLPUser> userPage = userSearchService.findUsers(firstName, null, lastName, null, null, null, null,
					false, PageRequest.of(0, 5));
			Assert.assertNotEquals(0, userPage.getNumberOfElements());
			MLPUser testUser = userPage.iterator().next();
			logger.info("testUser is " + testUser);
			logger.info("cu.getUserID is " + cu.getUserId());

			// Test search with every argument and empty result
			Page<MLPUser> emptySteps = userSearchService.findUsers("bogus", "bogus", "bogus", "bogus", "bogus", "bogus",
					Boolean.TRUE, false, PageRequest.of(0, 5));
			Assert.assertTrue(emptySteps.isEmpty());

			MLPNotification notif = null;
			notif = new MLPNotification();
			notif.setTitle("Notification title");
			notif.setMessage("Notification message");
			notif.setUrl("http://www.yahoo.com");
			notif.setMsgSeverityCode("LO");
			notif.setStart(Instant.now());
			notif.setEnd(Instant.now().plus(5, ChronoUnit.DAYS));
			notif = notificationRepository.save(notif);
			Assert.assertNotNull(notif.getNotificationId());
			Assert.assertNotNull(notif.getCreated());
			logger.info("\t\tNotification: " + notif);

			// put it in the map NotifUserMapRepository
			MLPNotifUserMap notifMap = null;
			notifMap = new MLPNotifUserMap();
			notifMap.setNotificationId(notif.getNotificationId());
			notifMap.setUserId(cu.getUserId());
			notifMap = notifUserMapRepository.save(notifMap);
			logger.info("\t\tNotification Map: " + notifMap);

			logger.info("NotificationRepository Info");
			Iterable<MLPUserNotification> nList = notificationRepository.findActiveByUser(cu.getUserId(), null);
			logger.info("User notifications for user {}: {}", cu.getUserId(), nList);

			MLPUserLoginProvider ulp = new MLPUserLoginProvider();
			ulp.setUserId(cu.getUserId());
			ulp.setProviderCode("GH");
			ulp.setProviderUserId("something");
			ulp.setAccessToken("bogus");
			ulp.setRank(0);
			ulp = userLoginProviderRepository.save(ulp);
			Assert.assertNotNull(ulp.getCreated());

			logger.info("UserLoginProviderRepository Info");
			Iterable<MLPUserLoginProvider> ulpList = userLoginProviderRepository.findByUserId(cu.getUserId());
			logger.info("User Login provider list {}", ulpList);

			// Create Peer
			MLPPeer pr = new MLPPeer();
			pr.setName("Peer-Name-Test-Repo");
			pr.setSubjectName("ssl.certificates.are.difficult.to.configure");
			pr.setApiUrl("http://peer-api");
			pr.setContact1("Tyrion Lannister");
			pr.setStatusCode("AC");
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			// Fetch back
			Page<MLPPeer> searchPeers = peerSearchService.findPeers(pr.getName(), null, null, null, null, null, null,
					false, PageRequest.of(0, 5));
			Assert.assertEquals(1, searchPeers.getNumberOfElements());

			// Test search with empty result
			Page<MLPPeer> emptyPeers = peerSearchService.findPeers("bogus", "bogus", "bogus", "bogus", "bogus", "bogus",
					null, false, PageRequest.of(0, 5));
			Assert.assertTrue(emptyPeers.isEmpty());

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(), "FL",
					AccessTypeCode.PB.name());
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertNotNull(ps.getSubId());
			// Column was defined as timestamp with autoupdate :(
			Assert.assertNull(ps.getProcessed());
			Instant processed = Instant.now();
			ps.setProcessed(processed);
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertEquals(processed, ps.getProcessed());
			logger.info("Peer subscription {}", ps);

			logger.info("Fetching PeerSubscriptions");
			Iterable<MLPPeerSubscription> peerSubscriptionList = peerSubscriptionRepository
					.findByPeerId(pr.getPeerId());
			Assert.assertTrue(peerSubscriptionList.iterator().hasNext());
			logger.info("Peer subscription list {}", peerSubscriptionList);

			logger.info("Creating test role");
			MLPRole cr2 = new MLPRole();
			cr2.setName("MLP System User4");
			cr2 = roleRepository.save(cr2);
			Assert.assertNotNull(cr2.getRoleId());

			long count = roleRepository.count();
			Assert.assertNotEquals(0, count);
			logger.info("Role count: {}", count);

			Page<MLPRole> searchRoles = roleSearchService.findRoles(cr2.getName(), null, false, PageRequest.of(0, 5));
			Assert.assertEquals(1, searchRoles.getNumberOfElements());

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr2.getRoleId());
			crf = roleFunctionRepository.save(crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			logger.info("User Info {}", cu);
			userRoleMapRepository.save(new MLPUserRoleMap(cu.getUserId(), cr2.getRoleId()));

			long usersInRole = userRoleMapRepository.countRoleUsers(cr2.getRoleId());
			Assert.assertNotEquals(0, usersInRole);
			logger.info("Count of users in role: {}", usersInRole);

			logger.info("Checking role content");
			Optional<MLPRole> opt = roleRepository.findById(cr2.getRoleId());
			Assert.assertTrue(opt.isPresent());
			Assert.assertNotNull(opt.get().getRoleId());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRoleId(cr2.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("RoleRepository Info");
			final String UserID = cu.getUserId();
			Iterable<MLPRole> roleList = roleRepository.findByUser(UserID);
			logger.info("Role list {}", roleList);

			userRoleMapRepository.deleteById(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr2.getRoleId()));

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.deleteById(cr2.getRoleId());

			logger.info("Creating artifacts");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact1");
			ca.setArtifactTypeCode("DI");
			ca.setUserId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setVersion("2.0B");
			ca2.setName("second artifact name");
			ca2.setUri("http://nexus/artifact2");
			ca2.setArtifactTypeCode("CD");
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = artifactRepository.save(ca2);
			// Use >= to allow test against populated db
			Assert.assertTrue(artifactRepository.count() >= 2);

			// Fetch artifact back
			Page<MLPArtifact> searchArts = artifactSearchService.findArtifacts(null, ca.getName(), null, null, null,
					false, PageRequest.of(0, 5));
			Assert.assertEquals(1, searchArts.getNumberOfElements());

			// Test search with empty result
			Page<MLPArtifact> emptyArts = artifactSearchService.findArtifacts("bogus", "bogus", "bogus", "bogus",
					"bogus", false, PageRequest.of(0, 5));
			Assert.assertTrue(emptyArts.isEmpty());

			MLPTag solTag1 = new MLPTag("soltag1");
			solTag1 = tagRepository.save(solTag1);
			MLPTag solTag2 = new MLPTag("soltag2");
			solTag2 = tagRepository.save(solTag2);

			MLPCatalog ca1 = new MLPCatalog("PB", "name", "http://pub.org");
			ca1 = catalogRepository.save(ca1);
			Assert.assertNotNull("Catalog ID", ca1.getCatalogId());
			logger.info("Created catalog {}", ca1);

			MLPSolution cs = new MLPSolution();
			final String solName = "solution name test repo";
			cs.setName(solName);
			cs.setActive(true);
			cs.setUserId(cu.getUserId());
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("SK");
			// tags must exist; they are not created here
			cs.getTags().add(solTag1);
			cs.getTags().add(solTag2);
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertEquals(2, cs.getTags().size());
			logger.info("Created solution " + cs.getSolutionId());

			MLPCatSolMap csm = new MLPCatSolMap(ca1.getCatalogId(), cs.getSolutionId());
			catSolMapRepository.save(csm);

			// This solution has one tag
			MLPSolution cs2 = new MLPSolution();
			cs2.setName("solution name");
			cs2.setActive(true);
			cs2.setUserId(cu.getUserId());
			cs2.setModelTypeCode("CL");
			cs2.setToolkitTypeCode("SK");
			cs2.getTags().add(solTag1);
			cs2 = solutionRepository.save(cs2);
			Assert.assertNotNull("Solution 2 ID", cs2.getSolutionId());
			logger.info("Created solution 2 " + cs2.getSolutionId());

			// Search with single values
			// Limit to one result, which helps detect Hibernate issues
			// of creating a cross-product when it should not
			Page<MLPSolution> searchSols = solutionSearchService.searchSolutions(solName, null, null, null, null, null,
					null, false, PageRequest.of(0, 5));
			// Ensure a single result; had a bug with dupes due to tags
			Assert.assertEquals(1, searchSols.getContent().size());
			// Ensure both tags were retrieved
			Assert.assertEquals(2, searchSols.getContent().get(0).getTags().size());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0X", cu.getUserId(),
					AccessTypeCode.PR.name());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("name1", "contact1"),
					new AuthorTransport("name2", "contact2") });
			cr.setPublisher("Big Data Org");
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Created solution revision " + cr.getRevisionId());

			MLPSolutionRevision rev2 = new MLPSolutionRevision(cs2.getSolutionId(), "1.0X", cu.getUserId(),
					AccessTypeCode.PR.name());
			rev2 = revisionRepository.save(rev2);
			Assert.assertNotNull("Revision ID", rev2.getRevisionId());
			logger.info("Created solution revision " + rev2.getRevisionId());

			logger.info("Adding artifact 1 to revision 1");
			solRevArtMapRepository.save(new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId()));
			logger.info("Added" + cr.getRevisionId() + " and " + ca.getArtifactId());

			solRevArtMapRepository.save(new MLPSolRevArtMap(rev2.getRevisionId(), ca2.getArtifactId()));
			logger.info("Added" + rev2.getRevisionId() + " and " + ca2.getArtifactId());

			MLPRevisionDescription revDesc = new MLPRevisionDescription(cr.getRevisionId(), "PB",
					"Some silly description");
			revDesc = revisionDescRepository.save(revDesc);
			Assert.assertNotNull(revDesc.getCreated());

			logger.info("Finding portal solutions");
			String[] solKw = { solName };
			String[] descKw = { "silly" }; // hey it's there
			boolean active = true;
			String[] userIds = { cu.getUserId() };
			String[] modelTypeCodes = { "CL" };
			String[] accTypeCodes = { AccessTypeCode.PR.name(), AccessTypeCode.OR.name() };
			String[] searchTags = { solTag1.getTag() };
			String[] searchAuths = null;
			String[] searchPubs = { "Data" };
			Page<MLPSolution> portalSearchResult = solutionSearchService.findPortalSolutions(solKw, descKw, active,
					userIds, modelTypeCodes, accTypeCodes, searchTags, searchAuths, searchPubs,
					PageRequest.of(0, 2, Direction.ASC, "name"));
			Assert.assertEquals(1, portalSearchResult.getNumberOfElements());
			logger.info("Found portal solution total " + portalSearchResult.getTotalElements());

			logger.info("Check that one tag yields multiple matches");
			Page<MLPSolution> oneTagSearchResult = solutionSearchService.findPortalSolutions(null, null, active, null,
					null, null, searchTags, null, null, PageRequest.of(0, 5));
			Assert.assertEquals(2, oneTagSearchResult.getNumberOfElements());

			String[] ids = { cs.getSolutionId() };
			Page<MLPSolution> idSearchResult = solutionSearchService.findPortalSolutionsByKwAndTags(ids, active,
					userIds, modelTypeCodes, accTypeCodes, searchTags, null, ca1.getCatalogId(),
					PageRequest.of(0, 2, Direction.ASC, "name"));
			Assert.assertEquals(1, idSearchResult.getNumberOfElements());
			logger.info("Found models by id total " + idSearchResult.getTotalElements());

			logger.info("Check all vs any tags single match");
			String[] allTags = new String[] { solTag1.getTag() };
			String[] anyTags = new String[] { solTag2.getTag(), "other" };
			Page<MLPSolution> allAnyTagsSearchResult = solutionSearchService.findPortalSolutionsByKwAndTags(null,
					active, userIds, modelTypeCodes, accTypeCodes, allTags, anyTags, null, PageRequest.of(0, 5));
			Assert.assertNotEquals(0, allAnyTagsSearchResult.getNumberOfElements());
			MLPSolution taggedSol = allAnyTagsSearchResult.getContent().get(0);
			Assert.assertTrue(taggedSol.getTags().contains(solTag1) && taggedSol.getTags().contains(solTag2));

			String[] kw = { "Big", "Data" };
			Page<MLPSolution> kwSearchResult = solutionSearchService.findPortalSolutionsByKwAndTags(kw, active, userIds,
					modelTypeCodes, accTypeCodes, searchTags, null, null, PageRequest.of(0, 2, Direction.ASC, "name"));
			Assert.assertNotEquals(0, kwSearchResult.getNumberOfElements());
			logger.info("Found models by kw total " + kwSearchResult.getTotalElements());

			Page<MLPSolution> userSearchResult = solutionSearchService.findUserSolutions(null, null, active,
					cu.getUserId(), null, null, null, PageRequest.of(0, 2, Direction.ASC, "name"));
			Assert.assertNotEquals(0, userSearchResult.getNumberOfElements());
			logger.info("Found models by user total " + userSearchResult.getTotalElements());

			Page<MLPSolution> userKwSearchResult = solutionSearchService.findUserSolutions(solKw, descKw, active,
					cu.getUserId(), modelTypeCodes, accTypeCodes, searchTags,
					PageRequest.of(0, 2, Direction.ASC, "name"));
			Assert.assertNotEquals(0, userKwSearchResult.getNumberOfElements());
			logger.info("Found models by user total " + userKwSearchResult.getTotalElements());

			logger.info("Querying for artifact by partial match");
			Iterable<MLPArtifact> al = artifactRepository.findBySearchTerm("name", PageRequest.of(0, 5));
			Assert.assertTrue(al.iterator().hasNext());
			logger.info("Artifact list {}", al);

			logger.info("Querying for solution by id");
			Optional<MLPSolution> optSol = solutionRepository.findById(cs.getSolutionId());
			Assert.assertTrue(optSol.isPresent());
			MLPSolution si = optSol.get();
			logger.info("Found solution: " + si.toString());

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", PageRequest.of(0, 5));
			Assert.assertTrue(sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolutionIdIn(new String[] { si.getSolutionId(), cs.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Assert.assertNotEquals(0, r.getAuthors().length);
				Iterable<MLPSolRevArtMap> arts = solRevArtMapRepository.findByRevisionId(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
			}

			MLPDocument doc = new MLPDocument();
			doc.setName("doc name");
			doc.setUri("http://doc.uri/");
			doc.setSize(10);
			doc.setUserId(cu.getUserId());
			doc = documentRepository.save(doc);
			Assert.assertNotNull(doc.getDocumentId());
			Assert.assertNotNull(doc.getCreated());

			logger.info("Adding document to revision");
			MLPSolRevDocMap docMap = solRevDocMapRepository
					.save(new MLPSolRevDocMap(cr.getRevisionId(), "PB", doc.getDocumentId()));
			Assert.assertNotNull(docMap);

			Iterable<MLPDocument> docs = documentRepository.findByRevisionAccess(cr.getRevisionId(), "PB");
			Assert.assertTrue(docs.iterator().hasNext());

			logger.info("Cleaning up revision");
			revisionDescRepository.deleteById(new MLPRevisionDescription.RevDescPK(cr.getRevisionId(), "PB"));
			solRevDocMapRepository.delete(docMap);
			documentRepository.deleteById(doc.getDocumentId());

			docs = documentRepository.findByRevisionAccess(cr.getRevisionId(), "PB");
			Assert.assertFalse(docs.iterator().hasNext());

			// Find by modified date
			String[] accTypes = new String[] { AccessTypeCode.PR.name() };
			Instant modifiedTs = Instant.now().minusSeconds(60);
			Page<MLPSolution> solsByDate = solutionSearchService.findSolutionsByModifiedDate(true, accTypes, modifiedTs,
					PageRequest.of(0, 5));
			Assert.assertNotNull(solsByDate);
			Assert.assertNotEquals(0, solsByDate.getNumberOfElements());
			logger.info("Found sols by date {}", solsByDate);

			// Touch artifact then search for it by modified date.
			// Use the created TS, a database time. Using a time of this server sorta
			// assumes perfect synch with a remote DB server which isn't safe.
			// So this test allows some slop - needs to find at least one artifact.
			// When the columns were modeled as Date this required Thread.sleep(1000)
			// calls, I believe because the driver only passed seconds to Mariadb.
			Instant beforeUpdateTs = ca.getCreated();
			ca.setDescription(ca.getDescription() + " a bit more");
			ca = artifactRepository.save(ca);
			Assert.assertNotEquals(beforeUpdateTs, ca.getModified());
			Page<MLPSolution> recentlyUpdated = solutionSearchService.findSolutionsByModifiedDate(true, accTypes,
					beforeUpdateTs, PageRequest.of(0, 5));
			Assert.assertNotNull(recentlyUpdated);
			Assert.assertNotEquals(0, recentlyUpdated.getNumberOfElements());
			Assert.assertTrue(recentlyUpdated.getContent().contains(cs));
			logger.info("Found recently updated sols {}", recentlyUpdated);

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = solutionDownloadRepository.save(sd);
			Assert.assertNotNull(sd.getDownloadId());
			Assert.assertNotNull(sd.getDownloadDate());
			logger.info("Created solution download: " + sd.toString());

			// Fetch the download count
			Long downloadCount = solutionDownloadRepository.countSolutionDownloads(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			logger.info("Querying for solution downloads for the specified solution ID");
			Iterable<MLPSolutionDownload> soldown = solutionDownloadRepository.findBySolutionId(cs.getSolutionId(),
					PageRequest.of(0, 5));
			logger.info("solutionDownloadRepository list {}", soldown);

			MLPSolutionRating solrate = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 2);
			solrate.setTextReview("Review text");
			solrate = solutionRatingRepository.save(solrate);
			Assert.assertNotNull(solrate.getSolutionId());
			logger.info("Created Solution Rating " + solrate.getSolutionId() + " Rating is " + solrate.getRating());

			logger.info("Querying for solution rating for the specified solution ID");
			Iterable<MLPSolutionRating> solrating = solutionRatingRepository.findBySolutionId(cs.getSolutionId(),
					PageRequest.of(0, 5));
			logger.info("SolutionRatingRepository list: {}", solrating);

			logger.info("Creating solution tag");
			MLPTag tag1 = new MLPTag("Java");
			tag1 = tagRepository.save(tag1);
			Iterable<MLPTag> tags = tagRepository.findAll();
			Assert.assertTrue(tags.iterator().hasNext());
			logger.info("First tag fetched back is " + tags.iterator().next());

			MLPSolTagMap solTagMap1 = new MLPSolTagMap(cs.getSolutionId(), tag1.getTag());
			solTagMapRepository.save(solTagMap1);

			MLPSolTagMap soltag = null;
			soltag = new MLPSolTagMap();
			soltag.setSolutionId(cs.getSolutionId());
			soltag.setTag("Java");
			Assert.assertNotNull(soltag.getSolutionId());
			logger.info("Created Solution Tag " + soltag.getSolutionId() + " Tag is " + soltag.getTag());
			soltag = solTagMapRepository.save(soltag);

			Iterable<MLPTag> soltag2 = tagRepository.findBySolution(soltag.getSolutionId());
			logger.info("Solution tag: {}", soltag2);
			logger.info("Solution Tag list above");

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifactId(ca.getArtifactId());
			Assert.assertNotNull(revsByArt);
			Assert.assertTrue(revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			logger.info("Querying for user by partial match");
			Page<MLPUser> sul = userRepository.findBySearchTerm("Test", PageRequest.of(0, 5));
			Assert.assertFalse(sul.getContent().isEmpty());
			logger.info("User list: {}", sul);

			Page<MLPSolution> solByTag = solutionRepository.findByTag("Java", PageRequest.of(0, 5));
			logger.info("Solutions by tag: {}", solByTag);
			Assert.assertFalse(solByTag.getContent().isEmpty());

			String[] accessTypes = new String[] { AccessTypeCode.PR.name() };
			Instant anHourAgo = Instant.now().minusSeconds(60 * 60);
			Page<MLPSolution> solByCriteria = solutionSearchService.findSolutionsByModifiedDate(true, accessTypes,
					anHourAgo, PageRequest.of(0, 5));
			logger.info("Solutions by date: {}", solByCriteria);
			Assert.assertFalse(solByCriteria.getContent().isEmpty());

			MLPUser founduser = userRepository.findByLoginOrEmail("test_user7");
			logger.info("Found user: {}", founduser);

			logger.info("Dropping artifact from revision");
			solRevArtMapRepository
					.deleteById(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
			logger.info("Dropped" + cr.getRevisionId() + " and " + ca.getArtifactId());

			MLPSiteConfig cc = new MLPSiteConfig("repotest-siteconfig", " { \"json\" : \"block\" }");
			cc = siteConfigRepository.save(cc);
			Assert.assertNotNull(cc);
			logger.info("Created site config {}", cc);
			siteConfigRepository.delete(cc);

			MLPThread thread = threadRepository.save(new MLPThread(cs.getSolutionId(), cr.getRevisionId()));
			Assert.assertNotNull(thread.getThreadId());
			logger.info("Created thread {}", thread);

			long threadCount = threadRepository.countBySolutionIdAndRevisionId(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertNotEquals(0, threadCount);
			Page<MLPThread> threads = threadRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), PageRequest.of(0, 5));
			Assert.assertNotEquals(0, threads.getNumberOfElements());

			MLPComment mc = commentRepository.save(new MLPComment(thread.getThreadId(), cu.getUserId(), "c"));
			long crc = commentRepository.count();
			Assert.assertNotEquals(0, crc);
			long tcc = commentRepository.countThreadComments(thread.getThreadId());
			Assert.assertNotEquals(0, tcc);
			Page<MLPComment> commentList = commentRepository.findByThreadId(thread.getThreadId(), PageRequest.of(0, 5));
			Assert.assertTrue(commentList.hasContent());
			Page<MLPComment> solRevComments = commentRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), PageRequest.of(0, 5));
			Assert.assertTrue(solRevComments.hasContent());

			commentRepository.deleteById(mc.getCommentId());
			threadRepository.deleteById(thread.getThreadId());

			// Composite solution support was added very late
			MLPCompSolMap compSolMap = new MLPCompSolMap(cs.getSolutionId(), cs.getSolutionId());
			logger.info("Created comp sol map {}", compSolMap.toString());
			compSolMapRepository.save(compSolMap);
			Iterable<MLPCompSolMap> parents = compSolMapRepository.findByParentId(cs.getSolutionId());
			Assert.assertTrue(parents.iterator().hasNext());
			compSolMap = parents.iterator().next();
			logger.info("Comp sol map child is {}", compSolMap.getChildId());
			MLPCompSolMap.CompSolMapPK compSolMapKey = new MLPCompSolMap.CompSolMapPK(cs.getSolutionId(),
					cs.getSolutionId());
			compSolMapRepository.deleteById(compSolMapKey);

			MLPPublishRequest pubReq = new MLPPublishRequest(cs.getSolutionId(), cr.getRevisionId(), cu.getUserId(),
					"PE");
			pubReq = publishRequestRepository.save(pubReq);
			Assert.assertNotNull(pubReq.getRequestId());
			long reqCountTrans = publishRequestRepository.count();
			Assert.assertNotEquals(0, reqCountTrans);
			Optional<MLPPublishRequest> optPubReq = publishRequestRepository.findById(pubReq.getRequestId());
			Assert.assertTrue(optPubReq.isPresent());
			logger.info("First publish request {}", optPubReq.get());

			Page<MLPPublishRequest> pubReqPage = publishRequestSearchService.findPublishRequests(cs.getSolutionId(),
					null, null, null, null, false, PageRequest.of(0, 5));
			Assert.assertNotEquals(0, pubReqPage.getNumberOfElements());

			// Test search with empty result
			Page<MLPPublishRequest> emptyPubReqs = publishRequestSearchService.findPublishRequests("bogus", "bogus",
					"bogus", "bogus", "bogus", false, PageRequest.of(0, 5));
			Assert.assertTrue(emptyPubReqs.isEmpty());

			publishRequestRepository.deleteById(pubReq.getRequestId());

			if (cleanup) {
				logger.info("Removing newly added entities");
				solRevArtMapRepository
						.deleteById(new MLPSolRevArtMap.SolRevArtMapPK(rev2.getRevisionId(), ca2.getArtifactId()));
				revisionRepository.delete(rev2);
				revisionRepository.delete(cr);
				solutionRatingRepository.delete(solrate);
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solTagMapRepository.deleteById(solTagMapKey);
				catSolMapRepository.delete(csm);
				solutionDownloadRepository.delete(sd);
				solutionRepository.deleteById(cs2.getSolutionId());
				solutionRepository.deleteById(cs.getSolutionId());
				catalogRepository.deleteById(ca1.getCatalogId());
				artifactRepository.delete(ca2);
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				notifUserMapRepository.delete(notifMap);
				notificationRepository.delete(notif);
				siteConfigRepository.delete(cc);
				tagRepository.delete(solTag1);
				tagRepository.delete(solTag2);
				userLoginProviderRepository.delete(ulp);
				userRepository.deleteById(cu.getUserId());

				if (solutionRepository.findById(cs.getSolutionId()).isPresent())
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findById(ca.getArtifactId()).isPresent())
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findById(pr.getPeerId()).isPresent())
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findById(ps.getSubId()).isPresent())
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findById(cu.getUserId()).isPresent())
					throw new Exception("Found a deleted user: " + cu.getUserId());
			}

		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}
	}

	@Test
	public void testValidationConstraints2() throws Exception {
		MLPUser cu = new MLPUser();
		try {
			userRepository.save(cu);
			throw new Exception("Validation failed to catch null field");
		} catch (Exception ex) {
			ConstraintViolationException cve = findConstraintViolationException(ex);
			if (cve == null)
				logger.info("Unexpected exception: " + ex.toString());
			else
				logger.info("Caught expected exception on create user: " + ex.getMessage());
		}
		try {
			cu.setLoginName("illegal extremely long string value should trigger constraint validation annotation");
			userRepository.save(cu);
			throw new Exception("Validation failed to catch long field value");
		} catch (TransactionSystemException ex) {
			logger.info("Caught expected constraint violation exception: " + ex.getMessage());
		}
	}

	@Test
	public void createSolutionWithArtifacts() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			// Want a unique first name for query below
			final String firstName = "FirstCreateSolArts";
			final String lastName = "TestLast";
			final String loginName = "cresolart_user_login";
			final String loginPass = "test_pass";
			cu.setEmail("testcreateSolArtRepouser@abc.com");
			cu.setFirstName(firstName);
			cu.setLastName(lastName);
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setAuthToken("JWT is Greek to me");
			byte[] bytes = { 0, 1, 2, 3, 4, 5 };
			cu.setPicture(bytes);
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			logger.info("Created user " + cu.getUserId());

			// Search by partial match
			logger.info("Searching for user by partial match");
			Iterable<MLPUser> userLikeList = userRepository.findBySearchTerm("First", PageRequest.of(0, 5));
			Assert.assertTrue(userLikeList.iterator().hasNext());

			// Fetch it back
			logger.info("Searching for user by exact match");
			Page<MLPUser> userPage = userSearchService.findUsers(firstName, null, lastName, null, null, null, null,
					false, PageRequest.of(0, 5));
			Assert.assertEquals(1, userPage.getNumberOfElements());

			MLPUserLoginProvider ulp = new MLPUserLoginProvider();
			ulp.setUserId(cu.getUserId());
			ulp.setProviderCode("GH");
			ulp.setProviderUserId("something");
			ulp.setAccessToken("bogus");
			ulp.setRank(0);
			ulp = userLoginProviderRepository.save(ulp);

			// Create Peer
			final String peerName = "Peer-Create-Sol-Arts";
			MLPPeer pr = new MLPPeer(peerName, "some.fqdn.subject.name", "http://peer-api", true, true, "", "AC");
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(), "FL",
					AccessTypeCode.PB.toString());
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertNotNull(ps.getSubId());

			logger.info("Creating artifact with new ID");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setArtifactTypeCode("DI");
			ca.setUserId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			Assert.assertNotNull(ca.getArtifactId());
			Assert.assertNotNull(ca.getCreated());
			Assert.assertNotEquals(0, artifactRepository.count());

			final String artId = "e007ce63-086f-4f33-84c6-cac270874d81";
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setArtifactId(artId);
			ca2.setVersion("2.0A");
			ca2.setName("replicated artifact ");
			ca2.setUri("http://other.foo");
			ca2.setArtifactTypeCode("CD");
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = artifactRepository.save(ca2);
			Assert.assertEquals(artId, ca2.getArtifactId());
			logger.info("Created artifact with preset ID: " + artId);
			artifactRepository.delete(ca2);

			logger.info("Creating solution tags");
			final String tagName1 = "tag1-create-sol-art";
			MLPTag tag1 = new MLPTag(tagName1);
			tagRepository.save(tag1);
			Iterable<MLPTag> tags = tagRepository.findAll();
			Assert.assertTrue(tags.iterator().hasNext());
			logger.info("First tag fetched back is " + tags.iterator().next());

			MLPSolution cs = new MLPSolution();
			cs.setName("solution name");
			cs.setActive(true);
			cs.setUserId(cu.getUserId());
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("SK");
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertNotNull("Solution create time", cs.getCreated());
			logger.info("Created solution " + cs.getSolutionId());

			final Long countBefore = cs.getViewCount();
			logger.info("Solution view count before: " + countBefore);
			solutionRepository.incrementViewCount(cs.getSolutionId());
			Optional<MLPSolution> optSol = solutionRepository.findById(cs.getSolutionId());
			Assert.assertTrue(optSol.isPresent());
			final Long countAfter = optSol.get().getViewCount();
			logger.info("Solution view count after: " + countAfter);
			Assert.assertNotEquals(countBefore, countAfter);

			// add tag
			MLPSolTagMap solTagMap1 = new MLPSolTagMap(cs.getSolutionId(), tag1.getTag());
			solTagMapRepository.save(solTagMap1);

			// Get the solution by ID
			Optional<MLPSolution> optSolById = solutionRepository.findById(cs.getSolutionId());
			Assert.assertTrue(optSolById.isPresent());
			Assert.assertTrue(!optSolById.get().getTags().isEmpty());
			logger.info("Fetched solution: " + optSolById.get());

			// Query for tags on the solution
			Iterable<MLPTag> solTags = tagRepository.findBySolution(cs.getSolutionId());
			Assert.assertTrue(solTags.iterator().hasNext());
			logger.info("Found tag on solution: " + solTags.iterator().next());
			// Find solution by tag
			Page<MLPSolution> taggedSolutions = solutionRepository.findByTag(tag1.getTag(), null);
			Assert.assertNotEquals(0, taggedSolutions.getNumberOfElements());

			// add user to access control list
			MLPSolUserAccMap solUserAccMap = new MLPSolUserAccMap(cs.getSolutionId(), cu.getUserId());
			solUserAccMapRepository.save(solUserAccMap);
			Iterable<MLPUser> usersWithAccess = solUserAccMapRepository.getUsersForSolution(cs.getSolutionId());
			Assert.assertTrue(usersWithAccess.iterator().hasNext());

			// This one has no tags
			MLPSolution cs2 = new MLPSolution();
			cs2.setName("solution name");
			cs2.setActive(true);
			cs2.setUserId(cu.getUserId());
			cs2.setModelTypeCode("CL");
			cs2.setToolkitTypeCode("SK");
			cs2 = solutionRepository.save(cs2);
			Assert.assertNotNull("Solution 2 ID", cs2.getSolutionId());
			logger.info("Created solution 2 " + cs2.getSolutionId());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId(),
					AccessTypeCode.PB.name());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("other name", "other contact") });
			cr.setPublisher("Big Data Org");
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Adding artifact to revision");
			solRevArtMapRepository.save(new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId()));

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = solutionDownloadRepository.save(sd);
			Assert.assertNotNull(sd.getDownloadId());
			Assert.assertNotNull(sd.getDownloadDate());
			logger.info("Created solution download: " + sd.toString());

			// Fetch the download count
			Long downloadCount = solutionDownloadRepository.countSolutionDownloads(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			// Create Solution Rating
			MLPSolutionRating ur = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 1);
			ur.setTextReview("Awesome");
			ur = solutionRatingRepository.save(ur);
			Assert.assertNotNull("Solution create time", ur.getCreated());
			logger.info("Created solution rating: " + ur);

			// Fetch average rating
			List<SolutionRatingStats> ratingStats = solutionRatingRepository.getSolutionRatingStats(cs.getSolutionId());
			Assert.assertNotNull("Solution rating stats", ratingStats);
			Assert.assertFalse(ratingStats.isEmpty());
			Assert.assertEquals(1, ratingStats.size());
			logger.info("Solution rating stats: " + ratingStats.get(0));

			logger.info("Querying for solution by id");
			Optional<MLPSolution> optSi = solutionRepository.findById(cs.getSolutionId());
			Assert.assertTrue(optSi.isPresent());
			MLPSolution si = optSi.get();
			logger.info("Found solution: {}", si);

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", PageRequest.of(0, 5));
			Assert.assertTrue(sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolutionIdIn(new String[] { si.getSolutionId(), cs2.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Iterable<MLPSolRevArtMap> arts = solRevArtMapRepository.findByRevisionId(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
			}

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifactId(ca.getArtifactId());
			Assert.assertTrue(revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			if (cleanup) {
				logger.info("Removing newly added entities");
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solUserAccMapRepository.delete(solUserAccMap);
				solTagMapRepository.deleteById(solTagMapKey);
				solutionRatingRepository.delete(ur);
				solutionDownloadRepository.delete(sd);
				solRevArtMapRepository
						.deleteById(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
				revisionRepository.delete(cr);
				solutionRepository.deleteById(cs.getSolutionId());
				solutionRepository.deleteById(cs2.getSolutionId());
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				userLoginProviderRepository.delete(ulp);
				userRepository.deleteById(cu.getUserId());
				tagRepository.delete(tag1);

				if (solutionRepository.findById(cs.getSolutionId()).isPresent())
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findById(ca.getArtifactId()).isPresent())
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findById(pr.getPeerId()).isPresent())
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findById(ps.getSubId()).isPresent())
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findById(cu.getUserId()).isPresent())
					throw new Exception("Found a deleted user: " + cu.getUserId());

				SolutionRatingPK ratingPK = new SolutionRatingPK(ur.getSolutionId(), ur.getUserId());
				if (solutionRatingRepository.findById(ratingPK).isPresent())
					throw new Exception("Found a deleted rating: " + ratingPK.toString());

				if (solutionDownloadRepository.findById(sd.getDownloadId()).isPresent())
					throw new Exception("Found a deleted download: " + sd.toString());
			}

		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}
	}

	/**
	 * Searches the exception-cause stack for a constraint-violation exceptions.
	 * 
	 * @param t
	 *              Throwable
	 * @return ConstraintViolationException if found; otherwise null.
	 */
	private ConstraintViolationException findConstraintViolationException(Throwable t) {
		while (t != null) {
			if (t instanceof ConstraintViolationException)
				return (ConstraintViolationException) t;
			t = t.getCause();
		}
		return null;
	}

	@Test
	public void testValidationConstraints() throws Exception {
		try {
			userRepository.save(new MLPUser());
			throw new Exception("Validation failed to catch null field");
		} catch (Exception ex) {
			ConstraintViolationException cve = findConstraintViolationException(ex);
			if (cve == null)
				logger.info("Unexpected exception: " + ex.toString());
			else
				logger.info("Caught expected exception: " + ex.getMessage());
		}
		try {
			MLPUser longUser = new MLPUser();
			longUser.setLoginName(
					"illegal extremely long string value should trigger constraint validation annotation");
			userRepository.save(longUser);
			throw new Exception("Validation failed to catch long field value");
		} catch (TransactionSystemException ex) {
			logger.info("Caught expected constraint violation exception: " + ex.getMessage());
		}
	}

	@Test
	public void testRoleAndFunctions() throws Exception {
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			cu.setLoginName("role_fn_user");
			cu.setEmail("testrolefnrepouser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			logger.info("Creating test role");
			MLPRole cr = new MLPRole();
			cr.setName("My test role");
			cr.setActive(true);
			cr = roleRepository.save(cr);
			Assert.assertNotNull(cr.getRoleId());

			logger.info("Searching for role");
			Page<MLPRole> searchRoles = roleSearchService.findRoles(cr.getName(), Boolean.TRUE, false,
					PageRequest.of(0, 5));
			Assert.assertEquals(1, searchRoles.getNumberOfElements());

			Page<MLPRole> emptyRoles = roleSearchService.findRoles("bogus", Boolean.TRUE, false, PageRequest.of(0, 5));
			Assert.assertTrue(emptyRoles.isEmpty());

			logger.info("Assigning role to user");
			userRoleMapRepository.save(new MLPUserRoleMap(cu.getUserId(), cr.getRoleId()));

			Iterable<MLPRole> roles = roleRepository.findByUser(cu.getUserId());
			Assert.assertTrue(roles.iterator().hasNext());

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr.getRoleId());
			roleFunctionRepository.save(crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			logger.info("Checking role content");
			Optional<MLPRole> res = roleRepository.findById(cr.getRoleId());
			Assert.assertTrue(res.isPresent());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRoleId(cr.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("Removing user from role");
			userRoleMapRepository.deleteById(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr.getRoleId()));

			logger.info("Deleting test user");
			userRepository.deleteById(cu.getUserId());

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.deleteById(cr.getRoleId());
		} catch (Exception ex) {
			logger.error("testRoleAndFunctions failed", ex);
			throw ex;
		}

	}

	@Test
	public void testNotifications() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			cu.setLoginName("notif_user_test");
			cu.setEmail("testnotificationuser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPNotification no = new MLPNotification();
			no.setTitle("notif title");
			no.setMessage("notif msg");
			no.setUrl("http://notify.me");
			no.setMsgSeverityCode("HI");
			// Start an hour ago
			no.setStart(Instant.now().minusSeconds(60 * 60));
			// End an hour from now
			no.setEnd(Instant.now().plusSeconds(60 * 60));
			no = notificationRepository.save(no);
			Assert.assertNotNull(no.getNotificationId());

			MLPNotifUserMap nm = new MLPNotifUserMap();
			nm.setNotificationId(no.getNotificationId());
			nm.setUserId(cu.getUserId());
			nm = notifUserMapRepository.save(nm);

			long count = notificationRepository.countActiveUnreadByUser(cu.getUserId());
			Assert.assertEquals(1, count);
			Page<MLPUserNotification> notifs = notificationRepository.findActiveByUser(cu.getUserId(),
					PageRequest.of(0, 5));
			Assert.assertFalse(notifs.getContent().isEmpty());

			// This next step mimics what a controller will do
			nm.setViewed(Instant.now());
			notifUserMapRepository.save(nm);

			// Notif has been viewed; item should have viewed-on date
			notifs = notificationRepository.findActiveByUser(cu.getUserId(), null);
			Assert.assertNotNull(notifs.iterator().next().getViewed());

			notifUserMapRepository.delete(nm);
			notificationRepository.delete(no);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}
	}

	@Test
	public void testUserNotificationPreferences() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			cu.setLoginName("user_notif_pref_test");
			cu.setEmail("testusernotifprefrepouser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPUserNotifPref usrNotifPref = new MLPUserNotifPref();
			usrNotifPref.setUserId(cu.getUserId());
			usrNotifPref.setNotfDelvMechCode("TX");
			usrNotifPref.setMsgSeverityCode("HI");

			usrNotifPref = usrNotifPrefRepository.save(usrNotifPref);
			Assert.assertNotNull(usrNotifPref.getUserNotifPrefId());

			Iterable<MLPUserNotifPref> usrNotifPrefs = usrNotifPrefRepository.findByUserId(cu.getUserId());
			Assert.assertTrue(usrNotifPrefs.iterator().hasNext());

			usrNotifPref.setNotfDelvMechCode("EM");
			usrNotifPref = usrNotifPrefRepository.save(usrNotifPref);
			Optional<MLPUserNotifPref> optNotifPref = usrNotifPrefRepository
					.findById(usrNotifPref.getUserNotifPrefId());
			Assert.assertTrue(optNotifPref.isPresent());
			Assert.assertEquals("EM", optNotifPref.get().getNotfDelvMechCode());

			usrNotifPrefRepository.delete(usrNotifPref);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("testUserNotificationPreferences failed", ex);
			throw ex;
		}

	}

	@Test
	public void testTaskStepResults() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			cu.setLoginName("user_task_step");
			cu.setEmail("test@user.task.step.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			final String taskName = "DoWorkQuickly";
			MLPTask st = new MLPTask("OB", taskName, cu.getUserId(), "SU");
			st = taskRepository.save(st);
			Assert.assertNotNull(st.getTaskId());

			final String statusCode = "FA";

			MLPTaskStepResult sr = new MLPTaskStepResult(st.getTaskId(), "solution ID creation", statusCode,
					Instant.now().minusSeconds(60));
			sr = stepResultRepository.save(sr);
			Assert.assertNotNull(sr.getStepResultId());

			long srCountTrans = stepResultRepository.count();
			Assert.assertNotEquals(0, srCountTrans);

			Optional<MLPTaskStepResult> optResult = stepResultRepository.findById(sr.getStepResultId());
			Assert.assertTrue(optResult.isPresent());
			logger.info("First step result {}", optResult.get());

			Iterable<MLPTaskStepResult> steps = stepResultRepository.findByTaskId(st.getTaskId());
			Assert.assertTrue(steps.iterator().hasNext());

			Page<MLPTaskStepResult> page = stepResultSearchService.findStepResults(null, null, statusCode, false,
					PageRequest.of(0, 5));
			Assert.assertNotEquals(0, page.getNumberOfElements());

			// Test search with empty result
			Page<MLPTaskStepResult> emptySteps = stepResultSearchService.findStepResults(0L, "bogus", "bogus", false,
					PageRequest.of(0, 5));
			Assert.assertTrue(emptySteps.isEmpty());

			sr.setResult("New stack trace");
			stepResultRepository.save(sr);

			Optional<MLPTaskStepResult> optStepResult = stepResultRepository.findById(sr.getStepResultId());
			Assert.assertTrue(optStepResult.isPresent());
			Assert.assertNotNull(optStepResult.get().getResult());
			stepResultRepository.deleteById(sr.getStepResultId());

			taskRepository.delete(st);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("testStepResults failed", ex);
			throw ex;
		}
	}

	@Test
	public void testPeerSolutionGroups() {
		// Need a user to create a solution
		MLPUser cu = null;
		cu = new MLPUser();
		cu.setEmail("testpeersolgrprepouser@abc.com");
		cu.setActive(true);
		cu.setLoginName("peer_sol_grp_user");
		cu = userRepository.save(cu);
		Assert.assertNotNull("User ID", cu.getUserId());
		logger.info("Created user {}", cu);

		MLPSolution cs1 = new MLPSolution("solutionName", cu.getUserId(), true);
		cs1 = solutionRepository.save(cs1);
		Assert.assertNotNull("Solution ID", cs1.getSolutionId());
		logger.info("Created solution {}", cs1);

		MLPSolution cs2 = new MLPSolution("solutionName2", cu.getUserId(), true);
		cs2 = solutionRepository.save(cs2);
		Assert.assertNotNull("Solution ID", cs2.getSolutionId());
		logger.info("Created solution {}", cs2);

		final String peerName = "Peer-Name-Sol-Group-1";
		MLPPeer pr1 = new MLPPeer(peerName, "1.fqdn.subject.name.x.y.z", "http://peer-api-1", true, false, "contact",
				"AC");
		pr1 = peerRepository.save(pr1);
		logger.info("Created peer {}", pr1);

		final String peerName2 = "Peer-Name-2-Sol-Group";
		MLPPeer pr2 = new MLPPeer(peerName2, "2.fqdn.subject.name.a.b.c", "http://peer-api-2", true, false, "contact",
				"AC");
		pr2 = peerRepository.save(pr2);
		logger.info("Created second peer {}", pr2);

		MLPPeerGroup pg1 = new MLPPeerGroup("RepSvcTest peer group 1");
		pg1 = peerGroupRepository.save(pg1);
		Assert.assertNotNull(pg1.getGroupId());
		logger.info("Created peer group {}", pg1);

		MLPPeerGroup pg2 = new MLPPeerGroup("RepSvcTest peer group 2");
		pg2 = peerGroupRepository.save(pg2);
		Assert.assertNotNull(pg2.getGroupId());
		logger.info("Created peer group {} ", pg2);

		MLPSolutionGroup sg = new MLPSolutionGroup("RevSvcTest sol group 1");
		sg = solutionGroupRepository.save(sg);
		Assert.assertNotNull(sg.getGroupId());
		logger.info("Created solution group {}", sg);

		// Used heavily below
		PageRequest pageable = PageRequest.of(0, 3);

		MLPPeerGrpMemMap pgm1 = new MLPPeerGrpMemMap(pg1.getGroupId(), pr1.getPeerId());
		pgm1 = peerGroupMemMapRepository.save(pgm1);
		logger.info("Created peer group map {}", pgm1);
		Page<MLPPeer> peers1 = peerGroupMemMapRepository.findPeersByGroupId(pg1.getGroupId(), pageable);
		Assert.assertNotEquals(0, peers1.getNumberOfElements());

		MLPPeerGrpMemMap pgm2 = new MLPPeerGrpMemMap(pg2.getGroupId(), pr2.getPeerId());
		pgm2 = peerGroupMemMapRepository.save(pgm2);
		logger.info("Created peer group map {}", pgm2);
		Page<MLPPeer> peers2 = peerGroupMemMapRepository.findPeersByGroupId(pg2.getGroupId(), pageable);
		Assert.assertNotEquals(0, peers2.getNumberOfElements());

		MLPSolGrpMemMap gsm = new MLPSolGrpMemMap(sg.getGroupId(), cs1.getSolutionId());
		gsm = solGroupMemMapRepository.save(gsm);
		logger.info("Created solution group map {}", gsm);
		Page<MLPSolution> sols = solGroupMemMapRepository.findSolutionsByGroupId(sg.getGroupId(), pageable);
		Assert.assertNotEquals(0, sols.getNumberOfElements());

		MLPPeerSolAccMap gpsm = new MLPPeerSolAccMap(pg1.getGroupId(), sg.getGroupId(), true);
		gpsm = peerSolAccMapRepository.save(gpsm);
		logger.info("Created peer group - sol group map {}", gpsm);
		MLPPeerSolAccMap.PeerSolAccMapPK pk = new MLPPeerSolAccMap.PeerSolAccMapPK(pg1.getGroupId(), sg.getGroupId());
		Optional<MLPPeerSolAccMap> map = peerSolAccMapRepository.findById(pk);
		Assert.assertTrue(map.isPresent());

		MLPPeerPeerAccMap gppm = new MLPPeerPeerAccMap(pg1.getGroupId(), pg2.getGroupId());
		gppm = peerPeerAccMapRepository.save(gppm);
		logger.info("Created peer group - peer group map {}", gppm);
		MLPPeerPeerAccMap.PeerPeerAccMapPK ppKey = new MLPPeerPeerAccMap.PeerPeerAccMapPK(pg1.getGroupId(),
				pg2.getGroupId());
		Optional<MLPPeerPeerAccMap> ppMap = peerPeerAccMapRepository.findById(ppKey);
		Assert.assertTrue(ppMap.isPresent());

		List<MLPPeer> accessPeers = peerPeerAccMapRepository.findAccessPeers(pr1.getPeerId());
		Assert.assertFalse(accessPeers.isEmpty());

		List<MLPPeer> accessPeers2 = peerPeerAccMapRepository.findAccessPeers(pr2.getPeerId());
		Assert.assertTrue(accessPeers2.isEmpty());

		long yesAccess = peerSolAccMapRepository.checkPeerSolutionAccess(pr1.getPeerId(), cs1.getSolutionId());
		Assert.assertNotEquals(0, yesAccess);
		logger.info("Access count: {}", yesAccess);
		long noAccess = peerSolAccMapRepository.checkPeerSolutionAccess(pr1.getPeerId(), cs2.getSolutionId());
		Assert.assertEquals(0, noAccess);
		logger.info("No access count: {}", noAccess);
		Page<MLPSolution> restrSols = solutionRepository.findRestrictedSolutions(pr1.getPeerId(), pageable);
		Assert.assertNotEquals(0, restrSols.getNumberOfElements());

		peerPeerAccMapRepository.delete(gppm);
		peerSolAccMapRepository.delete(gpsm);
		solGroupMemMapRepository.delete(gsm);
		peerGroupMemMapRepository.delete(pgm2);
		peerGroupMemMapRepository.delete(pgm1);
		solutionGroupRepository.delete(sg);
		peerGroupRepository.delete(pg2);
		peerGroupRepository.delete(pg1);
		peerRepository.delete(pr2);
		peerRepository.delete(pr1);
		solutionRepository.delete(cs2);
		solutionRepository.delete(cs1);
		userRepository.delete(cu);
	}

	@Test
	public void testCatalogs() {
		// Need a user to create a solution
		MLPUser cu = null;
		cu = new MLPUser();
		cu.setEmail("testcatalog@abc.com");
		cu.setActive(true);
		cu.setLoginName("cataloguser");
		cu = userRepository.save(cu);
		Assert.assertNotNull("User ID", cu.getUserId());
		logger.info("Created user {}", cu);

		MLPSolution cs1 = new MLPSolution("solutionName 1 for cat", cu.getUserId(), true);
		cs1 = solutionRepository.save(cs1);
		Assert.assertNotNull("Solution ID", cs1.getSolutionId());
		logger.info("Created solution {}", cs1);

		MLPCatalog ca1 = new MLPCatalog("PB", "name", "http://pub.org");
		ca1 = catalogRepository.save(ca1);
		Assert.assertNotNull("Catalog ID", ca1.getCatalogId());
		logger.info("Created catalog {}", ca1);

		MLPCatSolMap csm = new MLPCatSolMap(ca1.getCatalogId(), cs1.getSolutionId());
		catSolMapRepository.save(csm);

		Page<MLPSolution> sols = catSolMapRepository.findSolutionsByCatalogId(ca1.getCatalogId(), PageRequest.of(0, 3));
		Assert.assertNotNull(sols);
		Assert.assertEquals(1, sols.getNumberOfElements());

		catSolMapRepository.delete(csm);
		catalogRepository.delete(ca1);
		solutionRepository.delete(cs1);
		userRepository.delete(cu);
	}

	@Test
	public void testRightToUse() throws Exception {
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			cu.setLoginName("rtu_user");
			cu.setEmail("testRtuRepoUser@acumos.org");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPSolution cs = new MLPSolution("solName", cu.getUserId(), true);
			cs = solutionRepository.save(cs);
			Assert.assertNotNull(cs.getSolutionId());

			MLPRightToUse cr = new MLPRightToUse(cs.getSolutionId(), true);
			cr = rtuRepository.save(cr);
			Assert.assertNotNull(cr.getRtuId());

			MLPRtuReference ref = new MLPRtuReference("some-lum-system-ID");
			rtuRefRepository.save(ref);

			// cr.getRtuReferences().add(ref);
			// rtuRepository.save(cr);

			MLPRtuRefMap refMap = new MLPRtuRefMap(cr.getRtuId(), ref.getRef());
			rtuRefMapRepository.save(refMap);

			MLPRtuUserMap userMap = new MLPRtuUserMap(cr.getRtuId(), cu.getUserId());
			rtuUserMapRepository.save(userMap);

			Iterable<MLPRightToUse> rtus = rtuRepository.findBySolutionIdUserId(cs.getSolutionId(), cu.getUserId());
			Assert.assertTrue(rtus.iterator().hasNext());

			rtuUserMapRepository.delete(userMap);
			rtuRefMapRepository.delete(refMap);
			rtuRefRepository.delete(ref);
			rtuRepository.delete(cr);
			solutionRepository.delete(cs);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("testRtu failed", ex);
			throw ex;
		}
	}

	@Test
	public void testCodeNameService() {
		for (CodeNameType type : CodeNameType.values()) {
			List<MLPCodeNamePair> list = codeNameService.getCodeNamePairs(type);
			logger.info("testCodeNameService: type {} -> values {}", type, list);
			Assert.assertFalse(list.isEmpty());
		}
	}

	@Test
	public void testErrorConditions() throws Exception {
		try {
			artifactSearchService.findArtifacts(null, null, null, null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		try {
			peerSearchService.findPeers(null, null, null, null, null, null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		try {
			publishRequestSearchService.findPublishRequests(null, null, null, null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		try {
			roleSearchService.findRoles(null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		try {
			stepResultSearchService.findStepResults(null, null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		try {
			userSearchService.findUsers(null, null, null, null, null, null, null, false, PageRequest.of(0, 5));
			throw new Exception("Unexpected success");
		} catch (IllegalArgumentException ex) {
			logger.info("Search failed on missing query as expected: {}", ex.toString());
		}
		// invalid tests
		try {
			usrNotifPrefRepository.save(new MLPUserNotifPref());
			throw new RuntimeException("Unexpected success");
		} catch (Exception ex) {
			logger.info("create user notification preference failed as expected: " + ex.toString());
		}
		try {
			MLPUserNotifPref usrNotifPrefFld = new MLPUserNotifPref();
			usrNotifPrefFld.setUserNotifPrefId(999L);
			usrNotifPrefRepository.save(usrNotifPrefFld);
			throw new RuntimeException("Unexpected success");
		} catch (Exception ex) {
			logger.info("update user notification preference failed as expected:" + ex.toString());
		}
	}

}
