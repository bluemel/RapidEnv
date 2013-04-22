/*
 * RapidEnv: PropertyInterpretedString.java
 *
 * Copyright (C) 2012 Martin Bluemel
 *
 * Creation Date: 02/20/2012
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

import org.rapidbeans.core.basic.PropertyString;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.expr.ConfigExprTopLevel;

/**
 * Introduces the concept of a string that contains expressions written in the
 * RapidEnv expression language which will be interpreted at runtime.
 * 
 * @author Martin Bluemel
 */
public class PropertyInterpretedString extends PropertyString {

	public static final boolean CACHE_INTERPRETED_VALUES = false;

	private static boolean interpretationLock = false;

	public static void lockIntepretation() {
		interpretationLock = true;
	}

	public static void unlockIntepretation() {
		interpretationLock = false;
	}

	// private String interpretedValueCache = null;

	@Override
	public String getValue() {
		// if (CACHE_INTERPRETED_VALUES && this.interpretedValueCache != null) {
		// return this.interpretedValueCache;
		// }
		String stringValue = super.getValue();
		if (stringValue != null && (!interpretationLock)) {
			if (stringValue.length() > 0) {
				stringValue = interpret(stringValue);
			}
			// if (CACHE_INTERPRETED_VALUES) {
			// this.interpretedValueCache = stringValue;
			// }
		}
		return stringValue;
	}

	@Override
	public void setValue(final Object newValue) {
		if (newValue == null || (!newValue.equals(super.getValue()))) {
			// this.interpretedValueCache = null;
			super.setValue(newValue);
		}
	}

	public PropertyInterpretedString(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	private String interpret(final String s) {
		try {
			final RapidEnvInterpreter env = RapidEnvInterpreter.getInstance();
			if (env == null) {
				return new ConfigExprTopLevel(getEnclosingUnit(), getEnclosingProperty(), s, false).interpret();
			} else {
				return new ConfigExprTopLevel(getEnclosingUnit(), getEnclosingProperty(), s, env.getProject()
						.getExpressionLiteralEscaping()).interpret();
			}
		} catch (RapidEnvException e) {
			if (e.getCause() != null && e.getCause().getCause() != null
					&& e.getCause().getCause() instanceof ClassNotFoundException) {
				String funcname = e.getCause().getCause().getMessage();
				final String msg = e.getCause().getMessage();
				final int pos = msg.indexOf("ConfigExprFunction");
				if (pos != -1) {
					funcname = msg.substring(pos + "ConfigExprFunction".length());
				}
				throw new RapidEnvConfigurationException("Problem while interpreting value\n" + "  \"" + s + "\"\n"
						+ "  for attribute \"" + this.getBean().getType().getNameShort() + "." + this.getName()
						+ "\":\n" + "  No interpreter class found for function \"" + funcname + "()\".", e);
			}
			throw new RapidEnvConfigurationException("Problem while interpreting value\n" + "  \"" + s
					+ "\"\n  for attribute \"" + this.getBean().getType().getNameShort() + "." + this.getName() + "\"",
					e);
		}
	}

	private EnvProperty getEnclosingProperty() {
		RapidBean bean = getBean();
		while (bean != null) {
			if (bean instanceof EnvProperty) {
				return (EnvProperty) bean;
			} else {
				bean = bean.getParentBean();
			}
		}
		return null;
	}

	private Installunit getEnclosingUnit() {
		RapidBean bean = getBean();
		while (bean != null) {
			if (bean instanceof Installunit) {
				return (Installunit) bean;
			} else {
				bean = bean.getParentBean();
			}
		}
		return null;
	}
}
