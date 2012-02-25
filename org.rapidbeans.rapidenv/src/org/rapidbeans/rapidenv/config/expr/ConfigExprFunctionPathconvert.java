/*
 * RapidEnv: ConfigExprFunctionPathconvert.java
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

import java.io.File;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * Function Expression to determine platform independently an platform specific
 * paths.<br>
 * In the given path string it simply replaces all '/' and '\' separator
 * characters with the specified or the platform specific separator. To
 * determine a platform independent path you can specify a certain separator
 * character (usually '/').<br>
 * To determine a platform specific path just do not specify a certain
 * separator. The platform's separator the is taken by default.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionPathconvert extends RapidBeanBaseConfigExprFunctionPathconvert {

	/**
	 * The interpreting method determines converted path.<br>
	 * Interprets the given expression and converts it afterwards by just
	 * replacing all potential separator characters ('/' and '\') with given or
	 * the platform specific separator character.
	 * 
	 * @return the converted path as string
	 */
	public final String interpret() {
		char separator = File.separatorChar;
		final String path = this.getArgs().get(0).interpret();
		if (this.getArgs().size() > 1) {
			separator = this.getArgs().get(1).interpret().charAt(0);
		}
		return pathconvert(path, separator);
	}

	/**
	 * The constructor for Pathconvert Function Expressions.
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
	public ConfigExprFunctionPathconvert(final Installunit enclosingUnit, final Property enclosingProp,
	        final String funcContent, final Boolean escapeLiterals) {
		super();
		init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	/**
	 * The path converting method. Simply replaces all potential separator
	 * characters ('/' and '\') with the given or the platform specific
	 * separator character.
	 * 
	 * @param s
	 *            the string with the path to convert
	 * @param sep
	 *            separator
	 * 
	 * @return a string containing the converted path
	 */
	public static String pathconvert(final String s, final char sep) {
		String sConvert = s.replace('/', sep);
		sConvert = sConvert.replace('\\', sep);
		return sConvert;
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionPathconvert.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
