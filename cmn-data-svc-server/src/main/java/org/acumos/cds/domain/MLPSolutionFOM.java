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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Solution entity with full object mappings (FOM, a lousy name I know) for all
 * complex fields including revisions, user, tags and web stats. Inherits all
 * simple field mappings from the abstract superclass.
 * 
 * Defined in the server project because it's not exposed to clients.
 * 
 * Provides a method to convert self to the client-friendly class.
 */
@Entity
@Table(name = MLPAbstractSolution.TABLE_NAME)
@Immutable // never used for update
public class MLPSolutionFOM extends MLPAbstractSolution implements Serializable {

	private static final long serialVersionUID = -6075523082529564585L;

	/**
	 * A solution has exactly one owner. Unidirectional mapping.
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@OneToOne
	@JoinColumn(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	private MLPUser user;

	/**
	 * A solution has zero or one source peer. Unidirectional mapping.
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@OneToOne
	@JoinColumn(name = "SOURCE_ID", columnDefinition = "CHAR(36)")
	private MLPPeer source;

	/**
	 * A solution may have many solution revisions. The solution revision entity has
	 * the solutionId field. Bidirectional mapping.
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@OneToMany(mappedBy = "solution") // field in MLPSolutionRevision
	private Set<MLPSolutionRevisionFOM> revisions = new HashSet<>(0);

	/**
	 * Tags assigned to the solution via a join table. Tags can be reused by many
	 * solutions, so this is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - the MLPTag object is not annotated.
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }". With that
	 * annotation, use of an EXISTING tag when creating a solution yields a SQL
	 * constraint-violation error, Hibernate attempts to insert a duplicate row to
	 * the join table, also see https://hibernate.atlassian.net/browse/HHH-6776
	 */
	@ManyToMany
	@JoinTable(name = MLPSolTagMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPSolTagMap.SOL_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPSolTagMap.TAG_COL_NAME) })
	private Set<MLPTag> tags = new HashSet<>(0);

	/**
	 * User access assigned to the solution via a join table. Users can be assigned
	 * to many solutions, so this is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - the MLPUser object is not annotated.
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@ManyToMany
	@JoinTable(name = MLPSolUserAccMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPSolUserAccMap.SOL_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPSolUserAccMap.USER_ID_COL_NAME) })
	private Set<MLPUser> accessUsers = new HashSet<>(0);

	public MLPUser getUser() {
		return user;
	}

	public void setUser(MLPUser owner) {
		this.user = owner;
	}

	public MLPPeer getSource() {
		return source;
	}

	public void setSource(MLPPeer source) {
		this.source = source;
	}

	public Set<MLPSolutionRevisionFOM> getRevisions() {
		return revisions;
	}

	public void setRevisions(Set<MLPSolutionRevisionFOM> revisions) {
		this.revisions = revisions;
	}

	public Set<MLPTag> getTags() {
		return tags;
	}

	public void setTags(Set<MLPTag> tags) {
		this.tags = tags;
	}

	public Set<MLPUser> getAccessUsers() {
		return accessUsers;
	}

	public void setAccessUsers(Set<MLPUser> accessUsers) {
		this.accessUsers = accessUsers;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + getSolutionId() + ", name=" + getName() + ", user=" + user
				+ ", active=" + isActive() + ", modelTypeCode=" + getModelTypeCode() + ", source=" + source
				+ ", revisions=" + revisions + ", created=" + getCreated() + ", modified=" + getModified() + "]";
	}

	/**
	 * @return MLPSolution with the information from this entity
	 */
	public MLPSolution toMLPSolution() {
		MLPSolution sol = new MLPSolution(getName(), user.getUserId(), isActive());
		sol.setCreated(getCreated());
		sol.setDownloadCount(getDownloadCount());
		sol.setFeatured(isFeatured());
		sol.setLastDownload(getLastDownload());
		sol.setMetadata(getMetadata());
		sol.setModelTypeCode(getModelTypeCode());
		sol.setModified(getModified());
		sol.setName(getName());
		sol.setOrigin(getOrigin());
		sol.setRatingAverageTenths(getRatingAverageTenths());
		sol.setRatingCount(getRatingCount());
		sol.setSolutionId(getSolutionId());
		if (source != null)
			sol.setSourceId(source.getPeerId());
		sol.setTags(getTags());
		sol.setToolkitTypeCode(getToolkitTypeCode());
		sol.setViewCount(getViewCount());
		return sol;
	}

}
