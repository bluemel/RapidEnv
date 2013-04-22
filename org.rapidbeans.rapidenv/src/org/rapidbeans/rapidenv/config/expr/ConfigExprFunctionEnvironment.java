/*
 * RapidEnv: ConfigExprFunctionEnvironment.java
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
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.EnvProperty;

/**
 * Usage of function environment() is discouraged. In RapidEnv you are supposed
 * not to use any global environment variable but to define properties instead.
 * Some properties also have environment variables but since environment
 * variables can be platform dependent you should always reference properties
 * instead of environment variables.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionEnvironment extends RapidBeanBaseConfigExprFunctionEnvironment {

	/**
	 * constructor for an environment function expression.
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param funcContent
	 *            the function contents
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionEnvironment(final Installunit enclosingUnit, final EnvProperty enclosingProp,
	        final String funcContent, final Boolean escapeLiterals) {
		super();
		init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	/**
	 * the interpreter method of an Environment Variable Expression.
	 * 
	 * @return the resulting expanded string or an empty string in case the
	 *         environment variable is not defined.
	 */
	public final String interpret() {
		final String defaultReturnValue = "";
		final String varname = this.getArgs().get(0).interpret();
		final String value = System.getenv(varname);
		if (value == null) {
			return defaultReturnValue;
		} else {
			return value;
		}
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionHomedir.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
