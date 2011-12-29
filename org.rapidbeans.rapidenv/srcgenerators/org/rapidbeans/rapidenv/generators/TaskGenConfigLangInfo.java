/*
 * RapidEnv: TaskGenConfigLangInfo.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 11/15/2010
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

package org.rapidbeans.rapidenv.generators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.rapidbeans.core.basic.RapidEnum;
import org.rapidbeans.core.type.PropertyXmlBindingType;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.core.type.TypePropertyChoice;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.type.TypeRapidEnum;
import org.rapidbeans.core.util.PlatformHelper;

/**
 * the task to generate the RapidEnv Reference Configuration
 * Language Documentation in XML format usable for further
 * transformation.
 *
 * @author Martin Bluemel
 */
public final class TaskGenConfigLangInfo extends Task {

    private static final String LF = PlatformHelper.getLineFeed();

    /**
     * force flag.
     */
    private boolean force = false;

    /**
     * set the force flag which determines if the generation should be performed
     * not regarding modification dates.
     * @param argForce determines if the generation should be performed
     *                 not regarding modification dates
     */
    public void setForce(final boolean argForce) {
        this.force = argForce;
    }

    /**
     * the (root) bean type to analyze
     */
    private String type = null;

    /**
     * the (root) bean type to analyze.
     *
     * @param type the (root) directory to analyze
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * the output file
     */
    private File out = null;
    
    /**
     * determine the output file to generate
     *
     * @param out the output file to generate
     */
    public void setOut(final File out) {
        this.out = out;
    }

    /**
     * The model root directory (folder).
     */
    private String modelroot = null;
    
    /**
     * Determine the input model root directory (folder).
     * The model files are only used for modification date check.
     *
     * @param modelroot the model root directory (folder).
     */
    public void setModelroot(final String modelroot) {
        this.modelroot = modelroot;
    }

    /**
     * the execute method.
     */
    public void execute() {

        // checks for attributes
        if (this.type == null) {
            throw new BuildException("No bean type to analyze defined."
                    + " Please define value for attribute \"type\".");
        }

        final TypeRapidBean rootType = TypeRapidBean.forName(this.type);

        if (this.out == null) {
            throw new BuildException("No output file defined. Please define value for attribute \"out\".");
        }
        if (!this.out.getParentFile().exists()) {
            if (!this.out.getParentFile().mkdirs()) {
                throw new BuildException("Problems to create directory \""
                        + this.out.getParentFile() + "\".");
            }
        }

        if (this.modelroot == null) {
            getProject().log("     [genrefinput]   no modelroot defined, force = true",
                    Project.MSG_VERBOSE);
            force = true;
        }

        final File modelrootDir = new File(this.modelroot);
        getProject().log("     [genrefinput]   modelroot: "
                + this.modelroot, Project.MSG_DEBUG);
        getProject().log("     [genrefinput]   modelroot path: "
                + modelrootDir.getPath(), Project.MSG_DEBUG);
        getProject().log("     [genrefinput]   modelroot absolute path: "
                + modelrootDir.getAbsolutePath(), Project.MSG_DEBUG);

        if (!modelrootDir.exists()) {
            throw new BuildException("Model root directory \""
                    + this.modelroot + "\" not found");
        }
        if (!modelrootDir.isDirectory()) {
            throw new BuildException("Invalid model root directory. File \""
                    + this.modelroot + " is not a directory");
        }

        long dirLatestModified = 0;
        if (!this.force) {
            dirLatestModified = getLatestModificationDirDate(
                    modelrootDir, rootType, 0);
        }

        if (!this.out.exists()
                || this.force
                || dirLatestModified > this.out.lastModified()
                ) {
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(this.out), "UTF-8");

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + LF + LF);
                writer.write("<grammar>" + LF);

