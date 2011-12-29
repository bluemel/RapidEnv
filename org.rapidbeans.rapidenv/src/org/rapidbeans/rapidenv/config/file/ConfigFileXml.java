/*
 * RapidEnv: ConfigFileXml.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/08/2010
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;


/**
 * The root of all evil.
 */
public class ConfigFileXml extends RapidBeanBaseConfigFileXml {

    /**
     * Check if the file configuration has been performed properly or not
     * @return if the configuration has been performed properly or not
     */
    public boolean check(final boolean execute) {
        RapidEnvInterpreter.log(Level.FINE,
                "checking XML file configuration: "
                + ((Installunit) this.getParentBean()).getName()
                + this.getPathAsFile().getName());
        return super.check(execute);
    }

    /**
     * Creates a configuration file editor used for automatic changes
     *
     * @param cfgFile the parent file configuration
     * @param file the file to edit (may be null)
     *
     * @return the configuration  file editor
     */
    public ConfigFileEditor createEditor(final ConfigFile cfgFile, final File file) {
        final ConfigFileEditorXml editor = new ConfigFileEditorXml(cfgFile, file);
        return editor;
    }

    /**
     * Create a new XML configuration file.
     *
     * @param targetfile the file to create
     *
     * @throws IOException in case of IO problems
     */
    public void createNewFile(final File targetfile) throws IOException {
    	FileWriter writer = null;
    	try {
            writer = new FileWriter(targetfile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            writer.write(PlatformHelper.getLineFeed());
    	} finally {
    		if (writer != null) {
    			writer.close();
    		}
    	}
	}

    /**
     * default constructor.
     */
    public ConfigFileXml() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public ConfigFileXml(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public ConfigFileXml(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigFileXml.class);

    /**
     * @return the RapidBean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
