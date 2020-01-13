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
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Artifact entity with full object mappings (FOM, a lousy name I know) for all
 * complex fields including revisions, user, tags and web stats. Inherits all
 * simple field mappings from the abstract superclass.
 * 
 * Defined in the server project because it's not exposed to clients.
 * 
 * Provides a method to convert self to the client-friendly class.
 */
@Entity
@Table(name = MLPAbstractArtifact.TABLE_NAME)
@Immutable // never used for update
public class MLPArtifactFOM extends MLPAbstractArtifact implements Serializable {

	private static final long serialVersionUID = 814823907210569812L;

	/**
	 * An artifact has one owner. Unidirectional mapping
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@OneToOne
	@JoinColumn(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	private MLPUser owner;

	/**
	 * An artifact can participate in many revisions. Bidirectional mapping
	 * 
	 * Use default LAZY fetch. This is only used for searching (never fetched, never
	 * serialized as JSON).
	 */
	@ManyToMany(mappedBy = "artifacts")
	private Set<MLPSolutionRevisionFOM> revisions = new HashSet<>();

	public MLPUser getOwner() {
		return owner;
	}

	public void setOwner(MLPUser owner) {
		this.owner = owner;
	}

	public Set<MLPSolutionRevisionFOM> getRevisions() {
		return revisions;
	}

	public void setRevisions(Set<MLPSolutionRevisionFOM> revisions) {
		this.revisions = revisions;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[artifactId=" + getArtifactId() + ", owner=" + owner + ", artifactTypeCode="
				+ getArtifactTypeCode() + ", description=" + getDescription() + ", version=" + getVersion() + ", uri="
				+ getUri() + "revision count=" + revisions.size() + ", created=" + getCreated() + ", modified="
				+ getModified() + "]";
	}

	/**
	 * @return MLPArtifact with the information from this entity
	 * @throws URISyntaxException
	 *                                if a non-null URI violates RFC 2396
	 */
	public MLPArtifact toMLPArtifact() throws URISyntaxException {
		MLPArtifact art = new MLPArtifact(getVersion(), getArtifactTypeCode(), getName(), getUri(), owner.getUserId(),
				getSize());
		art.setArtifactId(getArtifactId());
		art.setCreated(getCreated());
		art.setDescription(getDescription());
		art.setMetadata(getMetadata());
		art.setModified(getModified());
		art.setUri(getUri());
		return art;
	}

}
