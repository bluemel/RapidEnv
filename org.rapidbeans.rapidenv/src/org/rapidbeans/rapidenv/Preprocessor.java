/*
 * RapidEnv: Preprocessor.java
 *
 * Copyright (C) 2010 - 2013 Martin Bluemel
 *
 * Creation Date: 03/28/2013
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
package org.rapidbeans.rapidenv;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

public class Preprocessor {

	private List<String> lines = new ArrayList<String>();

	public Preprocessor(final File file) throws FileNotFoundException {
		readLines(file, false);
	}

	private void readLines(final File file, final boolean include) throws FileNotFoundException {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), readXmlEncoding(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				if (include && line.trim().startsWith("<?")) {
					// overread
				} else if (include && line.trim().startsWith("<!DOCTYPE")) {
					// overread
				} else if (include && line.trim().length() == 0) {
					// overread
				} else if (line.trim().startsWith("#include")) {
					readLines(evalIncludeStatement(file, line.trim()), true);
				} else {
					this.lines.add(line);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new RapidEnvException("Internal problem opening reader for file \"" + file.getAbsolutePath()
			        + "\"\n" + "  Unsupported encoding  \"UTF-8\"", e);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new RapidEnvException("Internal problem reading file \"" + file.getAbsolutePath()
			        + "\"", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RapidEnvException("Internal problem closing reader for file \"" + file.getAbsolutePath()
					        + "\"");
				}
			}
		}
	}

	static File evalIncludeStatement(final File currentFile, final String statement) throws IOException {
		String path = statement.substring("#include".length()).trim();
		if ((path.startsWith("\"") && path.endsWith("\""))
		        || (path.startsWith("<") && path.endsWith(">"))) {
			path = path.substring(1, path.length() - 1).trim();
		} else {
			throw new RapidEnvConfigurationException("Problems to evaluate include statement: " + statement
			        + "\n  File expression is not embedded in '\"'");
		}
		if (!new File(path).isAbsolute()) {
			path = currentFile.getParentFile().getAbsolutePath().replace(File.separatorChar, '/') + '/' + path;
		}
		final File file = new File(path).getCanonicalFile();
		return file;
	}

	private String readXmlEncoding(final File file) throws FileNotFoundException {
		String encoding = "UTF-8";
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String firstLine = null;
			String line;
			while ((line = reader.readLine()) != null && firstLine == null) {
				if (line.trim().length() > 0)
				{
					firstLine = line;
				}
			}
			if (firstLine != null && firstLine.trim().startsWith("<?xml")) {
				encoding = extractEncoding(firstLine.trim());
			}
		} catch (UnsupportedEncodingException e) {
			throw new RapidEnvException("Internal problem opening reader for file \"" + file.getAbsolutePath()
			        + "\"\n" + "  Unsupported encoding  \"UTF-8\"", e);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new RapidEnvException("Internal problem reading file \"" + file.getAbsolutePath()
			        + "\"", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RapidEnvException("Internal problem closing reader for file \"" + file.getAbsolutePath()
					        + "\"");
				}
			}
		}
		return encoding;
	}

	static String extractEncoding(String xmlHeaderLine) {
		// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		String encoding = "UTF-8";
		int pos = xmlHeaderLine.indexOf("encoding");
		if (pos > -1) {
			final String[] tokens = StringHelper.splitQuoted(xmlHeaderLine);
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals("encoding=")) {
					encoding = tokens[i + 1];
					break;
				} else if (tokens[i].equals("encoding")) {
					encoding = tokens[i + 2];
					break;
				}
			}
		}
		return encoding;
	}

	public InputStream getInputStream() {
		final StringBuilder sb = new StringBuilder();
		for (final String line : this.lines) {
			sb.append(line);
		}
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
}
