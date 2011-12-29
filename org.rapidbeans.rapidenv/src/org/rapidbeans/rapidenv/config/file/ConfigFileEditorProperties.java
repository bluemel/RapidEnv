/*
 * RapidEnv: ConfigFileEditorProperties.java
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
import java.util.List;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.TrimMode;

/**
 * @author Martin Bluemel
 */
public class ConfigFileEditorProperties extends ConfigFileEditor {

    // the internal List data structure
    private List<String> properties = null;

    /**
     * The getter method for the properties field.
     * @return the list of property names
     */
    public List<String> getProperties() {
        return this.properties;
    }

    /**
     * the constructor.
     * 
     * @param cfile
     *            the file to configure
     * @param file
     *            the file to edit (may be null)
     */
    public ConfigFileEditorProperties(final ConfigFile cfile, final File file) {
        super(cfile, file);
    }

    /**
     * get a Property's value.
     * 
     * @param name -
     *            name of the property to retrieve
     * @return value of the property
     */
    public final String getProperty(final String name) {
        return getProperty(null, name);
    }

    /**
     * get a Property's value.
     * 
     * @param section the section e. g. [section_xyz]
     *        if null set the first match
     * @param name name of the property to retrieve
     * @return value of the property
     */
    public final String getProperty(final String section, final String name) {
        this.load();
        String currentSection = null;
        for (int i = 0; i < properties.size(); i++) {
            final String trimmedLine = StringHelper.trim((String) properties.get(i));
            if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                currentSection = trimmedLine;
            }
            final String prop = checkForProperty(
                            (String) properties.get(i), name);
            if (prop != null && (section == null ||
                    (currentSection != null && section.equals(currentSection)))) {
                return prop;
            }
        }

        return null;

    }

    /**
     * check one line for the property.
     *
     * @param line line to check
     * @param name name of the property
     * @return property value if valid property
     *         null if property not found
     */
    private String checkForProperty(final String line, final String name) {
    	String parsedPropertyValue = null;
    	final char[] trimChars = new char[] { ' ', '\t', '\n' };
        String trimmedLine = StringHelper.trim(line, trimChars, TrimMode.leading);
        if (trimmedLine.equals("")) {
            return null;
        } else if (trimmedLine.startsWith("#")) {
            return null;
        } else if (trimmedLine.startsWith("[")) {
            return null;
        }
        final int iEqualsChar = trimmedLine.indexOf('=');
        if (iEqualsChar == -1) {
        	return null;
        }
        final String parsedPropertyName = StringHelper.splitFirst(trimmedLine, "=").trim();
        if (name.equals(parsedPropertyName)) {
            parsedPropertyValue = trimmedLine.substring(iEqualsChar + 1);
        }
        return parsedPropertyValue;
    }

    /**
     * set a Property's value.
     * 
     * @param name - name of the property to set
     * @param value - value to set
     */
    public final void setProperty(final String name, final String value) {
        setProperty(null, name, value, null, true);
    }

    /**
     * set a Property's value.
     * 
     * @param section the section
     * @param name - name of the property to set
     * @param value - value to set
     */
    public final void setProperty(final String section,
            final String name, final String value) {
        setProperty(section, name, value, null, true);
    }

    /**
     * set a Property's value.
     * 
     * @param name - name of the property to set
     * @param value - value to set
     * @param appendIfNotExists - append a new Property if the
     *                            property was not found
     */
    public final void setProperty(final String name, final String value,
            final boolean appendIfNotExists) {
        setProperty(null, name, value, null, appendIfNotExists);
    }

    /**
     * set a Property's value.
     * 
     * @param section the section e. g. [section_xyz]
     *        if null set the first match
     * @param name - name of the property to set
     * @param value - value to set
     * @param commenttag - the comment tag
     * @param appendIfNotExists - append a new Property if the
     *                            property was not found
     */
    public final void setProperty(final String section, final String name,
            final String value, final String commenttag, final boolean appendIfNotExists) {
        this.load();
        String currentSection = null;
        for (int i = 0; i < properties.size(); i++) {
            final String trimmedLine = StringHelper.trim((String) properties.get(i));
            if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                currentSection = trimmedLine;
            }
            String prop = checkForProperty(
                            (String) properties.get(i), name);
            if (prop != null && (section == null ||
                    (currentSection != null && section.equals(currentSection)))) {
                properties.set(i, name + "=" + value);
                if (commenttag != null) {
                    this.insertComment(commenttag, i + 1, "property changed");
                }
                this.setChangedSomething();
                return;
            }
        }

        // property not found
        if (appendIfNotExists) {
            if (section != null && (currentSection == null
                    || (!currentSection.equals(section)))) {
                properties.add(section);
            }
            properties.add(name + "=" + value);
            if (commenttag != null) {
                this.insertComment(commenttag, properties.size(), "property added");
            }
            this.setChangedSomething();
        }
    }

    /**
     * inserts a comment "# (<tag>) " + &lt;comment text&gt;
     * before a given line.
     *
     * @param tag - defaults to RapidEnv project <propject name>
     * @param pos - the number of the given line
     * @param comment - the comment text
     */
    private void insertComment(final String tag, final int pos, final String comment) {
        if (pos == 0) {
            this.properties.add(0, "# (" + tag + ") " + comment);
        } else {
            String line = (String) this.properties.get(pos - 1);
            if (StringHelper.trim(line).startsWith("# (" + tag + ")")) {
                this.properties.set(pos - 1, "# (" + tag + ") " + comment);
            } else {
                this.properties.add(pos - 1, "# (" + tag + ") " + comment);
            }
        }
    }

    /**
     * load a property file.
     */
    public final void load() {
        if (this.properties != null) {
            return;
        }
        this.properties = super.loadFile();
    }

    /**
     * save a property file.
     */
    public final void save() {
        super.saveFile((List<String>) properties);
    }
}
