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

package org.acumos.cds.controller;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPCatSolMap;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerCatAccMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.CatRoleMapRepository;
import org.acumos.cds.repository.CatSolMapRepository;
import org.acumos.cds.repository.CatalogRepository;
import org.acumos.cds.repository.PeerCatAccMapRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.ApiPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Manages access control requests.
 * <P>
 * Validation design decisions:
 * <OL>
 * <LI>Keep queries fast, so check nothing on read.</LI>
 * <LI>Provide useful messages on failure, so check everything on write.</LI>
 * <LI>Also see:
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices
 * </LI>
 * </OL>
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.ACCESS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccessController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CatalogRepository catalogRepository;
	@Autowired
	private CatRoleMapRepository catRoleMapRepository;
	@Autowired
	private CatSolMapRepository catSolMapRepository;
	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private PeerCatAccMapRepository peerCatAccMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets the list of users who were granted write access to the specified solution.", response = MLPUser.class, responseContainer = "List")
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/"
			+ CCDSConstants.USER_PATH, method = RequestMethod.GET)
	public Iterable<MLPUser> getSolutionAccessUsers(@PathVariable("solutionId") String solutionId) {
		logger.debug("getSolutionAccessUsers: solutionId {}", solutionId);
		return solUserAccMapRepository.getUsersForSolution(solutionId);
	}

	@ApiOperation(value = "Gets a page of solutions for which the user has write permission but is not the owner, optionally sorted on fields. Answers empty if none are found.", //
			response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Page<MLPSolution> getUserAccessSolutions(@PathVariable("userId") String userId, Pageable pageable) {
		logger.debug("getUserAccessSolutions: user {}", userId);
		return solUserAccMapRepository.getSolutionsForUser(userId, pageable);
	}

	@ApiOperation(value = "Checks if the specified user can read the specified solution. Returns non-zero if yes, zero if no.", //
			response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.GET)
	public CountTransport checkUserAccessToSolution(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId) {
		logger.debug("checkUserAccessToSolution userId {} solutionId {}", userId, solutionId);
		Iterable<MLPCatSolMap> maps = catSolMapRepository.findBySolutionId(solutionId);
		if (maps.iterator().hasNext())
			return new CountTransport(1L);
		Optional<MLPSolution> sol = solutionRepository.findById(solutionId);
		if (sol.isPresent() && sol.get().getUserId().equals(userId))
			return new CountTransport(1L);
		Optional<MLPSolUserAccMap> map = solUserAccMapRepository
				.findById(new MLPSolUserAccMap.SolUserAccessMapPK(solutionId, userId));
		if (map.isPresent())
			return new CountTransport(1L);
		return new CountTransport(0L);
	}

	@ApiOperation(value = "Grants write permission for the specified user to the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.POST)
	public Object addSolutionUserAccess(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.debug("addSolutionUserAccess: solution {}, user {}", solutionId, userId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("addSolutionUserAccess failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("addSolutionUserAccess failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		solUserAccMapRepository.save(new MLPSolUserAccMap(solutionId, userId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes write permission for the specified user from the specified solution. Returns bad request if an ID is not found", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.DELETE)
	public Object dropSolutionUserAccess(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		logger.debug("dropSolutionUserAccess: solution {}, user {}", solutionId, userId);
		try {
			solUserAccMapRepository.deleteById(new MLPSolUserAccMap.SolUserAccessMapPK(solutionId, userId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropSolutionUserAccess failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionUserAccess failed", ex);
		}
	}

	@ApiOperation(value = "Gets the list of catalog IDs accessible to the specified peer; empty if none are found.", //
			response = String.class, responseContainer = "List")
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/"
			+ CCDSConstants.CATALOG_PATH, method = RequestMethod.GET)
	public Iterable<String> getPeerAccessCatalogIds(@PathVariable("peerId") String peerId) {
		logger.debug("getPeerAccessCatalogIds peerId {}", peerId);
		return peerCatAccMapRepository.findCatalogIdsByPeerId(peerId);
	}

	@ApiOperation(value = "Gets the list of peers with access to the specified restricted catalog; empty if none are found.", //
			response = MLPPeer.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.CATALOG_PATH + "/{catalogId}/"
			+ CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	public Object getCatalogAccessPeers(@PathVariable("catalogId") String catalogId, HttpServletResponse response) {
		logger.debug("getCatalogAccessPeers catalogId {}", catalogId);
		Optional<MLPCatalog> cat = catalogRepository.findById(catalogId);
		if (!cat.isPresent() || !"RS".equals(cat.get().getAccessTypeCode())) {
			logger.warn("getCatalogAccessPeers failed on catalogId ID {}", catalogId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + catalogId, null);
		}
		return peerCatAccMapRepository.findPeersByCatalogId(catalogId);
	}

	@ApiOperation(value = "Checks if the specified peer can read the specified catalog. Returns non-zero if yes, zero if no.", //
			response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.CATALOG_PATH
			+ "/{catalogId}", method = RequestMethod.GET)
	public CountTransport checkPeerAccessToCatalog(@PathVariable("peerId") String peerId,
			@PathVariable("catalogId") String catalogId) {
		logger.debug("checkPeerAccessToCatalog peerId {} solutionId {}", peerId, catalogId);
		// The common case is that the catalog is public
		Optional<MLPCatalog> cat = catalogRepository.findById(catalogId);
		if (cat.isPresent() && cat.get().getAccessTypeCode().equals("PB"))
			return new CountTransport(1L);
		// Less common, the peer was granted access
		Optional<MLPPeerCatAccMap> map = peerCatAccMapRepository
				.findById(new MLPPeerCatAccMap.PeerCatAccMapPK(peerId, catalogId));
		if (map.isPresent())
			return new CountTransport(1L);
		return new CountTransport(0L);
	}

	@ApiOperation(value = "Checks if the specified peer can read the specified solution. Returns non-zero if yes, zero if no.", //
			response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.GET)
	public CountTransport checkPeerAccessToSolution(@PathVariable("peerId") String peerId,
			@PathVariable("solutionId") String solutionId) {
		logger.debug("checkPeerAccessToSolution peerId {} solutionId {}", peerId, solutionId);
		// The common case is that the solution is in a public catalog
		long pubCount = catSolMapRepository.countCatalogsByAccessAndSolution("PB", solutionId);
		if (pubCount > 0)
			return new CountTransport(pubCount);
		long resCount = peerCatAccMapRepository.countCatalogsByPeerAccessAndSolution(peerId, solutionId);
		return new CountTransport(resCount);
	}

	@ApiOperation(value = "Add read access to the specified restricted catalog for the specified peer. Answers bad request if an ID is invalid.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.CATALOG_PATH
			+ "/{catalogId}", method = RequestMethod.POST)
	public MLPResponse addPeerAccessCatalog(@PathVariable("catalogId") String catalogId,
			@PathVariable("peerId") String peerId, HttpServletResponse response) {
		logger.debug("addPeerAccessCatalog catalogId {} peerId {}", catalogId, peerId);
		if (!catalogRepository.findById(catalogId).isPresent()) {
			logger.warn("addPeerAccessCatalog: failed on cat ID {}", catalogId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + catalogId, null);
		}
		// TODO: check if catalog access type is restricted?
		if (!peerRepository.findById(peerId).isPresent()) {
			logger.warn("addPeerAccessCatalog: failed on peer ID {}", peerId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		peerCatAccMapRepository.save(new MLPPeerCatAccMap(peerId, catalogId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes read access to the specified restricted catalog for the specified peer.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.CATALOG_PATH
			+ "/{catalogId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropPeerAccessCatalog(@PathVariable("catalogId") String catalogId,
			@PathVariable("peerId") String peerId, HttpServletResponse response) {
		logger.debug("dropPeerAccessCatalog catalogId {} peerId {}", catalogId, peerId);
		try {
			peerCatAccMapRepository.deleteById(new MLPPeerCatAccMap.PeerCatAccMapPK(peerId, catalogId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropPeerAccessCatalog failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropPeerAccessCatalog failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of catalogs accessible to the specified user, which includes public catalogs and restricted catalogs via catalog-role and user-role mappings; empty if none are found.", //
			response = MLPCatalog.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/"
			+ CCDSConstants.CATALOG_PATH, method = RequestMethod.GET)
	public Page<MLPCatalog> getUserAccessCatalogs(@PathVariable("userId") String userId, Pageable pageable) {
		logger.debug("getUserAccessCatalogs: user {}", userId);
		return catRoleMapRepository.findCatalogsByUserId(userId, pageable);
	}

}