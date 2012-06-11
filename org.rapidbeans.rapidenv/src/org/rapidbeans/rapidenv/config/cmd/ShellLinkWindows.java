/*
 * RapidEnv: ShellLinkWindows.java
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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.rapidbeans.core.util.EscapeMap;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvCmdExecutionException;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Wraps VBS scripts to create a shortcut on Windows operating systems.
 */
public class ShellLinkWindows {

	// Unicodes for German "Umlauts" and special characters
	public static final String AUML_UPPER = "\u00C4";

	public static final String OUML_UPPER = "\u00D6";

	public static final String UUML_UPPER = "\u00DC";

	public static final String AUML_LOWER = "\u00E4";

	public static final String OUML_LOWER = "\u00F6";

	public static final String UUML_LOWER = "\u00FC";

	public static final String UUML_ESZET = "\u00DF";

	/**
	 * The shortcut file. Usually ends with ".lnk".
	 */
	private File file = null;

	/**
	 * The shortcut's description.
	 */
	private String description = null;

	/**
	 * The shortcut's command target path. Must exist and be executable.
	 */
	private File targetPath = null;

	/**
	 * The shortcut's command arguments.
	 */
	private List<Argument> arguments = null;

	/**
	 * The shortcut's command working directory. Must exist and be a directory /
	 * folder.
	 */
	private File workingDirectory = null;

	/**
	 * The shortcut's icon file
	 */
	private File iconFile = null;

	/**
	 * The shortcut's icon number
	 */
	private int iconNumber = 0;

	/**
	 * The shortcut's window style.
	 */
	private ShortcutWindowStyle windowStyle = ShortcutWindowStyle.normalFocus;

	/**
	 * The shortcut's hot key. A combination of Keyboard key's.
	 */
	private List<Integer> hotKey = null;

	/**
	 * Constructor.
	 * 
	 * @param shorcutFile
	 *            the shortcut file. Usually ends with ".lnk".
	 */
	public ShellLinkWindows(final File shorcutFile) {
		this.file = shorcutFile;
	}

	/**
	 * Create a new or modify an existing windows shortcut.
	 * 
	 * @throws ShortcutFileAlreadyExistsException
	 */
	public void save() {

		// execute VB script to create a new or to moodify
		// a existing shortcut via system command.
		final SystemCommand command = new SystemCommand();
		command.setExecutable("cscript.exe");

		final File devEnvScriptFile = new File("scripts/windows/shortcutWrite.vbs");
		File scriptFile = new File(System.getenv("RAPID_ENV_HOME") + File.separator + "bin" + File.separator
		        + "shortcutWrite.vbs");
		if (devEnvScriptFile.exists()) {
			scriptFile = devEnvScriptFile;
		}
		command.addArgument(new Argument(scriptFile.getAbsolutePath(), true));
		command.addArgument(new Argument("//Nologo"));
		command.addArgument(new Argument(this.file.getAbsolutePath(), true));

		String descr = "";
		if (this.description != null) {
			descr = this.description.replace("\"", "&quot;");
		}
		command.addArgument(new Argument(descr, true));

		if (this.targetPath == null) {
			throw new RapidEnvConfigurationException("No target path specified for windows icon \""
			        + this.file.getAbsolutePath() + "\"");
		}
		command.addArgument(new Argument(this.targetPath.getAbsolutePath()));

		final StringBuffer args = new StringBuffer();
		if (this.arguments != null) {
			int i = 0;
			for (final Argument arg : this.arguments) {
				if (i > 0) {
					args.append(' ');
				}
				if (arg.getQuoted()) {
					args.append("&quot;");
				}
				args.append(arg.getValue().replace("\"", "\\&quot;"));
				if (arg.getQuoted()) {
					args.append("&quot;");
				}
				i++;
			}
		}
		command.addArgument(new Argument(args.toString(), true));

		if (this.workingDirectory == null) {
			throw new RapidEnvConfigurationException("No working directory specified for windows icon \""
			        + this.file.getAbsolutePath() + "\"");
		}
		final Argument argWorkingDirectory = new Argument(this.workingDirectory.getAbsolutePath());
		command.addArgument(argWorkingDirectory);

		String iconLocation;
		if (this.iconFile == null) {
			iconLocation = "%SystemRoot%" + File.separator + "system32" + File.separator + "csript.exe" + ",0";
		} else {
			iconLocation = this.iconFile.getAbsolutePath() + "," + Integer.toString(this.iconNumber);
		}
		command.addArgument(new Argument(iconLocation));

		command.addArgument(new Argument(Integer.toString(this.windowStyle.ordinal() + 1)));

		StringBuffer hotkey = new StringBuffer();
		if (this.hotKey != null && this.hotKey.size() > 0) {
			int i = 0;
			for (final int keyCode : this.hotKey) {
				if (i > 0) {
					hotkey.append('+');
				}
				hotkey.append(keyCode2Name(keyCode));
				i++;
			}
		}
		command.addArgument(new Argument(hotkey.toString(), true));

		command.setSilent(true);
		final CommandExecutionResult result = command.execute();
		if (result.getReturncode() != 0) {
			throw new RapidEnvCmdExecutionException("Error during writing windows shortcut \""
			        + getFile().getAbsolutePath() + ": " + result.getStderr(), result.getReturncode());
		}
	}

