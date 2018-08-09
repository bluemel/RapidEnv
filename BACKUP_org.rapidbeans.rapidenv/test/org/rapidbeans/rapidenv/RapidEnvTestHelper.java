/*
 * RapidEnv: RapidEnvTestHelper.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 06/18/2011
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
/**
 * 
 */
package org.rapidbeans.rapidenv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import org.rapidbeans.core.util.EscapeMap;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;

/**
 * Some Helper Methods.
 * 
 * @author Martin Bluemel
 */
public class RapidEnvTestHelper {

	private RapidEnvTestHelper() {
	}

	public static void tearDownProfile(final RapidEnvInterpreter env) {
		final File profileFileProps = env.getProfileProps();
		File profileFileCmd = env.getProfileCmd();
		profileFileProps.delete();
		profileFileCmd.delete();
	}

	public static void assertFilesEqual(final File file1, final File file2) throws IOException {
		final String renvhome = new File(".").getCanonicalPath();
		final String renvhomeEsc = StringHelper.escape(renvhome, ESC_MAP);
		final String renvprojecthome = new File("..").getCanonicalPath();
		LineNumberReader rd1 = null;
		LineNumberReader rd2 = null;
		try {
			rd1 = new LineNumberReader(new FileReader(file1));
			rd2 = new LineNumberReader(new FileReader(file2));
			int lineno = 1;
			String line1;
			String line2;
			while ((line1 = rd1.readLine()) != null) {
				final int index = line1.indexOf("@");
				if (index != -1) {
					if (line1.length() > index + 15 && line1.charAt(index + 11) == 'H') {
						line1 = line1.replace("@RAPID_ENV_HOME@", System.getenv("RAPID_ENV_HOME"));
					}
					if (line1.length() > index + 22 && line1.charAt(index + 19) == 'H') {
						line1 = line1.replace("@RAPID_ENV_PROJECT_HOME@", renvprojecthome);
					}
					if (line1.length() > index + 14 && line1.charAt(index + 10) == 'H') {
						line1 = line1.replace("@RAPIDENV_HOME@", renvhome);
					}
					if (line1.length() > index + 18 && line1.charAt(index + 15) == 'E') {
						line1 = line1.replace("@RAPIDENV_HOME_ESC@", renvhomeEsc);
					}
					if (line1.length() > index + 5 && line1.charAt(index + 1) == 'U') {
						line1 = line1.replace("@USER@", PlatformHelper.username());
					}
					if (line1.length() > index + 5 && line1.charAt(index + 1) == 'H') {
						line1 = line1.replace("@HOST@", PlatformHelper.hostname());
					}
				}
				line2 = rd2.readLine();
				if (line2 == null) {
					fail("Unexpectedly file \"" + file1.getAbsolutePath() + "\" misses line " + lineno + ": " + line1);
				}
				assertEquals("lines " + lineno + " differ: ", line1, line2);
				lineno++;
			}
			line2 = rd2.readLine();
			if (line2 != null) {
				fail("Unexpectedly file \"" + file2.getAbsolutePath() + "\" has additional line " + lineno + ": "
				        + line2);
			}

		} finally {
			if (rd1 != null) {
				rd1.close();
			}
			if (rd2 != null) {
				rd2.close();
			}
		}
	}

	private static final EscapeMap ESC_MAP = new EscapeMap(new String[] { "\\", "\\\\", "=", "\\=", ": ", "\\:" });

	public static void assertOutput(final File file, final ByteArrayOutputStream out) throws IOException {
		LineNumberReader rd1 = null;
		LineNumberReader rd2 = null;
		try {
			final String renvhome = new File(".").getCanonicalPath();
			final String renvhomeEsc = StringHelper.escape(renvhome, ESC_MAP);
			final String renvprojecthome = new File("..").getCanonicalPath();
			rd1 = new LineNumberReader(new FileReader(file));
			rd2 = new LineNumberReader(new StringReader(out.toString()));
			int lineno = 1;
			String line1;
			String line2;
			while ((line1 = rd1.readLine()) != null) {
				final int index = line1.indexOf("@");
				if (index != -1) {
					if (line1.length() > index + 15 && line1.charAt(index + 11) == 'H') {
						line1 = line1.replace("@RAPID_ENV_HOME@", System.getenv("RAPID_ENV_HOME"));
					}
					if (line1.length() > index + 22 && line1.charAt(index + 19) == 'H') {
						line1 = line1.replace("@RAPID_ENV_PROJECT_HOME@", renvprojecthome);
					}
					if (line1.length() > index + 14 && line1.charAt(index + 10) == 'H') {
						line1 = line1.replace("@RAPIDENV_HOME@", renvhome);
					}
					if (line1.length() > index + 18 && line1.charAt(index + 15) == 'E') {
						line1 = line1.replace("@RAPIDENV_HOME_ESC@", renvhomeEsc);
					}
					if (line1.length() > index + 5 && line1.charAt(index + 1) == 'U') {
						line1 = line1.replace("@USER@", PlatformHelper.username());
					}
					if (line1.length() > index + 5 && line1.charAt(index + 1) == 'H') {
						line1 = line1.replace("@HOST@", PlatformHelper.hostname());
					}
				}
				line2 = rd2.readLine();
				if (line2 == null) {
					fail("Unexpected output has less lines:\n  line " + lineno + ": " + line1);
				}
				assertEquals("lines " + lineno + " differ: ", line1, line2);
				lineno++;
			}
		} finally {
			if (rd1 != null) {
				rd1.close();
			}
			if (rd2 != null) {
				rd2.close();
			}
		}
	}
}
