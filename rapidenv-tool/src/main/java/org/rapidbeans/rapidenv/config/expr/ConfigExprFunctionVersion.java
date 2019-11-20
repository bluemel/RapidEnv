/*
 * RapidEnv: ConfigExprFunctionVersion.java
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
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Project;

/**
 * Used to insert the scheduled install unit version into strings.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionVersion extends RapidBeanBaseConfigExprFunctionVersion {

	/**
	 * constructor for an Environment Variable Expression.
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param funcContent
	 *            the environment variable's name
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionVersion(final Installunit enclosingUnit, final EnvProperty enclosingProp,
	        final String funcContent, final Boolean escapeLiterals) {
		super();
		init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	/**
	 * the interpreter method of an Environment Variable Expression.
	 * 
	 * @return the resulting expanded string
	 */
	public final String interpret() {
		final Project project = RapidEnvInterpreter.getInstance().getProject();
		Installunit unit = null;
		if (getArgs().size() > 0) {
			final String unitname = getArgs().get(0).interpret();
			unit = project.findInstallunitConfiguration(unitname);
		}
		if (unit == null) {
			unit = getEnclosingInstallUnit();
		}
		return unit.getVersion().toString();
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionVersion.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
