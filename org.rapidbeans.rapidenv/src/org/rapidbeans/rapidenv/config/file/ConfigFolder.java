/*
 * RapidEnv: ConfigFolder.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 10/29/2011
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

package org.rapidbeans.rapidenv.config.file;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.OperatingSystemFamily;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.Unpacker;
import org.rapidbeans.rapidenv.config.ConfigurationTask;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * A folder to configure.
 */
public class ConfigFolder extends RapidBeanBaseConfigFolder {

	/**
	 * Check if the file configuration has been performed properly or not
	 * 
	 * @param execute
	 *            if false only execute the check if the configuration is
	 *            necessary if true execute the configuration if necessary
	 * 
	 * @return if the configuration has been performed properly or not
	 */
	public boolean check(final boolean execute) {
		final URL url = getSourceurlAsUrl();
		File sourcefile = null;
		final File targetfolder = getPathAsFile();
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter
				.getInstance();
		final Unpacker unpacker = new Unpacker(interpreter.getAnt());
		boolean configured = false;

		if (getSourceurl() != null) {
			if (url.getProtocol().equals("file")) {
				RapidEnvInterpreter.log(Level.FINE,
						"Folder configuration sourceurl = \"" + getSourceurl()
								+ "\"");
				RapidEnvInterpreter.log(Level.FINE, "");
				sourcefile = new File(url.getFile());
				RapidEnvInterpreter.log(
						Level.FINE,
						"Folder configuration sourcefile \""
								+ sourcefile.getAbsolutePath() + "\"");
				if (!sourcefile.exists()) {
					throw new RapidEnvConfigurationException("File \""
							+ sourcefile.getAbsolutePath()
							+ "\" does not exist.");
				}
				if (!targetfolder.exists()) {
					if (execute) {
						RapidEnvInterpreter.log(Level.FINE,
								"Folder configuration unpacking sourcfile...");
						unpacker.unpack(sourcefile, targetfolder);
						final String msg = "    unpacked "
								+ sourcefile.getAbsolutePath() + " to folder "
								+ targetfolder.getAbsolutePath();
						interpreter.getOut().println(msg);
						configured = true;
					} else {
						final String msg = "Folder \""
								+ targetfolder.getAbsolutePath()
								+ "\" does not exist.";
						RapidEnvInterpreter.log(Level.FINE, msg);
						setIssue(msg);
						return false;
					}
				} else {
					RapidEnvInterpreter.log(Level.FINE,
							"Folder is already configured");
					return true;
				}
			} else {
				throw new RapidEnvConfigurationException(
						"Protocol different from file not supported"
								+ "for folder configuration property \"sourceurl\".");
			}
		} else {
			if (!targetfolder.exists()) {
				if (execute) {
					if (!targetfolder.exists()) {
						FileHelper.mkdirs(targetfolder);
					}
					final String msg = "    created new folder "
							+ targetfolder.getAbsolutePath();
					interpreter.getOut().println(msg);
					configured = true;
				} else {
					final String msg = "Folder \""
							+ targetfolder.getAbsolutePath()
							+ "\" does not exist.";
					RapidEnvInterpreter.log(Level.FINE, msg);
					setIssue(msg);
					return false;
				}
			}
		}

		if (sourcefile != null) {
			switch (getCopycondition()) {

			case sourcenewer:
				if (sourcefile.lastModified() > targetfolder.lastModified()) {
					if (execute) {
						unpacker.unpack(sourcefile, targetfolder);
						final String msg = "    unpacked "
								+ sourcefile.getAbsolutePath() + " to folder "
								+ targetfolder.getAbsolutePath();
						interpreter.getOut().println(msg);
						configured = true;
					} else {
						final String msg = "Folder \""
								+ targetfolder.getAbsolutePath()
								+ "\" is not up to date.";
						RapidEnvInterpreter.log(Level.FINE, msg);
						setIssue(msg);
						return false;
					}
				}
				break;

			case diff:
				throw new RapidEnvException(
						"copycondition (= unpackcondition) "
								+ "\"diff\" not yet soupported");
			}
		}

		if (getCanread() && (!targetfolder.canRead())) {
			if (execute) {
				interpreter.getOut().println(
						"Add read rights to " + "folder \""
								+ targetfolder.getAbsolutePath() + "\".");
				if (!targetfolder.setReadable(true)) {
					throw new RapidEnvException("Adding read rights"
							+ " to configuration file \""
							+ targetfolder.getAbsolutePath() + "\" failed.");
				}
			} else {
				final String msg = "Folder \"" + targetfolder.getAbsolutePath()
						+ "\" is not readable.";
				RapidEnvInterpreter.log(Level.FINE, msg);
				setIssue(msg);
				return false;
			}
		}
		if (getCanwrite() && (!targetfolder.canWrite())) {
			if (execute) {
				interpreter.getOut().println(
						"Add write rights to " + "folder \""
								+ targetfolder.getAbsolutePath() + "\".");
				if (!targetfolder.setWritable(true)) {
					throw new RapidEnvException("Adding write rights"
							+ " to folder \"" + targetfolder.getAbsolutePath()
							+ "\" failed.");
				}
			} else {
				final String msg = "Folder \"" + targetfolder.getAbsolutePath()
						+ "\" is not writeable.";
				RapidEnvInterpreter.log(Level.FINE, msg);
				setIssue(msg);
				return false;
			}
		}
		if (getCanexecute() && (!targetfolder.canExecute())) {
			if (execute) {
				interpreter.getOut().println(
						"Add execution rights to " + "folder \""
								+ targetfolder.getAbsolutePath() + "\".");
				if (!targetfolder.setExecutable(true)) {
					throw new RapidEnvException("Adding execution rights"
							+ " to folder \"" + targetfolder.getAbsolutePath()
							+ "\" failed.");
				}
			} else {
				final String msg = "Folder \"" + targetfolder.getAbsolutePath()
						+ "\" is not executeable.";
				RapidEnvInterpreter.log(Level.FINE, msg);
				setIssue(msg);
				return false;
			}
		}
		if (!getCanread() && targetfolder.canRead()) {
			if (execute) {
				interpreter.getOut().println(
						"Remove read rights from folder \""
								+ targetfolder.getAbsolutePath() + "\".");
				if (!targetfolder.setReadable(false)) {
					throw new RapidEnvException("Withdrawing read rights"
							+ " from folder \""
							+ targetfolder.getAbsolutePath() + "\" failed.");
				}
			} else {
				final String msg = "Folder \"" + targetfolder.getAbsolutePath()
						+ "\" is readable but should not be.";
				RapidEnvInterpreter.log(Level.FINE, msg);
				setIssue(msg);
				return false;
			}
		}
		if (!getCanwrite() && targetfolder.canWrite()) {
			if (execute) {
				interpreter.getOut().println(
						"Remove write rights from folder \""
								+ targetfolder.getAbsolutePath() + "\".");
				if (!targetfolder.setWritable(false)) {
					throw new RapidEnvException(
							"Withdrawing write rights from folder \""
									+ targetfolder.getAbsolutePath()
									+ "\" failed.");
				}
			} else {
				final String msg = "Folder \"" + targetfolder.getAbsolutePath()
						+ "\" is writeable but should not be.";
				RapidEnvInterpreter.log(Level.FINE, msg);
				setIssue(msg);
				return false;
			}
		}

		if (PlatformHelper.getOsfamily() != OperatingSystemFamily.windows) {
			if (!getCanexecute() && targetfolder.canExecute()) {
				if (execute) {
					interpreter.getOut().println(
							"Remove execution rights from folder \""
									+ targetfolder.getAbsolutePath() + "\".");
					if (!targetfolder.setExecutable(false)) {
						throw new RapidEnvException(
								"Withdrawing execution rights"
										+ " from configuration file \""
										+ targetfolder.getAbsolutePath()
										+ "\" failed.");
					}
				} else {
					final String msg = "Folder \""
							+ targetfolder.getAbsolutePath()
							+ "\" is executeable but should not be.";
					RapidEnvInterpreter.log(Level.FINE, msg);
					setIssue(msg);
					return false;
				}
			}
		}

		if (this.getTasks() != null && this.getTasks().size() > 0) {
			for (final ConfigurationTask cfgTask : this.getTasks()) {
				final boolean checkResult = cfgTask.check(execute, false);
				if (execute) {
					if (checkResult) {
						configured = true;
					}
				} else {
					if (!checkResult) {
						return false;
					}
				}
			}
		}

		if (configured) {
			cleanupFilesOnConfig();
			if (getCommandonconfig() != null) {
				getCommandonconfig().execute();
			}
		}

		boolean ret = false;
		if (execute) {
			ret = configured;
		} else {
			ret = true;
			this.setOk(ret);
		}
		return ret;
	}

	/**
	 * default constructor.
	 */
	public ConfigFolder() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigFolder(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigFolder(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean
			.createInstance(ConfigFolder.class);

	/**
	 * @return the RapidBean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}

	@Override
	public ConfigFileEditor createEditor(ConfigFile cfgFile, File file) {
		return null;
	}
}
