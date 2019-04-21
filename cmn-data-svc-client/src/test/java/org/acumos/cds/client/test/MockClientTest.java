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

package org.acumos.cds.client.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Cover the mock methods.
 */
public class MockClientTest {

	static class TrivialRestClientMockImplSubclass extends CommonDataServiceRestClientMockImpl {
		public TrivialRestClientMockImplSubclass(String webapiUrl, String user, String pass) {
			super(webapiUrl, user, pass);
			super.getRestTemplate();
		}
	}

	@Test
	public void coverMockMethods() {

		new TrivialRestClientMockImplSubclass("usl", "usr", "pass");
		CommonDataServiceRestClientMockImpl.getInstance("url", null);
		CommonDataServiceRestClientMockImpl.getInstance("url", "user", "pass");

		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl();
		client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		client = new CommonDataServiceRestClientMockImpl("url", new RestTemplate());

		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		Map<String, Object> queryParameters = new HashMap<>();
		boolean isOr = false;
		List<String> stringList = new ArrayList<>();

		SuccessTransport success = new SuccessTransport();
		client.setHealth(success);
		Assert.assertTrue(success == client.getHealth());
		client.setVersion(success);
		Assert.assertTrue(success == client.getVersion());

		long count = 3;
		client.setSolutionCount(count);
		Assert.assertTrue(count == client.getSolutionCount());

		RestPageResponse<MLPSolution> solutions1 = new RestPageResponse<>();
		client.setSolutions(solutions1);
		Assert.assertTrue(solutions1 == client.getSolutions(pageRequest));
		RestPageResponse<MLPSolution> solutions2 = new RestPageResponse<>();
		client.setSolutionsBySearchTerm(solutions2);
		Assert.assertTrue(solutions2 == client.findSolutionsBySearchTerm("string", pageRequest));
		RestPageResponse<MLPSolution> solutions3 = new RestPageResponse<>();
		client.setSolutionsByTag(solutions3);
		Assert.assertTrue(solutions3 == client.findSolutionsByTag("string", pageRequest));

		MLPSolution solution = new MLPSolution();
		client.setSolutionById(solution);
		Assert.assertTrue(solution == client.getSolution("id"));
		client.setSolution(solution);
		Assert.assertTrue(solution == client.createSolution(solution));
		client.updateSolution(solution);
		client.incrementSolutionViewCount("id");
		client.deleteSolution("id");

		client.saveSolutionPicture("id", new byte[0]);
		byte[] img = client.getSolutionPicture("id");
		Assert.assertNotNull(img);

		List<MLPSolutionRevision> solRevList = new ArrayList<>();
		client.setSolutionRevisionsById(solRevList);
		Assert.assertTrue(solRevList == client.getSolutionRevisions("id"));
		client.setSolutionRevisionsByIdList(solRevList);
		Assert.assertTrue(solRevList == client.getSolutionRevisions(new String[] { "id" }));
		client.setSolutionRevisionsForArtifact(solRevList);
		Assert.assertTrue(solRevList == client.getSolutionRevisionsForArtifact("id"));

		MLPSolutionRevision solRev = new MLPSolutionRevision();
		client.setSolutionRevisionById(solRev);
		Assert.assertTrue(solRev == client.getSolutionRevision("sid", "rid"));
		client.setSolutionRevision(solRev);
		Assert.assertTrue(solRev == client.createSolutionRevision(solRev));
		client.updateSolutionRevision(solRev);
		client.deleteSolutionRevision("id", "id");

		List<MLPArtifact> artList = new ArrayList<>();
		client.setSolutionRevisionArtifacts(artList);
		Assert.assertTrue(artList == client.getSolutionRevisionArtifacts("id", "id"));
		client.addSolutionRevisionArtifact("id", "id", "id");
		client.dropSolutionRevisionArtifact("id", "id", "id");

		RestPageResponse<MLPTag> tags = new RestPageResponse<>();
		client.setTags(tags);
		Assert.assertTrue(tags == client.getTags(pageRequest));

		MLPTag tag = new MLPTag();
		client.setTag(tag);
		Assert.assertTrue(tag == client.createTag(tag));
		client.deleteTag(tag);

		List<MLPTag> tagList = new ArrayList<>();
		client.setSolutionTags(tagList);
		Assert.assertTrue(tagList == client.getSolutionTags("id"));
		client.addSolutionTag("id", "tag");
		client.dropSolutionTag("id", "tag");

		client.setArtifactCount(count);
		Assert.assertTrue(count == client.getArtifactCount());

		RestPageResponse<MLPArtifact> artifacts = new RestPageResponse<>();
		client.setArtifacts(artifacts);
		Assert.assertTrue(artifacts == client.getArtifacts(pageRequest));
		client.setArtifactsBySearchTerm(artifacts);
		Assert.assertTrue(artifacts == client.findArtifactsBySearchTerm("search", pageRequest));
		RestPageResponse<MLPSolution> solutions4 = new RestPageResponse<>();
		client.setPublishedSolutionsByDate(solutions4);
		Assert.assertTrue(solutions4 == client.findPublishedSolutionsByDate(new String[0], Instant.now(), pageRequest));

		RestPageResponse<MLPArtifact> artPage = new RestPageResponse<>();
		client.setSearchArtifacts(artPage);
		Assert.assertTrue(artPage == client.searchArtifacts(queryParameters, isOr, pageRequest));

		MLPArtifact artifact = new MLPArtifact();
		client.setArtifactById(artifact);
		Assert.assertTrue(artifact == client.getArtifact("id"));
		client.setArtifact(artifact);
		Assert.assertTrue(artifact == client.createArtifact(artifact));
		client.updateArtifact(artifact);
		client.deleteArtifact("id");

		client.setUserCount(count);
		Assert.assertTrue(count == client.getUserCount());

		RestPageResponse<MLPUser> users = new RestPageResponse<>();
		client.setUsers(users);
		Assert.assertTrue(users == client.getUsers(pageRequest));
		client.setUsersBySearchTerm(users);
		Assert.assertTrue(users == client.findUsersBySearchTerm("term", pageRequest));

		RestPageResponse<MLPUser> userPage = new RestPageResponse<>();
		client.setSearchUsers(userPage);
		Assert.assertTrue(userPage == client.searchUsers(queryParameters, isOr, pageRequest));

		MLPUser user = new MLPUser();
		client.setLoginUser(user);
		Assert.assertTrue(user == client.loginUser("name", "pass"));
		Assert.assertTrue(user == client.loginApiUser("name", "pass"));
		Assert.assertTrue(user == client.verifyUser("name", "pass"));

		client.setUserById(user);
		Assert.assertTrue(user == client.getUser("id"));
		client.setUser(user);
		Assert.assertTrue(user == client.createUser(user));
		client.updateUser(user);
		client.deleteUser("id");

		List<MLPRole> roleList = new ArrayList<>();
		client.setUserRoles(roleList);
		Assert.assertTrue(roleList == client.getUserRoles("id"));
		client.addUserRole("id", "id");
		client.updateUserRoles("id", stringList);
		client.dropUserRole("user", "role");
		client.addUsersInRole(stringList, "role");
		client.dropUsersInRole(stringList, "role");

		client.setRoleUsersCount(count);
		Assert.assertTrue(count == client.getRoleUsersCount("role"));

		MLPUserLoginProvider userLoginProvider = new MLPUserLoginProvider();
		client.setUserLoginProviderById(userLoginProvider);
		Assert.assertTrue(userLoginProvider == client.getUserLoginProvider("id", "code", "login"));

		List<MLPUserLoginProvider> userLoginProviderList = new ArrayList<>();
		client.setUserLoginProviders(userLoginProviderList);
		Assert.assertTrue(userLoginProviderList == client.getUserLoginProviders("id"));

		client.setUserLoginProvider(userLoginProvider);
		Assert.assertTrue(userLoginProvider == client.createUserLoginProvider(userLoginProvider));
		client.updateUserLoginProvider(userLoginProvider);
		client.deleteUserLoginProvider(userLoginProvider);

		client.setRoleCount(count);
		Assert.assertTrue(count == client.getRoleCount());

		RestPageResponse<MLPRole> rolePage = new RestPageResponse<>();
		client.setSearchRoles(rolePage);
		Assert.assertTrue(rolePage == client.searchRoles(queryParameters, isOr, pageRequest));

		RestPageResponse<MLPRole> roles = new RestPageResponse<>();
		client.setRoles(roles);
		Assert.assertTrue(roles == client.getRoles(pageRequest));

		MLPRole role = new MLPRole();
		client.setRoleById(role);
		Assert.assertTrue(role == client.getRole("id"));
		client.setRole(role);
		Assert.assertTrue(role == client.createRole(role));
		client.updateRole(role);
		client.deleteRole("id");

		List<MLPRoleFunction> functions = new ArrayList<>();
		client.setRoleFunctions(functions);
		Assert.assertTrue(functions == client.getRoleFunctions("id"));

		MLPRoleFunction roleFunction = new MLPRoleFunction();
		client.setRoleFunctionById(roleFunction);
		Assert.assertTrue(roleFunction == client.getRoleFunction("id", "id"));
		client.setRoleFunction(roleFunction);
		Assert.assertTrue(roleFunction == client.createRoleFunction(roleFunction));
		client.updateRoleFunction(roleFunction);
		client.deleteRoleFunction("id", "id");

		RestPageResponse<MLPPeer> peers = new RestPageResponse<>();
		client.setPeers(peers);
		Assert.assertTrue(peers == client.getPeers(pageRequest));

		RestPageResponse<MLPPeer> peerPage = new RestPageResponse<>();
		client.setSearchPeers(peerPage);
		Assert.assertTrue(peerPage == client.searchPeers(queryParameters, isOr, pageRequest));

		MLPPeer peer = new MLPPeer();
		client.setPeerById(peer);
		Assert.assertTrue(peer == client.getPeer("id"));
		client.setPeer(peer);
		Assert.assertTrue(peer == client.createPeer(peer));
		client.updatePeer(peer);
		;
		client.deletePeer("id");

		List<MLPPeerSubscription> peerSubList = new ArrayList<>();
		peerSubList.add(new MLPPeerSubscription());
		client.setPeerSubscriptions(peerSubList);
		Assert.assertTrue(peerSubList == client.getPeerSubscriptions("id"));
		Assert.assertEquals(1, client.getPeerSubscriptionCount("id"));

		MLPPeerSubscription peerSub = new MLPPeerSubscription();
		client.setPeerSubscriptionById(peerSub);
		Assert.assertTrue(peerSub == client.getPeerSubscription(0L));
		client.setPeerSubscription(peerSub);
		Assert.assertTrue(peerSub == client.createPeerSubscription(peerSub));
		client.updatePeerSubscription(peerSub);
		client.deletePeerSubscription(0L);

		RestPageResponse<MLPSolutionDownload> solutionDownloads = new RestPageResponse<>();
		client.setSolutionDownloads(solutionDownloads);
		Assert.assertTrue(solutionDownloads == client.getSolutionDownloads("id", pageRequest));

		MLPSolutionDownload solutionDownload = new MLPSolutionDownload();
		client.setSolutionDownload(solutionDownload);
		Assert.assertTrue(solutionDownload == client.createSolutionDownload(solutionDownload));
		client.deleteSolutionDownload(solutionDownload);

		RestPageResponse<MLPSolution> faves = new RestPageResponse<>();
		client.setFavoriteSolutions(faves);
		Assert.assertTrue(faves == client.getFavoriteSolutions("id", pageRequest));

		MLPSolutionFavorite favorite = new MLPSolutionFavorite();
		client.setSolutionFavorite(favorite);
		Assert.assertTrue(favorite == client.createSolutionFavorite(favorite));
		client.deleteSolutionFavorite(favorite);

		RestPageResponse<MLPSolutionRating> ratings = new RestPageResponse<>();
		client.setSolutionRatings(ratings);
		Assert.assertTrue(ratings == client.getSolutionRatings("id", pageRequest));

		MLPSolutionRating rating = new MLPSolutionRating();
		client.setUserSolutionRating(rating);
		Assert.assertTrue(rating == client.getSolutionRating("id", "id"));
		client.setSolutionRating(rating);
		Assert.assertTrue(rating == client.createSolutionRating(rating));
		client.updateSolutionRating(rating);
		client.deleteSolutionRating(rating);

		client.setNotificationCount(count);
		Assert.assertTrue(count == client.getNotificationCount());

		RestPageResponse<MLPNotification> notifications = new RestPageResponse<>();
		client.setNotifications(notifications);
		Assert.assertTrue(notifications == client.getNotifications(pageRequest));

		MLPNotification notification = new MLPNotification();
		client.setNotification(notification);
		Assert.assertTrue(notification == client.createNotification(notification));
		client.updateNotification(notification);
		client.deleteNotification("id");

		client.setUserNotificationCount(1);
		Assert.assertTrue(1 == client.getUserUnreadNotificationCount("id"));
		RestPageResponse<MLPUserNotification> userNotifications = new RestPageResponse<>();
		client.setUserNotifications(userNotifications);
		Assert.assertTrue(userNotifications == client.getUserNotifications("id", pageRequest));
		client.addUserToNotification("id", "id");
		client.dropUserFromNotification("id", "id");
		client.setUserViewedNotification("id", "id");

		List<MLPUser> userList = new ArrayList<>();
		client.setSolutionAccessUsers(userList);
		Assert.assertTrue(userList == client.getSolutionAccessUsers("id"));

		RestPageResponse<MLPSolution> userAccSol = new RestPageResponse<>();
		client.setUserAccessSolutions(userAccSol);
		Assert.assertTrue(userAccSol == client.getUserAccessSolutions("id", pageRequest));
		client.addSolutionUserAccess("id", "id");
		client.isUserAccessToSolution("id", "id");
		client.dropSolutionUserAccess("id", "id");

		client.updatePassword(user, new MLPPasswordChangeRequest());

		RestPageResponse<MLPSolutionDeployment> deployments = new RestPageResponse<>();
		client.setUserDeployments(deployments);
		Assert.assertTrue(deployments == client.getUserDeployments("id", pageRequest));
		client.setSolutionDeployments(deployments);
		Assert.assertTrue(deployments == client.getSolutionDeployments("id", "id", pageRequest));
		client.setUserSolutionDeployments(deployments);
		Assert.assertTrue(deployments == client.getUserSolutionDeployments("id", "id", "id", pageRequest));

		MLPSolutionDeployment deployment = new MLPSolutionDeployment();
		client.setSolutionDeployment(deployment);
		Assert.assertTrue(deployment == client.createSolutionDeployment(deployment));
		client.updateSolutionDeployment(deployment);
		client.deleteSolutionDeployment(deployment);

		RestPageResponse<MLPSiteConfig> siteConfigs = new RestPageResponse<>();
		client.setSiteConfigs(siteConfigs);
		client.getSiteConfigs(pageRequest);
		MLPSiteConfig siteConfig = new MLPSiteConfig();
		client.setSiteConfigByKey(siteConfig);
		Assert.assertTrue(siteConfig == client.getSiteConfig("key"));
		client.setSiteConfig(siteConfig);
		Assert.assertTrue(siteConfig == client.createSiteConfig(siteConfig));
		client.updateSiteConfig(siteConfig);
		client.deleteSiteConfig("key");

		RestPageResponse<MLPSiteContent> siteContents = new RestPageResponse<>();
		client.setSiteContents(siteContents);
		client.getSiteContents(pageRequest);
		MLPSiteContent siteContent = new MLPSiteContent();
		client.setSiteContentByKey(siteContent);
		Assert.assertTrue(siteContent == client.getSiteContent("key"));
		client.setSiteContent(siteContent);
		Assert.assertTrue(siteContent == client.createSiteContent(siteContent));
		client.updateSiteContent(siteContent);
		client.deleteSiteContent("key");

		client.setThreadCount(count);
		Assert.assertTrue(count == client.getThreadCount());

		RestPageResponse<MLPThread> threads = new RestPageResponse<>();
		client.setThreads(threads);
		Assert.assertTrue(threads == client.getThreads(pageRequest));

		MLPThread thread = new MLPThread();
		client.setThreadById(thread);
		Assert.assertTrue(thread == client.getThread("id"));
		client.setThread(thread);
		Assert.assertTrue(thread == client.createThread(thread));
		client.updateThread(thread);
		client.deleteThread("id");

		client.setThreadCommentCount(count);
		Assert.assertTrue(count == client.getThreadCommentCount("id"));

		RestPageResponse<MLPComment> comments = new RestPageResponse<>();
		client.setThreadComments(comments);
		Assert.assertTrue(comments == client.getThreadComments("id", pageRequest));

		MLPComment comment = new MLPComment();
		client.setCommentById(comment);
		Assert.assertTrue(comment == client.getComment("id", "id"));
		client.setComment(comment);
		Assert.assertTrue(comment == client.createComment(comment));
		client.updateComment(comment);
		client.deleteComment("id", "id");

		RestPageResponse<MLPSolution> portalSols = new RestPageResponse<>();
		client.setPortalSolutions(portalSols);
		Assert.assertTrue(
				portalSols == client.findPortalSolutions(null, null, true, null, null, null, null, null, pageRequest));
		Assert.assertTrue(portalSols == client.findPublishedSolutionsByKwAndTags(null, true, null, null, null, null,
				null, pageRequest));
		Assert.assertTrue(portalSols == client.findPublishedSolutionsByKwAndTags(null, true, null, null, null, null,
				null, pageRequest));

		RestPageResponse<MLPSolution> userPrivSols = new RestPageResponse<>();
		client.setUserSolutions(userPrivSols);
		Assert.assertTrue(
				userPrivSols == client.findUserSolutions(true, true, null, null, null, null, null, pageRequest));

		RestPageResponse<MLPSolution> solutionResponse = new RestPageResponse<>();
		client.setSearchSolutions(solutionResponse);
		Assert.assertTrue(solutionResponse == client.searchSolutions(queryParameters, isOr, new RestPageRequest()));

		client.setSolutionRevisionThreads(threads);
		Assert.assertTrue(0 == client.getSolutionRevisionThreadCount("id", "id"));
		Assert.assertTrue(threads == client.getSolutionRevisionThreads("id", "id", pageRequest));

		client.setSolutionRevisionCommentCount(1L);
		Assert.assertTrue(1 == client.getSolutionRevisionCommentCount("id", "id"));
		client.setSolutionRevisionComments(comments);
		Assert.assertTrue(comments == client.getSolutionRevisionComments("id", "id", pageRequest));

		RestPageResponse<MLPTask> tasks = new RestPageResponse<>();
		client.setTasks(tasks);
		Assert.assertTrue(tasks == client.getTasks(pageRequest));
		MLPTask task = new MLPTask();
		client.setTask(task);
		Assert.assertTrue(task == client.createTask(task));
		client.updateTask(task);
		client.deleteTask(0L);

		List<MLPTaskStepResult> stepResults = new ArrayList<>();
		client.setStepResults(stepResults);
		Assert.assertTrue(stepResults == client.getTaskStepResults(0L));
		MLPTaskStepResult stepResult = new MLPTaskStepResult();
		client.setStepResult(stepResult);
		Assert.assertTrue(stepResult == client.createTaskStepResult(stepResult));
		client.updateTaskStepResult(stepResult);
		client.deleteTaskStepResult(1L);

		List<String> members = new ArrayList<>();
		client.setCompositeSolutionMembers(members);
		Assert.assertTrue(members == client.getCompositeSolutionMembers("id"));
		client.addCompositeSolutionMember("id1", "id2");
		client.dropCompositeSolutionMember("id1", "id2");

		MLPRevCatDescription revDesc = new MLPRevCatDescription();
		client.setRevCatDescription(revDesc);
		Assert.assertTrue(revDesc == client.getRevCatDescription("", ""));
		Assert.assertTrue(revDesc == client.createRevCatDescription(revDesc));
		client.updateRevCatDescription(revDesc);
		client.deleteRevCatDescription("", "");

		MLPDocument document = new MLPDocument();
		client.setDocumentById(document);
		Assert.assertTrue(document == client.getDocument("id"));
		client.setDocument(document);
		Assert.assertTrue(document == client.createDocument(document));
		client.updateDocument(document);
		client.deleteDocument("id");

		List<MLPDocument> docList = new ArrayList<>();
		client.setRevisionCatalogDocuments(docList);
		Assert.assertTrue(docList == client.getRevisionCatalogDocuments("id", "id"));
		client.addRevisionCatalogDocument("id", "id", "id");
		client.dropRevisionCatalogDocument("id", "id", "id");

		RestPageResponse<MLPPublishRequest> publishRequests = new RestPageResponse<>();
		client.setPublishRequests(publishRequests);
		Assert.assertTrue(publishRequests == client.getPublishRequests(pageRequest));
		client.setSearchPublishRequests(publishRequests);
		Assert.assertEquals(publishRequests, client.searchPublishRequests(queryParameters, isOr, pageRequest));
		Assert.assertFalse(client.isPublishRequestPending("sol", "rev"));
		MLPPublishRequest publishRequest = new MLPPublishRequest();
		client.setPublishRequest(publishRequest);
		Assert.assertTrue(publishRequest == client.createPublishRequest(publishRequest));
		client.updatePublishRequest(publishRequest);
		client.deletePublishRequest(0L);

		RestPageResponse<MLPCatalog> catalogs = new RestPageResponse<>();
		client.setCatalogs(catalogs);
		Assert.assertTrue(catalogs == client.getCatalogs(new RestPageRequest()));
		RestPageResponse<MLPCatalog> catPage = new RestPageResponse<>();
		client.setSearchCatalogs(catPage);
		Assert.assertTrue(catPage == client.searchCatalogs(queryParameters, isOr, pageRequest));
		MLPCatalog catalog = new MLPCatalog();
		client.setCatalog(catalog);
		Assert.assertEquals(catalog, client.createCatalog(catalog));
		client.updateCatalog(catalog);
		client.deleteCatalog(catalog.getCatalogId());
		RestPageResponse<MLPSolution> solsInCats = new RestPageResponse<>();
		client.setSolutionsInCatalogs(solsInCats);
		Assert.assertEquals(solsInCats, client.getSolutionsInCatalogs(new String[] { "abc" }, new RestPageRequest()));
		client.addSolutionToCatalog("solId", "catId");
		client.dropSolutionFromCatalog("solId", "catId");
		client.getCatalogSolutionCount("catId");

		RestPageResponse<MLPRtuReference> rtuRefs = new RestPageResponse<>();
		client.setRtuReferences(rtuRefs);
		Assert.assertTrue(rtuRefs == client.getRtuReferences(new RestPageRequest()));
		MLPRtuReference rtuRef = new MLPRtuReference("some-guid");
		client.createRtuReference(rtuRef);
		RestPageResponse<MLPRightToUse> rtus = new RestPageResponse<>();
		client.setRightToUses(rtus);
		Assert.assertTrue(rtus == client.getRightToUses(new RestPageRequest()));
		MLPRightToUse rtu = new MLPRightToUse();
		rtu.setRtuId(0L);
		client.setRightToUse(rtu);
		Assert.assertEquals(rtu, client.createRightToUse(rtu));
		client.updateRightToUse(rtu);
		client.deleteRightToUse(rtu.getRtuId());
		List<MLPRightToUse> rtuList = new ArrayList<>();
		client.setRightToUseList(rtuList);
		Assert.assertEquals(rtuList, client.getRightToUses("solution", "user"));
		client.addRefToRtu("refId", 0L);
		client.dropRefFromRtu("refId", 0L);
		client.addUserToRtu("userId", 0L);
		client.getRtuUsers(0L);
		client.dropUserFromRtu("userId", 0L);
		client.setRtusByReference(new ArrayList<>());
		client.getRtusByReference("ref");

		RestPageResponse<MLPProject> projects = new RestPageResponse<>();
		client.setProjects(projects);
		Assert.assertEquals(projects, client.getProjects(new RestPageRequest()));
		RestPageResponse<MLPProject> searchProjects = new RestPageResponse<>();
		client.setSearchProjects(searchProjects);
		Assert.assertEquals(searchProjects, client.searchProjects(queryParameters, isOr, new RestPageRequest()));
		MLPProject project = new MLPProject("abc", "def", "ghi");
		client.setProjectById(project);
		Assert.assertEquals(project, client.getProject("id"));
		client.setProject(project);
		client.createProject(project);
		client.updateProject(project);
		client.deleteProject(project.getProjectId());

		RestPageResponse<MLPNotebook> notebooks = new RestPageResponse<>();
		client.setNotebooks(notebooks);
		Assert.assertEquals(notebooks, client.getNotebooks(new RestPageRequest()));
		RestPageResponse<MLPNotebook> searchNotebooks = new RestPageResponse<>();
		client.setSearchNotebooks(searchNotebooks);
		Assert.assertEquals(searchNotebooks, client.searchNotebooks(queryParameters, isOr, new RestPageRequest()));
		MLPNotebook notebook = new MLPNotebook("abc", "def", "ghi", "jkl");
		client.setNotebookById(notebook);
		Assert.assertEquals(notebook, client.getNotebook("id"));
		client.setNotebook(notebook);
		client.createNotebook(notebook);
		client.updateNotebook(notebook);
		client.deleteNotebook(notebook.getNotebookId());

		RestPageResponse<MLPPipeline> pipelines = new RestPageResponse<>();
		client.setPipelines(pipelines);
		Assert.assertEquals(pipelines, client.getPipelines(new RestPageRequest()));
		RestPageResponse<MLPPipeline> searchPipelines = new RestPageResponse<>();
		client.setSearchPipelines(searchPipelines);
		Assert.assertEquals(searchPipelines, client.searchPipelines(queryParameters, isOr, new RestPageRequest()));
		MLPPipeline pipeline = new MLPPipeline("abc", "def", "ghi");
		client.setPipelineById(pipeline);
		Assert.assertEquals(pipeline, client.getPipeline("id"));
		client.setPipeline(pipeline);
		client.createPipeline(pipeline);
		client.updatePipeline(pipeline);
		client.deletePipeline(pipeline.getPipelineId());

		client.addProjectNotebook(project.getProjectId(), notebook.getNotebookId());
		client.addProjectPipeline(project.getProjectId(), pipeline.getPipelineId());
		client.getProjectNotebooks("id");
		client.getNotebookProjects("id");
		client.getProjectPipelines("id");
		client.getPipelineProjects("id");
		client.dropProjectNotebook(project.getProjectId(), notebook.getNotebookId());
		client.dropProjectPipeline(project.getProjectId(), pipeline.getPipelineId());

		List<String> catalogIds = new ArrayList<>();
		client.setPeerAccessCatalogIds(catalogIds);
		Assert.assertEquals(catalogIds, client.getPeerAccessCatalogIds("peerId"));
		client.addPeerAccessCatalog("peer", "catalog");
		client.isPeerAccessToCatalog("peerId", "catalog");
		client.isPeerAccessToSolution("peerId", "solutionId");
		client.dropPeerAccessCatalog("peer", "catalog");

		client.setUserFavoriteCatalogIds(catalogIds);
		Assert.assertEquals(catalogIds, client.getUserFavoriteCatalogIds("userId"));
		client.addUserFavoriteCatalog("user", "catalog");
		client.dropUserFavoriteCatalog("user", "catalog");

	}

}
