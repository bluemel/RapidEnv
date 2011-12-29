/*
 * RapidEnv: Verifyer.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 12/16/2011
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

package org.rapidbeans.rapidenv.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.rapidbeans.rapidenv.RapidEnvException;

/**
 * Verify file by computing checksums.
 */
public class Verifyer {

	public static String hashValue(final File file,
			final Hashalgorithm hashalg) {
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			final byte[] buf = new byte[1024];
			final MessageDigest digest = MessageDigest.getInstance(hashalg.name());
			int n;
			while ((n = fis.read(buf)) != -1) {
				if (n > 0) {
					digest.update(buf, 0, n);
				}
			}
			final byte[] ba = digest.digest();
			final StringBuilder result = new StringBuilder();
			for (int i = 0; i < ba.length; i++) {
				result.append(Integer.toString((ba[i] & 0xff) + 0x100, 16).substring(1));
			}
			return result.toString();
		} catch (FileNotFoundException e) {
			throw new RapidEnvException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RapidEnvException(e);
		} catch (IOException e) {
			throw new RapidEnvException(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				throw new RapidEnvException(e);
			}
		}
	}
}
