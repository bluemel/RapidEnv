/*
 * RapidEnv: ConfigExprProperty.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/27/2010
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copies of the GNU Lesser General Public License and the
 * GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.rapidbeans.rapidenv.config.expr;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * Used to insert RapidEnv property values into strings. For example:<br>
 * <p>
 * <code>MY_ENV=bbb</code><br>
 * <b>expression string:</b>&nbsp;<code>aaa/${myprop}/ccc</code><br>
 * <b>expanded string:</b>&nbsp;&nbsp;<code>aaa/bbb/ccc</code><br>
 * </p>
 * 
 * @author Martin Bluemel
 */
public class ConfigExprProperty extends RapidBeanBaseConfigExprProperty {

	// the environment variable's name
	private ConfigExpr childExpression = null;

    /**
     * constructor for an Environment Variable Expression.
     * 
     * @param enclosingUnit
     *            this enclosing install unit
     * @param enclosingProp
     *            the enclosing property
     * @param childExpression
     *            the child expression to interpret
     * @param escapeLiterals
     *            if literals should be escaped or not
     */
	public ConfigExprProperty(final Installunit enclosingUnit,
	        final Property enclosingProp,
			final ConfigExpr childExpression,
			final boolean escapeLiterals) {
		super();
		setEnclosingInstallUnit(enclosingUnit);
        setEnclosingProperty(enclosingProp);
        setEscapeLitrals(escapeLiterals);
		this.addChild(childExpression);
	}

	/**
	 * the interpreter method of an Environment Variable Expression.
	 * 
	 * @return the resulting expanded string
	 */
	public final String interpret() {
		final String propertyName = this.childExpression.interpret();
		final String defaultReturnValue = "${" + propertyName + "}";
		final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
		if (renv == null) {
			return defaultReturnValue;
		}
		String value = renv.getPropertyValue(propertyName);
		if (value == null) {
			value = renv.getPropertyValuePersisted(propertyName);
		}
		if (value == null) {
			final Property prop = renv.getProject().findPropertyConfiguration(propertyName);
			if (prop != null && prop.getValue() != null) {
				value = renv.interpret(null, getEnclosingProperty(), prop.getValue());
			}
		}
		if (value == null) {
			value = defaultReturnValue;
		} 
		return value;
	}

	/**
	 * this method must never be used.
	 * 
	 * @param child
	 *            - the child COnfiguration Expression
	 */
	public final void addChild(final ConfigExpr child) {
		if (this.childExpression != null) {
			throw new RapidEnvException("not more than one child allowd for ConfigExprProperty");
		}
		this.childExpression = child;
	}

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprProperty.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
