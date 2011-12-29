/*
 * RapidEnv: ConfigExprFunctionReplace.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 10/14/2011
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
 * Function Expression for simple text replacement.<br>
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionReplace extends RapidBeanBaseConfigExprFunctionReplace {

	/**
	 * The interpreting method performs the text replacement.
	 * 
	 * @return the processed text as string
	 */
	public final String interpret() {
		final String text = this.getArgs().get(0).interpret();
		final String pattern = this.getArgs().get(1).interpret();
		final String replacement = this.getArgs().get(2).interpret();
		final String replaced = text.replaceAll(pattern, replacement);
		return replaced;
	}

	/**
	 * The constructor for replace Function Expressions.
	 * 
     * @param enclosingUnit
     *            the enclosing install unit
     * @param enclosingProp
     *            the enclosing property
	 * @param funcContent
	 *            the function's parameter list. Potential arguments: <br>
	 *            <code>&lt;path to convert&gt;</code><br>
	 *            <code>&lt;path to convert&gt; &lt;separator char&gt;
     *                    </code><br>
	 *            <code>&lt;path to convert&gt;</code> the string with the path
	 *            to convert.<br>
	 *            <code>&lt;separator char&gt</code> the separator character to
	 *            use in the converted path<br>
	 *            If <code>&lt;separator char&gt</code> is not given the
	 *            platform specific separator char is used.
     * @param escapeLiterals
     *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionReplace(
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
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionReplace.class);

    /**
     * @return the bean's type
     */
    @Override
    public TypeRapidBean getType() {
        return type;
    }
}
