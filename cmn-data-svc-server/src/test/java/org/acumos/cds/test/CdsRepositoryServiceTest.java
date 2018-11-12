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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.LoginProviderCode;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.ModelTypeCode;
import org.acumos.cds.NotificationDeliveryMechanismCode;
import org.acumos.cds.PeerStatusCode;
import org.acumos.cds.StepStatusCode;
import org.acumos.cds.StepTypeCode;
import org.acumos.cds.SubscriptionScopeCode;
import org.acumos.cds.ToolkitTypeCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.domain.MLPArtifact;
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
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
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
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.repository.ArtifactRepository;
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
import org.acumos.cds.repository.RoleFunctionRepository;
import org.acumos.cds.repository.RoleRepository;
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
import org.acumos.cds.repository.SolutionWebRepository;
import org.acumos.cds.repository.StepResultRepository;
import org.acumos.cds.repository.TagRepository;
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
@SuppressWarnings("deprecation")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CdsRepositoryServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
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
	private SolutionWebRepository solutionWebRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolTagMapRepository solTagMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private TagRepository solutionTagRepository;
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
	private UserSearchService userService;
	@Autowired
	private StepResultRepository stepResultRepository;
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

	@Test
	public void testRepositories() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			Date lastLogin = new Date(new Date().getTime() - 60 * 1000);
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			final String firstName = "First_" + Long.toString(new Date().getTime());
			final String lastName = "TestLast";
			final String loginName = "testrepo_" + Long.toString(new Date().getTime());
			final String loginPass = "test_pass3";
			cu.setEmail("testRepouser1@abc.com");
			cu.setFirstName(firstName);
			cu.setLastName(lastName);
			cu.setLoginName(loginName);
			cu.setLoginFailCount((short) 0);
			cu.setLoginFailDate(lastLogin);
			cu.setLoginHash(loginPass);
			cu.setLoginPassExpire(new Date());
			cu = userRepository.save(cu);
			Date originalCreated = cu.getCreated();
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			// Created and modified timestamps may be milliseconds apart
			Assert.assertTrue(Math.abs(cu.getCreated().getTime() - cu.getModified().getTime()) < 5);
			logger.info("Created user {}", cu);
			// Update
			cu.setAuthToken("JWT is Greek to me");
			cu.setLastLogin(lastLogin);
			cu = userRepository.save(cu);
			// Check hibernate behavior on timestamps
			Assert.assertEquals(cu.getCreated(), originalCreated);
			Assert.assertNotEquals(cu.getCreated(), cu.getModified());
			Assert.assertEquals(cu.getLastLogin(), lastLogin);
			// The created-date field accepts updates, unfortunately
			Date changedCreate = new Date(new Date().getTime() - 99);
			cu.setCreated(changedCreate);
			cu = userRepository.save(cu);
			Assert.assertEquals(cu.getCreated(), changedCreate);

			// Fetch it back
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("firstName", firstName);
			restr.put("lastName", lastName);
			Page<MLPUser> userPage = userService.findUsers(restr, true, new PageRequest(0, 5, null));
			Assert.assertTrue(userPage.getNumberOfElements() > 0);
			MLPUser testUser = userPage.iterator().next();
			logger.info("testUser is " + testUser);
			logger.info("cu.getUserID is " + cu.getUserId());

			MLPNotification notif = null;
			notif = new MLPNotification();
			notif.setTitle("Notification title");
			notif.setMessage("Notification message");
			notif.setUrl("http://www.yahoo.com");
			notif.setStart(new Date());
			notif.setMsgSeverityCode(String.valueOf(MessageSeverityCode.LO));
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // Now use today date.
			c.add(Calendar.DATE, 5); // Adding 5 days
			notif.setEnd(c.getTime());
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
			pr.setName("Peer-" + Long.toString(new Date().getTime()));
			pr.setSubjectName("x." + Long.toString(new Date().getTime()));
			pr.setApiUrl("http://peer-api");
			pr.setContact1("Tyrion Lannister");
			pr.setStatusCode(PeerStatusCode.AC.name());
			pr.setValidationStatusCode(ValidationStatusCode.PS.name());
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			// Fetch back
			Map<String, String> peerParms = new HashMap<>();
			peerParms.put("name", pr.getName());
			Page<MLPPeer> searchPeers = peerSearchService.findPeers(peerParms, false, new PageRequest(0, 5, null));
			Assert.assertTrue(searchPeers.getNumberOfElements() == 1);

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(),
					SubscriptionScopeCode.FL.name(), AccessTypeCode.PB.name());
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertNotNull(ps.getSubId());
			// Column was defined as timestamp with autoupdate :(
			Assert.assertNull(ps.getProcessed());
			Date processed = new Date();
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
			Assert.assertTrue(count > 0);
			logger.info("Role count: {}", count);

			Map<String, String> roleParms = new HashMap<>();
			roleParms.put("name", cr2.getName());
			Page<MLPRole> searchRoles = roleSearchService.findRoles(roleParms, false, new PageRequest(0, 5, null));
			Assert.assertTrue(searchRoles.getNumberOfElements() == 1);

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr2.getRoleId());
			crf = roleFunctionRepository.save(crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			logger.info("User Info {}", cu);
			userRoleMapRepository.save(new MLPUserRoleMap(cu.getUserId(), cr2.getRoleId()));

			long usersInRole = userRoleMapRepository.getRoleUsersCount(cr2.getRoleId());
			Assert.assertTrue(usersInRole > 0);
			logger.info("Count of users in role: {}", usersInRole);

			logger.info("Checking role content");
			MLPRole res = roleRepository.findOne(cr2.getRoleId());
			Assert.assertNotNull(res.getRoleId());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRoleId(cr2.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("RoleRepository Info");
			final String UserID = cu.getUserId();
			Iterable<MLPRole> roleList = roleRepository.findByUser(UserID);
			logger.info("Role list {}", roleList);

			userRoleMapRepository.delete(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr2.getRoleId()));

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.delete(cr2.getRoleId());

			logger.info("Creating artifacts");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact1");
			ca.setArtifactTypeCode(ArtifactTypeCode.DI.name());
			ca.setUserId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setVersion("2.0B");
			ca2.setName("second artifact name");
			ca2.setUri("http://nexus/artifact2");
			ca2.setArtifactTypeCode(ArtifactTypeCode.CD.name());
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = artifactRepository.save(ca2);
			Assert.assertTrue(artifactRepository.count() == 2);

			// Fetch artifact back
			Map<String, String> artParms = new HashMap<>();
			artParms.put("name", ca.getName());
			Page<MLPArtifact> searchArts = artifactSearchService.findArtifacts(artParms, false,
					new PageRequest(0, 5, null));
			Assert.assertTrue(searchArts.getNumberOfElements() == 1);

			MLPTag solTag1 = new MLPTag("soltag1");
			solTag1 = solutionTagRepository.save(solTag1);
			MLPTag solTag2 = new MLPTag("soltag2");
			solTag2 = solutionTagRepository.save(solTag2);

			MLPSolution cs = new MLPSolution();
			final String solName = "solution name " + Long.toString(new Date().getTime());
			final String solDesc = "description";
			cs.setName(solName);
			cs.setDescription(solDesc);
			cs.setActive(true);
			cs.setUserId(cu.getUserId());
			cs.setModelTypeCode(ModelTypeCode.CL.name());
			cs.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			// tags must exist; they are not created here
			cs.getTags().add(solTag1);
			cs.getTags().add(solTag2);
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertTrue(cs.getTags().size() == 2);
			logger.info("Created solution " + cs.getSolutionId());

			// This solution has one tag
			MLPSolution cs2 = new MLPSolution();
			cs2.setName("solution name");
			cs2.setActive(true);
			cs2.setUserId(cu.getUserId());
			cs2.setDescription("Another Data Org");
			cs2.setModelTypeCode(ModelTypeCode.CL.toString());
			cs2.setToolkitTypeCode(ToolkitTypeCode.SK.toString());
			cs2.getTags().add(solTag1);
			cs2 = solutionRepository.save(cs2);
			Assert.assertNotNull("Solution 2 ID", cs2.getSolutionId());
			logger.info("Created solution 2 " + cs2.getSolutionId());

			// Search with single values
			Map<String, String> solParms = new HashMap<>();
			solParms.put("name", solName);
			solParms.put("description", solDesc);
			// Limit to one result, which helps detect Hibernate issues
			// of creating a cross-product when it should not
			Page<MLPSolution> searchSols = solutionSearchService.findSolutions(solParms, false,
					new PageRequest(0, 1, null));
			// Ensure a single result; had a bug with dupes due to tags
			Assert.assertEquals(searchSols.getContent().size(), 1);
			// Ensure both tags were retrieved
			Assert.assertEquals(searchSols.getContent().get(0).getTags().size(), 2);

			// Search with a list of values
			Map<String, Object> solListParms = new HashMap<>();
			solListParms.put("name", new String[] { cs.getName() });
			Page<MLPSolution> searchPageSols = solutionSearchService.findSolutions(solListParms, false,
					new PageRequest(0, 5, null));
			Assert.assertFalse(searchPageSols.getContent().isEmpty());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0X", cu.getUserId(),
					AccessTypeCode.PR.name(), ValidationStatusCode.NV.name());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("name1", "contact1"),
					new AuthorTransport("name2", "contact2") });
			cr.setDescription("Some description 2");
			cr.setPublisher("Big Data Org");
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Created solution revision " + cr.getRevisionId());

			MLPSolutionRevision rev2 = new MLPSolutionRevision(cs2.getSolutionId(), "1.0X", cu.getUserId(),
					AccessTypeCode.PR.name(), ValidationStatusCode.NV.name());
			rev2 = revisionRepository.save(rev2);
			Assert.assertNotNull("Revision ID", rev2.getRevisionId());
			logger.info("Created solution revision " + rev2.getRevisionId());

			logger.info("Adding artifact 1 to revision 1");
			solRevArtMapRepository.save(new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId()));
			logger.info("Added" + cr.getRevisionId() + " and " + ca.getArtifactId());

			solRevArtMapRepository.save(new MLPSolRevArtMap(rev2.getRevisionId(), ca2.getArtifactId()));
			logger.info("Added" + rev2.getRevisionId() + " and " + ca2.getArtifactId());

			MLPRevisionDescription revDesc = new MLPRevisionDescription(cr.getRevisionId(), "PB",
					"Some bogus description");
			revDesc = revisionDescRepository.save(revDesc);
			Assert.assertNotNull(revDesc.getCreated());

			logger.info("Finding portal solutions");
			String[] solKw = { solName };
			String[] descKw = { "bogus" }; // hey it's there
			boolean active = true;
			String[] userIds = { cu.getUserId() };
			String[] modelTypeCodes = { ModelTypeCode.CL.name() };
			String[] accTypeCodes = { AccessTypeCode.PR.name(), AccessTypeCode.OR.name() };
			String[] valStatusCodes = { ValidationStatusCode.NV.name() };
			String[] searchTags = { solTag1.getTag() };
			String[] searchAuths = null;
			String[] searchPubs = { "Data" };
			Page<MLPSolution> portalSearchResult = solutionSearchService.findPortalSolutions(solKw, descKw, active,
					userIds, modelTypeCodes, accTypeCodes, valStatusCodes, searchTags, searchAuths, searchPubs,
					new PageRequest(0, 2, Direction.ASC, "name"));
			Assert.assertTrue(portalSearchResult != null && portalSearchResult.getNumberOfElements() == 1);
			logger.info("Found portal solution total " + portalSearchResult.getTotalElements());

			logger.info("Check that one tag yields multiple matches");
			Page<MLPSolution> oneTagSearchResult = solutionSearchService.findPortalSolutions(null, null, active, null,
					null, null, null, searchTags, null, null, new PageRequest(0, 5));
			Assert.assertTrue(oneTagSearchResult != null && oneTagSearchResult.getNumberOfElements() == 2);

			logger.info("Check that two tags yields single match");
			searchTags = new String[] { solTag1.getTag(), solTag2.getTag() };
			Page<MLPSolution> twoTagsSearchResult = solutionSearchService.findPortalSolutions(null, null, active, null,
					null, null, null, searchTags, null, null, new PageRequest(0, 5));
			Assert.assertTrue(twoTagsSearchResult != null && twoTagsSearchResult.getNumberOfElements() == 1);

			String[] ids = { cs.getSolutionId() };
			Page<MLPSolution> idSearchResult = solutionSearchService.findPortalSolutionsByKw(ids, active, userIds,
					modelTypeCodes, accTypeCodes, searchTags, new PageRequest(0, 2, Direction.ASC, "name"));
			Assert.assertTrue(idSearchResult != null && idSearchResult.getNumberOfElements() == 1);
			logger.info("Found models by id total " + idSearchResult.getTotalElements());

			logger.info("Check all vs any tags single match");
			String[] allTags = new String[] { solTag1.getTag() };
			String[] anyTags = new String[] { solTag2.getTag(), "other" };
			Page<MLPSolution> allAnyTagsSearchResult = solutionSearchService.findPortalSolutionsByKwAndTags(null,
					active, userIds, modelTypeCodes, accTypeCodes, allTags, anyTags, new PageRequest(0, 5));
			Assert.assertTrue(allAnyTagsSearchResult != null && allAnyTagsSearchResult.getNumberOfElements() > 0);
			MLPSolution taggedSol = allAnyTagsSearchResult.getContent().get(0);
			Assert.assertTrue(taggedSol.getTags().contains(solTag1) && taggedSol.getTags().contains(solTag2));

			// All keywords must occur in the same field to match
			String[] kw = { "Big", "Data" };
			Page<MLPSolution> kwSearchResult = solutionSearchService.findPortalSolutionsByKw(kw, active, userIds,
					modelTypeCodes, accTypeCodes, searchTags, new PageRequest(0, 2, Direction.ASC, "name"));
			Assert.assertTrue(kwSearchResult != null && kwSearchResult.getNumberOfElements() > 0);
			logger.info("Found models by kw total " + kwSearchResult.getTotalElements());

			logger.info("Querying for artifact by partial match");
			Iterable<MLPArtifact> al = artifactRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(al != null && al.iterator().hasNext());
			logger.info("Artifact list {}", al);

			logger.info("Querying for solution by id");
			MLPSolution si = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(si != null);
			logger.info("Found solution: " + si.toString());

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(sl != null && sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolutionIdIn(new String[] { si.getSolutionId(), cs.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Assert.assertTrue(r.getAuthors().length > 0);
				Iterable<MLPArtifact> arts = artifactRepository.findByRevision(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
				for (MLPArtifact a : arts)
					logger.info("\t\tArtifact: " + a.toString());
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
			revisionDescRepository.delete(new MLPRevisionDescription.RevDescPK(cr.getRevisionId(), "PB"));
			solRevDocMapRepository.delete(docMap);
			documentRepository.delete(doc.getDocumentId());

			docs = documentRepository.findByRevisionAccess(cr.getRevisionId(), "PB");
			Assert.assertFalse(docs.iterator().hasNext());

			// Find by modified date
			String[] accTypes = new String[] { AccessTypeCode.PR.name() };
			String[] valCodes = new String[0];
			Date modifiedDate = new Date();
			modifiedDate.setTime(modifiedDate.getTime() - 60 * 1000);
			Page<MLPSolution> solsByDate = solutionSearchService.findSolutionsByModifiedDate(true, accTypes, valCodes,
					modifiedDate, new PageRequest(0, 5, null));
			Assert.assertTrue(solsByDate != null && solsByDate.getNumberOfElements() > 0);
			logger.info("Found sols by date {}", solsByDate);

			// Touch artifact then search again.
			// Zero/tiny time difference works on derby (code and db in same Java VM)
			// but not when talking to a separate DB server on the same host, note that
			// Derby TIMESTAMP has high precision but Mariadb TIMESTAMP has low precision.
			// Get separation from the previous actions
			Thread.sleep(1000);
			Date justAboutNow = new Date();
			// Get a bit more separation
			Thread.sleep(1000);
			ca.setDescription(ca.getDescription() + " a bit more");
			artifactRepository.save(ca);
			Page<MLPSolution> recentlyUpdated = solutionSearchService.findSolutionsByModifiedDate(true, accTypes,
					valCodes, justAboutNow, new PageRequest(0, 5, null));
			Assert.assertTrue(recentlyUpdated != null && recentlyUpdated.getNumberOfElements() == 1);
			logger.info("Found recently updated sols {}", recentlyUpdated);

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = solutionDownloadRepository.save(sd);
			Assert.assertNotNull(sd.getDownloadId());
			Assert.assertNotNull(sd.getDownloadDate());
			logger.info("Created solution download: " + sd.toString());

			// Fetch the download count
			Long downloadCount = solutionDownloadRepository.getSolutionDownloadCount(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			logger.info("Querying for solution downloads for the specified solution ID");
			Iterable<MLPSolutionDownload> soldown = solutionDownloadRepository.findBySolutionId(cs.getSolutionId(),
					new PageRequest(0, 5, null));
			logger.info("solutionDownloadRepository list {}", soldown);

			MLPSolutionRating solrate = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 2);
			solrate.setTextReview("Review text");
			solrate.setCreated(new Date());
			solrate = solutionRatingRepository.save(solrate);
			Assert.assertNotNull(solrate.getSolutionId());
			logger.info("Created Solution Rating " + solrate.getSolutionId() + " Rating is " + solrate.getRating());

			logger.info("Querying for solution rating for the specified solution ID");
			Iterable<MLPSolutionRating> solrating = solutionRatingRepository.findBySolutionId(cs.getSolutionId(),
					new PageRequest(0, 5, null));
			logger.info("SolutionRatingRepository list: {}", solrating);

			logger.info("Creating solution tag");
			MLPTag tag1 = new MLPTag("Java");
			tag1 = solutionTagRepository.save(tag1);
			Iterable<MLPTag> tags = solutionTagRepository.findAll();
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

			Iterable<MLPTag> soltag2 = solutionTagRepository.findBySolution(soltag.getSolutionId());
			logger.info("Solution tag: {}", soltag2);
			logger.info("Solution Tag list above");

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifactId(ca.getArtifactId());
			Assert.assertTrue(revsByArt != null && revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			logger.info("Querying for revisions by search term");
			Page<MLPSolutionRevision> rl = revisionRepository.findBySearchTerm("Some", new PageRequest(0, 5, null));
			Assert.assertFalse(rl.getContent().isEmpty());
			logger.info("Revision list: {}", rl);

			logger.info("Querying for user by partial match");
			Page<MLPUser> sul = userRepository.findBySearchTerm("Test", new PageRequest(0, 5, null));
			Assert.assertFalse(sul.getContent().isEmpty());
			logger.info("User list: {}", sul);

			Page<MLPSolution> solByTag = solutionRepository.findByTag("Java", new PageRequest(0, 5, null));
			logger.info("Solutions by tag: {}", solByTag);
			Assert.assertFalse(solByTag.getContent().isEmpty());

			String[] accessTypes = new String[] { AccessTypeCode.PR.name() };
			String[] valStatuses = new String[] { ValidationStatusCode.NV.name() };
			Date anHourAgo = new java.util.Date();
			anHourAgo.setTime(new Date().getTime() - (1000L * 60 * 60));

			Page<MLPSolution> solByCriteria = solutionSearchService.findSolutionsByModifiedDate(true, accessTypes,
					valStatuses, anHourAgo, new PageRequest(0, 5));
			logger.info("Solutions by date via criteria: {}", solByCriteria);
			Assert.assertFalse(solByCriteria.getContent().isEmpty());

			MLPUser founduser = userRepository.findByLoginOrEmail("test_user7");
			logger.info("Found user: {}", founduser);

			logger.info("Dropping artifact from revision");
			solRevArtMapRepository.delete(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
			logger.info("Dropped" + cr.getRevisionId() + " and " + ca.getArtifactId());

			MLPSiteConfig cc = new MLPSiteConfig("repotest-siteconfig", " { \"json\" : \"block\" }");
			cc = siteConfigRepository.save(cc);
			Assert.assertNotNull(cc);
			logger.info("Created site config {}", cc);
			siteConfigRepository.delete(cc);

			MLPThread thread = threadRepository.save(new MLPThread(cs.getSolutionId(), cr.getRevisionId()));
			Assert.assertTrue(thread.getThreadId() != null);
			logger.info("Created thread {}", thread);

			long threadCount = threadRepository.countBySolutionIdAndRevisionId(cs.getSolutionId(), cr.getRevisionId());
			Assert.assertTrue(threadCount > 0);
			Page<MLPThread> threads = threadRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), new PageRequest(0, 5, null));
			Assert.assertTrue(threads != null && threads.getNumberOfElements() > 0);

			MLPComment mc = commentRepository.save(new MLPComment(thread.getThreadId(), cu.getUserId(), "c"));
			long crc = commentRepository.count();
			Assert.assertTrue(crc > 0);
			long tcc = commentRepository.countThreadComments(thread.getThreadId());
			Assert.assertTrue(tcc > 0);
			Page<MLPComment> commentList = commentRepository.findByThreadId(thread.getThreadId(),
					new PageRequest(0, 5));
			Assert.assertTrue(commentList != null && commentList.hasContent());
			Page<MLPComment> solRevComments = commentRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), new PageRequest(0, 5, null));
			Assert.assertTrue(solRevComments != null && solRevComments.hasContent());

			commentRepository.delete(mc.getCommentId());
			threadRepository.delete(thread.getThreadId());

			// Composite solution support was added very late
			MLPCompSolMap compSolMap = new MLPCompSolMap(cs.getSolutionId(), cs.getSolutionId());
			logger.info("Created comp sol map {}", compSolMap.toString());
			compSolMapRepository.save(compSolMap);
			Iterable<MLPCompSolMap> parents = compSolMapRepository.findByParentId(cs.getSolutionId());
			Assert.assertTrue(parents != null && parents.iterator().hasNext());
			compSolMap = parents.iterator().next();
			logger.info("Comp sol map child is {}", compSolMap.getChildId());
			MLPCompSolMap.CompSolMapPK compSolMapKey = new MLPCompSolMap.CompSolMapPK(cs.getSolutionId(),
					cs.getSolutionId());
			compSolMapRepository.delete(compSolMapKey);

			MLPPublishRequest pubReq = new MLPPublishRequest(cs.getSolutionId(), cr.getRevisionId(), cu.getUserId(),
					"PE");
			pubReq = publishRequestRepository.save(pubReq);
			Assert.assertNotNull(pubReq.getRequestId());
			long reqCountTrans = publishRequestRepository.count();
			Assert.assertTrue(reqCountTrans > 0);
			MLPPublishRequest reqFound = publishRequestRepository.findOne(pubReq.getRequestId());
			Assert.assertNotNull(reqFound);
			logger.info("First publish request {}", reqFound);
			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("solutionId", cs.getSolutionId());
			Page<MLPPublishRequest> pubReqPage = publishRequestSearchService.findPublishRequests(queryParameters, false,
					new PageRequest(0, 5, null));
			Assert.assertTrue(pubReqPage.getNumberOfElements() > 0);
			publishRequestRepository.delete(pubReq.getRequestId());

			if (cleanup) {
				logger.info("Removing newly added entities");
				solRevArtMapRepository
						.delete(new MLPSolRevArtMap.SolRevArtMapPK(rev2.getRevisionId(), ca2.getArtifactId()));
				revisionRepository.delete(rev2);
				revisionRepository.delete(cr);
				solutionRatingRepository.delete(solrate);
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solTagMapRepository.delete(solTagMapKey);
				solutionDownloadRepository.delete(sd);
				solutionRepository.delete(cs2.getSolutionId());
				solutionRepository.delete(cs.getSolutionId());
				artifactRepository.delete(ca2);
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				notifUserMapRepository.delete(notifMap);
				notificationRepository.delete(notif);
				siteConfigRepository.delete(cc);
				userLoginProviderRepository.delete(ulp);
				userRepository.delete(cu.getUserId());

				if (solutionRepository.findOne(cs.getSolutionId()) != null)
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findOne(ca.getArtifactId()) != null)
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findOne(pr.getPeerId()) != null)
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findOne(ps.getSubId()) != null)
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findOne(cu.getUserId()) != null)
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
			final String firstName = "First" + Long.toString(new Date().getTime());
			final String lastName = "TestLast";
			final String loginName = "cresol_" + Long.toString(new Date().getTime());
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
			Iterable<MLPUser> userLikeList = userRepository.findBySearchTerm("First", new PageRequest(0, 5));
			Assert.assertTrue(userLikeList.iterator().hasNext());

			// Fetch it back
			logger.info("Searching for user by exact match");
			HashMap<String, Object> restr = new HashMap<>();
			// restr.put("active", true);
			restr.put("firstName", firstName);
			restr.put("lastName", lastName);
			Page<MLPUser> userPage = userService.findUsers(restr, false, new PageRequest(0, 5));
			Assert.assertTrue(userPage.getNumberOfElements() == 1);

			MLPUserLoginProvider ulp = new MLPUserLoginProvider();
			ulp.setUserId(cu.getUserId());
			ulp.setProviderCode(LoginProviderCode.GH.name());
			ulp.setProviderUserId("something");
			ulp.setAccessToken("bogus");
			ulp.setRank(0);
			ulp = userLoginProviderRepository.save(ulp);

			// Create Peer
			final String peerName = "Peer-" + Long.toString(new Date().getTime());
			MLPPeer pr = new MLPPeer(peerName, "x." + Long.toString(new Date().getTime()), "http://peer-api", true,
					true, "", PeerStatusCode.AC.name(), ValidationStatusCode.NV.name());
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(),
					SubscriptionScopeCode.FL.toString(), AccessTypeCode.PB.toString());
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertNotNull(ps.getSubId());

			logger.info("Creating artifact with new ID");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setArtifactTypeCode(ArtifactTypeCode.DI.name());
			ca.setUserId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			Assert.assertNotNull(ca.getArtifactId());
			Assert.assertNotNull(ca.getCreated());
			Assert.assertTrue(artifactRepository.count() > 0);

			final String artId = "e007ce63-086f-4f33-84c6-cac270874d81";
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setArtifactId(artId);
			ca2.setVersion("2.0A");
			ca2.setName("replicated artifact ");
			ca2.setUri("http://other.foo");
			ca2.setArtifactTypeCode(ArtifactTypeCode.CD.toString());
			ca2.setUserId(cu.getUserId());
			ca2.setSize(456);
			ca2 = artifactRepository.save(ca2);
			Assert.assertEquals(artId, ca2.getArtifactId());
			logger.info("Created artifact with preset ID: " + artId);
			artifactRepository.delete(ca2);

			logger.info("Creating solution tags");
			final String tagName1 = "tag1-" + Long.toString(new Date().getTime());
			MLPTag tag1 = new MLPTag(tagName1);
			solutionTagRepository.save(tag1);
			Iterable<MLPTag> tags = solutionTagRepository.findAll();
			Assert.assertTrue(tags.iterator().hasNext());
			logger.info("First tag fetched back is " + tags.iterator().next());

			MLPSolution cs = new MLPSolution();
			cs.setName("solution name");
			cs.setActive(true);
			cs.setUserId(cu.getUserId());
			cs.setModelTypeCode(ModelTypeCode.CL.name());
			cs.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertNotNull("Solution create time", cs.getCreated());
			logger.info("Created solution " + cs.getSolutionId());

			MLPSolutionWeb stats = new MLPSolutionWeb();
			stats.setSolutionId(cs.getSolutionId());
			stats = solutionWebRepository.save(stats);
			Assert.assertNotNull(stats);
			final Long countBefore = stats.getViewCount();
			logger.info("Solution view count before: " + countBefore);
			solutionWebRepository.incrementViewCount(cs.getSolutionId());
			stats = solutionWebRepository.findOne(stats.getSolutionId());
			final Long countAfter = stats.getViewCount();
			logger.info("Solution view count after: " + countAfter);
			Assert.assertNotEquals(countBefore, countAfter);

			// add tag
			MLPSolTagMap solTagMap1 = new MLPSolTagMap(cs.getSolutionId(), tag1.getTag());
			solTagMapRepository.save(solTagMap1);

			// Get the solution by ID
			MLPSolution solById = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(solById != null && !solById.getTags().isEmpty());
			logger.info("Fetched solution: " + solById);

			// Query for tags on the solution
			Iterable<MLPTag> solTags = solutionTagRepository.findBySolution(cs.getSolutionId());
			Assert.assertTrue(solTags.iterator().hasNext());
			logger.info("Found tag on solution: " + solTags.iterator().next());
			// Find solution by tag
			Page<MLPSolution> taggedSolutions = solutionRepository.findByTag(tag1.getTag(), null);
			Assert.assertTrue(taggedSolutions.getNumberOfElements() > 0);

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
			cs2.setDescription("Big Data Org");
			cs2.setModelTypeCode(ModelTypeCode.CL.toString());
			cs2.setToolkitTypeCode(ToolkitTypeCode.SK.toString());
			cs2 = solutionRepository.save(cs2);
			Assert.assertNotNull("Solution 2 ID", cs2.getSolutionId());
			logger.info("Created solution 2 " + cs2.getSolutionId());

			MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "1.0R", cu.getUserId(),
					AccessTypeCode.PB.name(), ValidationStatusCode.SB.name());
			cr.setAuthors(new AuthorTransport[] { new AuthorTransport("other name", "other contact") });
			cr.setDescription("Some description");
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
			Long downloadCount = solutionDownloadRepository.getSolutionDownloadCount(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			// Create Solution Rating
			MLPSolutionRating ur = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 1);
			ur.setTextReview("Awesome");
			ur = solutionRatingRepository.save(ur);
			Assert.assertNotNull("Solution create time", ur.getCreated());
			logger.info("Created solution rating: " + ur);

			// Fetch average rating
			Double avgRating = solutionRatingRepository.getSolutionRatingAverage(cs.getSolutionId());
			Assert.assertNotNull("Solution rating average", avgRating);
			logger.info("Solution rating average: " + avgRating);

			logger.info("Querying for solution by id");
			MLPSolution si = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(si != null);
			logger.info("Found solution: " + si.toString());

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(sl != null && sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolutionIdIn(new String[] { si.getSolutionId(), cs2.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Iterable<MLPArtifact> arts = artifactRepository.findByRevision(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
				for (MLPArtifact a : arts)
					logger.info("\t\tArtifact: " + a.toString());
			}

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifactId(ca.getArtifactId());
			Assert.assertTrue(revsByArt != null && revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			if (cleanup) {
				logger.info("Removing newly added entities");
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solutionWebRepository.delete(cs.getSolutionId());
				solUserAccMapRepository.delete(solUserAccMap);
				solTagMapRepository.delete(solTagMapKey);
				solutionRatingRepository.delete(ur);
				solutionDownloadRepository.delete(sd);
				solRevArtMapRepository
						.delete(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
				revisionRepository.delete(cr);
				solutionRepository.delete(cs.getSolutionId());
				solutionRepository.delete(cs2.getSolutionId());
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				userLoginProviderRepository.delete(ulp);
				userRepository.delete(cu.getUserId());
				solutionTagRepository.delete(tag1);

				if (solutionRepository.findOne(cs.getSolutionId()) != null)
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findOne(ca.getArtifactId()) != null)
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findOne(pr.getPeerId()) != null)
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findOne(ps.getSubId()) != null)
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findOne(cu.getUserId()) != null)
					throw new Exception("Found a deleted user: " + cu.getUserId());

				SolutionRatingPK ratingPK = new SolutionRatingPK(ur.getSolutionId(), ur.getUserId());
				if (solutionRatingRepository.findOne(ratingPK) != null)
					throw new Exception("Found a deleted rating: " + ratingPK.toString());

				if (solutionDownloadRepository.findOne(sd.getDownloadId()) != null)
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
		MLPUser cu = new MLPUser();
		try {
			userRepository.save(cu);
			throw new Exception("Validation failed to catch null field");
		} catch (Exception ex) {
			ConstraintViolationException cve = findConstraintViolationException(ex);
			if (cve == null)
				logger.info("Unexpected exception: " + ex.toString());
			else
				logger.info("Caught expected exception: " + ex.getMessage());
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
	public void testRoleAndFunctions() throws Exception {
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			final String loginName = "rolefnuser_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu.setEmail("testrolefnrepouser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			logger.info("Creating test role");
			MLPRole cr = new MLPRole();
			cr.setName("My test role");
			cr = roleRepository.save(cr);
			Assert.assertNotNull(cr.getRoleId());

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
			MLPRole res = roleRepository.findOne(cr.getRoleId());
			Assert.assertNotNull(res.getRoleId());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRoleId(cr.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("Removing user from role");
			userRoleMapRepository.delete(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr.getRoleId()));

			logger.info("Deleting test user");
			userRepository.delete(cu.getUserId());

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.delete(cr.getRoleId());
		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}

	}

	@Test
	public void testNotifications() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			final String loginName = "notif_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu.setEmail("testnotificationuser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPNotification no = new MLPNotification();
			no.setTitle("notif title");
			no.setMessage("notif msg");
			no.setUrl("http://notify.me");
			no.setMsgSeverityCode(String.valueOf(MessageSeverityCode.HI));
			Date now = new Date();
			// Start an hour ago
			no.setStart(new Date(now.getTime() - 60 * 60 * 1000));
			// End an hour from now
			no.setEnd(new Date(now.getTime() + 60 * 60 * 1000));
			no = notificationRepository.save(no);
			Assert.assertNotNull(no.getNotificationId());

			MLPNotifUserMap nm = new MLPNotifUserMap();
			nm.setNotificationId(no.getNotificationId());
			nm.setUserId(cu.getUserId());
			nm = notifUserMapRepository.save(nm);

			Page<MLPUserNotification> notifs = notificationRepository.findActiveByUser(cu.getUserId(),
					new PageRequest(0, 5));
			Assert.assertFalse(notifs.getContent().isEmpty());

			// This next step mimics what a controller will do
			nm.setViewed(new Date());
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
			final String loginName = "testnotif_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu.setEmail("testusernotifprefrepouser@abc.com");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPUserNotifPref usrNotifPref = new MLPUserNotifPref();
			usrNotifPref.setUserId(cu.getUserId());
			usrNotifPref.setNotfDelvMechCode(String.valueOf(NotificationDeliveryMechanismCode.TX));
			usrNotifPref.setMsgSeverityCode(String.valueOf(MessageSeverityCode.HI));

			usrNotifPref = usrNotifPrefRepository.save(usrNotifPref);
			Assert.assertNotNull(usrNotifPref.getUserNotifPrefId());

			Iterable<MLPUserNotifPref> usrNotifPrefs = usrNotifPrefRepository.findByUserId(cu.getUserId());
			Assert.assertTrue(usrNotifPrefs.iterator().hasNext());

			usrNotifPref.setNotfDelvMechCode(String.valueOf(NotificationDeliveryMechanismCode.EM));
			usrNotifPref = usrNotifPrefRepository.save(usrNotifPref);
			Assert.assertTrue(usrNotifPrefRepository.findOne(usrNotifPref.getUserNotifPrefId()).getNotfDelvMechCode()
					.equals("EM"));

			usrNotifPrefRepository.delete(usrNotifPref);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("testUserNotificationPreferences failed", ex);
			throw ex;
		}

		// invalid tests
		try {
			usrNotifPrefRepository.save(new MLPUserNotifPref());
			throw new Exception("Unexpected success");
		} catch (ConstraintViolationException ex) {
			logger.info("create user notification preference failed as expected: " + ex.toString());
		}
		try {
			MLPUserNotifPref usrNotifPrefFld = new MLPUserNotifPref();
			usrNotifPrefFld.setUserNotifPrefId(999L);
			usrNotifPrefRepository.save(usrNotifPrefFld);
			throw new Exception("Unexpected success");
		} catch (ConstraintViolationException ex) {
			logger.info("update user notification preference failed as expected:" + ex.toString());
		}
	}

	@Test
	public void testStepResults() throws Exception {
		try {
			MLPStepResult sr = new MLPStepResult();
			sr.setStepCode(String.valueOf(StepTypeCode.OB));
			sr.setName("Solution ID creation");
			sr.setStatusCode(StepStatusCode.FA.name());
			Date now = new Date();
			sr.setStartDate(new Date(now.getTime() - 60 * 1000));
			sr = stepResultRepository.save(sr);
			Assert.assertNotNull(sr.getStepResultId());

			long srCountTrans = stepResultRepository.count();
			Assert.assertTrue(srCountTrans > 0);

			MLPStepResult result = stepResultRepository.findOne(sr.getStepResultId());
			Assert.assertNotNull(result);
			logger.info("First step result {}", result);

			HashMap<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("statusCode", StepStatusCode.FA.toString());
			Page<MLPStepResult> page = stepResultSearchService.findStepResults(queryParameters, false,
					new PageRequest(0, 5, null));
			Assert.assertTrue(page.getNumberOfElements() > 0);

			sr.setResult("New stack trace");
			stepResultRepository.save(sr);

			Assert.assertNotNull(stepResultRepository.findOne(sr.getStepResultId()).getResult());
			stepResultRepository.delete(sr.getStepResultId());
		} catch (Exception ex) {
			logger.error("Failed", ex);
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
		final String loginName = "test_user_" + Long.toString(new Date().getTime());
		cu.setLoginName(loginName);
		cu = userRepository.save(cu);
		Assert.assertNotNull("User ID", cu.getUserId());
		logger.info("Created user " + cu);

		MLPSolution cs1 = new MLPSolution("solutionName", cu.getUserId(), true);
		cs1 = solutionRepository.save(cs1);
		Assert.assertNotNull("Solution ID", cs1.getSolutionId());
		logger.info("Created solution " + cs1);

		MLPSolution cs2 = new MLPSolution("solutionName2", cu.getUserId(), true);
		cs2 = solutionRepository.save(cs2);
		Assert.assertNotNull("Solution ID", cs2.getSolutionId());
		logger.info("Created solution " + cs2);

		final String peerName = "Peer-" + Long.toString(new Date().getTime());
		MLPPeer pr1 = new MLPPeer(peerName, "x." + Long.toString(new Date().getTime()), "http://peer-api", true, false,
				"contact", PeerStatusCode.AC.name(), ValidationStatusCode.FA.name());
		pr1 = peerRepository.save(pr1);
		logger.info("Created peer " + pr1);

		final String peerName2 = "Peer-" + Long.toString(new Date().getTime());
		MLPPeer pr2 = new MLPPeer(peerName2, "x." + Long.toString(new Date().getTime()), "http://peer-api", true, false,
				"contact", PeerStatusCode.AC.name(), ValidationStatusCode.FA.name());
		pr2 = peerRepository.save(pr2);
		logger.info("Created second peer " + pr2);

		MLPPeerGroup pg1 = new MLPPeerGroup("RepSvcTest peer group 1");
		pg1 = peerGroupRepository.save(pg1);
		Assert.assertNotNull(pg1.getGroupId());
		logger.info("Created peer group " + pg1);

		MLPPeerGroup pg2 = new MLPPeerGroup("RepSvcTest peer group 2");
		pg2 = peerGroupRepository.save(pg2);
		Assert.assertNotNull(pg2.getGroupId());
		logger.info("Created peer group " + pg2);

		MLPSolutionGroup sg = new MLPSolutionGroup("RevSvcTest sol group 1");
		sg = solutionGroupRepository.save(sg);
		Assert.assertNotNull(sg.getGroupId());
		logger.info("Created solution group " + sg);

		// Used heavily below
		PageRequest pageable = new PageRequest(0, 3);

		MLPPeerGrpMemMap pgm1 = new MLPPeerGrpMemMap(pg1.getGroupId(), pr1.getPeerId());
		pgm1 = peerGroupMemMapRepository.save(pgm1);
		logger.info("Created peer group map " + pgm1);
		Page<MLPPeer> peers1 = peerGroupMemMapRepository.findPeersByGroupId(pg1.getGroupId(), pageable);
		Assert.assertTrue(peers1 != null && peers1.getNumberOfElements() > 0);

		MLPPeerGrpMemMap pgm2 = new MLPPeerGrpMemMap(pg2.getGroupId(), pr2.getPeerId());
		pgm2 = peerGroupMemMapRepository.save(pgm2);
		logger.info("Created peer group map " + pgm2);
		Page<MLPPeer> peers2 = peerGroupMemMapRepository.findPeersByGroupId(pg2.getGroupId(), pageable);
		Assert.assertTrue(peers2 != null && peers2.getNumberOfElements() > 0);

		MLPSolGrpMemMap gsm = new MLPSolGrpMemMap(sg.getGroupId(), cs1.getSolutionId());
		gsm = solGroupMemMapRepository.save(gsm);
		logger.info("Created solution group map " + gsm);
		Page<MLPSolution> sols = solGroupMemMapRepository.findSolutionsByGroupId(sg.getGroupId(), pageable);
		Assert.assertTrue(sols != null && sols.getNumberOfElements() > 0);

		MLPPeerSolAccMap gpsm = new MLPPeerSolAccMap(pg1.getGroupId(), sg.getGroupId(), true);
		gpsm = peerSolAccMapRepository.save(gpsm);
		logger.info("Created peer group - sol group map " + gpsm);
		MLPPeerSolAccMap.PeerSolAccMapPK pk = new MLPPeerSolAccMap.PeerSolAccMapPK(pg1.getGroupId(), sg.getGroupId());
		MLPPeerSolAccMap map = peerSolAccMapRepository.findOne(pk);
		Assert.assertNotNull(map);

		MLPPeerPeerAccMap gppm = new MLPPeerPeerAccMap(pg1.getGroupId(), pg2.getGroupId());
		gppm = peerPeerAccMapRepository.save(gppm);
		logger.info("Created peer group - peer group map " + gppm);
		MLPPeerPeerAccMap.PeerPeerAccMapPK ppKey = new MLPPeerPeerAccMap.PeerPeerAccMapPK(pg1.getGroupId(),
				pg2.getGroupId());
		MLPPeerPeerAccMap ppMap = peerPeerAccMapRepository.findOne(ppKey);
		Assert.assertNotNull(ppMap);

		List<MLPPeer> accessPeers = peerPeerAccMapRepository.findAccessPeers(pr1.getPeerId());
		Assert.assertTrue(accessPeers != null && !accessPeers.isEmpty());

		List<MLPPeer> accessPeers2 = peerPeerAccMapRepository.findAccessPeers(pr2.getPeerId());
		Assert.assertTrue(accessPeers2 != null && accessPeers2.isEmpty());

		long yesAccess = peerSolAccMapRepository.checkPeerSolutionAccess(pr1.getPeerId(), cs1.getSolutionId());
		Assert.assertTrue(yesAccess > 0);
		logger.info("Access count: " + yesAccess);
		long noAccess = peerSolAccMapRepository.checkPeerSolutionAccess(pr1.getPeerId(), cs2.getSolutionId());
		Assert.assertTrue(noAccess == 0);
		logger.info("No access count: " + noAccess);
		Page<MLPSolution> restrSols = solutionRepository.findRestrictedSolutions(pr1.getPeerId(), pageable);
		Assert.assertTrue(restrSols != null && restrSols.getNumberOfElements() > 0);

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
	public void testCodeNameService() {
		for (CodeNameType type : CodeNameType.values()) {
			List<MLPCodeNamePair> list = codeNameService.getCodeNamePairs(type);
			logger.info("testCodeNameService: type {} -> values {}", type, list);
			Assert.assertFalse(list.isEmpty());
		}
	}

}
