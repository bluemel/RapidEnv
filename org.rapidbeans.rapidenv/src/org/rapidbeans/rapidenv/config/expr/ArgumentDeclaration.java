/*
 * RapidEnv: ArgumentDeclaration.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 07/02/2010
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
package org.rapidbeans.rapidenv.config.expr;

/**
 * @author Martin Bluemel
 */
public class ArgumentDeclaration {

	public static final int UNLIMITED = -1;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	private String name = null;

	private boolean optional = false;

	private int minLength = 1;

	/**
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	private int maxLength = UNLIMITED;

	public ArgumentDeclaration(final String name) {
		this.name = name;
	}

	public ArgumentDeclaration(final String name, final boolean optional) {
		this.name = name;
		this.optional = optional;
	}

	public ArgumentDeclaration(final String name, final boolean optional,
			final int minLength, final int maxLength) {
		this.name = name;
		this.optional = optional;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
}
