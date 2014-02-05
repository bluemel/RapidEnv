/*
 * RapidEnv: RapidEnvInterpreter.java
 * 
 * Copyright (C) 2011 Martin Bluemel
 * 
 * Creation Date: 07/16/2011
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.config.cmd.CommandExecutionResult;
import org.rapidbeans.rapidenv.config.cmd.SystemCommand;

/**
 * Unpacks archive files in various formats.
 * 
 * @author Martin Bluemel
 */
public class Unpacker {

	private static final int BUF_SIZE = 1024;

	/**
	 * The Ant gateway.
	 */
	private AntGateway ant = null;

	/**
	 * Standard constructor.
	 */
	public Unpacker() {
		this.ant = new AntGateway();
	}

	/**
	 * Constructor with Ant gateway.
	 * 
	 * @param ant
	 *            the Ant gateway to be used
	 */
	public Unpacker(final AntGateway ant) {
		this.ant = ant;
	}

	/**
	 * Constructor with Ant properties file.
	 * 
	 * @param antProperties
	 *            properties for the new Ant gateway
	 */
	public Unpacker(final File antProperties) {
		this.ant = new AntGateway(antProperties);
	}

	/**
	 * Unpacks archive files in the following formats: zip, jar, war, tar The
	 * tar files could be compressed using gzip or b2zip compression. The
	 * formats are simply recognized by the archive file name's ending: zip:
	 * "*.zip" war: "*.war" jar: "*.jar" tar: "*.tar" tar compressed with gzip:
	 * "*.tar.gz", "*.tgz" tar compressed with bzip2: "*.tar.bz2", "*.bz2"
	 * 
	 * @param packedFile
	 *            the file to unpack
	 * @param dest
	 *            the destination directory (must already exist)
	 * @throws IOException
	 */
	public void unpack(final File packedFile, final File dest) {
		try {
			if (packedFile.getName().endsWith(".zip") || packedFile.getName().endsWith(".jar")
					|| packedFile.getName().endsWith(".war")) {
				this.ant.expand(packedFile, dest);
			} else if (packedFile.getName().endsWith(".tar")) {
				// this.ant.unpack(packedFile, dest,
				// (UntarCompressionMethod) UntarCompressionMethod.getInstance(UntarCompressionMethod.class, "none"));
				unpackArchive(packedFile, dest, false);
			} else if (packedFile.getName().endsWith(".tar.gz") || packedFile.getName().endsWith(".tgz")) {
				// this.ant.unpack(packedFile, dest,
				// (UntarCompressionMethod) UntarCompressionMethod.getInstance(UntarCompressionMethod.class, "gzip"));
				unpackArchive(packedFile, dest, true);
			} else if (packedFile.getName().endsWith("tar.bz2") || packedFile.getName().endsWith(".bz2")) {
				this.ant.unpack(packedFile, dest,
						(UntarCompressionMethod) UntarCompressionMethod.getInstance(UntarCompressionMethod.class,
								"bzip2"));
			}
		} catch (IOException e) {
			throw new RapidEnvException(e);
		} catch (ArchiveException e) {
			throw new RapidEnvException(e);
		} catch (CompressorException e) {
			throw new RapidEnvException(e);
		}
	}

	private void unpackArchive(final File file, final File dest, final boolean compressed)
			throws IOException, ArchiveException, CompressorException {
		if (!dest.exists()) {
			if (!dest.mkdirs()) {
				throw new RapidEnvException(
						"Error while trying to create destination directory: " + dest.getAbsolutePath());
			}
		}
		InputStream uncompressedIs = null;
		ArchiveInputStream is = null;
		try {
			if (compressed) {
				uncompressedIs = new BufferedInputStream(
						new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(
								new FileInputStream(file))));
			} else {
				uncompressedIs = new BufferedInputStream(new FileInputStream(file));
			}
			is = new ArchiveStreamFactory().createArchiveInputStream(uncompressedIs);
			ArchiveEntry entry = null;
			while ((entry = is.getNextEntry()) != null) {
				final File outFile = new File(dest, entry.getName());
				FileMode fileMode = null;
				if (entry instanceof TarArchiveEntry) {
					fileMode = new FileMode(((TarArchiveEntry) entry).getMode());
					RapidEnvInterpreter.log(Level.FINE, "unpacking tar entry: " + outFile.getAbsolutePath()
							+ ", Mode: " + fileMode.toString() + ", " + fileMode.toChmodStringFull());
				} else {
					RapidEnvInterpreter.log(Level.FINE, "unpacking file entry: " + outFile.getAbsolutePath());
				}
				if (entry.isDirectory() && (!outFile.exists())) {
					if (!outFile.mkdirs()) {
						throw new RapidEnvException("Error while trying to create directory: "
								+ outFile.getAbsolutePath());
					}
				} else {
					FileOutputStream os = null;
					try {
						os = new FileOutputStream(outFile);
						final byte[] buf = new byte[BUF_SIZE];
						int readBytesCount;
						while ((readBytesCount = is.read(buf)) != -1) {
							os.write(buf, 0, readBytesCount);
						}
					} finally {
						if (os != null) {
							os.close();
						}
					}
				}
				if (fileMode != null) {
					setOutFileMode(file, fileMode);
				}
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private void setOutFileMode(final File file, final FileMode fileMode) {
		switch (PlatformHelper.getOsfamily())
		{
		case linux:
			final String smode = fileMode.toChmodStringFull();
			final SystemCommand cmd = new SystemCommand("chmod " + smode + " " + file.getAbsolutePath());
			final CommandExecutionResult result = cmd.execute();
			if (result.getReturncode() != 0)
			{
				throw new RapidEnvException("Error while trying to set mode \"" + smode + "\" for file: "
						+ file.getAbsolutePath());
			}
			break;
		default:
			file.setReadable(fileMode.isUr() || fileMode.isGr() || fileMode.isOr());
			file.setWritable(fileMode.isUw() || fileMode.isGw() || fileMode.isOw());
			file.setExecutable(fileMode.isUx() || fileMode.isGx() || fileMode.isOx());
			break;
		}
	}
}
