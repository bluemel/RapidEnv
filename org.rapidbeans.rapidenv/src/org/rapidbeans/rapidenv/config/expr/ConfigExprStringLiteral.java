/*
 * RapidEnv: ConfigExprStringLiteral.java
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
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.EnvProperty;

/**
 * A Constant String Expression can be defined implicitly and explicitly<br>
 * - text that is not recognized as any other expression implicitly is a
 * Constant String Expression.<br>
 * - text between ' characters explicitly is a Constant String Expression. This
 * text is prevented from being interpreted<br>
 * For example<br>
 * &nbsp;&nbsp;<code>xxx${ENVVAR1}'yyy${yyy}'hostname()zzz</code> is interpreted
 * as a sequence of 5 ConfigfileChange Expressions:<br>
 * - <code>Constant String Expression: text = "xxx"</code><br>
 * - <code>Environment Variable Expression: varname = "ENVVAR1"</code><br>
 * - <code>Constant String Expression: text = "yyy${yyy}"</code><br>
 * - <code>Function Expression: function = hostname()</code><br>
 * - <code>Constant String Expression: text = "zzz"</code><br>
 * 
 * @author Martin Bluemel
 */
public class ConfigExprStringLiteral extends RapidBeanBaseConfigExprStringLiteral {

	// the string with text
	private String text = null;

	/**
	 * The constructor for a Constant String Expression.
	 * 
	 * @param enclosingUnit
	 *            this enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param text
	 *            the string with text
	 */
	public ConfigExprStringLiteral(final Installunit enclosingUnit, final EnvProperty enclosingProp, final String text) {
		super();
		setEnclosingInstallUnit(enclosingUnit);
		setEnclosingProperty(enclosingProp);
		this.text = text;
	}

	/**
	 * The interpreting method just returns the text.
	 * 
	 * @return constant text as string
	 */
	public String interpret() {
		return this.text;
	}

	/**
	 * As a constant string expression must not have children this method always
	 * throws an Exception.
	 * 
	 * @param child
	 *            the argument is not used for this method.
	 */
	public void addChild(final ConfigExpr child) {
		throw new RapidEnvException("no childs for ConfigExprString");
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprStringLiteral.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
