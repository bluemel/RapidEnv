/*
 * RapidEnv: Argument.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/30/2010
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

import java.util.logging.Level;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * manual code part of Rapid Bean class: Argument.
 */
public class Argument extends RapidBeanBaseArgument {

	/**
	 * @return value of Property 'quoted'
	 */
	@Override
	public boolean getQuoted() {
		if (super.getProperty("quoted").getValue() == null) {
			if (getValue().contains(" ") || getValue().contains("\t")) {
				super.setQuoted(true);
			} else {
				super.setQuoted(false);
			}
		}
		return super.getQuoted();
	}

	@Override
	public String getValue() {
		if (super.getValue() == null) {
			return null;
		}
		final RapidEnvInterpreter env = RapidEnvInterpreter.getInstance();
		final Installunit parentInstallUnit = getParentInstallUnit();
		if (env != null) {
			final String interpreted = env.interpret(parentInstallUnit, null, super.getValue());
			RapidEnvInterpreter.log(Level.FINER, "Intepreted argument value \"" + super.getValue() + "\" to \""
			        + interpreted + "\"");
			return interpreted;
		} else {
			return super.getValue();
		}
	}

	private Installunit getParentInstallUnit() {
		RapidBean parentBean = this.getParentBean();
		while (parentBean != null) {
			if (parentBean instanceof Installunit) {
				return (Installunit) parentBean;
			}
			parentBean = parentBean.getParentBean();
		}
		return null;
	}

	/**
	 * default constructor.
	 */
	public Argument() {
		super();
	}

	/**
	 * constructor out of a value string.
	 * 
	 * @param value
	 *            the value string
	 */
	public Argument(final String value) {
		super();
		this.setValue(value);
	}

	/**
	 * constructor out of a value string with "quoted" flag.
	 * 
	 * @param value
	 *            the value string
	 * @param quoted
	 *            if the argument has quotes
	 */
	public Argument(final String value, final boolean quoted) {
		super();
		this.setValue(value);
		this.setQuoted(quoted);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Argument(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Argument.class);

	/**
	 * @return the Biz Bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}
}
