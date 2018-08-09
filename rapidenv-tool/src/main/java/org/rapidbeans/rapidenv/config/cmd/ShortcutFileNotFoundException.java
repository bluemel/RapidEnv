/*
 * RapidEnv: ShortcutFileNotFoundException.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 08/15/2011
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
package org.rapidbeans.rapidenv.config.cmd;

import org.rapidbeans.rapidenv.RapidEnvException;

/**
 * Indicates that a shortcut that was intended to be read not exists.
 * 
 * @author Martin Bluemel
 */
public class ShortcutFileNotFoundException extends RapidEnvException {

	/**
	 * The unavoidable serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            the exception message.
	 */
	public ShortcutFileNotFoundException(String message) {
		super(message);
	}
}
