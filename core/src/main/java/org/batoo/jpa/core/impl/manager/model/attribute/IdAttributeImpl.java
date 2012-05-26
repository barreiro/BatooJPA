/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.manager.model.attribute;

import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.manager.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.manager.jdbc.PkPhysicalColumn;
import org.batoo.jpa.core.impl.manager.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.manager.model.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeImpl<X, T> extends PhysicalAttributeImpl<X, T> {

	private final String generator;
	private final IdType idType;
	private PkPhysicalColumn column;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeImpl(ManagedTypeImpl<X> declaringType, IdAttributeMetadata metadata) {
		super(declaringType, metadata);

		final JdbcAdaptor jdbcAdaptor = declaringType.getMetamodel().getJdbcAdaptor();

		final GeneratedValueMetadata generatedValue = metadata.getGeneratedValue();
		if (generatedValue != null) {
			this.generator = generatedValue.getGenerator();
			this.idType = jdbcAdaptor.supports(generatedValue.getStrategy());
		}
		else {
			this.generator = null;
			this.idType = IdType.MANUAL;
		}

		if (metadata.getSequenceGenerator() != null) {
			declaringType.getMetamodel().addSequenceGenerator(metadata.getSequenceGenerator());
		}
		if (metadata.getSequenceGenerator() != null) {
			declaringType.getMetamodel().addSequenceGenerator(metadata.getSequenceGenerator());
		}

		this.initColumn(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PhysicalColumn getColumn() {
		return this.column;
	}

	/**
	 * Returns the idType.
	 * 
	 * @return the idType
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdType getIdType() {
		return this.idType;
	}

	/**
	 * Initializes the column for the attribute.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initColumn(IdAttributeMetadata metadata) {
		final int sqlType = TypeFactory.getSqlType(this.getJavaType(), this.getTemporalType(), null, false);

		final JdbcAdaptor jdbcAdaptor = this.getDeclaringType().getMetamodel().getJdbcAdaptor();

		this.column = new PkPhysicalColumn(jdbcAdaptor, this, sqlType, (metadata != null) && (metadata.getColumn() != null)
			? metadata.getColumn() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

}