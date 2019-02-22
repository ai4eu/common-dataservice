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

import org.acumos.cds.domain.MLPProjPipelineMap.ProjPlMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the project-pipeline mapping table.
 */
@Entity
@IdClass(ProjPlMapPK.class)
@Table(name = "C_PROJ_PL_MAP")
public class MLPProjPipelineMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 535005148071293009L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class ProjPlMapPK implements Serializable {

		private static final long serialVersionUID = -2350615411857874125L;
		private String projectId;
		private String pipelineId;

		public ProjPlMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param projId
		 *                   project ID
		 * @param plId
		 *                   pipeline ID
		 */
		public ProjPlMapPK(String projId, String plId) {
			this.projectId = projId;
			this.pipelineId = plId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof ProjPlMapPK))
				return false;
			ProjPlMapPK thatPK = (ProjPlMapPK) that;
			return Objects.equals(projectId, thatPK.projectId) && Objects.equals(pipelineId, thatPK.pipelineId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId, pipelineId);
		}

	}

	@Id
	@Column(name = "PROJECT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Project ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String projectId;

	@Id
	@Column(name = "PIPELINE_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Pipeline ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String pipelineId;

	public MLPProjPipelineMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param projId
	 *                   project ID
	 * @param plId
	 *                   pipeline ID
	 */
	public MLPProjPipelineMap(String projId, String plId) {
		if (projId == null || plId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.projectId = projId;
		this.pipelineId = plId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPProjPipelineMap(MLPProjPipelineMap that) {
		this.projectId = that.projectId;
		this.pipelineId = that.pipelineId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String theId) {
		this.projectId = theId;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String theId) {
		this.pipelineId = theId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPProjPipelineMap))
			return false;
		MLPProjPipelineMap thatObj = (MLPProjPipelineMap) that;
		return Objects.equals(projectId, thatObj.projectId) && Objects.equals(pipelineId, thatObj.pipelineId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, pipelineId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[projectId=" + projectId + ", pipelineId=" + pipelineId + "]";
	}

}
