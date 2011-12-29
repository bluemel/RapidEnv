/*
 * RapidEnv: RapidEnvCmdException.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 05/23/2010
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

package org.rapidbeans.rapidenv;

public class RapidEnvCmdException extends RapidEnvException {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor with message.
	 *
	 * @param message the message
	 */
	public RapidEnvCmdException(final String message) {
		super(message);
	}

	/**
	 * The constructor with message.
	 *
	 * @param message the message
	 * @param errorcode the error code
	 */
	public RapidEnvCmdException(final String message, final int errorcode) {
		super(message, errorcode);
	}

	/**
	 * The constructor with message and cause.
	 *
	 * @param cause the cause
	 */
	public RapidEnvCmdException(final Throwable cause) {
		super(cause);
	}

	/**
	 * The constructor with message and cause.
	 *
	 * @param cause the cause
	 * @param errorcode the error code
	 */
	public RapidEnvCmdException(final Throwable cause, final int errorcode) {
		super(cause, errorcode);
	}

	/**
	 * The constructor with message and cause.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RapidEnvCmdException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * The constructor with message and cause.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param errorcode the error code
	 */
	public RapidEnvCmdException(final String message, final Throwable cause, final int errorcode) {
		super(message, cause, errorcode);
	}
}
