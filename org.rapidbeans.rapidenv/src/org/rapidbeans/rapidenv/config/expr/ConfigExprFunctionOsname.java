/*
 * RapidEnv: ConfigExprFunctionOsname.java
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

import java.util.HashMap;
import java.util.List;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Determines the operating system (family) name
 * using the RapidBeans frameworks platform helper.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionOsname extends RapidBeanBaseConfigExprFunctionOsname {

	/**
	 * The interpreting method determines the operating system (family).<br>
	 * The operating system (family) determination is delegated to the java variable os.name
	 *
	 * @return osName as string
	 */
	public String interpret() {
	    final HashMap<String, String> conversionMap = new HashMap<String, String>();
	    if (this.getArgs().size() > 0) {
	        final String conversionMapString = this.getArgs().get(0).interpret();
	        for (final String conversionMapEntry : StringHelper.split(conversionMapString, ";")) {
	            final List<String> splitEntry = StringHelper.split(conversionMapEntry, "=");
	            switch (splitEntry.size()) {
	            case 0:
	                throw new RapidEnvConfigurationException("Error evaluating entry \""
	                        + conversionMapEntry + "\" from conversion map: \"" + conversionMapString + "\"\n"
	                        + "No separator '=' specified.");
	            case 1:
	                conversionMap.put(splitEntry.get(0), "");
	                break;
                case 2:
                    conversionMap.put(splitEntry.get(0), splitEntry.get(1));
                    break;
                default:
                    throw new RapidEnvConfigurationException("Error evaluating entry \""
                            + conversionMapEntry + "\" from conversion map: \"" + conversionMapString + "\"\n"
                            + "More than one separator '=' specified.");
	            }
	        }
	    }
		final String osname = PlatformHelper.getOs().name();
		final String converted = conversionMap.get(osname);
		if (converted != null) {
		    return converted;
		} else {
		    return osname;
		}
	}

	/**
	 * The constructor for the Function Expression.
	 * 
    * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
	 * @param funcContent
	 *            function parameter list. Must be empty for Hostname Function
	 *            Expressions.
     * @param escapeLiterals
     *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionOsname(
	        final Installunit enclosingUnit,
            final Property enclosingProp,
			final String funcContent,
			final Boolean escapeLiterals) {
        super();
        init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionOsname.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}