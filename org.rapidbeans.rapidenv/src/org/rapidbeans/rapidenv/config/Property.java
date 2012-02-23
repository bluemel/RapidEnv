/*
 * RapidEnv: Property.java
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.rapidbeans.core.basic.Id;
import org.rapidbeans.core.basic.IdKeyprops;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.OperatingSystem;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.CmdRenvCommand;
import org.rapidbeans.rapidenv.InstallStatus;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdLineInteractions;

/**
 * A RapidEnv environment property definition.
 */
public class Property extends RapidBeanBaseProperty {

	private PropertyValue specificValueCommon = null;

	private Map<OperatingSystem, PropertyValue> specificvaluesOsSpecific = null;

	private Environment environmentCommon = null;

	private Map<OperatingSystem, Environment> environmentsOsSpecific = null;

	@Override
	public synchronized Id getId() {
		if (super.getId() == null || (!super.getId().toString().equals(getFullyQualifiedName()))) {
			super.setId(new IdKeyprops(this, getFullyQualifiedName()));
		}
		return super.getId();
	}

	@Override
	public final String getIdString() {
		return getFullyQualifiedName();
	}

	@Override
	public String getFullyQualifiedName() {
		return getFullyQualifiedName(true);
	}

	public String getFullyQualifiedName(final boolean withParentUnits) {
		final StringBuffer fqName = new StringBuffer();
		if (getParentInstallunit() != null) {
			fqName.append(getParentInstallunit().getFullyQualifiedName(false));
			fqName.append('.');
		}
		fqName.append(getName());
		return fqName.toString();
	}

	@Override
	public String getValue() {

		try {
			// take value or specific value
			String currentValue = "";

			if (!getInterpret()) {
				PropertyInterpretedString.lockIntepretation();
			}
			final PropertyValue specificValue = getSpecificvalue(PlatformHelper.getOs());
			if (specificValue != null && specificValue.getValue() != null) {
				currentValue = specificValue.getValue();
			} else {
				if (super.getValue() != null && super.getValue().length() > 0) {
					currentValue = super.getValue();
				}
			}

			final StringBuffer sb = new StringBuffer();

			if (getValuetype() == PropertyValueType.path) {
				final List<String> pathComponents = StringHelper.split(currentValue, File.pathSeparator);
				if (getExtensions() != null) {
					// remove path extensions to be removed
					final ReadonlyListCollection<PropertyExtension> extensions = getExtensions();
					final int size = extensions.size();
					for (int i = size - 1; i >= 0; i--) {
						final PropertyExtension ext = extensions.get(i);
						if (ext.getPropextmode() == PropertyExtensionMode.remove) {
							removePathExtension(pathComponents, ext.getPropextmode(), normalize(ext.getValue()));
						} else {
							if (ext.getPropextmode() == PropertyExtensionMode.prepend) {
								addPathExtension(pathComponents, ext.getPropextmode(), normalize(ext.getValue()));
							}
						}
					}
					for (int i = 0; i < size; i++) {
						final PropertyExtension ext = extensions.get(i);
						if (ext.getPropextmode() == PropertyExtensionMode.append) {
							addPathExtension(pathComponents, ext.getPropextmode(), normalize(ext.getValue()));
						}
					}
				}
				if (getInstallunitextensions() != null) {
					final RapidEnvInterpreter env = RapidEnvInterpreter.getInstance();
					final ReadonlyListCollection<PropertyExtensionFromInstallUnit> extfus = getInstallunitextensions();
					final int size = extfus.size();
					for (int i = size - 1; i >= 0; i--) {
						final PropertyExtensionFromInstallUnit extfu = extfus.get(i);
						// interpret path extension in the context of its parent
						// install unit
						final Installunit parentInstallUnit = (Installunit) extfu.getParentBean();
						final String value = normalize(extfu.getValue());
						final InstallStatus parentInstallUnitInstallStatus = parentInstallUnit
								.getInstallationStatus(CmdRenvCommand.stat);
						switch (parentInstallUnitInstallStatus) {
						case deinstallrequired:
							removePathExtension(pathComponents, extfu.getPropextmode(), value);
							break;
						default:
							if (env != null
									&& (env.getCommand() == CmdRenvCommand.install || parentInstallUnit
											.getInstallcontrol() == InstallControl.normal)) {
								// add path extension
								if (extfu.getPropextmode() == PropertyExtensionMode.prepend) {
									addPathExtension(pathComponents, extfu.getPropextmode(), value);
								}
							}
							break;
						}
					}
					for (int i = 0; i < size; i++) {
						final PropertyExtensionFromInstallUnit extfu = extfus.get(i);
						// interpret path extension in the context of its parent
						// install unit
						final Installunit parentInstallUnit = (Installunit) extfu.getParentBean();
						final String value = normalize(extfu.getValue());
						final InstallStatus parentInstallUnitInstallStatus = parentInstallUnit
								.getInstallationStatus(CmdRenvCommand.install);
						switch (parentInstallUnitInstallStatus) {
						case deinstallrequired:
							break;
						default:
							if (env != null
									&& (env.getCommand() == CmdRenvCommand.install || parentInstallUnit
											.getInstallcontrol() == InstallControl.normal)) {
								// add path extension
								if (extfu.getPropextmode() == PropertyExtensionMode.append) {
									addPathExtension(pathComponents, extfu.getPropextmode(), value);
								}
							}
							break;
						}
					}
				}
				int i = 0;
				for (final String pc : pathComponents) {
					if (i > 0) {
						sb.append(File.pathSeparator);
					}
					sb.append(pc);
					i++;
				}
			} else {
				sb.append(currentValue);
			}

			// add extensions
			if (getValuetype() == PropertyValueType.path) {
			}

			// normalize
			final String normalized = normalize(sb.toString());

			return normalized;
		} finally {
			if (!getInterpret()) {
				PropertyInterpretedString.unlockIntepretation();
			}
		}
	}

