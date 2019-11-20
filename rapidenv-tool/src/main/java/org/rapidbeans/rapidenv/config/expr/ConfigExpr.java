/*
 * RapidEnv: ConfigExpr.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 05/30/2010
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

import java.util.List;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * The parent class of each configuration expression. Configuration expressions
 * are mainly used to expand strings in configuration files with values of
 * variables. Currently only environment variables are supported. Additionally
 * there is a concept of functions for more complex expansions<br>
 * For the implementation of configuration expressions we use the Interpreter
 * pattern.
 * 
 * @author Martin Bluemel
 */
public abstract class ConfigExpr extends RapidBeanBaseConfigExpr {

	private boolean escapeLitrals = false;

	/**
	 * @return the escapeLitrals
	 */
	public boolean getEscapeLitrals() {
		return escapeLitrals;
	}

	/**
	 * @param escapeLitrals
	 *            the escapeLitrals to set
	 */
	public void setEscapeLitrals(boolean escapeLitrals) {
		this.escapeLitrals = escapeLitrals;
	}

	/**
	 * The interpreter method of every ConfigfileChange Expression.
	 * 
	 * @return the resulting expanded string
	 */
	public abstract String interpret();

	/**
	 * interprets the ConfigfileChange Expression's child expressions.
	 * 
	 * @return the interpreted string
	 */
	protected final String interpretChildExpressions() {
		StringBuffer buf = new StringBuffer();
		final List<ConfigExpr> childs = this.getChilds();
		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				buf.append(childs.get(i).interpret());
			}
		}
		return buf.toString();
	}

	/**
	 * Convenient interface that builds and evaluates a Configuation Expression
	 * in one step.
	 * 
	 * Expands environment variables in a String and additionally interprets
	 * some functions like hostname() or pathconvert().
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * 
	 * @param enclosingProp
	 *            the enclosing property
	 * @param s
	 *            the string to expand
	 * @param escapeLiterals
	 *            if literals should be escaped
	 * @return the expanded string
	 */
	public static String expand(final Installunit enclosingUnit, final EnvProperty enclosingProp, final String s,
	        final boolean escapeLiterals) {
		String s1 = s;
		if (enclosingUnit != null) {
			s1 = s.replaceAll("\\$\\{version\\}", enclosingUnit.getVersion().toString());
		}
		return new ConfigExprTopLevel(enclosingUnit, enclosingProp, s1, escapeLiterals).interpret();
	}

	/**
	 * property references initialization.
	 */
	public void initProperties() {
		super.initProperties();
	}

	public Installunit getNextEnclosingInstallUnit() {
		if (getEnclosingInstallUnit() != null) {
			if (this instanceof ConfigExpr)
				return getEnclosingInstallUnit();
		}
		final RapidBean parentBean = this.getParentBean();
		if (parentBean != null) {
			if (parentBean instanceof ConfigExpr) {
				return ((ConfigExpr) this.getParentBean()).getNextEnclosingInstallUnit();
			}
		}
		return null;
	}

	/**
	 * default constructor.
	 */
	public ConfigExpr() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigExpr(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigExpr(final String[] sa) {
		super(sa);
	}
}
