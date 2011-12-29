/*
 * RapidEnv: ConfigFileTextTaskInsert.java
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


import java.util.logging.Level;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.rapidenv.AntGateway;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;
import org.rapidbeans.rapidenv.config.expr.ConfigExprTopLevel;


/**
 * The root of all evil.
 */
public class ConfigFileTextTaskInsert extends RapidBeanBaseConfigFileTextTaskInsert {

    /**
     * Check if the line has been inserted properly or not
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
        final int[] searchLinesExactlyResult = editor.searchLinesExactly(getLine());
        if (searchLinesExactlyResult.length == 0) {
            if (getRegexp() == null) {
                if (execute) {
                    switch (getMode()) {
                    case append:
                        editor.appendLine(getLine());
                        final String msg1 = "Appended text line\n"
                            + "  \"" + getLine() + "\" into file "
                            + fileCfg.getPathAsFile().getAbsolutePath();
                        if (!silent) {
                            interpreter.getOut().println(msg1);
                        }
                        RapidEnvInterpreter.log(Level.FINE, msg1);
                        ok = true;
                        break;
                    case prepend:
                        editor.prependLine(getLine());
                        final String msg2 = "Prepended text line\n"
                            + "  \"" + getLine() + "\" into file "
                            + fileCfg.getPathAsFile().getAbsolutePath();
                        if (!silent) {
                            interpreter.getOut().println(msg2);
                        }
                        RapidEnvInterpreter.log(Level.FINE, msg2);
                        ok = true;
                        break;
                    }
                } else {
                    final String msg = "Text line \"" + getLine() + "\" not found in file "
                    + fileCfg.getPathAsFile().getAbsolutePath();
                    RapidEnvInterpreter.log(Level.FINE, msg);
                    fileCfg.setIssue(msg);
                    ok = false;                    
                }
            } else {
                final int[] matches = editor.search(getRegexp());
                if (matches.length == 0) {
                    throw new RapidEnvConfigurationException(
                            "line with regexp \""
                            + getRegexp() + "\" not found in file "
                            + fileCfg.getPathAsFile().getAbsolutePath());
                } else if (matches.length == 1) {
                    if (execute) {
                        editor.insertLine(getLine(), matches[0], getMode());
                        String msg = "    inserted line \"" + getLine() + "\" ";
                        switch (getMode()) {
                        case append:
                            msg += "after ";
                            break;
                        case prepend:
                            msg += "before ";
                            break;
                        }
                        msg += "line " + Integer.toString(matches[0] + 1)
                            + ": \"" + editor.getLines().get(matches[0]) + "\""
                            + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                        if (!silent) {
                            interpreter.getOut().println(msg);
                        }
                        RapidEnvInterpreter.log(Level.FINE, msg);
                        ok = true;
                    } else {
                        String msg = "line \"" + getLine() + "\""
                        + " not found ";
                        switch (getMode()) {
                        case append:
                            msg += "after ";
                            break;
                        case prepend:
                            msg += "before ";
                            break;
                        }
                        msg += "line " + Integer.toString(matches[0] + 1)
                            + ": \"" + editor.getLines().get(matches[0]) + "\""
                            + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                        RapidEnvInterpreter.log(Level.FINE, msg);
                        fileCfg.setIssue(msg);
                        ok = false;                    
                    }
                }
            }
        }
        return ok;
    }

    /**
     * Tweaked getter with lazy initialization and expression interpretation.
     */
    public synchronized String getLine() {
    	String line = super.getLine();
    	if (line == null) {
    		return null;
    	}
    	final RapidEnvInterpreter renv = RapidEnvInterpreter.getInstance();
    	if (renv != null) {
    		line = interpret(line, renv.getAnt());
    	}
    	return line;
    }

    /**
     * Interpret a configuration expression.
     *
     * @param expression the configuration expression to interpret
     * @param ant the ant gateway used to support expression interpretation
     *
     * @return the interpreted (expanded) configuration expression
     */
    private String interpret(final String expression, final AntGateway ant) {
    	return new ConfigExprTopLevel((Installunit) this.getParentBean().getParentBean(), null, expression, false).interpret();
    }

    /**
     * default constructor.
     */
    public ConfigFileTextTaskInsert() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public ConfigFileTextTaskInsert(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public ConfigFileTextTaskInsert(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigFileTextTaskInsert.class);

    /**
     * @return the RapidBean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
