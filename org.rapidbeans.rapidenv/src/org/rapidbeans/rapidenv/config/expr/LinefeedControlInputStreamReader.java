/*
 * RapidEnv: LinefeedControlInputStreamReader.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 07/10/2011
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

package org.rapidbeans.rapidenv.config.expr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvException;

/**
 * Reads an input stream while adapting / converting line feed characters.
 * 
 * @author Martin Bluemel
 */
public class LinefeedControlInputStreamReader extends InputStreamReader {

	private boolean firstChar = true;

	private LinefeedControl linefeedControl = null;

	private int bufferedChar = -1;

	private int generatedChar = -1;

	public LinefeedControlInputStreamReader(final File inputFile, final LinefeedControl ctrl)
	        throws FileNotFoundException {
		super(new FileInputStream(inputFile));
		this.linefeedControl = ctrl;
	}

	public int read() throws IOException {
		int currentChar;
		if (this.firstChar) {
			currentChar = super.read();
			this.bufferedChar = super.read();
			this.firstChar = false;
		} else {
			if (this.generatedChar == -1) {
				currentChar = this.bufferedChar;
				this.bufferedChar = super.read();
			} else {
				currentChar = this.generatedChar;
				this.generatedChar = -1;
			}
		}

		switch (this.linefeedControl) {
		case normalize:
			if (currentChar == '\r' && this.bufferedChar == '\n') {
				currentChar = '\n';
				this.bufferedChar = super.read();
			}
			break;
		case platform:
			switch (PlatformHelper.getOsfamily()) {
			case windows:
				if (this.bufferedChar == '\n' && currentChar != '\r') {
					this.generatedChar = '\r';
				}
				break;
			case linux:
				if (currentChar == '\r' && this.bufferedChar == '\n') {
					currentChar = '\n';
					this.bufferedChar = super.read();
				}
				break;
			default:
				throw new RapidEnvException("Operating system \"" + PlatformHelper.getOsfamily().name()
				        + "\" not yet supported");
			}
			break;
		case preserve:
			// do nothing
			break;
		default:
			throw new RapidEnvException("Unknown linefeed control type \"" + this.linefeedControl.name() + "\".");
		}
		return currentChar;
	}
}
