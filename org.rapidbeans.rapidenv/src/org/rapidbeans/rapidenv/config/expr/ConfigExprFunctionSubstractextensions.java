/*
 * RapidEnv: ConfigExprFunctionSubstractextensions.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 12/26/2010
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
import java.util.List;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;
import org.rapidbeans.rapidenv.config.PropertyExtension;
import org.rapidbeans.rapidenv.config.PropertyValueType;

public class ConfigExprFunctionSubstractextensions extends RapidBeanBaseConfigExprFunctionSubstractextensions {

    /**
    * The interpreting method determines the user name platform independently.<br>
    * The user name determination is done by over Java system property user.name
    * 
    * @return the interpreted path expression
    */
    public String interpret() {
        final Property prop = this.getEnclosingProperty();
        if (prop == null) {
            throw new RapidEnvException("ERROR while trying to interpret expression."
                    + "\n  No enclosing property defined");
        }
        if (prop.getValuetype() != PropertyValueType.path) {
            throw new RapidEnvException("ERROR while trying to interpret expression."
                    + "\n  Enclosing property is no \"path\" property."
                    + "\n  The function substractextensions is only valid to apply in the context of paths.");
        }
        final String path = this.getArgs().get(0).interpret();
        final List<String> pathElements = StringHelper.split(path, File.pathSeparator);
        if (pathElements.size() == 0) {
            return "";
        }
        for (final PropertyExtension pe : prop.getExtensions()) {
            if (pathElements.size() == 0) {
                return "";
            }
            final String pevalue = prop.normalize(pe.getValue());
            final int index = pathElements.indexOf(pevalue);
            if (index != -1) {
                pathElements.remove(index);
            }
        }
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < pathElements.size(); i++) {
            if (i > 0) {
                result.append(File.pathSeparator);
            }
            result.append(pathElements.get(i));
        }
        return result.toString();
    }

    /**
     * The constructor for the substractextensions Function Expression.
     * 
     * @param enclosingUnit
     *            the enclosing install unit instance or null if property
     * @param enclosingProp
     *            the enclosing property instance or null if install unit
     * @param funcContent
     *            function parameter list. Must be empty for Hostname
     *            Function Expressions.
     * @param escapeLiterals
     *            if escaping literals is desired or not
     */
    public ConfigExprFunctionSubstractextensions(final Installunit enclosingUnit,
            final Property enclosingProp, final String funcContent,
			final Boolean escapeLiterals) {
        super();
        init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionSubstractextensions.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
