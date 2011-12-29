/*
 * RapidEnv: .java
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

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Project;
import org.rapidbeans.rapidenv.config.Property;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Function Expression to determine the home folder of a given tool.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionHomedir extends RapidBeanBaseConfigExprFunctionHomedir {

	/**
	 * The interpreting method.
	 * 
	 * @return home folder of the given tool
	 */
	public final String interpret() {
		
		String installUnitFullyQualifiedName = null;
        String otherVersion = null;

		if (getArgs().size() > 1) {
		    otherVersion = getArgs().get(1).interpret();
		}
		if (getArgs().size() > 0) {
			installUnitFullyQualifiedName = getArgs().get(0).interpret();
		} else if (getNextEnclosingInstallUnit() != null){
			installUnitFullyQualifiedName = getEnclosingInstallUnit().getFullyQualifiedName();
		} else {
			throw new RapidEnvConfigurationException("Error in fuction call homedir()."
					+ " Need an unambiguous or fully qualified valid install unit name or"
					+ " must be called in the context of an install unit.");
		}
		return homeDirOfInstallunit(installUnitFullyQualifiedName, otherVersion);
	}

	/**
	 * The constructor for "homedir" function expressions.
	 * 
     * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
	 * @param funcContent
	 *            should be empty for "homedir" functions.
     * @param escapeLiterals
     *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionHomedir(final Installunit enclosingUnit,
	        final Property enclosingProp,
			final String funcContent,
			final Boolean escapeLiterals) {
        super();
        init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	private String homeDirOfInstallunit(final String fullyQualifiedName,
	        final String otherVersion) {
		final Project project = RapidEnvInterpreter.getInstance().getProject();
		Installunit unit = project.findInstallunitConfiguration(fullyQualifiedName);
		if (unit == null) {
			throw new RapidEnvException("Install unit \"" + fullyQualifiedName
					+ "\" is not defined.");
		}
		String homedir = unit.getHomedirAsFile().getAbsolutePath();
		if (otherVersion != null) {
		    homedir = homedir.replace(unit.getVersion().toString(), otherVersion);
		}
		return homedir;
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
