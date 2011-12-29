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

import java.io.File;

import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;

/**
 * Unpacks archive files in various formats.
 *
 * @author Martin Bluemel
 */
public class Unpacker {

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
     *@param ant
     *    the Ant gateway to be used
     */
    public Unpacker(final AntGateway ant) {
        this.ant = ant;
    }

    /**
     * Constructor with Ant properties file.
     *
     * @param antProperties
     *     properties for the new Ant gateway
     */
    public Unpacker(final File antProperties) {
        this.ant = new AntGateway(antProperties);
    }

    /**
     * Unpacks archive files in the following formats:
     * zip, jar, war, tar
     * The tar files could be compressed using gzip or b2zip compression.
     * The formats are simply recognized by the archive file name's ending:
     * zip: "*.zip"
     * war: "*.war"
     * jar: "*.jar"
     * tar: "*.tar"
     * tar compressed with gzip: "*.tar.gz", "*.tgz"
     * tar compressed with bzip2: "*.tar.bz2", "*.bz2"
     * 
     * @param packedFile
     *     the file to unpack
     * @param dest
     *     the destination directory (must already exist)
     */
    public void unpack(final File packedFile, final File dest) {
        if (packedFile.getName().endsWith(".zip")
                || packedFile.getName().endsWith(".jar")
                || packedFile.getName().endsWith(".war")) {
            this.ant.expand(packedFile, dest);
        } else if (packedFile.getName().endsWith(".tar")) {
            this.ant.unpack(packedFile, dest, (UntarCompressionMethod)
                    UntarCompressionMethod.getInstance(
                            UntarCompressionMethod.class, "none"));
        } else if (packedFile.getName().endsWith(".tar.gz")
                || packedFile.getName().endsWith(".tgz")) {
            this.ant.unpack(packedFile, dest, (UntarCompressionMethod)
                    UntarCompressionMethod.getInstance(
                            UntarCompressionMethod.class, "gzip"));
        } else if (packedFile.getName().endsWith("tar.bz2")
                || packedFile.getName().endsWith(".bz2")) {
            this.ant.unpack(packedFile, dest, (UntarCompressionMethod)
                    UntarCompressionMethod.getInstance(
                            UntarCompressionMethod.class, "bzip2"));
        }
    }
}
