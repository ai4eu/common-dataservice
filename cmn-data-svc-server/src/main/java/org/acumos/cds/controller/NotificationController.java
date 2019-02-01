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
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.repository.NotifUserMapRepository;
import org.acumos.cds.repository.NotificationRepository;
import org.acumos.cds.repository.UserNotificationPreferenceRepository;
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
import org.springframework.http.HttpHeaders;
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
 * Answers REST requests to get, add, update and delete notifications; to record
 * which users should receive a notification; to get notifications relevant for
 * a user; and to update when a user has viewed a notification.
 * <P>
 * Validation design decisions:
 * <OL>
 * <LI>Keep queries fast, so check nothing on read</LI>
 * <LI>Provide useful messages on failure, so check everything on write</LI>
 * <LI>Also see:
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices</LI>
 * </OL>
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.NOTIFICATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private UserNotificationPreferenceRepository notificationPreferenceRepository;
	@Autowired
	private NotifUserMapRepository notifUserMapRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets the count of notifications.", response = CountTransport.class)
	@RequestMapping(value = "/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getNotificationCount() {
		logger.debug("getNotificationCount");
		long count = notificationRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of notifications, optionally sorted; empty if none are found.", //
			response = MLPNotification.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPNotification> getNotifications(Pageable pageable) {
		logger.debug("getNotifications: request {} ", pageable);
		return notificationRepository.findAll(pageable);
	}

	@ApiOperation(value = "Creates a new notification and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPNotification.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createNotification(@RequestBody MLPNotification notif, HttpServletResponse response) {
		logger.debug("createNotification: notification {} ", notif);
		try {
			String id = notif.getNotificationId();
			if (id != null) {
				UUID.fromString(id);
				if (notificationRepository.findById(id).isPresent()) {
					logger.warn("createNotification: failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Create a new row
			MLPNotification result = notificationRepository.save(notif);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.NOTIFICATION_PATH + "/" + notif.getNotificationId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createNotification took exception {} on data {}", cve.toString(), notif.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createNotification failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing notification with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{notificationId}", method = RequestMethod.PUT)
	public MLPTransportModel updateNotification(@PathVariable("notificationId") String notifId,
			@RequestBody MLPNotification notif, HttpServletResponse response) {
		logger.debug("updateNotification: notifId {} ", notifId);
		// Check that one exists
		if (!notificationRepository.findById(notifId).isPresent()) {
			logger.warn("updateNotification: failed on ID {}", notifId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notifId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			notif.setNotificationId(notifId);
			// Update the existing row
			notificationRepository.save(notif);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateNotification took exception {} on data {}", cve.toString(), notif.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateNotification failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the notification with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteNotification(@PathVariable("notificationId") String notifId,
			HttpServletResponse response) {
		logger.debug("deleteNotification: notifId {} ", notifId);
		try {
			notificationRepository.deleteById(notifId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteNotification failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteNotification failed", ex);
		}
	}

	@ApiOperation(value = "Gets the count of unread active notifications for the specified user.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.UNREAD_PATH + "/"
			+ CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getUserUnreadNotificationCount(@PathVariable("userId") String userId) {
		logger.debug("getUserNotificationCount user {}", userId);
		long count = notificationRepository.countActiveUnreadByUser(userId);
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of active notifications for the specified user; returns empty if none are found.", //
			response = MLPUserNotification.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.GET)
	public Iterable<MLPUserNotification> getUserNotifications(@PathVariable("userId") String userId,
			Pageable pageable) {
		logger.debug("getUserNotifications: userId {}", userId);
		return notificationRepository.findActiveByUser(userId, pageable);
	}

	@ApiOperation(value = "Adds a user as a recipient of the notification. Returns bad request if an ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{notificationId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.POST)
	public MLPTransportModel addUserNotification(@PathVariable("userId") String userId,
			@PathVariable("notificationId") String notificationId, @RequestBody MLPNotifUserMap notifUserMap,
			HttpServletResponse response) {
		logger.debug("addUserNotification: user {}, notif {}", userId, notificationId);
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("addUserNotification: failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		if (!notificationRepository.findById(notificationId).isPresent()) {
			logger.warn("addUserNotification: failed on notif ID {}", notificationId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notificationId, null);
		}
		notifUserMap.setUserId(userId);
		notifUserMap.setNotificationId(notificationId);
		notifUserMapRepository.save(notifUserMap);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Records the date when the user viewed the notification in the notification-user mapping table. "
			+ "Returns bad request if an ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{notificationId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.PUT)
	public MLPTransportModel updateUserNotification(@PathVariable("userId") String userId,
			@PathVariable("notificationId") String notificationId, @RequestBody MLPNotifUserMap notifUserMap,
			HttpServletResponse response) {
		logger.debug("updateUserNotification: user {}, notif {}", userId, notificationId);
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("updateUserNotification: failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		if (!notificationRepository.findById(notificationId).isPresent()) {
			logger.warn("updateUserNotification: failed on notif ID {}", notificationId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notificationId, null);
		}
		try {
			notifUserMap.setNotificationId(notificationId);
			notifUserMap.setUserId(userId);
			notifUserMapRepository.save(notifUserMap);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateUserNotification took exception {} on data {}", cve.toString(), notifUserMap.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "updateUserNotification failed", cve);
		}
	}

	@ApiOperation(value = "Drops a user as a recipient of the notification.", response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{notificationId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.DELETE)
	public SuccessTransport dropUserRecipient(@PathVariable("userId") String userId,
			@PathVariable("notificationId") String notificationId) {
		logger.debug("dropUserRecipient: user {}, notif{}", userId, notificationId);
		notifUserMapRepository.delete(new MLPNotifUserMap(notificationId, userId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Gets the user notification preference for the specified ID. Returns null if the ID is not found.", //
			response = MLPUserNotifPref.class)
	@RequestMapping(value = CCDSConstants.NOTIFICATION_PREF_PATH + "/{userNotifPrefId}", method = RequestMethod.GET)
	public MLPResponse getUserNotificationPreference(@PathVariable("userNotifPrefId") Long userNotifPrefId) {
		logger.debug("getUserNotificationPreference: userNotifPrefId {}", userNotifPrefId);
		Optional<MLPUserNotifPref> usrnp = notificationPreferenceRepository.findById(userNotifPrefId);
		return usrnp.isPresent() ? usrnp.get() : null;
	}

	@ApiOperation(value = "Gets notification preferences for the specified user ID. Returns empty if not found.", //
			response = MLPUserNotifPref.class, responseContainer = "List")
	@RequestMapping(value = CCDSConstants.NOTIFICATION_PREF_PATH + "/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.GET)
	public Object getNotificationPreferencesForUser(@PathVariable("userId") String userId) {
		logger.debug("getNotificationPreferencesForUser: userId {}", userId);
		return notificationPreferenceRepository.findByUserId(userId);
	}

	@ApiOperation(value = "Creates a new user notification preference. Returns bad request on constraint violation etc.", //
			response = MLPUserNotifPref.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTIFICATION_PREF_PATH, method = RequestMethod.POST)
	public MLPResponse createUserNotificationPreference(@RequestBody MLPUserNotifPref usrNotifPref,
			HttpServletResponse response) {
		logger.debug("createUserNotificationPreference: userNotifPrefId {}", usrNotifPref.getUserNotifPrefId());
		try {
			// Validate enum codes
			super.validateCode(usrNotifPref.getMsgSeverityCode(), CodeNameType.MESSAGE_SEVERITY);
			super.validateCode(usrNotifPref.getNotfDelvMechCode(), CodeNameType.NOTIFICATION_DELIVERY_MECHANISM);
			// Create a new row
			MLPUserNotifPref result = notificationPreferenceRepository.save(usrNotifPref);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.USER_PATH + "/" + CCDSConstants.NOTIFICATION_PREF_PATH);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createUserNotificationPreference took exception {} on data {}", cve.toString(),
					usrNotifPref.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createUserNotificationPreference failed",
					cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTIFICATION_PREF_PATH + "/{userNotifPrefId}", method = RequestMethod.PUT)
	public MLPTransportModel updateUserNotificationPreference(@PathVariable("userNotifPrefId") Long userNotifPrefId,
			@RequestBody MLPUserNotifPref usrNotifPref, HttpServletResponse response) {
		logger.debug("updateUserNotificationPreference: userNotifPrefId {} ", userNotifPrefId);
		// Check the existing one
		if (!notificationPreferenceRepository.findById(userNotifPrefId).isPresent()) {
			logger.warn("updateUserNotificationPreference: failed on ID {}", userNotifPrefId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userNotifPrefId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(usrNotifPref.getMsgSeverityCode(), CodeNameType.MESSAGE_SEVERITY);
			super.validateCode(usrNotifPref.getNotfDelvMechCode(), CodeNameType.NOTIFICATION_DELIVERY_MECHANISM);
			// Use the path-parameter id; don't trust the one in the object
			usrNotifPref.setUserNotifPrefId(userNotifPrefId);
			// Update the existing row
			notificationPreferenceRepository.save(usrNotifPref);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateUserNotificationPreference took exception {} on data {}", cve.toString(),
					usrNotifPref.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateUserNotificationPreference failed",
					cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTIFICATION_PREF_PATH + "/{userNotifPrefId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteUserNotificationPreference(@PathVariable("userNotifPrefId") Long userNotifPrefId,
			HttpServletResponse response) {
		logger.debug("deleteUserNotificationPreference: userNotifPrefId {} ", userNotifPrefId);
		try {
			notificationPreferenceRepository.deleteById(userNotifPrefId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteUserNotificationPreference failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteUserNotificationPreference failed",
					ex);
		}
	}

}
