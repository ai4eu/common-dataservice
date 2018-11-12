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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPValidationSequence.ValidationSequencePK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a validation sequence. This table specifies the required validation
 * steps and their order.
 */
@Entity
@IdClass(ValidationSequencePK.class)
@Table(name = "C_SOL_VAL_SEQ")
public class MLPValidationSequence extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -1820957829436363560L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class ValidationSequencePK implements Serializable {

		private static final long serialVersionUID = 7660390039219881481L;
		private Integer sequence;
		private String valTypeCode;

		public ValidationSequencePK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param sequence
		 *                        Sequence
		 * @param valTypeCode
		 *                        validation type code
		 */
		public ValidationSequencePK(Integer sequence, String valTypeCode) {
			this.sequence = sequence;
			this.valTypeCode = valTypeCode;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof ValidationSequencePK))
				return false;
			ValidationSequencePK thatPK = (ValidationSequencePK) that;
			return Objects.equals(sequence, thatPK.sequence) && Objects.equals(valTypeCode, thatPK.valTypeCode);
		}

		@Override
		public int hashCode() {
			return Objects.hash(sequence, valTypeCode);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[sequence=" + sequence + ", valTypeCode=" + valTypeCode + "]";
		}

	}

	@Id
	@Column(name = "SEQ", nullable = false, columnDefinition = "SMALLINT")
	@NotNull(message = "Sequence cannot be null")
	@ApiModelProperty(required = true, value = "Validation step sequence")
	private Integer sequence;

	@Id
	@Column(name = "VAL_TYPE_CD", nullable = false, updatable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Validation type code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Validation type code", example = "TA")
	private String valTypeCode;

	public MLPValidationSequence() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param sequence
	 *                        Sequence
	 * @param valTypeCode
	 *                        validation type code
	 */
	public MLPValidationSequence(Integer sequence, String valTypeCode) {
		if (sequence == null || valTypeCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.sequence = sequence;
		this.valTypeCode = valTypeCode;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPValidationSequence(MLPValidationSequence that) {
		super(that);
		this.sequence = that.sequence;
		this.valTypeCode = that.valTypeCode;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getValTypeCode() {
		return valTypeCode;
	}

	public void setValTypeCode(String valTypeCode) {
		this.valTypeCode = valTypeCode;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPValidationSequence))
			return false;
		MLPValidationSequence thatObj = (MLPValidationSequence) that;
		return Objects.equals(sequence, thatObj.sequence) && Objects.equals(valTypeCode, thatObj.valTypeCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequence, valTypeCode);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[sequence=" + sequence + ", valTypeCode=" + valTypeCode + ", created="
				+ getCreated() + "]";
	}

}