	public Installunit getParentInstallunit() {
		Installunit installunit = null;
		if (this.getParentBean() instanceof Installunit) {
			installunit = (Installunit) this.getParentBean();
		}
		return installunit;
	}

	private void removePathExtension(final List<String> pathComponents, final PropertyExtensionMode extMode,
			final String extvalue) {
		final int index = pathComponents.indexOf(extvalue);
		if (index != -1) {
			pathComponents.remove(index);
		}
	}

	private void addPathExtension(final List<String> pathComponents, final PropertyExtensionMode extMode,
			final String extvalue) throws AssertionError {
		switch (extMode) {
		case append:
			pathComponents.add(extvalue);
			break;
		case prepend:
			// important in case the extension is a function to interpret
			pathComponents.add(0, extvalue);
			break;
		case remove:
			// do nothing
			break;
		default:
			throw new AssertionError("Unexpected property extension mode \"" + extMode.name() + "\"");
		}
	}

	public String normalize(final String s) {
		if (!getNormalize()) {
			return s;
		}
		final StringBuffer sb = new StringBuffer();
		try {
			switch (getValuetype()) {
			case file:
				final File file = new File(s);
				if (file.exists()) {
					sb.append(file.getCanonicalPath());
				} else {
					sb.append(s.replace('/', File.separatorChar).replace('\\', File.separatorChar));
				}
				break;
			case path:
				final List<String> list = new ArrayList<String>();
				final Map<String, File> map = new HashMap<String, File>();
				int i = 0;
				for (final String sPath : StringHelper.split(s, File.pathSeparator)) {
					final File pathfile = new File(sPath);
					if (pathfile.exists()) {
						if (sPath.equals(".")) {
							if (map.get(sPath) == null) {
								list.add(sPath);
								map.put(sPath, pathfile);
							}
						} else if (sPath.equals("." + File.separator)) {
							if (map.get(sPath) == null) {
								list.add(sPath);
								map.put(sPath, pathfile);
							}
						} else {
							final String scPath = pathfile.getCanonicalPath();
							if (map.get(scPath) == null) {
								list.add(scPath);
								map.put(scPath, pathfile);
							}
						}
					} else {
						final String sPathnorm = sPath.replace('/', File.separatorChar).replace('\\',
								File.separatorChar);
						if (map.get(sPathnorm) == null) {
							list.add(sPathnorm);
							map.put(sPathnorm, pathfile);
						}
					}
				}
				for (final String scPath : list) {
					if (i > 0) {
						sb.append(File.pathSeparator);
					}
					sb.append(scPath);
					i++;
				}
				break;
			default:
				sb.append(s);
				break;
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
	 * @param os
	 *            defines a specific operation system (family).
	 * 
	 * @return the specific value for the given operation system
	 */
	public synchronized PropertyValue getSpecificvalue(final OperatingSystem os) {
		if (this.specificValueCommon == null) {
			initSpecificvalue();
		}
		if (this.specificvaluesOsSpecific.get(os) != null) {
			return this.specificvaluesOsSpecific.get(os);
		}
		return this.specificValueCommon;
	}

	/**
	 * @param os
	 *            defines a specific operation system (family).
	 * 
	 * @return the environment variable definitions associated to this property
	 */
	public synchronized Environment getEnvironment(final OperatingSystem os) {
		if (this.environmentsOsSpecific == null) {
			initEnvironment();
		}
		if (this.environmentsOsSpecific.get(os) != null) {
			return this.environmentsOsSpecific.get(os);
		}
		return this.environmentCommon;
	}

	/**
	 * (Lazy) initialization of the environment map and common.
	 */
	private void initEnvironment() {
		this.environmentsOsSpecific = new HashMap<OperatingSystem, Environment>();
		if (this.getEnvironments() == null) {
			this.setEnvironments(new ArrayList<Environment>());
		}
		for (final Environment env : this.getEnvironments()) {
			if (env.getOsfamily() == null) {
				if (this.environmentCommon != null) {
					throw new RapidEnvConfigurationException(
							"More than one general (OS independent) environment definition" + " for property \""
									+ getFullyQualifiedName() + "\"");
				}
				this.environmentCommon = env;
			} else {
				if (this.environmentsOsSpecific.get(env.getOsfamily()) != null) {
					throw new RapidEnvConfigurationException("More than one OS specific environment definition"
							+ " for property \"" + getFullyQualifiedName() + "\"" + " for OS (familiy) \""
							+ env.getOsfamily() + "\"");
				}
				this.environmentsOsSpecific.put(env.getOsfamily(), env);
			}
		}
	}

	/**
	 * (Lazy) initialization of the specific value map and common.
	 */
	private void initSpecificvalue() {
		this.specificvaluesOsSpecific = new HashMap<OperatingSystem, PropertyValue>();
		if (getSpecificvalues() != null) {
			for (final PropertyValue value : this.getSpecificvalues()) {
				if (value.getOsfamily() == null) {
					if (this.specificValueCommon != null) {
						throw new RapidEnvConfigurationException(
								"More than one general (OS independent) specific value definition" + " for property \""
										+ getFullyQualifiedName() + "\"");
					}
					this.specificValueCommon = value;
				} else {
					if (this.specificvaluesOsSpecific.get(value.getOsfamily()) != null) {
						throw new RapidEnvConfigurationException("More than one OS specific value definition"
								+ " for property \"" + getFullyQualifiedName() + "\"" + " for OS (familiy) \""
								+ value.getOsfamily() + "\"");
					}
					this.specificvaluesOsSpecific.put(value.getOsfamily(), value);
				}
			}
		}
	}

	/**
	 * default constructor.
	 */
	public Property() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public Property(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Property(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Property.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * Display the given property's status.
	 */
	public void stat() {
		final String propName = getFullyQualifiedName();
		String propValue = getValue();
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		final String propValuePersisted = interpreter.getPropertyValuePersisted(propName);
		switch (getCategory()) {
		case common:
			if (propValuePersisted == null) {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println(
							"  ! " + getPrintName(interpreter) + ": " + "new common property with value \"" + propValue
									+ "\"" + " should be introduced.");
				}
			} else if (!propValuePersisted.equals(propValue)) {
				if (interpreter.getOut() != null) {
					if (propValue.length() > 20) {
						interpreter.getOut().println(
								"  ! " + getPrintName(interpreter) + ": "
										+ "value of common property should be changed\n" + "    from \""
										+ propValuePersisted + "\"\n      to \"" + propValue + "\"");
					} else {
						interpreter.getOut().println(
								"  ! " + getPrintName(interpreter) + ": "
										+ "value of common property should be changed\n" + "    from \""
										+ propValuePersisted + "\"\n      to \"" + propValue + "\"");
					}
				}
			} else {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println("  = " + getPrintName(interpreter) + " = " + "\"" + propValue + "\"");
				}
			}
			break;
		case personal:
			if (propValuePersisted == null) {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println(
							"  ! " + getPrintName(interpreter) + ": " + "new personal property needs to be specified");
				}
				propValue = null;
			} else {
				propValue = propValuePersisted;
				if (interpreter.getOut() != null) {
					interpreter.getOut().println("  p " + getPrintName(interpreter) + " = " + "\"" + propValue + "\"");
				}
			}
			break;
		}
	}

	private String getPrintName(final RapidEnvInterpreter interpreter) {
		final StringBuffer buf = new StringBuffer(getFullyQualifiedName());
		if (interpreter.getLogLevel().intValue() > Level.INFO.intValue()) {
			buf.append(" (");
			buf.append(getCategory().name());
			buf.append("): ");
		}
		return buf.toString();
	}

	/**
	 * Update / Configure the given property's value.
	 * 
	 * @return the new (updated) property value
	 */
	public String update() {
		final String propName = getFullyQualifiedName();
		String propValue = getValue();
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		final String propValuePersisted = interpreter.getPropertyValuePersisted(propName);
		if (this.getCategory() == PropertyCategory.personal && propValuePersisted != null) {
			propValue = propValuePersisted;
		}
		switch (getCategory()) {
		case common:
			// propValue stays the configured one
			if (propValuePersisted == null) {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println(
							"  - introduced property \"" + propName + "\" with value \"" + propValue + "\"");
				}
			} else if (!propValuePersisted.equals(propValue)) {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println(
							"  - changed value of property \"" + propName + "\"\n" + "    from \"" + propValuePersisted
									+ "\"\n" + "      to \"" + propValue + "\"");
				}
			} else {
				if (interpreter.getOut() != null) {
					interpreter.getOut().println("  = " + getPrintName(interpreter) + " = " + "\"" + propValue + "\"");
				}
			}
			break;
		case personal:
			if (propValuePersisted == null
					|| (interpreter.getPropertiesToProcess().size() < interpreter.getProject().getPropertys().size() && interpreter
							.getPropertiesToProcess().contains(this))) {
				switch (interpreter.getCommand()) {
				case update:
				case config:
					if (interpreter.getInstallUnitOrPropertyNamesExplicitelySpecified()) {
						propValue = CmdLineInteractions.enterValue(interpreter.getIn(), interpreter.getOut(), this,
								propValue);
					} else {
						if (interpreter.getOut() != null) {
							interpreter.getOut().println(
									"  = " + getPrintName(interpreter) + " = " + "\"" + propValue + "\"");
						}
					}
					break;
				default:
					propValue = CmdLineInteractions.enterValue(interpreter.getIn(), interpreter.getOut(), this,
							propValue);
					break;
				}
			} else {
				propValue = propValuePersisted;
				if (interpreter.getOut() != null) {
					interpreter.getOut().println("  p " + getPrintName(interpreter) + " = " + "\"" + propValue + "\"");
				}
			}
			break;
		}
		return propValue;
	}
}
