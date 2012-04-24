/*
 * RapidEnv: ConfigFilePropertiesTaskDeleteprop.java
 *
 * Copyright (C) 2012 Martin Bluemel
 *
 * Creation Date: 04/24/2012
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

/**
 * The root of all evil.
 */
public class ConfigFilePropertiesTaskDeleteprop extends
		RapidBeanBaseConfigFilePropertiesTaskDeleteprop {

	/**
	 * Check if the node value has been set properly or not
	 * 
	 * @param execute
	 *            if false only execute the check if the configuration task is
	 *            necessary if true execute the configuration task if necessary
	 * @param silent
	 *            if to execute silently
	 * 
	 * @return execute = false: if the configuration task shall be executed or
	 *         not execute == true: if the configuration task as been performed
	 *         properly or not
	 */
	@Override
	public boolean check(final boolean execute, final boolean silent) {
		boolean ok = true;
		if (execute) {
			ok = false;
		}
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter
				.getInstance();
		final ConfigFileProperties fileCfg = (ConfigFileProperties) getParentBean();
		final ConfigFileEditorProperties editor = (ConfigFileEditorProperties) fileCfg
				.getEditor();
		if (execute) {
			if (editor.getProperty(getName()) != null) {
				editor.deleteProperty(getSection(), getName());
				String msg = "    deleted property ";
				if (getSection() != null) {
					msg += "[" + getSection() + "] ";
				}
				msg += "\"" + getName() + "\"" + " in file "
						+ fileCfg.getPathAsFile().getAbsolutePath();
				if (!silent) {
					interpreter.getOut().println(msg);
				}
				RapidEnvInterpreter.log(Level.FINE, msg);
				ok = true;
			} else {
				String msg = "Property ";
				if (getSection() != null) {
					msg += "[" + getSection() + "] ";
				}
				msg += "\"" + getName() + "\"" + " already deleted in file "
						+ fileCfg.getPathAsFile().getAbsolutePath();
				RapidEnvInterpreter.log(Level.FINE, msg);
				fileCfg.setIssue(msg);
				ok = true;
			}
		} else {
			String msg = "Property ";
			if (getSection() != null) {
				msg += "[" + getSection() + "] ";
			}
			if (editor.getProperty(getName()) != null) {
				msg += "\"" + getName() + "\"" + " should be deleted in file "
						+ fileCfg.getPathAsFile().getAbsolutePath();
				RapidEnvInterpreter.log(Level.FINE, msg);
				fileCfg.setIssue(msg);
				ok = false;
			} else {
				msg += "\"" + getName() + "\"" + " already deleted in file "
						+ fileCfg.getPathAsFile().getAbsolutePath();
				RapidEnvInterpreter.log(Level.FINE, msg);
				fileCfg.setIssue(msg);
				ok = true;
			}
		}
		return ok;
	}

	/**
	 * default constructor.
	 */
	public ConfigFilePropertiesTaskDeleteprop() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigFilePropertiesTaskDeleteprop(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigFilePropertiesTaskDeleteprop(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean
			.createInstance(ConfigFilePropertiesTaskDeleteprop.class);

	/**
	 * @return the RapidBean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
