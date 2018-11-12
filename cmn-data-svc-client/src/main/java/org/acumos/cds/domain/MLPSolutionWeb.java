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
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for solution website metadata, which transports descriptive statistics
 * recorded on the server to the client. Never used for update from the client,
 * so there are no Swagger API annotations on this class.
 * 
 * In a one:one relationship with solution. The relationship is mapped via
 * annotations on MLPSolution (unidirectional).
 * 
 * These stats were separated out of the solution entity to distinguish updates
 * ON a solution made by the owner (e.g., description) from updates ABOUT a
 * solution made by the system (e.g., downloads or views).
 */
@Entity
@Table(name = "C_SOLUTION_WEB")
public class MLPSolutionWeb implements MLPEntity, Serializable {

	/* package */ static final String SOL_ID_COL_NAME = "SOLUTION_ID";

	private static final long serialVersionUID = 5613267071143825368L;

	@Id
	@Column(name = SOL_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "VIEW_COUNT", columnDefinition = "INT")
	@ApiModelProperty(value = "View count", example = "1")
	private Long viewCount = 0L;

	@Column(name = "DOWNLOAD_COUNT", columnDefinition = "INT")
	@ApiModelProperty(value = "Download count", example = "1")
	private Long downloadCount = 0L;

	@Column(name = "LAST_DOWNLOAD", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Date of most recent download")
	private Date lastDownload;

	@Column(name = "RATING_COUNT", columnDefinition = "INT")
	@ApiModelProperty(value = "Rating count", example = "1")
	private Long ratingCount = 0L;

	@Column(name = "RATING_AVG_TENTHS", columnDefinition = "INT")
	@ApiModelProperty(value = "Rating average in tenths; e.g., value 35 means 3.5", example = "35")
	private Long ratingAverageTenths = 0L;

	@Column(name = "FEATURED_YN", columnDefinition = "CHAR(1)")
	@Type(type = "yes_no")
	@ApiModelProperty(value = "Featured indicator")
	private boolean featured;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionWeb() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param solutionId
	 *                       ID of solution
	 */
	public MLPSolutionWeb(String solutionId) {
		if (solutionId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPSolutionWeb(MLPSolutionWeb that) {
		this.downloadCount = that.downloadCount;
		this.featured = that.featured;
		this.lastDownload = that.lastDownload;
		this.ratingAverageTenths = that.ratingAverageTenths;
		this.ratingCount = that.ratingCount;
		this.solutionId = that.solutionId;
		this.viewCount = that.viewCount;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Date getLastDownload() {
		return lastDownload;
	}

	public void setLastDownload(Date lastDownload) {
		this.lastDownload = lastDownload;
	}

	public Long getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Long ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Long getRatingAverageTenths() {
		return ratingAverageTenths;
	}

	public void setRatingAverageTenths(Long ratingAverageTenths) {
		this.ratingAverageTenths = ratingAverageTenths;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolutionWeb))
			return false;
		MLPSolutionWeb thatObj = (MLPSolutionWeb) that;
		return Objects.equals(solutionId, thatObj.solutionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, downloadCount, viewCount);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + solutionId + ", views=" + viewCount + ", downloads="
				+ downloadCount + ", ratings=" + ratingCount + ", ..]";
	}

}
