/*
 * RapidEnv: AntGateway.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/15/2010
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Untar;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;

/**
 * this class just provides a convenient interface
 * to use this lots of pretty ant components called
 * tasks.
 * 
 * @author Martin Bluemel
 */
public final class AntGateway {
	private Project project = new Project();
	private Properties envProps = null;

    /**
     * The normal constructor for the Ant gateway.
     * The Ant Property task member is used for
     * caching the environment variables are cached
     * by the Ant Property task.
     */
    public AntGateway() {
        // load environment variables into the
        // project's propertyValueMap (prefix "env.")
        Property propTask = new Property();
        propTask.setProject(this.project);
        propTask.setEnvironment("env");
        propTask.execute();
    }

    /**
     * A constructor only for testing reasons.
     * You can initialize the environment by
     * means of a propertyValueMap file.
     * 
     * @param envProps - the file withe the environment varibles
     *                   defined as propertyValueMap.
     */
    public AntGateway(final File envProps) {
        this();
        this.envProps = new Properties();
        try {
            this.envProps.load(new FileInputStream(envProps));
        } catch (IOException e) {
            throw new RapidEnvException(e);
        }
    }

    /**
     * Unpacks a zip, jar, war archive file
     * by means of the Ant Expand task. 
     * 
     * @param packedFile - the source archive file.
     * @param dest - the destination directory.
     */
    public void expand(final File packedFile, final File dest) {
        final Expand task = new Expand();
        task.setProject(this.project);
        task.setSrc(packedFile);
        task.setDest(dest);
        task.execute();
    }

    /**
     * Unpacks a zip, jar, tar, or war archive file
     * by means of the Ant Untar task optionally using
     * compression. 
     * 
     * @param packedFile - the source archive file.
     * @param dest - the destination directory.
     * @param compressionMethod
     *     the compression method used
     *     { 'none' | 'gzip' | 'bzip2' }
     */
    public void unpack(final File packedFile, final File dest,
            final UntarCompressionMethod compressionMethod) {
        Untar task = new Untar();
        task.setProject(this.project);
        task.setSrc(packedFile);
        task.setDest(dest);
        task.setCompression(compressionMethod);
        task.execute();
    }

    /**
     * Packs an archive file by mean of the Ant Expand task.
     *
     * @param folder the destination directory.
     * @param zipfile the source archive file.
     */
    public void zip(final File folder, final File zipfile) {
    	Zip task = new Zip();
        task.setProject(this.project);
        task.setTaskName("Zip");
        task.setBasedir(folder);
        task.setDestFile(zipfile);
        task.execute();
    }
}
