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
 * Model for message sent as role bulk-modification request.
 */
public class BatchIdRoleRequest implements MLPTransportModel {

	private boolean isAdd;
	private String roleId;
	private List<String> ids;

	/**
	 * Builds an empty object.
	 */
	public BatchIdRoleRequest() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified values.
	 * 
	 * @param isAdd
	 *                   If true, add IDs to role; if false, remove from role.
	 * @param ids
	 *                   List of IDs to update
	 * @param roleId
	 *                   Role ID
	 */
	public BatchIdRoleRequest(boolean isAdd, List<String> ids, String roleId) {
		this.isAdd = isAdd;
		this.ids = ids;
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

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[add=" + isAdd() + ", roleId=" + roleId + ", ids=" + ids + "]";
	}

}
