/*
 * RapidEnv: ConfigExprFunctionHostname.java
 *
 * Copyright (C) 2010 - 2013 Martin Bluemel
 *
 * Creation Date: 04/04/2013
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
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Function Expression to determine the hostname platform indepently.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionName extends
        RapidBeanBaseConfigExprFunctionName {

	/**
	 * The interpreting method determines the hostname platform indepently.<br>
	 * The hostname determination is delegated to
	 * <code><a href='UtilsPlatform.html#hostname()'>
	 * UtilsPlatform.hostname()</a></code>
	 * 
	 * @return hostname as string
	 */
	public final String interpret() {
		String name = "";
		final Installunit enclosingUnit = getEnclosingInstallUnit();
		if (enclosingUnit != null) {
			name = enclosingUnit.getName();
		} else {
			final EnvProperty enclosingProp = getEnclosingProperty();
			if (enclosingProp != null) {
				name = enclosingProp.getName();
			}
		}
		return name;
	}

	/**
	 * The constructor for the "hostname" function expression.
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param funcContent
	 *            function parameter list. Must be empty for "hostname" function
	 *            expressions.
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionName(final Installunit enclosingUnit,
	        final EnvProperty enclosingProp, final String funcContent,
	        final Boolean escapeLiterals) {
		super();
		init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean
	        .createInstance(ConfigExprFunctionName.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