	private String keyCode2Name(final int keyCode) {
		String keyName = null;
		switch (keyCode) {
		case KeyEvent.VK_CONTROL:
			keyName = "Ctrl";
		break;
		case KeyEvent.VK_ALT:
			keyName = "Alt";
		break;
		case KeyEvent.VK_ALT_GRAPH:
			keyName = "AltGraph";
		break;
		case KeyEvent.VK_SHIFT:
			keyName = "Shift";
		break;
		default:
			keyName = new String(new char[] { (char) keyCode });
		break;
		}
		return keyName;
	}

	/**
	 * Read an existing windows shortcut.
	 * 
	 * @throws ShortcutFileNotFoundException
	 */
	public void load() {

		// check shortcut file existence.
		if (!this.file.exists()) {
			throw new ShortcutFileNotFoundException(this.file.getAbsolutePath());
		}

		// execute VB script to read the shortcut via
		// system command.
		final SystemCommand command = new SystemCommand();
		command.setExecutable("cscript.exe");
		File scriptFile = new File(System.getenv("RAPID_ENV_HOME") + File.separator + "bin" + File.separator
		        + "shortcutRead.vbs");
		if (!scriptFile.exists()) {
			final File devEnvScriptFile = new File("scripts/windows/shortcutRead.vbs");
			if (devEnvScriptFile.exists()) {
				scriptFile = devEnvScriptFile;
			}
		}
		final String scriptFilePath = scriptFile.getAbsolutePath();
		final String shorcutFilePath = getFile().getAbsolutePath();
		command.addArgument(new Argument(scriptFilePath));
		command.addArgument(new Argument("//Nologo"));
		command.addArgument(new Argument(shorcutFilePath));
		command.setSilent(true);
		final CommandExecutionResult result = command.execute();
		if (result.getReturncode() != 0) {
			throw new RapidEnvCmdExecutionException("Error during reading windows shortcut \""
			        + getFile().getAbsolutePath() + ": " + result.getStderr(), result.getReturncode());
		}

		// parse standard out into a Properties (Map) object.
		final Properties props = new Properties();
		try {
			props.load(new StringReader(StringHelper.escape(result.getStdout(), new EscapeMap(new String[] { "\\",
			        "\\\\" }))));
		} catch (IOException e) {
			throw new RapidEnvException("IO exception while reading standard out: " + e.getMessage(), e);
		}

		// read in the shortcut details (properties)
		this.description = props.getProperty("Description").replace("\\\\\"", "\\\"");
		// TargetPath=C:\WINNT\system32\cmd.exe
		this.targetPath = new File(props.getProperty("TargetPath"));
		// Arguments=/C "echo Hello shortcut!& pause"
		final String argstrings = props.getProperty("Arguments").replace("\\\\\"", "\\\"");
		if (this.arguments != null) {
			this.arguments.clear();
		}
		for (final StringHelper.SplitToken token : StringHelper.splitQuotedIsQuoted(argstrings)) {
			final Argument arg = new Argument(token.getToken());
			if (token.isQuoted()) {
				arg.setQuoted(true);
			}
			addArgument(arg);
		}
		// WorkingDirectory=C:\WINNT\system32
		this.workingDirectory = new File(props.getProperty("WorkingDirectory"));
		// IconLocation=D:\Projects\RapidBeans\org.rapidbeans.rapidenv\testdata\shelllink\test.ico,0
		final String iconLocation = props.getProperty("IconLocation");
		final List<String> iconLocationList = StringHelper.split(iconLocation, ",");
		this.iconFile = new File(iconLocationList.get(0));
		this.iconNumber = Integer.parseInt(iconLocationList.get(1));
		// WindowStyle=1
		final String styleNumberString = props.getProperty("WindowStyle");
		final int styleNumber = Integer.parseInt(styleNumberString);
		this.windowStyle = ShortcutWindowStyle.values()[styleNumber];
		// Hotkey=Alt+Ctrl+C
		final String hotkey = props.getProperty("Hotkey");
		this.hotKey = new ArrayList<Integer>();
		for (String key : StringHelper.split(hotkey, "+")) {
			if (key.equals("Ctrl")) {
				key = "CONTROL";
			}
			try {
				final Field field = KeyEvent.class.getField("VK_" + key.toUpperCase());
				final int keyCode = field.getInt(KeyEvent.class);
				this.hotKey.add(keyCode);
			} catch (SecurityException e) {
				throw new RapidEnvException(e);
			} catch (NoSuchFieldException e) {
				throw new RapidEnvException(e);
			} catch (IllegalArgumentException e) {
				throw new RapidEnvException(e);
			} catch (IllegalAccessException e) {
				throw new RapidEnvException(e);
			}
		}
	}

