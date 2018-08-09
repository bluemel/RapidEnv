/*
 * RapidEnv: ConfigurationTask.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/10/2010
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

package org.rapidbeans.rapidenv.config;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.PlatformHelper;

public abstract class ConfigurationTask extends RapidBeanBaseConfigurationTask {

	/**
	 * Check if the configuration task has been performed properly or not
	 * 
	 * @param execute
	 *            if false only execute the check if the configuration task is
	 *            necessary. if true execute the configuration if necessary.
	 * 
	 * @return if the configuration has been performed properly or not
	 */
	public abstract boolean check(final boolean execute, final boolean silent);

	/**
	 * Check if the configuration is valid for the OS (family) currently used.
	 * 
	 * @return if the configuration is valid for the OS (family) currently used.
	 */
	public boolean checkOsfamily() {
		if (getOsfamily() == null) {
			return true;
		}
		return getOsfamily() == PlatformHelper.getOsfamily();
	}

	/**
	 * default constructor.
	 */
	public ConfigurationTask() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigurationTask(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigurationTask(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigurationTask.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
