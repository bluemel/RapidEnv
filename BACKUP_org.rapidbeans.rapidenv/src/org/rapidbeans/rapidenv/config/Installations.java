/*
 * RapidEnv: Installations.java
 *
 * Copyright (C) 2005 - 2013 Martin Bluemel
 *
 * Creation Date: 05/02/2013
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

import java.util.logging.Logger;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.Version;

/**
 * The root of all evil.
 */
public class Installations extends RapidBeanBaseInstallations {

	private Logger logger = Logger.getLogger(Installations.class.getName());

	/**
	 * default constructor.
	 */
	public Installations() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public Installations(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Installations(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Installations.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}

	public void addInstallunit(String fullyQualifiedName, InstallState installstate, Version version) {
		InstallunitData inst = findInstallunit(fullyQualifiedName);
		if (inst == null) {
			inst = new InstallunitData(fullyQualifiedName);
			addInstallunit(inst);
		}
		inst.setInstallstate(installstate);
		inst.setVersion(version);
	}

	public InstallunitData findInstallunit(final String fullyQualifiedName) {
		if (getInstallunits() != null)
		{
			for (final InstallunitData data : getInstallunits()) {
				if (data.getFullname().equals(fullyQualifiedName)) {
					return data;
				}
			}
		}
		return null;
	}

	public void removeInstallunit(String fullyQualifiedName) {
		InstallunitData inst = findInstallunit(fullyQualifiedName);
		if (inst != null) {
			removeInstallunit(inst);
		} else {
			logger.warning("Installation unit entry \"" + fullyQualifiedName
			        + "\" has already been removed.");
		}
	}
}
