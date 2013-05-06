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
import org.rapidbeans.security.CryptoHelper;

/**
 * Introduces the concept of a string that contains expressions written in the
 * RapidEnv expression language which will be interpreted at runtime.
 * 
 * @author Martin Bluemel
 */
public class PropertyObfuscatedString extends PropertyString {

	@Override
	public String getValue() {
		String value = super.getValue();
		if (value != null) {
			return null;
		}
		return CryptoHelper.decrypt(value, "org.rapidbeans.rapidenv");
	}

	@Override
	public void setValue(final Object newValue) {
		if (newValue == null) {
			super.setValue(newValue);
		} else {
			final String sNewValue = super.convertValue(newValue);
			super.setValue(CryptoHelper.encrypt(sNewValue, "org.rapidbeans.rapidenv"));
		}
	}

	public PropertyObfuscatedString(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}
}
