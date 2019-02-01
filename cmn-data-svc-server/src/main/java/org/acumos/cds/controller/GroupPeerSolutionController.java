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
import org.acumos.cds.domain.MLPDomainModel;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeerPeerAccMap;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPSolGrpMemMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.repository.PeerGroupRepository;
import org.acumos.cds.repository.PeerGrpMemMapRepository;
import org.acumos.cds.repository.PeerPeerAccMapRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.PeerSolAccMapRepository;
import org.acumos.cds.repository.SolGrpMemMapRepository;
import org.acumos.cds.repository.SolutionGroupRepository;
import org.acumos.cds.repository.SolutionRepository;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides methods to manage peer groups and solution groups, peer group and
 * solution group memberships, and also peer group and solution group access.
 * 
 * The prefix "group" is pretty generic.
 * <P>
 * Validation design decisions:
 * <OL>
 * <LI>Keep queries fast, so check nothing on read</LI>
 * <LI>Provide useful messages on failure, so check everything on write</LI>
 * <LI>Also see:
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices
 * </LI>
 * </OL>
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupPeerSolutionController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private SolutionRepository solutionRepository;
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

	@ApiOperation(value = "Gets a page of peer groups, optionally sorted.", //
			response = MLPPeerGroup.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	public Page<MLPPeerGroup> getPeerGroups(Pageable pageRequest) {
		logger.debug("getPeerGroups {}", pageRequest);
		return peerGroupRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Creates a new entity with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPPeerGroup.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH, method = RequestMethod.POST)
	public MLPResponse createPeerGroup(@RequestBody MLPPeerGroup group, HttpServletResponse response) {
		logger.debug("createPeerGroup: group {}", group);
		try {
			return peerGroupRepository.save(group);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPeerGroup took exception {} on data {}", cve.toString(), group.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPeerGroup failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.PUT)
	public MLPResponse updatePeerGroup(@PathVariable("groupId") Long groupId, @RequestBody MLPPeerGroup group,
			HttpServletResponse response) {
		logger.debug("updatePeerGroup groupId {}", groupId);
		// Get the existing one
		Optional<MLPPeerGroup> existing = peerGroupRepository.findById(groupId);
		if (!existing.isPresent()) {
			logger.warn("updatePeerGroup: failed on ID {}", groupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			peerGroupRepository.save(group);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePeerGroup took exception {} on data {}", cve.toString(), group.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePeerGroup failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.DELETE)
	public MLPTransportModel deletePeerGroup(@PathVariable("groupId") Long groupId, HttpServletResponse response) {
		logger.debug("deletePeerGroup groupId {}", groupId);
		try {
			peerGroupRepository.deleteById(groupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePeerGroup failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePeerGroup failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of peer members of the specified peer group, optionally sorted; empty if none are found.", //
			response = MLPPeer.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	public Page<MLPPeer> getPeersInGroup(@PathVariable("groupId") Long groupId, Pageable pageRequest) {
		logger.debug("getPeersInGroup groupId {}", groupId);
		return peerGroupMemMapRepository.findPeersByGroupId(groupId, pageRequest);
	}

	@ApiOperation(value = "Adds the specified peer as a member of the specified peer group.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.POST)
	public MLPResponse addPeerToGroup(@PathVariable("groupId") Long groupId, @PathVariable("peerId") String peerId,
			@RequestBody MLPPeerGrpMemMap map, HttpServletResponse response) {
		logger.debug("addPeerToGroup groupId {} peerId {}", groupId, peerId);
		if (!peerRepository.findById(peerId).isPresent()) {
			logger.warn("addPeerToGroup: failed on peer ID {}", peerId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		if (!peerGroupRepository.findById(groupId).isPresent()) {
			logger.warn("addPeerToGroup: failed on group ID {}", groupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		// Use path parameters only
		map.setGroupId(groupId);
		map.setPeerId(peerId);
		peerGroupMemMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops the specified peer as a member of the specified peer group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropPeerFromGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("peerId") String peerId, HttpServletResponse response) {
		logger.debug("dropPeerFromGroup groupId {} peerId {}", groupId, peerId);
		try {
			MLPPeerGrpMemMap.PeerGrpMemMapPK pk = new MLPPeerGrpMemMap.PeerGrpMemMapPK(groupId, peerId);
			peerGroupMemMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropPeerFromGroup failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropPeerFromGroup failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of solution groups, optionally sorted.", //
			response = MLPSolutionGroup.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Page<MLPSolutionGroup> getSolutionGroups(Pageable pageRequest) {
		logger.debug("getSolutionGroups {}", pageRequest);
		return solutionGroupRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Creates a new entity with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPSolutionGroup.class)
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH, method = RequestMethod.POST)
	public MLPResponse createSolutionGroup(@RequestBody MLPSolutionGroup group, HttpServletResponse response) {
		logger.debug("createSolutionGroup group {}", group);
		try {
			group.setGroupId(null);
			return solutionGroupRepository.save(group);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createSolutionGroup took exception {} on data {}", cve.toString(), group.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionGroup failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.PUT)
	public MLPResponse updateSolutionGroup(@PathVariable("groupId") Long groupId, @RequestBody MLPSolutionGroup group,
			HttpServletResponse response) {
		logger.debug("updateSolutionGroup groupId {}", groupId);
		// Get the existing one
		Optional<MLPSolutionGroup> existing = solutionGroupRepository.findById(groupId);
		if (!existing.isPresent()) {
			logger.warn("updateSolutionGroup: failed on ID {}", groupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "NO_ENTRY_WITH_ID " + groupId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			solutionGroupRepository.save(group);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateSolutionGroup took exception {} on data {}", cve.toString(), group.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionGroup failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.DELETE)
	public MLPTransportModel deleteSolutionGroup(@PathVariable("groupId") Long groupId, HttpServletResponse response) {
		logger.debug("deleteSolutionGroup groupId {}", groupId);
		try {
			solutionGroupRepository.deleteById(groupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteSolutionGroup failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionGroup failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of solution members in the specified solution group, optionally sorted; empty if none are found.", //
			response = MLPSolutionGroup.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Object getSolutionsInGroup(@PathVariable("groupId") Long groupId, Pageable pageRequest) {
		logger.debug("getSolutionsInGroup groupId {}", groupId);
		return solGroupMemMapRepository.findSolutionsByGroupId(groupId, pageRequest);
	}

	@ApiOperation(value = "Adds the specified solution as a member of the specified solution group.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}", method = RequestMethod.POST)
	public MLPResponse addSolutionToGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, @RequestBody MLPSolGrpMemMap map,
			HttpServletResponse response) {
		logger.debug("addSolutionToGroup groupId {} solutionId {}", groupId, solutionId);
		if (!solutionRepository.findById(solutionId).isPresent()) {
			logger.warn("addSolutionToGroup: failed on sol ID {}", solutionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (!solutionGroupRepository.findById(groupId).isPresent()) {
			logger.warn("addSolutionToGroup: failed on grp ID {}", groupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		// Use path parameters only
		map.setGroupId(groupId);
		map.setSolutionId(solutionId);
		solGroupMemMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Drops the specified solution as a member of the specified solution group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropSolutionFromGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.debug("dropSolutionFromGroup groupId {} solutionId {}", groupId, solutionId);
		try {
			MLPSolGrpMemMap.SolGrpMemMapPK pk = new MLPSolGrpMemMap.SolGrpMemMapPK(groupId, solutionId);
			solGroupMemMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropSolutionFromGroup failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionFromGroup failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of peer-solution membership mappings, optionally sorted.", //
			response = MLPPeerSolAccMap.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Page<MLPPeerSolAccMap> getPeerSolutionGroupMaps(Pageable pageRequest) {
		logger.debug("getPeerSolutionGroupMaps request {}", pageRequest);
		return peerSolAccMapRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Grants access for the specified peer group to the specified solution group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.POST)
	public MLPTransportModel mapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, @RequestBody MLPPeerSolAccMap map,
			HttpServletResponse response) {
		logger.debug("mapPeerSolutionGroups: peerGroupId {} solutionGroupId {}", peerGroupId, solutionGroupId);
		if (!peerGroupRepository.findById(peerGroupId).isPresent()) {
			logger.warn("mapPeerSolutionGroups: failed on peer grp ID {}", peerGroupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerGroupId, null);
		}
		if (!solutionGroupRepository.findById(solutionGroupId).isPresent()) {
			logger.warn("mapPeerSolutionGroups: failed on sol grp ID {}", solutionGroupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionGroupId, null);
		}
		try {
			// Use path parameters only
			map.setPeerGroupId(peerGroupId);
			map.setSolutionGroupId(solutionGroupId);
			peerSolAccMapRepository.save(map);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("mapPeerSolutionGroups failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "mapPeerSolutionGroups failed", cve);
		}
	}

	@ApiOperation(value = "Removes access for the specified peer group to the specified solution group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.DELETE)
	public MLPTransportModel unmapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, HttpServletResponse response) {
		logger.debug("unmapPeerSolutionGroups: peerGroupId {} solutionGroupId {}", peerGroupId, solutionGroupId);
		try {
			MLPPeerSolAccMap.PeerSolAccMapPK pk = new MLPPeerSolAccMap.PeerSolAccMapPK(peerGroupId, solutionGroupId);
			peerSolAccMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("unmapPeerSolutionGroups failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapPeerSolutionGroups failed", ex);
		}
	}

	@ApiOperation(value = "Grants access for the specified principal peer group to the specified resource peer group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{principalGroupId}/" + CCDSConstants.PEER_PATH
			+ "/{resourceGroupId}", method = RequestMethod.POST)
	public MLPTransportModel mapPeerPeerGroups(@PathVariable("principalGroupId") Long principalGroupId,
			@PathVariable("resourceGroupId") Long resourceGroupId, @RequestBody MLPPeerPeerAccMap map,
			HttpServletResponse response) {
		logger.debug("mapPeerPeerGroups: principal {} resource {}", principalGroupId, resourceGroupId);
		if (!peerGroupRepository.findById(principalGroupId).isPresent()) {
			logger.warn("mapPeerPeerGroups: failed on pg ID {}", principalGroupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + principalGroupId, null);
		}
		if (!peerGroupRepository.findById(resourceGroupId).isPresent()) {
			logger.warn("mapPeerPeerGroups: failed on rg ID {}", principalGroupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + resourceGroupId, null);
		}
		if (principalGroupId == resourceGroupId) {
			logger.warn("mapPeerPeerGroups: failed on rg==pg ID {}", principalGroupId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "IDs must be different", null);
		}
		try {
			// Use path parameters only
			map.setPrincipalPeerGroupId(principalGroupId);
			map.setResourcePeerGroupId(resourceGroupId);
			peerPeerAccMapRepository.save(map);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("mapPeerPeerGroups failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "mapPeerPeerGroups failed", cve);
		}
	}

	@ApiOperation(value = "Removes access for the specified principal peer group to the specified resource peer group.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{principalGroupId}/" + CCDSConstants.PEER_PATH
			+ "/{resourceGroupId}", method = RequestMethod.DELETE)
	public MLPTransportModel unmapPeerPeerGroups(@PathVariable("principalGroupId") Long principalGroupId,
			@PathVariable("resourceGroupId") Long resourceGroupId, HttpServletResponse response) {
		logger.debug("unmapPeerSolutionGroups: principalGroupId {} resourceGroupId {}", principalGroupId,
				resourceGroupId);
		try {
			MLPPeerPeerAccMap.PeerPeerAccMapPK pk = new MLPPeerPeerAccMap.PeerPeerAccMapPK(principalGroupId,
					resourceGroupId);
			peerPeerAccMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("unmapPeerPeerGroups failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapPeerPeerGroups failed", ex);
		}
	}

	@ApiOperation(value = "Checks access for the specified peer to the specified solution.", response = MLPDomainModel.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	public MLPTransportModel checkPeerSolutionAccess(@PathVariable("peerId") String peerId,
			@PathVariable("solutionId") String solutionId) {
		logger.debug("checkPeerSolutionAccess: peerId {} solutionId {}", peerId, solutionId);
		long count = peerSolAccMapRepository.checkPeerSolutionAccess(peerId, solutionId);
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of non-public solutions accessible to specified peer; empty if none found.", response = MLPSolution.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/"
			+ CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	public Page<MLPSolution> getRestrictedSolutions(@PathVariable("peerId") String peerId, Pageable pageRequest) {
		logger.debug("getRestrictedSolutions: peerId {} ", peerId);
		return solutionRepository.findRestrictedSolutions(peerId, pageRequest);
	}

	@ApiOperation(value = "Gets peers accessible to the specified peer; returns empty if none found.", response = MLPPeer.class, responseContainer = "List")
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	public Iterable<MLPPeer> getPeerAccessList(@PathVariable("peerId") String peerId) {
		logger.debug("getPeerAccessList: peerId {}", peerId);
		return peerPeerAccMapRepository.findAccessPeers(peerId);
	}

}
