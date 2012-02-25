/*
 * RapidEnv: ConfigFileEditor.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/11/2010
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.rapidenv.RapidEnvException;

/**
 * Abstract superclass for all configfile editors.<br>
 * 
 * @author Martin Bluemel
 */
public abstract class ConfigFileEditor {

	/**
	 * the file to edit during configuration.
	 */
	private ConfigFile configfile = null;

	/**
	 * The concrete file to work on.
	 */
	private File file = null;

	/**
	 * @return the file
	 */
	protected File getFile() {
		return this.file;
	}

	/**
	 * Getter for field <code>configfile</code>.
	 * 
	 * @return the field <code>configfile</code>
	 */
	protected final ConfigFile getConfigfile() {
		return this.configfile;
	}

	private boolean changedSomething = false;

	/**
	 * Simply provide access to field <code>configfile</code> to true.
	 * 
	 * @return a boolean: true means there has been something changed.
	 */
	protected final boolean getChangedSomething() {
		return this.changedSomething;
	}

	/**
	 * Simply sets field <code>configfile</code> to true.
	 */
	protected final void setChangedSomething() {
		this.changedSomething = true;
	}

	private boolean createIfNotExists = false;

	/**
	 * @param createIfNotExists
	 *            the createIfNotExists to set
	 */
	public void setCreateIfNotExists(final boolean createIfNotExists) {
		this.createIfNotExists = createIfNotExists;
	}

	/**
	 * The method reading the file to edit into the editor.
	 */
	protected abstract void load();

	/**
	 * The method saving the edited file.
	 */
	protected abstract void save();

	/**
	 * constructor.
	 * 
	 * @param configfile
	 *            configuration file
	 * @param file
	 *            the file to edit (may be null)
	 */
	public ConfigFileEditor(final ConfigFile configfile, final File file) {
		this.configfile = configfile;
		this.file = file;
		if (this.file == null) {
			this.file = this.configfile.getPathAsFile();
		}
	}

	/**
	 * load a file.
	 * 
	 * @return ArrayList of lines
	 */
	protected final ArrayList<String> loadFile() {
		if (!this.file.exists() && this.createIfNotExists) {
			try {
				if (!this.file.createNewFile()) {
					throw new RapidEnvException("Error while trying to create new file \""
					        + this.file.getAbsolutePath() + "\"");
				}
			} catch (IOException e) {
				throw new RapidEnvException("Exception while trying to create new file \""
				        + this.file.getAbsolutePath() + "\"", e);
			}
		}
		ArrayList<String> lines = new ArrayList<String>();
		LineNumberReader rd = null;
		try {
			rd = new LineNumberReader(new InputStreamReader(new FileInputStream(this.file)));
			String line;
			int i = 0;
			while ((line = rd.readLine()) != null) {
				lines.add(i++, line);
			}
			rd.close();
		} catch (FileNotFoundException e) {
			throw new RapidEnvException(e);
		} catch (IOException e) {
			throw new RapidEnvException(e);
		} finally {
			try {
				if (rd != null) {
					rd.close();
				}
			} catch (IOException e) {
				throw new RapidEnvException(e);
			}
		}
		return lines;
	}

	/**
	 * save a file.
	 * 
	 * @param lines
	 *            ArrayList of lines to write to the file
	 */
	protected final void saveFile(final List<String> lines) {
		if (!this.changedSomething) {
			return;
		}
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.file)));

			for (int i = 0; i < lines.size(); i++) {
				wr.println((String) lines.get(i));
			}
		} catch (FileNotFoundException e) {
			throw new RapidEnvException(e);
		} finally {
			if (wr != null) {
				wr.close();
			}
		}
	}
}
