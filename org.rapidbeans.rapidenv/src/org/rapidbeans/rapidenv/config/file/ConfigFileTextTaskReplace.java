/*
 * RapidEnv: ConfigFileTextTaskReplace.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/28/2010
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

package org.rapidbeans.rapidenv.config.file;

import java.util.logging.Level;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Replaces the given regular expression by the given text.
 */
public class ConfigFileTextTaskReplace extends RapidBeanBaseConfigFileTextTaskReplace {

	/**
	 * Check if the line(s) still exist
	 * 
	 * @param execute
	 *            if false only execute the check if the configuration task is
	 *            necessary if true execute the configuration task if necessary
	 * @param silent
	 *            if to execute silent
	 * 
	 * @return if the configuration task as been performed properly or not
	 */
	@Override
	public boolean check(final boolean execute, final boolean silent) {
		boolean ok = true;
		if (execute) {
			ok = false;
		}
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		final ConfigFileText fileCfg = (ConfigFileText) getParentBean();
		final ConfigFileEditorText editor = (ConfigFileEditorText) fileCfg.getEditor();

		final int[] matches = editor.search(getRegexp());
		if (matches.length > 0) {
			if (execute) {
				final String replaceby = interpreter.interpret((Installunit) getParentBean().getParentBean(), null,
				        getReplaceby());
				editor.replace(getRegexp(), replaceby);
				String msg1;
				if (matches.length == 1) {
					msg1 = "Replaced 1 ocurrence of \"" + getRegexp() + "\"\n    by \"" + replaceby
					        + "\"\n    in file " + fileCfg.getPathAsFile().getAbsolutePath();
				} else {
					msg1 = "Replaced " + Integer.toString(matches.length) + " ocurrences of \"" + getRegexp()
					        + "\"\n    by \"" + replaceby + "\"\n    in file "
					        + fileCfg.getPathAsFile().getAbsolutePath();
				}
				if (!silent) {
					interpreter.getOut().println(msg1);
				}
				RapidEnvInterpreter.log(Level.FINE, msg1);
				ok = true;
			} else {
				String msg2;
				if (matches.length == 1) {
					msg2 = "1 ocurrence of \"" + getRegexp() + "\"\n    should be replaced by \"" + getReplaceby()
					        + "\"\n    in file " + fileCfg.getPathAsFile().getAbsolutePath();
				} else {
					msg2 = Integer.toString(matches.length) + " ocurrences of \"" + getRegexp()
					        + "\"\n    should be replaced by \"" + getReplaceby() + "\"\n    in file "
					        + fileCfg.getPathAsFile().getAbsolutePath();
				}
				RapidEnvInterpreter.log(Level.FINE, msg2);
				fileCfg.setIssue(msg2);
				ok = false;
			}
		} else { // no matches
			final String msg3 = "no matches to replace found" + " with regular expression: \"" + getRegexp() + "\".";
			if (execute) {
				RapidEnvInterpreter.log(Level.FINE, msg3);
			}
		}
		return ok;
	}

	// /**
	// * Interpret a configuration expression.
	// *
	// * @param expression the configuration expression to interpret
	// * @param ant the ant gateway used to support expression interpretation
	// *
	// * @return the interpreted (expanded) configuration expression
	// */
	// private String interpret(final String expression, final AntGateway ant) {
	// return new ConfigExprTopLevel((Installunit)
	// this.getParentBean().getParentBean(), ant, expression).interpret();
	// }

	/**
	 * default constructor.
	 */
	public ConfigFileTextTaskReplace() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigFileTextTaskReplace(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigFileTextTaskReplace(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigFileTextTaskReplace.class);

	/**
	 * @return the RapidBean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
