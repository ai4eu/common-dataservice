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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.PeerSubscriptionRepository;
import org.acumos.cds.service.PeerSearchService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Answers REST requests to get, add, update and delete peers.
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
@RequestMapping(value = "/" + CCDSConstants.PEER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class PeerController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private PeerSubscriptionRepository peerSubRepository;
	@Autowired
	private PeerSearchService peerSearchService;

	@ApiOperation(value = "Gets a page of peers, optionally sorted; empty if none are found.", //
			response = MLPPeer.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPPeer> getPeers(Pageable pageable) {
		logger.debug("getPeers {}", pageable);
		return peerRepository.findAll(pageable);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String NAME = "name";
	private static final String SUBJECT_NAME = "subjectName";
	private static final String API_URL = "apiUrl";
	private static final String WEB_URL = "webUrl";
	private static final String CONTACT_1 = "contact1";
	private static final String STATUS_CODE = "statusCode";

	@ApiOperation(value = "Searches for peers with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPeer.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchPeers( //
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Name") //
			@RequestParam(name = NAME, required = false) String name, //
			@ApiParam(value = "Subject name") //
			@RequestParam(name = SUBJECT_NAME, required = false) String subjectName, //
			@ApiParam(value = "API URL") //
			@RequestParam(name = API_URL, required = false) String apiUrl, //
			@ApiParam(value = "Web URL") //
			@RequestParam(name = WEB_URL, required = false) String webUrl, //
			@ApiParam(value = "Contact 1") //
			@RequestParam(name = CONTACT_1, required = false) String contact1, //
			@ApiParam(value = "Status code") //
			@RequestParam(name = STATUS_CODE, required = false) String statusCode, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchPeer enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (name == null && subjectName == null && apiUrl == null && webUrl == null && contact1 == null
				&& statusCode == null) {
			logger.warn("searchPeers: no parameters");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return peerSearchService.findPeers(name, subjectName, apiUrl, webUrl, contact1, statusCode, isOr,
					pageRequest);
		} catch (Exception ex) {
			logger.error("searchPeers failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchPeers failed", ex);
		}
	}

	@ApiOperation(value = "Gets the peer for the specified ID. Returns null if the ID is not found.", //
			response = MLPPeer.class)
	@RequestMapping(value = "/{peerId}", method = RequestMethod.GET)
	public MLPPeer getPeer(@PathVariable("peerId") String peerId) {
		logger.debug("getPeer peerId {}", peerId);
		Optional<MLPPeer> peer = peerRepository.findById(peerId);
		return peer.isPresent() ? peer.get() : null;
	}

	@ApiOperation(value = "Creates a new peer and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPPeer.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createPeer(@RequestBody MLPPeer peer, HttpServletResponse response) {
		logger.debug("createPeer: peer {}", peer);
		try {
			String id = peer.getPeerId();
			if (id != null) {
				UUID.fromString(id);
				if (peerRepository.findById(id).isPresent()) {
					logger.warn("createPeer failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Validate enum codes
			super.validateCode(peer.getStatusCode(), CodeNameType.PEER_STATUS);
			// Create a new row
			MLPPeer result = peerRepository.save(peer);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.PEER_PATH + "/" + peer.getPeerId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPeer failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPeer failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{peerId}", method = RequestMethod.PUT)
	public MLPTransportModel updatePeer(@PathVariable("peerId") String peerId, @RequestBody MLPPeer peer,
			HttpServletResponse response) {
		logger.debug("updatePeer peerId {}", peerId);
		// Get the existing one
		if (!peerRepository.findById(peerId).isPresent()) {
			logger.warn("updatePeer failed on ID {}", peerId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(peer.getStatusCode(), CodeNameType.PEER_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			peer.setPeerId(peerId);
			// Update the existing row
			peerRepository.save(peer);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePeer failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePeer failed", cve);
		}
	}

	/*
	 * Originally this was declared void and accordingly returned nothing. But when
	 * used in SpringBoot, after invoking the method it would look for a ThymeLeaf
	 * template, fail to find it, then throw internal server error.
	 */
	@ApiOperation(value = "Deletes the peer with the specified ID. Cascades delete to peer subscriptions. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{peerId}", method = RequestMethod.DELETE)
	public MLPTransportModel deletePeer(@PathVariable("peerId") String peerId, HttpServletResponse response) {
		logger.debug("deletePeer peerId {}", peerId);
		try {
			Iterable<MLPPeerSubscription> subs = peerSubRepository.findByPeerId(peerId);
			if (subs != null)
				peerSubRepository.deleteAll(subs);
			peerRepository.deleteById(peerId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePeer failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePeer failed", ex);
		}
	}

	/* Peer Subscriptions */

	@ApiOperation(value = "Gets count of subscriptions for the specified peer.", //
			response = CountTransport.class)
	@RequestMapping(value = "/{peerId}/" + CCDSConstants.SUBSCRIPTION_PATH + "/"
			+ CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getPeerSubCount(@PathVariable("peerId") String peerId) {
		logger.debug("getPeerSubCount peerId {}", peerId);
		long count = peerSubRepository.countPeerSubscriptions(peerId);
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets all subscriptions for the specified peer. Returns empty if none are found", //
			response = MLPPeerSubscription.class, responseContainer = "List")
	@RequestMapping(value = "/{peerId}/" + CCDSConstants.SUBSCRIPTION_PATH, method = RequestMethod.GET)
	public Iterable<MLPPeerSubscription> getPeerSubs(@PathVariable("peerId") String peerId) {
		logger.debug("getPeerSubs peerId {}", peerId);
		return peerSubRepository.findByPeerId(peerId);
	}

	@ApiOperation(value = "Gets the peer subscription for the specified ID. Returns null if not found.", response = MLPPeerSubscription.class)
	@RequestMapping(value = "/" + CCDSConstants.SUBSCRIPTION_PATH + "/{subId}", method = RequestMethod.GET)
	public MLPPeerSubscription getPeerSub(@PathVariable("subId") Long subId) {
		logger.debug("getPeerSub subId {}", subId);
		Optional<MLPPeerSubscription> peerSub = peerSubRepository.findById(subId);
		return peerSub.isPresent() ? peerSub.get() : null;
	}

	@ApiOperation(value = "Creates a new entity with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPPeerSubscription.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SUBSCRIPTION_PATH, method = RequestMethod.POST)
	public MLPResponse createPeerSub(@RequestBody MLPPeerSubscription peerSub, HttpServletResponse response) {
		logger.debug("createPeerSub: sub {}", peerSub);
		if (!peerRepository.findById(peerSub.getPeerId()).isPresent()) {
			logger.warn("createPeerSub failed on ID {}", peerSub);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerSub.getPeerId(), null);
		}
		try {
			// Validate enum codes
			super.validateCode(peerSub.getAccessType(), CodeNameType.ACCESS_TYPE);
			super.validateCode(peerSub.getScopeType(), CodeNameType.SUBSCRIPTION_SCOPE);
			// Null out any existing ID to get an auto-generated ID
			peerSub.setSubId(null);
			// Create a new row
			MLPPeerSubscription result = peerSubRepository.save(peerSub);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.PEER_PATH + "/" + CCDSConstants.SUBSCRIPTION_PATH + "/" + peerSub.getSubId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPeerSub failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPeerSub failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing peer subscription with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SUBSCRIPTION_PATH + "/{subId}", method = RequestMethod.PUT)
	public MLPTransportModel updatePeerSub(@PathVariable("subId") Long subId, @RequestBody MLPPeerSubscription peerSub,
			HttpServletResponse response) {
		logger.debug("updatePeerSub subId {}", subId);
		// Check the existing one
		if (!peerSubRepository.findById(subId).isPresent()) {
			logger.warn("updatePeerSub failed on ID {}", subId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + subId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(peerSub.getAccessType(), CodeNameType.ACCESS_TYPE);
			super.validateCode(peerSub.getScopeType(), CodeNameType.SUBSCRIPTION_SCOPE);
			// Use the path-parameter id; don't trust the one in the object
			peerSub.setSubId(subId);
			// Update the existing row
			peerSubRepository.save(peerSub);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePeerSub failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePeerSub failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SUBSCRIPTION_PATH + "/{subId}", method = RequestMethod.DELETE)
	public MLPTransportModel deletePeerSub(@PathVariable("subId") Long subId, HttpServletResponse response) {
		logger.debug("deletePeerSub subId {}", subId);
		try {
			peerSubRepository.deleteById(subId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePeerSub failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePeerSub failed", ex);
		}
	}

}
