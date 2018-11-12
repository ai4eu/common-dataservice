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

package org.acumos.cds.transport;

import java.util.List;

/**
 * Model for message sent as bulk-modification request.
 */
public class UsersRoleRequest implements MLPTransportModel {

	private boolean isAdd;
	private String roleId;
	private List<String> userIds;

	/**
	 * Builds an empty object.
	 */
	public UsersRoleRequest() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified values.
	 * 
	 * @param isAdd
	 *                    If true, add users to role; if false, remove from role.
	 * @param userIds
	 *                    List of users to update
	 * @param roleId
	 *                    Role ID
	 */
	public UsersRoleRequest(boolean isAdd, List<String> userIds, String roleId) {
		this.isAdd = isAdd;
		this.userIds = userIds;
		this.roleId = roleId;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[add=" + isAdd() + ", roleId=" + roleId + ", userIds=" + userIds + "]";
	}

}
