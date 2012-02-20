/*
 * RapidEnv: ConfigExprFunctionFileContents.java
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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Property;

/**
 * Function Expression to determine the contents of a given file.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionFilecontents extends RapidBeanBaseConfigExprFunctionFilecontents {

	/**
	 * The constructor for filecontents function expressions.
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param funcContent
	 *            the function's parameter list. arguments: <br>
	 *            <code>&lt;path to file&gt;</code><br>
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 */
	public ConfigExprFunctionFilecontents(final Installunit enclosingUnit, final Property enclosingProp,
	        final String funcContent, final Boolean escapeLiterals) {
		super();
		init(enclosingUnit, enclosingProp, funcContent, escapeLiterals);
	}

	/**
	 * The interpreting method determines the contents of the given file
	 * 
	 * @return contents of the given file
	 */
	public final String interpret() {

		final String filename = this.getArgs().get(0).interpret();
		final File file = new File(filename);
		if (!file.exists()) {
			throw new RapidEnvException("File \"" + file.getAbsolutePath() + "\" does not exist.");
		}

		String charsToEscape = null;
		if (this.getArgs().size() > 1 && this.getArgs().get(1) != null) {
			charsToEscape = this.getArgs().get(1).interpret();
		}
		final List<Character> caToEscape = new ArrayList<Character>();
		if (charsToEscape != null) {
			final int len = charsToEscape.length();
			char c1;
			for (int i = 0; i < len; i++) {
				c1 = charsToEscape.charAt(i);
				if (c1 == '\\') {
					if ((i + 1) == len) {
						throw new RapidEnvException("Problems with argument 1 (characters to excape):"
						        + " unecpected end of string after escaping character '\\'");
					}
					c1 = charsToEscape.charAt(++i);
					switch (c1) {
					case 'n':
						caToEscape.add('\n');
						break;
					case 'r':
						caToEscape.add('\r');
						break;
					case 't':
						caToEscape.add('\t');
						break;
					case '\\':
						caToEscape.add('\\');
						break;
					default:
						throw new RapidEnvException("Problems with argument 1 (characters to excape):"
						        + " unecpected escaped character '" + c1 + "'");
					}
				} else {
					caToEscape.add(c1);
				}
			}
		}

		// preserve: take the line feed from the file
		// platform: always use the platform specific line feed
		// normalize: always use \n as line feed
		LinefeedControl linefeedControl = LinefeedControl.preserve;
		if (this.getArgs().size() > 2 && this.getArgs().get(2) != null) {
			linefeedControl = LinefeedControl.valueOf(this.getArgs().get(2).interpret());
		}

		InputStreamReader reader = null;
		try {
			final StringBuffer buf = new StringBuffer();
			reader = new LinefeedControlInputStreamReader(file, linefeedControl);
			int i;
			if (caToEscape.size() == 0) {
				while ((i = reader.read()) != -1) {
					buf.append((char) i);
				}
			} else {
				char c2;
				boolean translated = false;
				final int size = caToEscape.size();
				while ((i = reader.read()) != -1) {
					c2 = (char) i;
					for (int i2 = 0; i2 < size; i2++) {
						if (c2 == caToEscape.get(i2).charValue()) {
							switch (c2) {
							case '\n':
								c2 = 'n';
								break;
							case '\r':
								c2 = 'r';
								break;
							case '\t':
								c2 = 't';
								break;
							default: // do nothing
								break;
							}
							buf.append('\\');
							break;
						}
					}
					if (!translated) {
						buf.append(c2);
					}
				}
			}
			return buf.toString();
		} catch (IOException e) {
			throw new RapidEnvException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RapidEnvException(e);
				}
			}
		}
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigExprFunctionFilecontents.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
