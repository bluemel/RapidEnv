/*
 * RapidEnv: ConfigFileTextTaskDelete.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/21/2010
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


import java.util.List;
import java.util.logging.Level;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;


/**
 * Deletes the line(s) specified by the give regular expressions
 */
public class ConfigFileTextTaskDelete extends RapidBeanBaseConfigFileTextTaskDelete {

    /**
     * Check if the line(s) still exist
     *
     * @param execute if false only execute the check if the configuration task is necessary
     *                if true execute the configuration task if necessary
     * @param silent if to execute silent
     *
     * @return if the configuration task as been performed properly or not
     */
    @Override
    public boolean check(final boolean execute, final boolean silent) {
        boolean ok = true;
        if (execute) {
        	ok = false;
        }
        final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
        final ConfigFileText fileCfg = (ConfigFileText) getParentBean();
        final ConfigFileEditorText editor = (ConfigFileEditorText) fileCfg.getEditor();

        final int[] matches = editor.search(getRegexp());
        if (matches.length > 0) {
            if (execute) {
                final List<String> deletedLines = editor.deleteLines(getRegexp(), true);
                final int size = deletedLines.size();
                String msg1;
                if (size == 1) {
                    msg1 = "Deleted text line\n"
                        + "   \"" + deletedLines.get(0) + "\" from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                } else if (size > 10) {
                    msg1 = "Deleted " + Integer.toString(size) + " text lines\n"
                        + " matching regular expression \"" + getRegexp() + "\" from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                } else {
                    msg1 = "Deleted text lines:\n";
                    for (int i = 0; i < size; i++) {
                        msg1 += "   \"" + deletedLines.get(i) + "\"\n";
                    }
                    msg1 += " from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                }
                if (!silent) {
                    interpreter.getOut().println(msg1);
                }
                RapidEnvInterpreter.log(Level.FINE, msg1);
                ok = true;
            } else {
                final List<String> linesToDelete = editor.searchLines(getRegexp(), true);
                final int size = linesToDelete.size();
                String msg2;
                if (size == 1) {
                    msg2 = "Text line\n"
                        + "   \"" + linesToDelete.get(0) + "\"\n"
                        + " should be deleted from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                } else if (size > 10) {
                    msg2 = Integer.toString(size) + " text lines"
                        + " matching regular expression\n  \""
                        + getRegexp() + "\"\n"
                        + "  should be deleted from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                } else {
                    msg2 = "Text lines:\n";
                    for (int i = 0; i < size; i++) {
                        msg2 += "    \"" + linesToDelete.get(i) + "\"\n";
                    }
                    msg2 += "  should be deleted from file "
                        + fileCfg.getPathAsFile().getAbsolutePath();
                }
                RapidEnvInterpreter.log(Level.FINE, msg2);
                fileCfg.setIssue(msg2);
                ok = false;
            }
        } else { // no matches
            final String msg3 = "no line to delete found"
                + " with regular expression: \""
                + getRegexp() + "\".";
            RapidEnvInterpreter.log(Level.FINE, msg3);
        }
        return ok;
    }

//    /**
//     * Tweaked getter with lazy initialization and expression interpretation.
//     */
//    public synchronized String getValue() {
//        String value = super.getValue();
//        if (value == null) {
//            return null;
//        }
//        final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
//        if (super.getValue() != null && renv != null) {
//            value = interpret(value, renv.getAnt());
//        }
//        return value;
//    }

//    /**
//     * Interpret a configuration expression.
//     *
//     * @param expression the configuration expression to interpret
//     * @param ant the ant gateway used to support expression interpretation
//     *
//     * @return the interpreted (expanded) configuration expression
//     */
//    private String interpret(final String expression, final AntGateway ant) {
//        return new ConfigExprTopLevel((Installunit) this.getParentBean().getParentBean(), ant, expression).interpret();
//    }

    /**
     * default constructor.
     */
    public ConfigFileTextTaskDelete() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public ConfigFileTextTaskDelete(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public ConfigFileTextTaskDelete(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigFileTextTaskDelete.class);

    /**
     * @return the RapidBean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
