/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for colesce function.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CoalesceExpression<T> extends AbstractExpression<T> implements Coalesce<T> {

	private final List<Expression<? extends T>> values = Lists.newArrayList();
	private String alias;

	/**
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public CoalesceExpression() {
		super((Class<T>) Object.class);
	}

	/**
	 * @param x
	 *            the x expression
	 * @param y
	 *            the y expression
	 * 
	 * @since 2.0.0
	 */
	public CoalesceExpression(Expression<? extends T> x, Expression<? extends T> y) {
		this();

		this.value(x);
		this.value(y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		return "coalesce(" + Joiner.on(", ").join(Lists.transform(this.values, new Function<Expression<? extends T>, String>() {

			@Override
			public String apply(Expression<? extends T> input) {
				return ((AbstractExpression<? extends T>) input).generateJpqlRestriction(query);
			}
		})) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
		}

		return this.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(final BaseQueryImpl<?> query) {
		return new String[] { "COALESCE(" + Joiner.on(", ").join(Lists.transform(this.values, new Function<Expression<? extends T>, String>() {

			@Override
			public String apply(Expression<? extends T> input) {
				return ((AbstractExpression<? extends T>) input).getSqlRestrictionFragments(query)[0];
			}
		})) + ")" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (T) row.getObject(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CoalesceExpression<T> value(Expression<? extends T> value) {
		this.values.add(value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CoalesceExpression<T> value(T value) {
		return this.value(new EntityConstantExpression<T>(null, value));
	}
}