                this.getProject().log(
                        "     [genrefinput] Processing bean type "
                        + this.type
                        + " to " + this.out.getAbsolutePath(),
                        Project.MSG_INFO);
                generateConfLangDescr(rootType, writer, this.getProject());
                writer.write("</grammar>" + LF);
            } catch (IOException e) {
                throw new BuildException(e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        throw new BuildException(e);
                    }
                }
            }
        }
    }

    /**
     * Generate the DTD recursively
     * @param typename the type to process
     * @param writer the writer
     * @param project the ant project
     */
    protected static void generateConfLangDescr(final TypeRapidBean type,
            final Writer writer, final Project project) {
        generateConfLangDescr(type, null, new HashMap<String, Object>(), writer, project);
    }

    /**
     * Generate the DTD recursively
     * @param typename the type to process
     * @param xmlname the XML name
     * @param alreadydefined the map storing XML element / type combinations
     *                               already described so far.
     * @param writer the writer
     * @param project the ant project
     */
    private static void generateConfLangDescr(final TypeRapidBean type,
            final String xmlname,
            final Map<String, Object> alreadydefined,
            final Writer writer,
            final Project project) {

        try {
            // write element name
            String elementname = xmlname;
            if (elementname == null) {
                elementname = type.getXmlRootElement();
                if (elementname == null) {
                    throw new BuildException("No XML root element definition for bean type \""
                            + type.getName() + "\"");
                }
            }

            writer.write(LF + "\t<element name=\"" + elementname + "\"" + LF);
            writer.write("\t\tbeantype=\"" + type.getName() + "\"" + LF);
            writer.write("\t\t>" + LF);
            if (type.getDescription() != null) {
                writer.write("\t\t<description><![CDATA["
                        + type.getDescription()
                        + " ]]></description>" + LF);
            } else {
                writer.write("\t\t<description>@@@undefined@@@</description>" + LF);
            }
            alreadydefined.put(elementname, type);

            // write sub element list
            final List<TypePropertyCollection> colProptypes =
                type.getColPropertyTypes();

/*
            int proptypesWithElementXmlBindingCount = 0;
            for (final TypeProperty proptype : type.getPropertyTypes()) {
                if (proptype.getXmlBindingType() == PropertyXmlBindingType.element) {
                    proptypesWithElementXmlBindingCount++;
                }
            }
            if (colProptypes.size() == 0
                    && proptypesWithElementXmlBindingCount == 0) {
                writer.write("EMPTY>");
            } else {
                writer.write("(#PCDATA");
                for (final TypeProperty proptype : type.getPropertyTypes()) {
                    if (proptype.getXmlBindingType() == PropertyXmlBindingType.element) {
                        writer.write(" | ");
                        writer.write(proptype.getPropName());                   
                    }
                }
                for (final TypePropertyCollection colPropType : colProptypes) {
                    if (colPropType.isComposition() && (!colPropType.isTransient())) {
                        for (final String subelementname : getSubelementNames(colPropType, type)) {
                            writer.write(" | ");
                            writer.write(subelementname);
                        }
                    }
                }
                writer.write(")*>");
            }
*/


            // write attribute list
            for (final TypeProperty proptype : type.getPropertyTypes()) {
                if (proptype instanceof TypePropertyCollection
                        && ((TypePropertyCollection) proptype).isComposition()) {
                    for (final String subelementname : getSubelementNames(
                            (TypePropertyCollection) proptype, type)) {
                        writeAttribute(proptype, subelementname, writer);
                    }
                } else {
                    writeAttribute(proptype, proptype.getPropName(), writer);
                }
            }

            writer.write("\t</element>" + LF);

            // recurse over composition associated types
            for (final TypePropertyCollection colPropType : colProptypes) {
                if (!colPropType.isComposition()
                        || colPropType.isTransient()) {
                    continue;
                }
                for (final String subelementname : getSubelementNames(colPropType, type)) {
                    TypeRapidBean targetType = colPropType.getTargetType();
                    if (type.getXmlElementsTypeMap() != null
                            && type.getXmlElementsTypeMap().get(subelementname) != null) {
                        targetType = type.getXmlElementsTypeMap().get(subelementname);
                    }
                    if (alreadydefined.get(subelementname) == null) {
                        generateConfLangDescr(targetType, subelementname, alreadydefined, writer, project);
                    } else if (alreadydefined.get(subelementname) != targetType) {
                        if (alreadydefined.get(subelementname) instanceof TypeRapidBean) {
                            throw new BuildException("XML Element \"" + subelementname
                                    + "\" already used for bean type \""
                                    + ((TypeRapidBean) alreadydefined.get(subelementname) ).getName()
                                    + "\"");
                        } else {
                            throw new BuildException("XML Element \"" + subelementname
                                    + "\" already used for property type \""
                                    + ((TypeProperty) alreadydefined.get(subelementname) ).getPropName()
                                    + "\"");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Write a single attribute.
     *
     * @param proptype property type
     * @param propname the property name
     * @param writer the writer
     *
     * @throws IOException if IO fails
     */
    private static void writeAttribute(
            final TypeProperty proptype, final String propname, final Writer writer)
        throws IOException {
        if (proptype.isTransient()
                || proptype.getXmlBindingType() == PropertyXmlBindingType.element) {
            return;
        }
        writer.write("\t\t<attribute name=\"" + propname + "\"" + LF);
        writer.write("\t\t\ttype=\"" + proptype.getProptype().name() + "\"" + LF);
        writer.write("\t\t\tmandatory=\"" + Boolean.toString(proptype.getMandatory()) + "\"" + LF);
        writer.write("\t\t\ttransient=\"" + Boolean.toString(proptype.isTransient()) + "\"" + LF);
        if (proptype.getDefaultValue() != null) {
            writer.write("\t\t\tdefault=\"");
            if (proptype instanceof TypePropertyChoice) {
                final TypePropertyChoice cProptype = (TypePropertyChoice) proptype;
                @SuppressWarnings("unchecked")
                final List<RapidEnum> defaultValue = (List<RapidEnum>) cProptype.getDefaultValue();
                if (cProptype.getMultiple()) {
                    int i = 0;
                    for (final RapidEnum element : defaultValue) {
                        if (i > 0) {
                            writer.write(" ,");
                        }
                        writer.write(element.name());
                        i++;
                    }
                } else {
                    writer.write(defaultValue.get(0).name());
                }
            } else {
                writer.write(proptype.getDefaultValue().toString());
            }
            writer.write("\"" + LF);
        } else {
            writer.write("\t\t\tdefault=\"@@@undefined@@@\"" + LF);
        }
        if (proptype instanceof TypePropertyCollection) {
            if (((TypePropertyCollection) proptype).isComposition()) {
                writer.write("\t\t\tcomposition=\"true\"" + LF);
            } else {
                writer.write("\t\t\tcomposition=\"false\"" + LF);
            }
            String minmult = "0";
            if (((TypePropertyCollection) proptype).getMinmult() > 0) {
                minmult = Integer.toString(((TypePropertyCollection) proptype).getMinmult());
            }
            String maxmult = "*";
            if (((TypePropertyCollection) proptype).getMaxmult() >= 0) {
                maxmult = Integer.toString(((TypePropertyCollection) proptype).getMaxmult());
            }
            writer.write("\t\t\tminmult=\"" + minmult + "\"" + LF);
            writer.write("\t\t\tmaxmult=\"" + maxmult + "\"" + LF);
        }
        writer.write("\t\t\t>" + LF);
        if (proptype.getDescription() != null) {
            writer.write("\t\t\t<description><![CDATA["
                    + proptype.getDescription()
                    + "]]></description>" + LF);
        } else {
            writer.write("\t\t\t<description>@@@undefined@@@</description>" + LF);
        }

        if (proptype instanceof TypePropertyChoice) {
            final TypePropertyChoice choicePropType = (TypePropertyChoice) proptype;
            final TypeRapidEnum enumtype = choicePropType.getEnumType();
            writer.write("\t\t\t<enumtype name=\"");
            writer.write(enumtype.getName());
            writer.write("\">" + LF);
            if (enumtype.getDescription() != null) {
                writer.write("\t\t\t\t<description><![CDATA[");
                writer.write(enumtype.getDescription());
                writer.write("]]></description>" + LF);
            } else {
                writer.write("\t\t\t\t<description>@@@undefined@@@</description>" + LF);
            }
            for (final RapidEnum enumElement : enumtype.getElements()) {
                writer.write("\t\t\t\t<enum name=\"");
                writer.write(enumElement.name());
                writer.write("\">"+ LF);
                if (enumElement.getDescription() != null) {
                    writer.write("\t\t\t\t\t<description>");
                    writer.write(enumElement.getDescription());
                    writer.write("</description>" + LF);
                } else {
                    writer.write("\t\t\t\t\t<description>@@@undefined@@@</description>" + LF);
                }
                writer.write("\t\t\t\t</enum>" + LF);
            }
            writer.write("\t\t\t</enumtype>" + LF);
        }
        writer.write("\t\t</attribute>" + LF);
    }

//    /**
//     * Count the attributes of the given bean type.
//     *
//     * @param type the bean type to analyze.
//     * @param colProptypes the collection property types
//     *
//     * @return the attributes count of the given bean type
//     */
//    private static int countAttributes(final TypeRapidBean type, final List<TypePropertyCollection> colProptypes) {
//        // count attributes
//        int attrcount = 0;
//        if (type.getPropertyTypes().size() > colProptypes.size()) {
//            for (final TypeProperty proptype : type.getPropertyTypes()) {
//                if (proptype.isTransient()
//                        || proptype.getXmlBindingType() == PropertyXmlBindingType.element) {
//                    continue;
//                }
//                if ((!(proptype instanceof TypePropertyCollection))
//                        || (!((TypePropertyCollection) proptype).isComposition())) {
//                    attrcount++;
//                }
//            }
//        }
//        return attrcount;
//    }

//    /**
//     * Write a DTD header comment.
//     *
//     * @param description the bean type or property type description
//     */
//    private static void writeHeaderComment(final Object type,
//            final String elementname,
//            final Writer writer,
//            final String indent) throws IOException {
//        TypeRapidBean beantype = null;
//        TypeProperty proptype = null;
//        String description = null;
//        if (type instanceof TypeRapidBean) {
//            beantype = (TypeRapidBean) type;
//            description = beantype.getDescription();
//        } else if (type instanceof TypeProperty) {
//            proptype = (TypeProperty) type;
//            description = proptype.getDescription();
//        }
//        if (description != null) {
//            writer.write(indent + "<!-- Element: " + elementname + LF);
//            for (final String line : StringHelper.split(description, "\n")) {
//                writer.write(indent + '\t' + line + LF);
//            }
//            if (beantype != null) {
//                // write sub element list
//                final List<TypePropertyCollection> colProptypes =
//                    beantype.getColPropertyTypes();
//                int proptypesWithElementXmlBindingCount = 0;
//                for (final TypeProperty beanproptype : beantype.getPropertyTypes()) {
//                    if (beanproptype.getXmlBindingType() == PropertyXmlBindingType.element) {
//                        proptypesWithElementXmlBindingCount++;
//                    }
//                }
//                if (colProptypes.size() > 0
//                        || proptypesWithElementXmlBindingCount > 0) {
//                    for (final TypeProperty beanproptype : beantype.getPropertyTypes()) {
//                        if (beanproptype.getXmlBindingType() == PropertyXmlBindingType.element) {
//                            writer.write(indent + "\tSubelement: " + beanproptype.getPropName() + LF);
//                            if (beanproptype.getDescription() != null) {
//                                for (final String line : StringHelper.split(beanproptype.getDescription(), "\n")) {
//                                    writer.write(indent + "\t\t" + line + LF);
//                                }
//                            }
//                        }
//                    }
//                    for (final TypePropertyCollection colPropType : colProptypes) {
//                        if (colPropType.isComposition() && (!colPropType.isTransient())) {
//                            for (final String subelementname : getSubelementNames(colPropType, beantype)) {
//                                writer.write(indent + "\tSubelement: " + subelementname + LF);
//                                if (colPropType.getDescription() != null) {
//                                    for (final String line : StringHelper.split(colPropType.getDescription(), "\n")) {
//                                        writer.write(indent + "\t\t" + line + LF);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                final int attrcount = countAttributes(beantype, colProptypes);
//
//                // write attribute list
//                if (attrcount > 0) {
//                    for (final TypeProperty beanproptype : beantype.getPropertyTypes()) {
//                        if (beanproptype.isTransient()
//                                || beanproptype.getXmlBindingType() == PropertyXmlBindingType.element) {
//                            continue;
//                        }
//                        if (beanproptype instanceof TypePropertyBoolean) {
//                            writer.write(indent + "\tAttribute: " + beanproptype.getPropName() + LF);
//                            if (beanproptype.getDescription() != null) {
//                                for (final String line : StringHelper.split(beanproptype.getDescription(), "\n")) {
//                                    writer.write(indent + "\t\t" + line + LF);
//                                }
//                            }
//                        } else if ((!(beanproptype instanceof TypePropertyCollection))
//                                || (!((TypePropertyCollection) beanproptype).isComposition())) {
//                            writer.write(indent + "\tAttribute: " + beanproptype.getPropName() + LF);
//                            if (beanproptype.getDescription() != null) {
//                                for (final String line : StringHelper.split(beanproptype.getDescription(), "\n")) {
//                                    writer.write(indent + "\t\t" + line + LF);
//                                }
//                            }
//                            if (beanproptype instanceof TypePropertyChoice) {
//                                final TypeRapidEnum enumtype = ((TypePropertyChoice) beanproptype).getEnumType();
//                                if (enumtype.getDescription() != null) {
//                                    for (final String line : StringHelper.split(enumtype.getDescription(), "\n")) {
//                                        writer.write(indent + "\t\t" + line + LF);
//                                    }
//                                }
//                                if (enumtype.getElements() != null) {
//                                    for (final RapidEnum enumElem : enumtype.getElements()) {
//                                        if (enumtype.getDescription(enumElem.name()) != null) {
//                                            writer.write(indent + "\t\t" + "Value: " + enumElem.name() + LF);
//                                            for (final String line : StringHelper.split(enumtype.getDescription(enumElem.name()), "\n")) {
//                                                writer.write(indent + "\t\t\t" + line + LF);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    } 
//                }
//            }
//            writer.write(indent + "-->" + LF);                
//        }
//    }

    /**
     * @param colPropType
     * @return
     */
    private static List<String> getSubelementNames(final TypePropertyCollection colPropType,
            final TypeRapidBean type) {
        final List<String> subelementNames = new ArrayList<String>();
        if (type.getXmlElements() != null && type.getXmlElements().size() > 0) {
            for (final Entry<String, TypeProperty> entry : type.getXmlElements().entrySet()) {
                if (colPropType == entry.getValue()) {
                    subelementNames.add(entry.getKey());
                }
            }
        }
        if (subelementNames.size() == 0) {
            String subelementName = colPropType.getPropName();
            if (colPropType.getMaxmult() != 1
                    || subelementName.endsWith("s")) {
                subelementName = subelementName.substring(0, subelementName.length() - 1);
            }
            subelementNames.add(subelementName);
        }
        return subelementNames;
    }

    /**
     * iterates recursively over a complete directory tree
     * and determines the newest (latest) modification date
     * of all the directories contained in the file system
     * hierarchy.
     */
    protected static long getLatestModificationDirDate(
            final File dir, final TypeRapidBean type, final long latest) {
        long lastModified = latest;
        final File typefile = new File(dir, type.getName().replace('.', '/'));
        if (typefile.lastModified() > lastModified) {
            lastModified = typefile.lastModified();
        }

        // recurse over composition associated types
        for (final TypePropertyCollection colPropType : type.getColPropertyTypes()) {
            if (!colPropType.isComposition()
                    || colPropType.isTransient()) {
                continue;
            }
            for (final String subelementname : getSubelementNames(colPropType, type)) {
                if (type.getXmlElementsTypeMap() != null
                        && type.getXmlElementsTypeMap().get(subelementname) != null) {
                    lastModified = getLatestModificationDirDate(dir,
                            type.getXmlElementsTypeMap().get(subelementname), lastModified);
                } else if (!(colPropType.getTargetType().equals(type))) {
                    lastModified = getLatestModificationDirDate(dir,
                            colPropType.getTargetType(), lastModified);
                }
            }
        }

        return lastModified;
    }
}
