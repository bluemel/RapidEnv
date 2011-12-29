/*
 * RapidEnv: ConfigExprFunctionUsername.java
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

public class ConfigExprFunctionUsername extends RapidBeanBaseConfigExprFunctionUsername {

    /**
    * The interpreting method determines the user name platform independently.<br>
    * The user name determination is done by over Java system property user.name
    * 
    * @return username as string
    */
    public String interpret() {
        return PlatformHelper.username();
    }
    
    /**
     * The constructor for the Hostname Function Expression.
     * 
     * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
     * @param funcContent function parameter list. Must be empty for Hostname
     *            Function Expressions.
     * @param escapeLiterals
     *            if escaping literals is desired or not
     */
    public ConfigExprFunctionUsername(
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
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionUsername.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
