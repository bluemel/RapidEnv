/*
 * RapidEnv: ConfigFilePropertiesTaskSetpropvalue.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/16/2010
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
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Configuration;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.expr.ConfigExprTopLevel;


/**
 * The root of all evil.
 */
public class ConfigFilePropertiesTaskSetpropvalue extends RapidBeanBaseConfigFilePropertiesTaskSetpropvalue {

    /**
     * Check if the node value has been set properly or not
     *
     * @param execute if false only execute the check if the configuration task is necessary
     *                if true execute the configuration task if necessary
     * @param silent if to execute silently
     *
     * @return
     *       execute = false:
     *          if the configuration task shall be executed or not
     *       execute == true:
     *       	if the configuration task as been performed properly or not
     */
    @Override
    public boolean check(final boolean execute, final boolean silent) {
        boolean ok = true;
        if (execute) {
        	ok = false;
        }
        final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
        final ConfigFileProperties fileCfg = (ConfigFileProperties) getParentBean();
        final ConfigFileEditorProperties editor = (ConfigFileEditorProperties) fileCfg.getEditor();
        final String propval = editor.getProperty(getSection(), getName());
        if (propval == null) {
            if (execute) {
                editor.setProperty(getSection(), getName(), getValue(), getCommenttag(), true);
                String msg = "    created property ";
                if (getSection() != null) {
                    msg += "[" + getSection() + "] ";
                }
                msg += "\"" + getName() + "\""
                            + " with value \"" + getValue() + "\""
                            + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                if (!silent) {
                    interpreter.getOut().println(msg);
                }
                RapidEnvInterpreter.log(Level.FINE, msg);
                ok = true;
            } else {
                String msg = "Property ";
                if (getSection() != null) {
                    msg += "[" + getSection() + "] ";
                }
                msg += "\"" + getName() + "\""
                            + " with value \"" + getValue() + "\""
                            + " not found in file " + fileCfg.getPathAsFile().getAbsolutePath();
                RapidEnvInterpreter.log(Level.FINE, msg);
                fileCfg.setIssue(msg);
                ok = false;
            }
        } else {
            if (propval.equals(getValue())) {
                String msg = "Property ";
                if (getSection() != null) {
                    msg += "[" + getSection() + "] ";
                }
                msg += "\"" + getName() + "\""
                            + " already has value \"" + getValue() + "\""
                            + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                RapidEnvInterpreter.log(Level.FINE, msg);
            } else {
                if (execute) {
                    editor.setProperty(getSection(), getName(), getValue(), getCommenttag(), true);
                    String msg = "    set value of property ";
                    if (getSection() != null) {
                        msg += "[" + getSection() + "] ";
                    }
                    msg += "\"" + getName() + "\""
                                + " to \"" + getValue() + "\""
                                + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                    if (!silent) {
                        interpreter.getOut().println(msg);
                    }
                    RapidEnvInterpreter.log(Level.FINE, msg);
                    ok = true;
                } else {
                    String msg = "    Property ";
                    if (getSection() != null) {
                        msg += "[" + getSection() + "] ";
                    }
                    msg += "\"" + getName() + "\""
                                + " has a value different to \"" + getValue() + "\""
                                + " (" + propval + ")"
                                + " in file " + fileCfg.getPathAsFile().getAbsolutePath();
                    RapidEnvInterpreter.log(Level.FINE, msg);
                    fileCfg.setIssue(msg);
                    ok = false;                    
                }
            }
        }
        return ok;
    }

    /**
     * Tweaked getter with lazy initialization and expression interpretation.
     */
    public synchronized String getValue() {
        String value = super.getValue();
        if (value == null) {
            return null;
        }
        if (super.getValue() != null) {
            value = interpret(value);
        }
        return value;
    }

    public String getCommenttag() {
        if (super.getCommenttag() != null
                && super.getCommenttag().length() == 0) {
            return "RapidEnv project " + RapidEnvInterpreter.getInstance().getProject().getName();
        } else {
            return super.getCommenttag();
        }
    }

    /**
     * Interpret a configuration expression.
     *
     * @param expression the configuration expression to interpret
     *
     * @return the interpreted (expanded) configuration expression
     */
    private String interpret(final String expression) {
        return new ConfigExprTopLevel(
        		(Installunit) getParentBean().getParentBean(),
        		null, expression,
        		((Configuration) getParentBean()).getExpressionliteralescaping()).interpret();
    }

    /**
     * default constructor.
     */
    public ConfigFilePropertiesTaskSetpropvalue() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public ConfigFilePropertiesTaskSetpropvalue(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public ConfigFilePropertiesTaskSetpropvalue(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ConfigFilePropertiesTaskSetpropvalue.class);

    /**
     * @return the RapidBean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
