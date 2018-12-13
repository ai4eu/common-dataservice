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

package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends PagingAndSortingRepository<MLPNotification, String> {

	/**
	 * Gets the count of notifications that are active (current time falls within
	 * the notification's time window), assigned to the specified user unread.
	 * 
	 * @param userId
	 *                   User ID
	 * @return Page of notification objects
	 */
	@Query(value = "select count(n.notificationId) "//
			+ " from MLPNotification n, MLPNotifUserMap m" //
			+ " where n.start <= CURRENT_TIMESTAMP " //
			+ "   and n.end >= CURRENT_TIMESTAMP " //
			+ "   and n.notificationId = m.notificationId " //
			+ "   and m.userId = :userId " //
			+ "   and m.viewed is null ")
	long countActiveUnreadByUser(@Param("userId") String userId);

	/**
	 * Finds a page of notifications that are active (current time falls within the
	 * notification's time window) and assigned to the specified user. Result
	 * includes the viewed timestamp, which is null if the user has not read it.
	 * 
	 * @param userId
	 *                     User ID
	 * @param pageable
	 *                     PageRequest
	 * @return Page of notification objects
	 */
	@Query(value = "select new org.acumos.cds.domain.MLPUserNotification" //
			+ "    (n.notificationId, n.title, n.message, n.url, n.start, n.end, m.viewed)" //
			+ " from MLPNotification n, MLPNotifUserMap m" //
			+ " where n.start <= CURRENT_TIMESTAMP " //
			+ "   and n.end >= CURRENT_TIMESTAMP   " //
			+ "   and n.notificationId = m.notificationId " //
			+ "   and m.userId = :userId ")
	Page<MLPUserNotification> findActiveByUser(@Param("userId") String userId, Pageable pageable);

}
