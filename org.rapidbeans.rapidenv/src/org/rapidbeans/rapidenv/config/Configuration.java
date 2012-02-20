/*
 * RapidEnv: Configuration.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/08/2010
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

import java.io.File;

import org.rapidbeans.core.type.TypePropertyString;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.EscapeMap;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;

/**
 * The root of all evil.
 */
public abstract class Configuration extends RapidBeanBaseConfiguration {

	/**
	 * Check if the configuration has been performed properly or not
	 * 
	 * @param execute
	 *            if false only execute the check if the configuration is
	 *            neccessary if true execute the configuration if necessary
	 * 
	 * @return if the configuration has been performed properly or not
	 */
	public abstract boolean check(final boolean execute);

	public Installunit getParentInstallunit() {
		Installunit installunit = null;
		if (this.getParentBean() instanceof Installunit) {
			installunit = (Installunit) this.getParentBean();
		}
		return installunit;
	}

	public Property getParentPropertyRenv() {
		Property property = null;
		if (this.getParentBean() instanceof Property) {
			property = (Property) this.getParentBean();
		}
		return property;
	}

	/**
	 * Hack to overcome current RapidBeans framework deficiency. Remove when it
	 * is fixed in RapidBeans.
	 */
	@Override
	public void setIssue(final String msg) {
		final TypePropertyString type = (TypePropertyString) this.getProperty("issue").getType();
		final EscapeMap escMap = type.getEscapeMap();
		final String msgEsc = StringHelper.escape(msg, escMap);
		super.setIssue(msgEsc);
	}

	/**
	 * default constructor.
	 */
	public Configuration() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public Configuration(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Configuration(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Configuration.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * Print for output reasons.
	 */
	public abstract String print();

	/**
	 * Check if the configuration is valid for the OS (family) currently used.
	 * 
	 * @return if the configuration is valid for the OS (family) currently used.
	 */
	public boolean checkOsfamily() {
		if (getOsfamily() == null) {
			return true;
		}
		return getOsfamily() == PlatformHelper.getOs();
	}

	public void cleanupFilesOnConfig() {
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		if (getCleanupfilesonconfig() != null) {
			for (String filename : StringHelper.split(getCleanupfilesonconfig(), ",")) {
				if (interpreter != null) {
					filename = interpreter.interpret(getParentInstallunit(), null, filename);
				}
				final File file = new File(filename);
				if (file.exists()) {
					if (file.delete()) {
						if (interpreter != null) {
							interpreter.getOut().println("    Cleaned up file \"" + file.getAbsolutePath() + "\"");
						}
					} else {
						throw new RapidEnvException("Could not delete file \"" + file.getAbsolutePath() + "\".");
					}
				} else {
					if (interpreter != null) {
						interpreter.getOut().println(
						        "    Nothing to do for cleaup of file \"" + file.getAbsolutePath() + "\"");
					}
				}
			}
		}
	}
}
