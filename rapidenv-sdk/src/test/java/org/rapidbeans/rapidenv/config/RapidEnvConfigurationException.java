/*
 * RapidEnv: RapidEnvConfigurationException.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/10/2010
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

public class RapidEnvConfigurationException extends RuntimeException {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor with message.
	 * 
	 * @param message
	 *            the message
	 */
	public RapidEnvConfigurationException(final String message) {
		super(message);
	}

	/**
	 * The constructor with message and cause.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public RapidEnvConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
