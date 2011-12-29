/*
 * RapidEnv: Installunit.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/03/2010
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.FileFilterRegExp;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.AntGateway;
import org.rapidbeans.rapidenv.InstallStatus;
import org.rapidbeans.rapidenv.RapidEnvCmdException;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.Unpacker;
import org.rapidbeans.rapidenv.cmd.CmdLineInteractions;
import org.rapidbeans.rapidenv.config.cmd.ShellLinkIcon;
import org.rapidbeans.rapidenv.config.expr.ConfigExprTopLevel;
import org.rapidbeans.rapidenv.config.file.ConfigFile;

/**
 * The central class for RapidEnv installation units.
 * 
 * @author Martin Bluemel
 */
public class Installunit extends RapidBeanBaseInstallunit {

	/**
	 * The data document.
	 */
	private Document dataDoc = null;

	/**
	 * Print the current status of this installation unit
	 */
	public void stat() {
		final InstallStatus installStatus = getInstallationStatus();
		String sign = null;

		switch (installStatus) {

		case uptodate:
			if (getInstallcontrol() == InstallControl.discontinued) {
				sign = "!";
			} else {
				sign = "=";
			}
			// fall through intended
		case configurationrequired:
			if (installStatus == InstallStatus.configurationrequired) {
				// this is conditional because of the fall through before
				sign = "!";
			}
			if (getInstallcontrol() == InstallControl.discontinued) {
				RapidEnvInterpreter.getInstance().getOut().println(
						"  " + sign + " " + getFullyQualifiedName()
						+ " should be deinstalled");
			} else {
				String issue = null;
				if (getConfigurations() != null) {
					for (final Configuration cfg : getConfigurations()) {
						if (cfg.getInstallphase() == ConfigurationPhase.config
								&& cfg.checkOsfamily() && (cfg.getIssue() != null)) {
							issue = cfg.getIssue();
							break;
						}
					}
				}
				RapidEnvInterpreter.getInstance().getOut().print("  " + sign + " " + getFullyQualifiedName() + " " + getNearestInstalledVersion());
				final List<Version> installedVersions = findInstalledVersions();
				if (installedVersions.size() == 1) {
					RapidEnvInterpreter.getInstance().getOut().println();
				} else {
					RapidEnvInterpreter.getInstance().getOut().print(" (");
					int i = 0;
					for (final Version version : installedVersions) {
						if (!version.equals(getVersion())) {
							if (i > 0) {
								RapidEnvInterpreter.getInstance().getOut().print(", ");
							}
							RapidEnvInterpreter.getInstance().getOut().print(version.toString());
							i++;
						}
					}
					RapidEnvInterpreter.getInstance().getOut().println(")");
				}
				if (issue != null) {
					RapidEnvInterpreter.getInstance().getOut().println("    " + issue);
				}
			}
			break;

		case deinstallrequired:
			sign = "!";
			RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getVersion()
					+ " deinstallation required");
			break;

		case notinstalled:
			if (this.getInstallcontrol() == InstallControl.optional) {
				sign = "-";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getVersion() + " optional");
			} else if (this.getInstallcontrol() == InstallControl.discontinued) {
				sign = "-";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getVersion() + " discontinued");
			} else {
				sign = "!";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getVersion()
						+ " installation required");
			}
			break;

		case upgraderequired:
			if (getInstallcontrol() == InstallControl.discontinued) {
				sign = "=";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getNearestInstalledVersion()
						+ " should be deinstalled");
			} else {
				sign = "!";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getNearestInstalledVersion()
						+ " upgrade required to version " + getVersion().toString());
			}
			break;

		case downgraderequired:
			if (getInstallcontrol() == InstallControl.discontinued) {
				sign = "=";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getNearestInstalledVersion()
						+ " should be deinstalled");
			} else {
				sign = "!";
				RapidEnvInterpreter.getInstance().getOut().println("  " + sign + " " + getFullyQualifiedName() + " " + getNearestInstalledVersion()
						+ " downgrade required to version " + getVersion().toString());
			}
			break;

		default:
			throw new AssertionError("unexpected installation status \"" + installStatus.name());
		}
	}

	public void install(final List<String> chosenUnits) {
		install(true, chosenUnits);
	}

	private void install(final boolean checkInstalled, final List<String> chosenUnits) {
		final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
		if (renv != null) {
        	RapidEnvInterpreter.log(Level.FINE, "Starting to install unit \"" + getFullyQualifiedName() + "\".");
		}
		if (checkInstalled
				&& getInstallationStatus() != InstallStatus.notinstalled) {
			throw new RapidEnvException("Installation unit \"" + getFullyQualifiedName() + "\" is already installed");
		}

		if (this.getInstallcontrol() == InstallControl.optional
				&& (!chosenUnits.contains(this.getFullyQualifiedName()))) {
			boolean explicitlyChosen = false;
			for (final String unitName : chosenUnits) {
				final Installunit unit = renv.getProject().findInstallunitConfiguration(unitName);
				if (unit == this) {
					explicitlyChosen = true;
					break;
				}
			}
			if (!explicitlyChosen) {
				return;
			}
		}
		storeData(InstallState.installing);
		final File homedir = getHomedirAsFile();
		boolean createdHomedir = false;
		boolean installedSuccessfully = false;
		File localsourcefile = null;
		boolean removeLocalsourcefile = false;
		try {
			// create the home directory
			if (!homedir.exists()) {
				homedir.mkdirs();
				createdHomedir = true;
			}

			// determine the local source file and download if necessary
			switch (getSourcetype()) {
			case url:
				final URL sourceurl = getSourceurlAsUrl();
				if (sourceurl == null) {
					throw new RapidEnvConfigurationException("No source URL specified for"
							+ " install unit \"" + getFullyQualifiedName() + "\"");
				}
				if (sourceurl.getProtocol().equals("file")) {
					final int start = getSourceurlAsUrl().getProtocol().length() + 1;
					final String path = sourceurl.toString().substring(start);
					if (getProject() != null
							&& getProject().getInstallsourceurlAsUrl() != null) {
						localsourcefile = new File(
								getProject().getInstallsourceurlAsUrl().getFile(),
								getFullyQualifiedName().replace('.', '/')
								+ File.separator + getVersion().toString()
								+ File.separator + new File(getSourceurlAsUrl().getFile()).getName());
					}
					if (localsourcefile == null) {
						localsourcefile = new File(path);
					} else {
						if (!(localsourcefile.exists())) {
							if (localsourcefile.getAbsolutePath().equals(new File(path).getAbsolutePath())) {
//								throw new RapidEnvException("Installation source file \""
//										+ localsourcefile.getAbsolutePath() + "\" not found");
							} else {
								RapidEnvInterpreter.getInstance().getOut().println(
										"Copying \"" + localsourcefile.getAbsolutePath()
										+ "\" from \"" + new File(path).getAbsolutePath() + "\"");
								FileHelper.copyFile(new File(path), localsourcefile);
							}
						}
					}
				} else if (getSourceurlAsUrl().getProtocol().equals("http")) {
					if (getProject().getInstallsourceurl() != null
							&& getProject().getInstallsourceurlAsUrl().getProtocol().equals("file")) {
						if (getSourcefile() != null) {
							localsourcefile = new File(
									getProject().getInstallsourceurlAsUrl().getFile(),
									getFullyQualifiedName().replace('.', '/')
									+ File.separator + getVersion().toString()
									+ File.separator + interpret(getSourcefile(), renv.getAnt()));
						} else {
							localsourcefile = new File(
									getProject().getInstallsourceurlAsUrl().getFile(),
									getFullyQualifiedName().replace('.', '/')
									+ File.separator + getVersion().toString()
									+ File.separator + new File(getSourceurlAsUrl().getFile()).getName());
						}
						if (!localsourcefile.getParentFile().exists()) {
							if (!localsourcefile.getParentFile().mkdirs()) {
								throw new RapidEnvException("Could not create directory " + localsourcefile.getParentFile());
							}
						} else if (!localsourcefile.getParentFile().isDirectory()) {
							throw new RapidEnvException("Local source file parent " + localsourcefile.getParentFile()
									+ " is no directory.");
						}
					} else {
						localsourcefile = new File(
								System.getProperty("java.io.tmpdir"),
								new File(getProject().getInstallsourceurlAsUrl().getFile()).getName());
						removeLocalsourcefile = true;
					}
					if (!localsourcefile.exists()) {
						RapidEnvInterpreter.getInstance().getOut().println(
								"Downloading source URL " + getSourceurl().toString() + "\n"
										+ "  to local source file " + localsourcefile.getAbsolutePath() + "...");
						final long timeStart = System.currentTimeMillis();
						HttpDownload.download(getSourceurlAsUrl(),
								localsourcefile, getSourcefilechecks());
						final double duration = (System.currentTimeMillis() - timeStart) / 1000;
						RapidEnvInterpreter.getInstance().getOut().println(
								"  Download finished after " + duration + " s");
						break;
					}
				} else {
					throw new RapidEnvException("Source URL protocol different to \"file\" not yet supported");
				}
				break;

			case mavenrepo:
				final String mavenLocalRepoPath = System.getenv("MAVEN_REPO");
				if (mavenLocalRepoPath == null) {
					throw new RapidEnvConfigurationException("In Order to support installation units"
							+ " of source type \"" + getSourcetype().name()
							+ "\"\nplease define environment variable \"MAVEN_REPO\".");
				}
				localsourcefile = new File(mavenLocalRepoPath + File.separatorChar
						+ getSpace().replace('.', File.separatorChar) + File.separatorChar + getName()
						+ File.separatorChar + getVersion().toString() + File.separatorChar + getName() + '-'
						+ getVersion().toString() + ".zip");
				break;
			default:
				throw new AssertionError("Unexpected source type \"" + getSourcetype().name() + "\"");
			}

			// execute configurations before installation
			if (renv != null) {
	        	RapidEnvInterpreter.log(Level.FINE, "Checking configurations to execute before installation.");
			}
			if (getConfigurations() != null) {
				for (final Configuration cfg : getConfigurations()) {
					if (cfg.checkOsfamily()
							&& (cfg.getInstallphase() == ConfigurationPhase.preinstall)
							) {
						if (renv != null) {
				        	RapidEnvInterpreter.log(Level.FINE, "Checking configuration \""
				        			+ cfg.getClass().getName() + "\".");
						}
						cfg.check(true);
					}
				}
			}

			// execute installation
			switch (getInstallmode()) {
			case unpack:
				// prefer the (automatically packaged) standard archive file if the local source file is no archive
				if ((!localsourcefile.getName().endsWith(".zip"))
						&& (!localsourcefile.getName().endsWith(".tar"))
						&& (!localsourcefile.getName().endsWith(".jar"))
						&& (!localsourcefile.getName().endsWith(".gzip"))
						&& (!localsourcefile.getName().endsWith(".tgz"))
						&& (!localsourcefile.getName().endsWith(".b2zip"))
						) {
					final File localsourcefiledir = localsourcefile.getParentFile();
					final String standardArchiveFileName = getName() + "-" + getVersion()
							+ "-" + PlatformHelper.getOs().name() + "-" + PlatformHelper.getArchName();
					if (new File(localsourcefiledir, standardArchiveFileName + ".zip").exists()) {
						localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".zip");
					} else if (new File(localsourcefiledir, standardArchiveFileName + ".tar").exists()) {
							localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".tar");
					} else if (new File(localsourcefiledir, standardArchiveFileName + ".jar").exists()) {
						localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".jar");
					} else if (new File(localsourcefiledir, standardArchiveFileName + ".gzip").exists()) {
						localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".gzip");
					} else if (new File(localsourcefiledir, standardArchiveFileName + ".tgz").exists()) {
						localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".tgz");
					} else if (new File(localsourcefiledir, standardArchiveFileName + ".b2zip").exists()) {
						localsourcefile = new File(localsourcefiledir, standardArchiveFileName + ".b2zip");
					}
				}
				RapidEnvInterpreter.getInstance().getOut().println("installing "
						+ getFullyQualifiedName() + " " + this.getVersion().toString()
						+ " by unpacking file " + localsourcefile.getAbsolutePath() + "\n"
						+ "  into local folder " + homedir.getAbsolutePath() + "...");
				final Unpacker unpacker = new Unpacker(renv.getAnt());
				unpacker.unpack(localsourcefile, homedir);
				break;
			case put:
				RapidEnvInterpreter.getInstance().getOut().println("installing " + getFullyQualifiedName()
						+ " " + this.getVersion().toString()
						+ " by copying file " + localsourcefile.getAbsolutePath() + "\n"
						+ "  into local folder " + homedir.getAbsolutePath() + "...");
				final String targetFileName = StringHelper.splitLast(
						getFullyQualifiedName(), "/")
						+ '-' + getVersion() + ".jar";
				final File targetFile = new File(homedir, targetFileName);
				if (targetFile.exists()) {
					throw new RapidEnvException("Target file \"" + targetFile.getAbsolutePath() + "\" already exists");
				}
				FileHelper.copyFile(localsourcefile, targetFile);
				break;
			case execute:
				getInstallcommand().execute();
				break;
			default:
				throw new AssertionError("Unexpected installation mode \"" + getInstallmode().name() + "\"");
			}

			// remove root directories
			if (getRemoverootdirs()) {
				removeRootDirs(homedir);
			}
			installedSuccessfully = true;

			// execute configurations after installation
			if (getConfigurations() != null) {
				for (final Configuration cfg : getConfigurations()) {
					if (cfg.checkOsfamily()
							&& (cfg.getInstallphase() == ConfigurationPhase.postinstall
							|| cfg.getInstallphase() == ConfigurationPhase.config)) {
						cfg.check(true);
					}
				}
			}

			storeData(InstallState.installed);

		} catch (RuntimeException e) {
			if (createdHomedir && (!installedSuccessfully)) {
				FileHelper.deleteDeep(homedir);
			}
			throw e;
		} finally {
			if (removeLocalsourcefile) {
				localsourcefile.delete();
			}
		}
	}

	/**
	 * Deinstall the current version of this install unit.
	 */
	public void deinstall() {
		deinstall(false);
	}

	/**
	 * Deinstall the current version of this install unit.
	 *
	 * @param keepIcons determines if to keep icons (in case of
	 *                  update) or not.
	 */
	public void deinstall(final boolean keepIcons) {
		if (getInstallationStatus() == InstallStatus.notinstalled) {
			throw new RapidEnvException("Installation unit \"" + getFullyQualifiedName() + "\" is not installed");
		}

		// execute configurations before deinstallation
		if (getConfigurations() != null) {
			for (final Configuration cfg : getConfigurations()) {
				if (cfg.checkOsfamily()
						&& (cfg.getInstallphase() == ConfigurationPhase.predeinstall)) {
					cfg.check(true);
				}
			}
		}

		// execute deinstallation
		final File homedir = getHomedirAsFile();
		if (!homedir.exists()) {
			throw new RapidEnvException("Homedir \"" + getHomedir() + " of installation unit \""
					+ getFullyQualifiedName() + "\" does not exist.");
		}
		switch (getInstallmode()) {
		case unpack:
			RapidEnvInterpreter.getInstance().getOut().println("  deinstalling "
					+ getFullyQualifiedName() + " " + this.getVersion().toString()
					+ " by deleting local folder " + homedir.getAbsolutePath() + "...");
			FileHelper.deleteDeep(homedir);
			// remove folder above if named like the tool itself
			if (homedir.getName().equals(getVersion().toString())
					&& homedir.getParentFile().getName().equals(getName())
					&& homedir.getParentFile().list().length == 0) {
				if (!homedir.getParentFile().delete()) {
					RapidEnvInterpreter.log(
							Level.WARNING, "Failed to delete folder \""
									+ homedir.getParentFile().getAbsolutePath() + "\"");
				}
			}
			break;
		case put:
			final String targetFileName = StringHelper.splitLast(
					getFullyQualifiedName(), "/")
					+ '-' + getVersion() + ".jar";
			final File targetFile = new File(getHomedirAsFile(), targetFileName);
			if (!targetFile.exists()) {
				throw new RapidEnvException("Target file \"" + targetFile.getAbsolutePath() + "\" does not exist");
			}
			RapidEnvInterpreter.getInstance().getOut().println("  deinstalling "
					+ getFullyQualifiedName() + " " + this.getVersion().toString()
					+ " by deleting file " + targetFile.getAbsolutePath() + "...");
			if (!targetFile.delete()) {
				throw new RapidEnvException("Failed to delete file "
						+ targetFile.getAbsolutePath());
			}
			break;
		case execute:
			getDeinstallcommand().execute();
			break;
		default:
			throw new AssertionError("Unexpected installation mode \"" + getInstallmode().name() + "\"");
		}

		// execute configurations after deinstallation
		if (getConfigurations() != null) {
			for (final Configuration cfg : getConfigurations()) {
				if (cfg.checkOsfamily()
						&& (cfg.getInstallphase() == ConfigurationPhase.postdeinstall)) {
					cfg.check(true);
				}
			}
		}

		// remove all configuration files after deinstallation that are
		// still existent except they are explicitly marked that this
		// shall not happen.
		if (getConfigurations() != null) {
			for (final Configuration cfg : getConfigurations()) {
				if (cfg instanceof ConfigFile) {
					final File file = ((ConfigFile) cfg).getPathAsFile();
					if (((ConfigFile) cfg).getDeleteafterdeinstall()
							&& file.exists()) {
						if (!file.delete()) {
							RapidEnvInterpreter.log(Level.WARNING,
									"Failed to delete configuration file \""
											+ file.getAbsolutePath() + "\"");
						}
					}
				}
			}
		}

		// remove (shell link) icons
		if (!keepIcons) {
			removeIcons();
			removeSpecificProperties();
		}
	}

	/**
	 * Deinstall the given version of this install unit.
	 * The trick: temporarily set the (scheduled) version
	 * of the install unit to the unit to deinstall in order
	 * to get all expressions using version() or homedir()
	 * interpreted correctly.
	 *
	 * @param version the version to deinstall
	 */
	private void deinstall(final Version version, final boolean keepIcons) {
		final Version scheduledVersion = getVersion();
		try {
			setVersion(version);
			deinstall(keepIcons);
		} finally {
			setVersion(scheduledVersion);
		}
	}

	/**
	 * Remove all Icons defined for this installation unit.
	 */
	private void removeIcons() {

		final RapidEnvInterpreter intepreter = RapidEnvInterpreter.getInstance();
		for (final ShellLinkIcon icon : getIcons()) {

			switch (PlatformHelper.getOs()) {

			case windows:
				final File desktopFolder =
				new File(System.getenv("USERPROFILE")
						+ File.separator + "Desktop");
				File iconFile = new File(desktopFolder,
						interpret(icon.getTitle(), intepreter.getAnt()) + ".lnk");
				if (iconFile.exists()) {
					if (iconFile.delete()) {
						intepreter.getOut().println("  deleted icon file \""
								+ iconFile.getAbsolutePath() + "\"");
					} else {
						throw new RapidEnvException("Problems to delete file \""
								+ iconFile.getAbsolutePath() + "\"");
					}
				}
				final File startmenuFolderProjectname =
						new File(System.getenv("USERPROFILE")
								+ File.separator + "Start Menu"
								+ File.separator + intepreter.getProject().getName());
				final File startmenuFolderProjectag =
						new File(System.getenv("USERPROFILE")
								+ File.separator + "Start Menu"
								+ File.separator + intepreter.getProject().getName()
								+ File.separator + intepreter.getProject().getTag());
				iconFile = new File(startmenuFolderProjectag,
						interpret(icon.getTitle(), intepreter.getAnt()) + ".lnk");
				if (iconFile.exists()) {
					if (iconFile.delete()) {
						intepreter.getOut().println("  deleted icon file \""
								+ iconFile.getAbsolutePath() + "\"");
					} else {
						throw new RapidEnvException("Problems to delete file \""
								+ iconFile.getAbsolutePath() + "\"");
					}
				}
				if (startmenuFolderProjectag.exists()
						&& startmenuFolderProjectag.listFiles().length == 0) {
					if (!startmenuFolderProjectag.delete()) {
						throw new RapidEnvException("Problems to delete folder \""
								+ startmenuFolderProjectag.getAbsolutePath() + "\"");
					}
					if (startmenuFolderProjectname.exists()
							&& startmenuFolderProjectname.listFiles().length == 0) {
						if (!startmenuFolderProjectname.delete()) {
							throw new RapidEnvException("Problems to delete folder \""
									+ startmenuFolderProjectname.getAbsolutePath() + "\"");
						}
					}
				}
				break;

			case linux:
				final File desktopFolderLinux =
				new File(PlatformHelper.userhome()
						+ File.separator + "Desktop");
				final File desktopIconFileLinux = new File(desktopFolderLinux,
						interpret(icon.getTitle(), intepreter.getAnt()) + ".desktop");
				if (desktopIconFileLinux.exists()) {
					if (desktopIconFileLinux.delete()) {
						intepreter.getOut().println("  deleted desktop icon file \""
								+ desktopIconFileLinux.getAbsolutePath() + "\"");
					} else {
						throw new RapidEnvException("Problems to delete desktop icon file \""
								+ desktopIconFileLinux.getAbsolutePath() + "\"");
					}
				}
				final File startmenuFolderLinux =
						new File(PlatformHelper.userhome()
								+ File.separator + ".local/share/applications");
				final File startmenuIconFileLinux = new File(startmenuFolderLinux,
						interpret(icon.getTitle(), intepreter.getAnt()) + ".desktop");
				if (startmenuIconFileLinux.exists()) {
					if (startmenuIconFileLinux.delete()) {
						intepreter.getOut().println("  deleted start menu icon file \""
								+ startmenuIconFileLinux.getAbsolutePath() + "\"");
					} else {
						throw new RapidEnvException("Problems to delete start menu icon file \""
								+ startmenuIconFileLinux.getAbsolutePath() + "\"");
					}
				}
				break;
			}
		}
	}

	/**
	 * Remove all specific properties defined for this installation unit.
	 */
	private void removeSpecificProperties() {
		if (getPropertys() != null) {
			final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
			for (final Property prop : getPropertys()) {
				if (interpreter.getPropertyValue(prop.getFullyQualifiedName()) == null) {
					RapidEnvInterpreter.log(Level.WARNING, "Tool specific property \""
							+ prop.getFullyQualifiedName() + "\" was not defined.");
				} else {
					interpreter.getOut().println("    removing specific property \""
							+ prop.getFullyQualifiedName() + "\" ...");
					interpreter.removeProperty(prop.getFullyQualifiedName());
				}
			}
		}
	}

	public List<ShellLinkIcon> getIcons() {
		final List<ShellLinkIcon> icons = new ArrayList<ShellLinkIcon>();
		if (getConfigurations() != null) {
			for (final Configuration config : getConfigurations()) {
				if (config instanceof ShellLinkIcon) {
					icons.add((ShellLinkIcon) config);
				}
			}
		}
		return icons;
	}

	public void updowngrade() {

		if (getInstallationStatus() == InstallStatus.notinstalled) {
			throw new RapidEnvException("Installation unit \"" + getFullyQualifiedName() + "\" is not installed");
		}

		// determine versions of this installation unit currently installed
		// and if they should be deinstalled
		final List<Version> installedVersions = findInstalledVersions();
		boolean deinstall = false;
		if (installedVersions.size() > 0) {
			switch (getDeinstallunusedmode()) {
			case never:
				deinstall = false;
				break;
			case always:
				deinstall = true;
				break;
			case prompt:
				final StringBuffer ivList = new StringBuffer();
				String sversion = "version";
				if (installedVersions.size() == 1) {
					ivList.append(installedVersions.get(0).toString());
				} else {
					sversion = "versions"; 
					int i = 0;
					for (final Version version : installedVersions) {
						if (i > 0) {
							ivList.append(", ");
						}
						ivList.append(version.toString());
						i++;
					}
				}
				switch (RapidEnvInterpreter.getInstance().getRunMode()) {
				case command:
					deinstall = CmdLineInteractions.promptYesNo(
							RapidEnvInterpreter.getInstance().getIn(),
							RapidEnvInterpreter.getInstance().getOut(),
							"Deinstall old " + sversion + " "
									+ getFullyQualifiedName() + " "
									+ ivList
									+ "?", true);
					break;
				default:
					throw new AssertionError("Run mode \""
							+ RapidEnvInterpreter.getInstance().getRunMode().name()
							+ " not yet supported");
				}
				break;
			default:
				break;
			}

			if (deinstall) {
				for (final Version version : installedVersions) {
					deinstall(version, true);
				}
			}
		}

		install(false, new ArrayList<String>());
	}

	public void configure(final boolean execute) {   
		if (getConfigurations() != null) {
			RapidEnvInterpreter.getInstance().getOut().println("  ! " + getFullyQualifiedName() + " " + getNearestInstalledVersion());
			for (final Configuration cfg : getConfigurations()) {
				if (cfg.getInstallphase() == ConfigurationPhase.config
						&& cfg.checkOsfamily()) {
					cfg.check(true);
				}
			}
		}
	}

	/**
	 * Determine packaging automatically if not already initialized.
	 */
	@Override
	public Packaging getPackaging() {
		if (super.getPackaging() == null) {
			final String extension = StringHelper.splitLast(getSourceurl(), ".");
			for (final Packaging current : Packaging.values()) {
				if (current.getExtension().equals(extension)) {
					super.setPackaging(current);
					break;
				}
			}
		}
		return super.getPackaging();
	}

	/**
	 * Determines the installation status of this install unit.
	 * 
	 * @return the installation status
	 */
	public InstallStatus getInstallationStatus() {
		final Version nearestInstalledVersion = getNearestInstalledVersion();
		if (nearestInstalledVersion == null) {
			return InstallStatus.notinstalled;
		}
		if (getInstallcontrol() == InstallControl.discontinued) {
			return InstallStatus.deinstallrequired;
		} else {
			switch (nearestInstalledVersion.compareTo(getVersion())) {
			case 1:
				return InstallStatus.downgraderequired;
			case -1:
				return InstallStatus.upgraderequired;
			case 0:
				InstallStatus stat = InstallStatus.uptodate;
				if (getConfigurations() != null) {
					for (final Configuration cfg : getConfigurations()) {
						if (cfg.getInstallphase() == ConfigurationPhase.config
								&& cfg.checkOsfamily() && (!cfg.check(false))) {
							stat = InstallStatus.configurationrequired;
							break;
						}
					}
				}
				return stat;
			default:
				throw new AssertionError("Unexpected compareTo result "
						+ Integer.toString(nearestInstalledVersion.compareTo(getVersion())));
			}
		}
	}

	/**
	 * Remove all singular root folders below the given directory.
	 * E.g. if under folder "unpack" we find a singular root folder
	 * "eclipse" the content of eclipse will be moved upwards into
	 * "unpack" and eclipse will be removed.
	 *
	 * @param dir
	 */
	private void removeRootDirs(final File dir) {
		File rootdirMostUpper = null;
		File rootdirLowest = null;
		File[] dirElements = dir.listFiles();
		if (dirElements.length == 2) {
			if (dirElements[1].isDirectory() && dirElements[0].getName().equals(".renvstate.xml")) {
				rootdirMostUpper = dirElements[1];
				rootdirLowest = dirElements[1];
			} else if (dirElements[0].isDirectory() && dirElements[1].getName().equals(".renvstate.xml")) {
				rootdirMostUpper = dirElements[0];
				rootdirLowest = dirElements[0];
			}
		} else if (dirElements.length == 1 && dirElements[0].isDirectory()) {
			rootdirMostUpper = dirElements[0];
			rootdirLowest = dirElements[0];
		}
		
		if (rootdirLowest != null) {
			dirElements = rootdirLowest.listFiles();
			while (dirElements.length == 1 && dirElements[0].isDirectory()) {
				rootdirLowest = dirElements[0];
				dirElements = rootdirLowest.listFiles();
			}

			File renamedContentFile = null;
			RapidEnvInterpreter.log(Level.FINE, "Removing lowest root directory \""
					+ rootdirLowest.getAbsolutePath() + "\" below directory \""
					+ dir.getAbsolutePath() + "\"...");
			RapidEnvInterpreter.log(Level.FINE, "Most upper root directory is \""
					+ rootdirMostUpper.getAbsolutePath() + "\"");
			for (final File contentFile : rootdirLowest.listFiles()) {
				if (contentFile.getName().equals(rootdirMostUpper.getName())) {
					renamedContentFile = new File(contentFile.getParentFile(),
							contentFile.getName() + ".tmp.rmrdirs");
					RapidEnvInterpreter.log(Level.FINER, "Renaming file / directory \""
							+ contentFile.getAbsolutePath() + "\" to \""
							+ contentFile.getName() + ".tmp.rmrdirs" + "\"...");
					if (contentFile.renameTo(renamedContentFile) == false) {
						throw new RapidEnvCmdException("Problems to rename content file \""
								+ contentFile.getAbsolutePath() + "\" to name \""
								+ contentFile.getName() + ".tmp.rmrdirs"
								+ "\".");
					}
					RapidEnvInterpreter.log(Level.FINER, "Moving renamed content file / directory \""
							+ renamedContentFile.getAbsolutePath() + "\" below directory \""
							+ dir.getAbsolutePath() + "\"...");
					if (renamedContentFile.renameTo(new File(dir, renamedContentFile.getName())) == false) {
						throw new RapidEnvCmdException("Problems moving renamed content file / directory \""
								+ renamedContentFile.getAbsolutePath() + "\" below directory \""
								+ dir.getAbsolutePath() + "\"..."
								+ "\".");
					}
				} else {
					RapidEnvInterpreter.log(Level.FINER, "Moving file / directory \""
							+ contentFile.getAbsolutePath() + "\" below directory \""
							+ dir.getAbsolutePath() + "\"...");
					if (contentFile.renameTo(new File(dir, contentFile.getName())) == false) {
						throw new RapidEnvCmdException("Problems moving content file / directory \""
								+ contentFile.getAbsolutePath() + "\" below directory \""
								+ dir.getAbsolutePath() + "\"..."
								+ "\".");
					}
				}
			}
			RapidEnvInterpreter.log(Level.FINER, "Deleting directory \""
					+ dir.getAbsolutePath() + "\"...");
			if (rootdirMostUpper.delete() == false) {
				throw new RapidEnvCmdException("Problems to delete root directory \""
						+ rootdirMostUpper.getAbsolutePath() + "\".");
			}
			if (renamedContentFile != null) {
				final File renamedMovedContentFile = new File(dir, renamedContentFile.getName());
				RapidEnvInterpreter.log(Level.FINER, "Renaming file / directory \""
						+ renamedMovedContentFile.getAbsolutePath() + "\" to \""
						+ rootdirMostUpper.getName() + "\"...");
				if (renamedMovedContentFile.renameTo(new File(dir, rootdirMostUpper.getName())) == false) {
					throw new RapidEnvCmdException("Problems to rename renamed moved content file \""
							+ renamedMovedContentFile.getAbsolutePath() + "\" to name \""
							+ rootdirMostUpper.getName()
							+ "\".");
				}
			}
			//            renv.getAnt().moveToDir(rootdir, dir, true);
		}
	}

	public String getFullyQualifiedName() {
		return getFullyQualifiedName(true);
	}

	public String getFullyQualifiedName(final boolean withParentUnits) {
		final StringBuffer fqName = new StringBuffer();
		final List<Installunit> parentUnits = new ArrayList<Installunit>();
		if (withParentUnits) {
			for (final RapidBean bean : getParentBeans()) {
				if (bean instanceof Installunit) {
					parentUnits.add((Installunit) bean);
				}
			}
		}
		parentUnits.add(this);
		final int len = parentUnits.size();
		for (int i = 0; i < len; i++) {
			final Installunit unit = parentUnits.get(i);
			if (i > 0) {
				fqName.append('/');
			}
			if (unit.getSpace() != null && unit.getSpace().length() > 0) {
				fqName.append(unit.getSpace());
				fqName.append('.');
				fqName.append(unit.getName());
			} else {
				fqName.append(unit.getName());
			}
		}
		return fqName.toString();
	}

	/**
	 * Tweaked getter with lazy initialization and expression interpretation.
	 */
	public synchronized String getHomedir() {
		String homedir = null;
		if (super.getHomedir() == null) {
			homedir = getDefaultHomedirPath();
		} else {
			homedir = super.getHomedir();
		}
		if (getParentUnit() != null) {
			homedir = getParentUnit().getHomedir() + '/' + homedir;
		}
		if (homedir != null) {
			final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
			if (renv != null) {
				homedir = interpret(homedir, renv.getAnt());
			}
			homedir = homedir.replace('\\', '/');
		}
		return homedir;
	}

	public File getHomedirAsFile() {
		try {
			return new File(getHomedir()).getCanonicalFile();
		} catch (IOException e) {
			throw new RapidEnvConfigurationException(
					"Configuration problem of property \"homedir\" in installunit \""
							+ getFullyQualifiedName() + "\"", e);
		}
	}

	/**
	 * Tweaked getter with lazy initialization and expression interpretation.
	 */
	public synchronized String getSourceurl() {
		if (super.getSourceurl() == null
				&& (super.getDownloads() == null || super.getDownloads().size() == 0)) {
			final String defaultSourceurl = getDefaultSourceurl();
			RapidEnvInterpreter.log(Level.FINER,
					"Setting default source URL \""
					+ defaultSourceurl + "\"");
			super.setSourceurl(defaultSourceurl);
		}
		String sourceurl = null;
		if (this.getDownloads().size() > 0) {
			sourceurl = getDownloads().get(0).getUrl();
			RapidEnvInterpreter.log(Level.FINER,
					"DOWNLOAD source URL \"" + sourceurl + "\"");
		} else {
			sourceurl = super.getSourceurl();
			RapidEnvInterpreter.log(Level.FINER,
					"SOURCEURL source URL \"" + sourceurl + "\"");
		}
		sourceurl = RapidEnvInterpreter.interpretStat(this, null, sourceurl);
		return sourceurl;
	}

	/**
	 * @return the install unit's installation package source URL.
	 */
	public URL getSourceurlAsUrl() {
		if (getSourceurl() == null) {
			return null;
		}
		try {
			return new URL(getSourceurl());
		} catch (MalformedURLException e) {
			throw new RapidEnvConfigurationException(
					"Configuration problem of property \"sourceurl\" in installunit \""
							+ getFullyQualifiedName() + "\"\n"
							+ "  Malformed URL: \""
							+ getSourceurl().toString() + "\"", e);
		}
	}

	/**
	 * Interpret a configuration expression.
	 *
	 * @param expression the configuration expression to interpret
	 *
	 * @return the interpreted (expanded) configuration expression
	 */
	private String interpret(final String expression, final AntGateway ant) {
		return new ConfigExprTopLevel(this, null, expression,
				getProject().getExpressionLiteralEscaping()).interpret();
	}

	/**
	 * @return the parent Project in a type safe manner.
	 */
	public Project getProject() {
		Installunit unit = this;
		while (unit.getParentUnit() != null) {
			unit = unit.getParentUnit();
		}
		return (Project) unit.getParentBean();
	}

	/**
	 * Semantics check for the project
	 */
	public void checkSemantics() {
		if (getSourcetype() == InstallunitSourceType.url && getProject().getInstallsourceurl() == null
				&& getSourceurl() == null) {
			throw new RapidEnvConfigurationException("No source URL defined for installunit \""
					+ getFullyQualifiedName() + "\".\n" + "Please ether define this property or"
					+ " define the project's global install source URL");
		}
		if (getHomedir() == null) {
			throw new RapidEnvConfigurationException("No home directory defined for installunit \""
					+ getFullyQualifiedName() + "\".\n" + "Please ether define this property or"
					+ " define the project's global install target directory");
		}
		if (getInstallmode() == InstallMode.execute) {
			if (getInstallcommand() == null) {
				throw new RapidEnvConfigurationException("No install command defined vor install unit \""
						+ getFullyQualifiedName() + "\" of install mode \"" + getInstallmode().name() + "\"");
			}
			if (getDeinstallcommand() == null) {
				throw new RapidEnvConfigurationException("No deinstall command defined vor install unit \""
						+ getFullyQualifiedName() + "\" of install mode \"" + getInstallmode().name() + "\"");
			}
		}
	}

	/**
	 * Search for already installed versions and retrieve the one that is
	 * nearest to the scheduled one. This function is based on the convention
	 * that an install unit's home directory path always end with the version
	 * number as separate folder.
	 * 
	 * @return the nearest installed version or null if no version at all is
	 *         installed
	 */
	public Version getNearestInstalledVersion() {
		final List<Version> installedVersions = findInstalledVersions();
		if (installedVersions.size() == 0) {
			return null;
		}
		return this.getVersion().getNearest(installedVersions);
	}

	/**
	 * Search for already installed versions.
	 *
	 * @return a list of already installed versions
	 */
	private List<Version> findInstalledVersions() {
		final List<Version> installedVersions = new ArrayList<Version>();
		final File homedir = getHomedirAsFile();
		if (homedir == null) {
			return installedVersions;
		}
		switch (getInstallmode()) {
		case put:
			final String targetFileName = StringHelper.splitLast(
					getFullyQualifiedName(), "/")
					+ '-' + getVersion() + ".jar";
			if (homedir.exists()) {
				for (final File file : homedir.listFiles(
						new FileFilterRegExp(targetFileName))) {
					String sversion = StringHelper.splitLast(file.getName(), "-");
					sversion = sversion.substring(0, sversion.length() - 4);
					installedVersions.add(new Version(sversion));
				}
			}
			break;

		case unpack:
			if (homedir.exists()) {
				final Document thisDataDoc = getDataDoc(homedir);
				if (thisDataDoc != null) {
					final InstallunitData thisInstallData = (InstallunitData) thisDataDoc.getRoot();
					if (thisInstallData.getFullname().equals(getFullyQualifiedName())
							&& thisInstallData.getVersion().equals(getVersion())
							&& thisInstallData.getInstallstate() == InstallState.installed) {
						installedVersions.add(thisInstallData.getVersion());
					}
				} else {
					// Fallback to maintain downward compatibility
					if (homedir.getName().matches("\\A[0-9.]*\\z")) {
						RapidEnvInterpreter.log(Level.INFO, "Migrating project \""
								+ getFullyQualifiedName() + "\" by generating new data file!");
						initNewDataDoc();
						getData().setVersion(new Version(homedir.getName()));
						storeData(InstallState.installed);
					}
				}
			}
			final File homedirParent = homedir.getParentFile();
			if (homedirParent.exists()) {
				for (final File subdir : homedirParent.listFiles()) {
					if (subdir.equals(homedir)) {
						continue;
					}
					final Document otherDataDoc = getDataDoc(subdir);
					if (otherDataDoc == null) {
						continue;
					}
					final InstallunitData otherInstallData = (InstallunitData) otherDataDoc.getRoot();
					if (otherInstallData != null
							&& otherInstallData.getFullname().equals(getFullyQualifiedName())
							&& otherInstallData.getInstallstate() == InstallState.installed) {
						installedVersions.add(otherInstallData.getVersion());
					}
				}				
			}
			break;
		}

		return installedVersions;
	}

	/**
	 * @return the data describing the install unit's state.
	 */
	public InstallunitData getData() {
		if (this.dataDoc == null) {
			if (getHomedirAsFile().exists()) {
				this.dataDoc = getDataDoc(getHomedirAsFile());
				if (this.dataDoc == null) {
					initNewDataDoc();
				}
			} else {
				initNewDataDoc();
			}
		}
		return (InstallunitData) this.dataDoc.getRoot();
	}

	/**
	 * @return the data describing the install unit's state.
	 */
	private Document getDataDoc(final File homedir) {
		if (homedir.exists()) {
			final File dataFile = getRenvstateFile(homedir);
			if (dataFile.exists()) {
				final Document doc = new Document(TypeRapidBean.forName(
						"org.rapidbeans.rapidenv.config.InstallunitData"), dataFile);
				return doc;
			}
		}
		return null;
	}

	/**
	 * Initialize a new data document.
	 */
	private void initNewDataDoc() {
		final InstallunitData installdata = new InstallunitData();
		installdata.setFullname(getFullyQualifiedName());
		installdata.setVersion(getVersion());
		this.dataDoc = new Document(installdata);
		try {
			this.dataDoc.setUrl(new URL("file:" + getRenvstateFile(getHomedirAsFile())));
		} catch (MalformedURLException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
	 * Persist the install unit's state.
	 *
	 * @param installstate the install unit's state.
	 */
	private void storeData(final InstallState installstate) {
		final InstallunitData installdata = this.getData();
		if (installdata.getFullname() == null) {
			installdata.setFullname(getFullyQualifiedName());
		}
		if (installdata.getVersion() == null) {
			installdata.setVersion(getVersion());
		}
		installdata.setInstallstate(installstate);
		this.dataDoc.save();
	}

	/**
	 * Construct the install unit's state file.
	 *
	 * @param homedir the directory to investigate
	 *
	 * @return the file storing the installation unit's state
	 */
	private File getRenvstateFile(final File homedir) {
		return new File(homedir, ".renvstate.xml");
	}

	/**
	 * The installation root folder per default is<br/>
	 * &lt;project installation target file&gt;/&lt;name space
	 * folders&gt;/&lt;name&gt;.
	 * 
	 * @return the default installation root folder
	 */
	private String getDefaultHomedirPath() {
		StringBuilder path = new StringBuilder();
		if (getParentUnit() == null) {
			final Project project = this.getProject();
			final String projectInstallTargetDirPath = project.getInstalltargetdir();
			if (projectInstallTargetDirPath == null) {
				return null;
			}
			path.append(projectInstallTargetDirPath);
			for (final String s : StringHelper.split(getSpace(), ".")) {
				path.append('/');
				path.append(s);
			}
			path.append('/');
			path.append(getName());
			path.append('/');
			path.append(getVersion().toString());
		} else {
			path.append('/');
			path.append(getFullyQualifiedName(false));
			path.append('/');
			path.append(getVersion().toString());
		}
		return path.toString();
	}

	/**
	 * The installation source URL per default is<br/>
	 * &lt;project installation source URL&gt;/&lt;name space
	 * folders&gt;/&lt;name&gt;.
	 * 
	 * @return the default installation root folder
	 */
	private String getDefaultSourceurl() {
		final Project project = this.getProject();
		final String projectInstallSourceUrl = project.getInstallsourceurl();
		if (projectInstallSourceUrl == null) {
			return null;
		}
		String url = projectInstallSourceUrl;
		if (getParentUnit() == null) {
			for (final String s : StringHelper.split(getSpace(), ".")) {
				url += '/' + s;
			}
			url += '/' + getName()
					+ '/' + getVersion().toString()
					+ '/' + getName() + "-" + getVersion() + ".zip";
		} else {
			url += '/' + getFullyQualifiedName(true)
					+ '/' + getVersion().toString()
					+ '/' + getName() + "-" + getVersion() + ".zip";
		}
		return url;
	}

	/**
	 * default constructor.
	 */
	public Installunit() {
		super();
		init();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public Installunit(final String s) {
		super(s);
		init();
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public Installunit(final String[] sa) {
		super(sa);
		init();
	}

	/**
	 * Hook for initialization.
	 */
	private void init() {
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Installunit.class);

	/**
	 * @return the bean's type
	 */
	@Override
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * Determine if this installation unit is a subunit.
	 *
	 * @return true if this installation unit is a subunit and
	 *      false otherwise
	 */
	public boolean isSubunit() {
		return getParentBean() instanceof Installunit;
	}

	/**
	 * @return the parent unit of this install unit
	 *         or null if it's a top level unit.
	 */
	public Installunit getParentUnit() {
		if (getParentBean() != null && getParentBean() instanceof Installunit) {
			return (Installunit) getParentBean();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve the install unit's parent units.
	 *
	 * @return a list of parent units of this install units
	 *         starting with the top level unit.
	 */
	public List<Installunit> getParentUnits() {
		final List<Installunit> parentUnits = new ArrayList<Installunit>();
		RapidBean current = this.getParentBean();
		while (current != null && current instanceof Installunit) {
			parentUnits.add((Installunit) current);
			current = current.getParentBean();
		}
		swapUnits(parentUnits);
		return parentUnits;
	}

	/**
	 * Swap a list of units quickly without allocating new memory.
	 *
	 * @param units the units to swap
	 */
	public static void swapUnits(final List<Installunit> units) {
		final int size = units.size();
		final int sizeMin1 = size - 1;
		final int sizeDiv2 = size / 2;
		for (int i = 0; i < sizeDiv2; i++) {
			final int j = sizeMin1 - i;
			if (i < j) {
				final Installunit unitToSwap = units.get(j);
				units.set(j, units.get(i));
				units.set(i, unitToSwap);
			}
		}
	}

	/**
	 * Download and zip together an Eclipse update site not provided
	 * as site zip file.
	 *
	 * @param sourceRootUrl the root url 
	 * @param localSourceFile the target zip file
	 */
	protected void downloadEclipseupdatesite(final URL sourceRootUrl,
			final File localSourceFile) {
		final File localSourceRoot = localSourceFile.getParentFile();
		if (!localSourceRoot.exists() && !localSourceRoot.mkdirs()) {
			throw new RapidEnvException("Could not create directrory \""
					+ localSourceRoot.getAbsolutePath() + "\"");
		}
		final File localSiteRoot = new File(localSourceRoot, "site");
		final File localSiteRootFeatures = new File(localSiteRoot, "features");
		final File localSiteRootPlugins = new File(localSiteRoot, "plugins");
		if (!localSiteRoot.exists() && !localSiteRoot.mkdir()) {
			throw new RapidEnvException("Could not create directrory \""
					+ localSiteRoot.getAbsolutePath() + "\"");
		}
		if (!localSiteRootFeatures.exists() && !localSiteRootFeatures.mkdir()) {
			throw new RapidEnvException("Could not create directrory \""
					+ localSiteRootFeatures.getAbsolutePath() + "\"");
		}
		if (!localSiteRootPlugins.exists() && !localSiteRootPlugins.mkdir()) {
			throw new RapidEnvException("Could not create directrory \""
					+ localSiteRootPlugins.getAbsolutePath() + "\"");
		}
		final File artifactsXml = new File(localSiteRoot, "artifacts.xml");
		final File contentXml = new File(localSiteRoot, "content.xml");
		InputStream artifactsXmlIs = null;
		try {
			RapidEnvInterpreter.getInstance().getOut().println(
					"Downloading Eclipse update site configuration file "
							+ new URL(sourceRootUrl.toString() + "/artifacts.xml").toString()
							+ "...");
			HttpDownload.download(new URL(sourceRootUrl.toString() + "/artifacts.xml"),
					artifactsXml, getSourcefilechecks());
			RapidEnvInterpreter.getInstance().getOut().println(
					"Downloading Eclipse update site configuration file "
							+ new URL(sourceRootUrl.toString() + "/content.xml").toString()
							+ "...");
			HttpDownload.download(new URL(sourceRootUrl.toString() + "/content.xml"),
					contentXml, getSourcefilechecks());
			artifactsXmlIs = new FileInputStream(artifactsXml);
			for (final Artifact art : Artifact.parse(artifactsXmlIs)) {
				String downloadUrlString = sourceRootUrl.toString();
				String localFilePath = null;
				if (art.getClassifier().equals("org.eclipse.update.feature")) {
					downloadUrlString += "/features/";
					localFilePath = localSiteRootFeatures.getAbsolutePath();
				} else if (art.getClassifier().equals("osgi.bundle")) {
					downloadUrlString += "/plugins/";
					localFilePath = localSiteRootPlugins.getAbsolutePath();
				}
				final URL downloadUrl = new URL(downloadUrlString
						+ art.getId() + "_" + art.getVersion().toString() + ".zip");
				final File localFile = new File(localFilePath + "/"
						+ art.getId() + "_" + art.getVersion().toString() + ".zip");
				RapidEnvInterpreter.getInstance().getOut().println(
						"Downloading Eclipse update site artifact file "
								+ downloadUrl + "...");
				HttpDownload.download(downloadUrl, localFile, getSourcefilechecks());
			}			
			RapidEnvInterpreter.getInstance().getOut().println(
					"Packaging Eclipse update site artifact files under "
							+ localSiteRoot + " to " + localSourceFile + "...");
			new AntGateway().zip(localSiteRoot, localSourceFile);
		} catch (MalformedURLException e) {
			throw new RapidEnvException(e);
		} catch (FileNotFoundException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
	 * Get all source file checks relevant for the current platform.
	 */
	public ReadonlyListCollection<Download> getDownloads() {
		final List<Download> downloads = new ArrayList<Download>();
		if (super.getDownloads() != null) {
			int i = 0;
			for (final Download dl : super.getDownloads()) {
				RapidEnvInterpreter.log(Level.FINER,
						"DOWNLOAD[" + (i++) + "]: " + dl.getUrl() + "\"");
				if (dl.getOsfamily() == null
						|| dl.getOsfamily() == PlatformHelper.getOs()) {
					downloads.add(dl);
				}
			}
		}
		return new ReadonlyListCollection<Download>(downloads,
				this.getProperty("downloads").getType());
	}

	/**
	 * Get all source file checks relevant for the current platform.
	 */
	public ReadonlyListCollection<Filecheck> getSourcefilechecks() {
		final List<Filecheck> filechecks = new ArrayList<Filecheck>();
		for (final Filecheck check : super.getSourcefilechecks()) {
			if (check.getOsfamily() == null
					|| check.getOsfamily() == PlatformHelper.getOs()) {
				filechecks.add(check);
			}
		}
		return new ReadonlyListCollection<Filecheck>(filechecks,
				this.getProperty("sourcefilechecks").getType());
	}
}