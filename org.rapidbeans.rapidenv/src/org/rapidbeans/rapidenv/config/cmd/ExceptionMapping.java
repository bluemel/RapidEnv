/*
 * RapidEnv: ExceptionMapping.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 12/26/2011
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

import java.util.Locale;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvException;

/**
 * A single ExceptionMapping.
 */
public class ExceptionMapping extends RapidBeanBaseExceptionMapping {

	/**
	 * The exception analyzed.
	 */
	private RapidEnvException mappedException = null;

	/**
	 * @return the exception
	 */
	protected RapidEnvException getMappedException() {
		return mappedException;
	}

	/**
	 * @param mappedException
	 *            the exception to set
	 */
	protected void setMappedException(RapidEnvException mappedException) {
		this.mappedException = mappedException;
	}

	/**
	 * Find the message.
	 * 
	 * @param locale
	 *            the locale
	 * 
	 * @return the message with the specified language
	 */
	public String getMessage(final Locale locale) {
		final String slang = locale.getLanguage();
		if (getErrormessages() == null) {
			return this.mappedException.getMessage();
		} else {
			for (final ErrorMessage message : getErrormessages()) {
				if (message.getLang().equals(slang)) {
					String messagetext = message.getText();
					if (messagetext.contains("${cause.cause.message}") && this.mappedException.getCause() != null
					        && this.mappedException.getCause().getCause() != null) {
						messagetext = messagetext.replace("${cause.cause.message}", this.mappedException.getCause()
						        .getCause().getMessage());
					}
					if (messagetext.contains("${cause.message}") && this.mappedException.getCause() != null) {
						messagetext = messagetext.replace("${cause.message}", this.mappedException.getCause()
						        .getMessage());
					}
					if (messagetext.contains("${message}")) {
						messagetext = messagetext.replace("${message}", this.mappedException.getMessage());
					}
					return messagetext;
				}
			}
		}
		return null;
	}

	public int getErrorcodeAsInteger() {
		int errorcode = -1;
		if (getErrorcode() != null) {
			try {
				errorcode = ExceptionMap.class.getField(getErrorcode()).getInt(ExceptionMap.class);
			} catch (IllegalArgumentException e) {
				throw new RapidEnvException(e);
			} catch (SecurityException e) {
				throw new RapidEnvException(e);
			} catch (IllegalAccessException e) {
				throw new RapidEnvException(e);
			} catch (NoSuchFieldException e) {
				throw new RapidEnvException("Unspecified error code \"" + getErrorcode() + "\"", e);
			}
		}
		return errorcode;
	}

	/**
	 * default constructor.
	 */
	public ExceptionMapping() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ExceptionMapping(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ExceptionMapping(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ExceptionMapping.class);

	/**
	 * @return the RapidBean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