	/**
	 * @return the shortcut file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the shortcut's description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the shortcut's description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the shortcut's command target path
	 */
	public File getTargetPath() {
		return targetPath;
	}

	/**
	 * @param targetPath
	 *            the shortcut's command target path
	 */
	public void setTargetPath(final File targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @return the shortcut's command arguments.
	 */
	public List<Argument> getArguments() {
		if (this.arguments == null) {
			return null;
		}
		final List<Argument> args = new ArrayList<Argument>();
		for (final Argument arg : this.arguments) {
			args.add((Argument) arg.clone());
		}
		return args;
	}

	/**
	 * @param args
	 *            the shortcut's command arguments.
	 */
	public void setArguments(final List<Argument> args) {
		if (this.arguments != null) {
			this.arguments.clear();
		}
		for (final Argument arg : args) {
			addArgument(arg);
		}
	}

	/**
	 * Add a command argument for this shortcut.
	 * 
	 * @param arg
	 *            the argument to add
	 */
	public void addArgument(final Argument arg) {
		if (this.arguments == null) {
			this.arguments = new ArrayList<Argument>();
		}
		this.arguments.add((Argument) arg.clone());
	}

	/**
	 * @return the shorcut's working directory
	 */
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * @param workingDirectory
	 *            the shortcut's working directory
	 */
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/**
	 * @return the shorcut's icon file
	 */
	public File getIconFile() {
		return iconFile;
	}

	/**
	 * @param iconFile
	 *            the icon file to set
	 */
	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}

	/**
	 * @return the icon number within the icon file
	 */
	public int getIconNumber() {
		return iconNumber;
	}

	/**
	 * @param iconNumber
	 *            the iconNumber to set
	 */
	public void setIconNumber(int iconNumber) {
		this.iconNumber = iconNumber;
	}

	/**
	 * @return the windowStyle
	 */
	public ShortcutWindowStyle getWindowStyle() {
		return windowStyle;
	}

	/**
	 * @param windowStyle
	 *            the windowStyle to set
	 */
	public void setWindowStyle(ShortcutWindowStyle windowStyle) {
		this.windowStyle = windowStyle;
	}

	/**
	 * @return the hotKey
	 */
	public List<Integer> getHotKey() {
		return hotKey;
	}

	/**
	 * @param hotKey
	 *            the hotKey to set
	 */
	public void setHotKey(List<Integer> hotKey) {
		this.hotKey = hotKey;
	}

	public static File getStartMenuFolder() {

		// German
		File startMenuFolder = new File(System.getenv("USERPROFILE") + File.separator + "Startmen" + UUML_LOWER);
		if (startMenuFolder.exists()) {
			return startMenuFolder;
		}

		// English / Default
		startMenuFolder = new File(System.getenv("USERPROFILE") + File.separator + "Start Menu");
		System.out.println("@@@ start menu floder 2: " + startMenuFolder.getAbsolutePath() + ": "
		        + startMenuFolder.exists());

		return startMenuFolder;
	}
}
