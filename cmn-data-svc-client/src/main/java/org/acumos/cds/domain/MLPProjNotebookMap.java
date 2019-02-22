/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPProjNotebookMap.ProjNbMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the project-notebook mapping table.
 */
@Entity
@IdClass(ProjNbMapPK.class)
@Table(name = "C_PROJ_NB_MAP")
public class MLPProjNotebookMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -5624135334134901373L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class ProjNbMapPK implements Serializable {

		private static final long serialVersionUID = 5961583696260134497L;
		private String projectId;
		private String notebookId;

		public ProjNbMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param projId
		 *                   project ID
		 * @param nbId
		 *                   notebook ID
		 */
		public ProjNbMapPK(String projId, String nbId) {
			this.projectId = projId;
			this.notebookId = nbId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof ProjNbMapPK))
				return false;
			ProjNbMapPK thatPK = (ProjNbMapPK) that;
			return Objects.equals(projectId, thatPK.projectId) && Objects.equals(notebookId, thatPK.notebookId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId, notebookId);
		}

	}

	@Id
	@Column(name = "PROJECT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Project ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String projectId;

	@Id
	@Column(name = "NOTEBOOK_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Notebook ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String notebookId;

	public MLPProjNotebookMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param projId
	 *                   project ID
	 * @param nbId
	 *                   notebook ID
	 */
	public MLPProjNotebookMap(String projId, String nbId) {
		if (projId == null || nbId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.projectId = projId;
		this.notebookId = nbId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPProjNotebookMap(MLPProjNotebookMap that) {
		this.projectId = that.projectId;
		this.notebookId = that.notebookId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String theId) {
		this.projectId = theId;
	}

	public String getNotebookId() {
		return notebookId;
	}

	public void setNotebookId(String theId) {
		this.notebookId = theId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPProjNotebookMap))
			return false;
		MLPProjNotebookMap thatObj = (MLPProjNotebookMap) that;
		return Objects.equals(projectId, thatObj.projectId) && Objects.equals(notebookId, thatObj.notebookId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, notebookId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[projectId=" + projectId + ", notebookId=" + notebookId + "]";
	}

}
