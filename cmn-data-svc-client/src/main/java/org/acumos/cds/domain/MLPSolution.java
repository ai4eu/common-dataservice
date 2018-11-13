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

package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Solution entity with complex mappings for tags and web stats. Inherits all
 * simple field mappings from the abstract superclass.
 */
@Entity
@Table(name = MLPAbstractSolution.TABLE_NAME)
public class MLPSolution extends MLPAbstractSolution implements Serializable {

	private static final long serialVersionUID = 745945642089325612L;

	@Column(name = USER_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	/**
	 * ID of the peer where this was onboarded; null indicates local. Supports
	 * federation.
	 */
	@Column(name = "SOURCE_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Peer ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String sourceId;

	/**
	 * Tags assigned to the solution via a join table. Tags can be reused by many
	 * solutions, so this is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - the MLPTag object is not annotated.
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }". With that
	 * annotation, use of an EXISTING tag when creating a solution yields a SQL
	 * constraint-violation error, Hibernate attempts to insert a duplicate row to
	 * the join table, also see https://hibernate.atlassian.net/browse/HHH-6776
	 * 
	 * Eager fetch type ensures that tags are present when an entity is fetched by
	 * ID via a Spring-generated repository method.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = MLPSolTagMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPSolTagMap.SOL_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPSolTagMap.TAG_COL_NAME) })
	private Set<MLPTag> tags = new HashSet<>(0);

	/**
	 * No-arg constructor
	 */
	public MLPSolution() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits solution ID, which is generated
	 * on save.
	 * 
	 * @param name
	 *                   Solution Name
	 * @param userId
	 *                   User ID of owner
	 * @param active
	 *                   Boolean flag
	 */
	public MLPSolution(String name, String userId, boolean active) {
		super(name, active);
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolution(MLPSolution that) {
		super(that);
		this.userId = that.userId;
		this.sourceId = that.sourceId;
		this.tags = that.tags;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Solution tags may be updated by modifying this set, but all tag objects must
	 * exist; i.e., have been created previously.
	 * 
	 * @return Set of MLPTag, which may be empty.
	 */
	public Set<MLPTag> getTags() {
		return tags;
	}

	/**
	 * Solution tags may be updated via this method, but all tag objects must exist;
	 * i.e., have been created previously.
	 * 
	 * @param tags
	 *                 Set of MLPTag
	 */
	public void setTags(Set<MLPTag> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + getSolutionId() + ", name=" + getName() + ", owner="
				+ userId + ", active=" + isActive() + ", modelTypeCode=" + getModelTypeCode() + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}

}
