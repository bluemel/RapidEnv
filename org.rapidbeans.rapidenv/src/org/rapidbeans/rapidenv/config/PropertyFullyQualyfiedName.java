/*
 * RapidEnv: PropertyFullyQualyfiedName.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 02/20/2010
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

/**
 * Implements the dependent string property "fullyQualifiedName" of class
 * "Installunit".
 * 
 * @author Martin Bluemel
 */
public class PropertyFullyQualyfiedName extends PropertyString {

	public PropertyFullyQualyfiedName(TypeProperty type, RapidBean parentBean) {
		super(type, parentBean);
	}

	@Override
	public String getValue() {
		return ((Property) getBean()).getFullyQualifiedName();
	}
}
