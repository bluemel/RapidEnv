/*
 * RapidEnv: ConfigExprFunctionPathconvertc.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/28/2010
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

import java.io.File;
import java.io.IOException;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * Function Expression to determine a platform specific
 * canonical (absolute and normalized) path.<br>
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionPathconvertc extends RapidBeanBaseConfigExprFunctionPathconvertc {

    /**
     * The interpreting method determines the canonical path.<br>
     *
     * @return the canonical path
     */
    public final String interpret() {
    	final String path = getArgs().get(0).interpret();
        return pathconvert(path);
    }

    /**
     * The constructor for canonical Function Expressions.
     * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
     * @param funcContent the function's parameter list. Potential arguments:<br>
     *            <code>&lt;path to convert&gt;</code><br>
     * @param escapeLiterals
     *            if escaping literals is desired or not
     */
    public ConfigExprFunctionPathconvertc(
            final Installunit enclosingUnit,
            final Property enclosingProp,
            final String funcContent,
			final Boolean escapeLiterals) {
        super();
        init(enclosingUnit, enclosingProp, funcContent,escapeLiterals);
    }

    /**
     * The path converting method. 
     * Simply applies Java getCanonocatPath method.
     * 
     * @param s the string with the path to convert
     * @return a string containing the converted path
     */
    private String pathconvert(final String s) {
        try {
            return new File(s).getCanonicalPath();
        } catch (IOException e) {
            throw new RapidEnvException(e);
        }
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionPathconvertc.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
