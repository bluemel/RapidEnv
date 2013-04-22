/*
 * RapidEnv: CmdLineInteractions.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/16/2010
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
package org.rapidbeans.rapidenv.cmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.RunMode;
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.Property;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * A utility class performing simple command line interactions.
 * 
 * @author Martin Bluemel
 */
public class CmdLineInteractions {

	public static String enterValue(final InputStream in, final PrintStream out, final EnvProperty prop,
	        final String defaultValue) {
		throw new AssertionError("FIXME");
	}

	/**
	 * Prompt for entering a value and read it.
	 * 
	 * @param in
	 *            the input stream to read from
	 * @param out
	 *            the output stream to write to
	 * @param prop
	 *            the property for which a value should be entered
	 * @param defaultValue
	 *            the default value take if nothing is entered
	 * @return the value entered or the default value if nothing is entered
	 */
	public static String enterValue(final InputStream in, final PrintStream out, final Property prop,
	        final String defaultValue) {
		final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
		String newValue = null;
		RapidEnvInterpreter.log(Level.FINER, "START enterValue for property: " + prop.getFullyQualifiedName());
		final StringBuffer msg = new StringBuffer(prop.getFullyQualifiedName());
		if (prop.getDescription() != null && prop.getDescription().length() > 0) {
			msg.append('\n');
			for (final String line : StringHelper.split(prop.getDescription(), "\n")) {
				msg.append("  " + line + "\n");
			}
			msg.append(' ');
		}
		switch (prop.getValuetype()) {
		case string:
			newValue = enterValue(renv, in, out, msg.toString(), defaultValue);
			break;
		case file:
			try {
				String defaultPath = null;
				if (defaultValue != null) {
					// final String interpretedDefaultValue =
					// renv.interpret(null, prop, defaultValue);
					final File defaultFile = new File(defaultValue);
					if (defaultFile.exists()) {
						defaultPath = defaultFile.getCanonicalPath();
					} else {
						defaultPath = defaultFile.getAbsolutePath();
					}
					RapidEnvInterpreter.log(Level.FINE, "defaultPath = \"" + defaultPath + "\"");
				}
				boolean ok = false;
				while (!ok) {
					newValue = enterValue(renv, in, out, msg.toString(), defaultPath);
					ok = true;
					if (prop.getMustExist()) {
						if (newValue == null || StringHelper.strip(newValue, StringHelper.StripMode.both).length() == 0) {
							ok = false;
						} else {
							newValue = StringHelper.strip(newValue, StringHelper.StripMode.both);
							if (prop.getCreateIfNotExist()) {
								if (!new File(newValue).exists()) {
									if (promptYesNo(in, out, prop.getFiletype().name() + " " + newValue
									        + " does not exist. Create it?", true)) {
										switch (prop.getFiletype()) {
										case directory:
											if (!new File(newValue).mkdirs()) {
												throw new RapidEnvException("Failed to creat directory \"" + newValue
												        + "\"");
											}
											break;
										case file:
											if (!new File(newValue).createNewFile()) {
												throw new RapidEnvException("Failed to create file \"" + newValue
												        + "\"");
											}
											break;
										}
									}
								}
							}
							if (!new File(newValue).exists()) {
								out.println("  Invalid input: " + prop.getFiletype().name() + " " + newValue
								        + " does not exist.");
								ok = false;
							}
						}
					}
				}
			} catch (IOException e) {
				throw new RapidEnvConfigurationException(e.getMessage(), e);
			}
			break;
		case path:
			throw new RapidEnvException("Individual path properties are not yet supported.");
		case url:
			boolean ok = false;
			while (!ok) {
				newValue = enterValue(renv, in, out, msg.toString(), defaultValue);
				try {
					new URL(newValue);
					ok = true;
				} catch (MalformedURLException e) {
					out.println("  Invalid input: URL " + newValue + " is not valid.");
					ok = false;
				}
			}
			break;
		}
		out.println("  " + prop.getFullyQualifiedName() + " = \"" + newValue + "\"\n");
		return newValue;
	}

	/**
	 * Prompt for entering a value and read it.
	 * 
	 * @param interpreter
	 *            the RapidEnv interpreter instance
	 * @param in
	 *            the input stream to read from
	 * @param out
	 *            the output stream to write to
	 * @param msg
	 *            the message prompt message describing the value to enter
	 * @param defaultValue
	 *            the default value take if nothing is entered
	 * @return the value entered or the default value if nothing is entered
	 */
	protected static String enterValue(final RapidEnvInterpreter interpreter, final InputStream in,
	        final PrintStream out, final String msg, final String defaultValue) {
		if (interpreter.getRunMode() == RunMode.batch) {
			return defaultValue;
		}
		final StringBuffer prompt = new StringBuffer(msg);
		if (defaultValue != null) {
			prompt.append(" [");
			prompt.append(defaultValue);
			prompt.append("]");
		}
		prompt.append(" >");
		out.print(prompt.toString());
		String enteredValue = read(in);
		if (enteredValue.length() == 0) {
			enteredValue = defaultValue;
		}
		return enteredValue;
	}

	/**
	 * Prompt the given question and read the answer.
	 * 
	 * @param in
	 *            the input stream to read from
	 * @param out
	 *            the output stream to write to
	 * @param question
	 *            the question to ask the user
	 * @param defaultValue
	 *            the default answer chosen if the user types nothing
	 * @return true if the answer is yes or false if the answer is no
	 */
	public static boolean promptYesNo(final InputStream in, final PrintStream out, final String question,
	        final boolean defaultValue) {
		if (RapidEnvInterpreter.getInstance() != null
		        && RapidEnvInterpreter.getInstance().getRunMode() == RunMode.batch) {
			return true;
		}
		boolean ret = defaultValue;
		String defaultValString = "N";
		if (defaultValue) {
			defaultValString = "Y";
		}
		out.print(question + " <Y|N> [" + defaultValString + "] >");
		final String answer = read(in);
		if (answer.equalsIgnoreCase("Y")) {
			ret = true;
		} else if (answer.equalsIgnoreCase("N")) {
			ret = false;
		}
		return ret;
	}

	/**
	 * Read some simple input string line.
	 * 
	 * @param sin
	 *            the input stream to read from
	 * 
	 * @return the input line as string
	 */
	protected static String read(final InputStream sin) {
		String line = null;
		try {
			java.io.BufferedReader rdin = new java.io.BufferedReader(new java.io.InputStreamReader(sin));
			line = rdin.readLine();
		} catch (java.io.IOException e) {
			throw new RapidEnvException(e);
		}
		return line;
	}
}
