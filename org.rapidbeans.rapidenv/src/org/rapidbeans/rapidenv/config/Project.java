/*
 * RapidEnv: Project.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 07/27/2010
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.expr.ConfigExprTopLevel;

/**
 * The root of all evil.
 */
public class Project extends RapidBeanBaseProject {

	private Map<String, Property> propertyMap = null;

	private Map<String, Installunit> installunitMap = null;

	/**
	 * @return value of Property 'propertys'
	 */
	@Override
	public ReadonlyListCollection<Property> getPropertys() {
		final List<Property> props = new ArrayList<Property>();
		if (super.getPropertys() != null) {
			for (final Property prop : super.getPropertys()) {
				props.add(prop);
			}
		}
		if (getInstallunits() != null) {
			for (final Installunit installunit : this.getInstallunits()) {
				addLocalProps(props, installunit);
			}
		}
		return new ReadonlyListCollection<Property>(props, this.getType().getPropertyType("properties"));
	}

	private void addLocalProps(final List<Property> props, final Installunit installunit) {
		if (installunit == null || installunit.getPropertys() == null) {
			return;
		}
		for (final Property prop : installunit.getPropertys()) {
			props.add(prop);
		}
		if (installunit.getSubunits() != null) {
			for (final Installunit subunit : installunit.getSubunits()) {
				addLocalProps(props, subunit);
			}
		}
	}

	/**
	 * As long as we do not have map properties find a property (configuration)
	 * this way.
	 * 
	 * @param propertyName
	 *            the name of the property to find
	 * 
	 * @return the property configuration with the specified name
	 */
	public Property findPropertyConfiguration(final String propertyName) {
		if (this.propertyMap == null || getPropertys() == null || this.propertyMap.size() != getPropertys().size()) {
			updatePropertyMap();
		}
		Property found = this.propertyMap.get(propertyName);
		if (found != null) {
			return found;
		}
		for (final Property current : this.propertyMap.values()) {
			if (found == null) {
				if (current.getFullyQualifiedName().equals(propertyName)) {
					found = current;
				}
			} else {
				if (current.getFullyQualifiedName().equals(propertyName)) {
					throw new RapidEnvConfigurationException("Ambigouus property name \"" + propertyName + "\"");
				}
			}
		}
		return found;
	}

	/**
	 * Find the tool by it's fully qualified name or by its name only as far the
	 * name is unique.
	 * 
	 * @param name
	 *            the name of the install unit to find
	 * 
	 * @return the tool with the specified name
	 */
	public Installunit findInstallunitConfiguration(final String name) {

		// first try: find the unit directly from the tool map
		// by the fully qualified name.
		if (this.installunitMap == null || getInstallunits() == null
		        || this.installunitMap.size() != getInstallunits().size()) {
			updateToolMap();
		}
		Installunit found = this.installunitMap.get(name);
		if (found != null) {
			return found;
		}

		// second try: find the unit by name or fully qualified name without
		// parents
		for (final Installunit current : this.installunitMap.values()) {
			if (found == null) {
				if (current.getName().equals(name)
				        || (current.isSubunit() && current.getFullyQualifiedName(false).equals(name))) {
					found = current;
				}
			} else {
				if (current.getName().equals(name)
				        || (current.isSubunit() && current.getFullyQualifiedName(false).equals(name))) {
					throw new RapidEnvConfigurationException("Ambigouus tool name \"" + name + "\"");
				}
			}
		}
		return found;
	}

	/**
	 * Initialize the property map.
	 */
	public void updatePropertyMap() {
		this.propertyMap = new HashMap<String, Property>();
		if (getPropertys() != null) {
			for (final Property prop : getPropertys()) {
				this.propertyMap.put(prop.getFullyQualifiedName(), prop);
			}
		}
	}

	/**
	 * Initialize the tool map.
	 */
	public void updateToolMap() {
		this.installunitMap = new HashMap<String, Installunit>();
		final List<RapidBean> units = this.getContainer().findBeansByType("org.rapidbeans.rapidenv.config.Installunit");
		for (final RapidBean bean : units) {
			final Installunit unit = (Installunit) bean;
			this.installunitMap.put(unit.getFullyQualifiedName(), unit);
		}
	}

	/**
	 * @return if the project has at least one personal property.
	 */
	public boolean atLeastOnePersonalProperty() {
		boolean atLeastOneIndiviualProperty = false;
		if (getPropertys() != null) {
			for (final Property propCfg : getPropertys()) {
				if (propCfg.getCategory() == PropertyCategory.personal) {
					atLeastOneIndiviualProperty = true;
					break;
				}
			}
		}
		return atLeastOneIndiviualProperty;
	}

	/**
	 * Semantics check for the project
	 */
	public void checkSemantics() {
		if (getInstallunits() != null) {
			for (final Installunit installunit : getInstallunits()) {
				installunit.checkSemantics();
			}
		}
	}

	public URL getInstallsourceurlAsUrl() {
		String surl = this.getInstallsourceurl();
		if (surl == null) {
			return null;
		}
		if (RapidEnvInterpreter.getInstance() != null) {
			surl = RapidEnvInterpreter.getInstance().interpret(null, null, surl);
		}
		try {
			return new URL(surl);
		} catch (MalformedURLException e) {
			throw new RapidEnvConfigurationException("Misconfiguration of the project's installsourceurl\n" + surl
			        + " is no valid URL.");
		}
	}

	public File getInstalltargetdirAsFile() {
		String path = getInstalltargetdir();
		if (path == null) {
			return null;
		}
		if (RapidEnvInterpreter.getInstance() != null) {
			path = RapidEnvInterpreter.getInstance().interpret(null, null, path);
		}
		return new File(path);
	}

	/**
	 * Overwrite the getTag with expression interpretation.
	 * 
	 * @return value of Property 'tag' interpreted
	 */
	@Override
	public String getTag() {
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		if (interpreter != null) {
			return new ConfigExprTopLevel(null, null, super.getTag(), getExpressionLiteralEscaping()).interpret();
		} else {
			return super.getTag();
		}
	}

	/**
	 * default constructor.
	 */
	public Project() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public Project(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Project(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Project.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
