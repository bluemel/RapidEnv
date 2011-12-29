/*
 * RapidEnv: ConfigExprTopLevel.java
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
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * The Top Level ConfigfileChange Expression just serves as a root node for the
 * whole expression tree<BR>.
 * It just has a list of child expessions - nothing more.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprTopLevel extends RapidBeanBaseConfigExprTopLevel {

    /**
     * The constructor for a Top Level ConfigfileChange Expression takes the
     * whole expression to interpret as an argument.
     * 
     * @param enclosingUnit
     *            this enclosing install unit
     * @param enclosingProp
     *            the enclosing property
     * @param experssionString
     *            the expression string to interpret
     * @param escapeLiterals
     *            determine if literals should be escaped or not
     */
    public ConfigExprTopLevel(final Installunit enclosingUnit,
            final Property enclosingProp, final String experssionString,
            final boolean escapeLiterals) {
        super();
        setEnclosingInstallUnit(enclosingUnit);
        setEnclosingProperty(enclosingProp);
        ConfigExprParser.parse(this, enclosingUnit,
        		enclosingProp, experssionString, escapeLiterals);
    }

    /**
     * The interpreting method just concatenates the interpretations of all
     * child expressions.
     * 
     * @return hostname as string
     */
    public final String interpret() {
        return this.interpretChildExpressions();
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprTopLevel.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
