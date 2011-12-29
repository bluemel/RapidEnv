/*
 * RapidEnv: ConfigExprFunctionHostname.java
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
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * Function Expression to determine the hostname platform indepently.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionHostname extends RapidBeanBaseConfigExprFunctionHostname {

    /**
     * The interpreting method determines the hostname platform indepently.<br>
     * The hostname determination is delegated to
     * <code><a href='UtilsPlatform.html#hostname()'>
     * UtilsPlatform.hostname()</a></code>
     * 
     * @return hostname as string
     */
    public final String interpret() {
        return PlatformHelper.hostname();
    }

    /**
     * The constructor for the "hostname" function expression.
     * 
     * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
     * @param funcContent
     *            function parameter list. Must be empty for "hostname"
     *            function expressions.
     * @param escapeLiterals
     *            if escaping literals is desired or not
     */
    public ConfigExprFunctionHostname(
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
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionHostname.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
