/*
 * RapidEnv: AntTask.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 12/12/2011
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

package org.rapidbeans.rapidenv.config.cmd;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.ClassHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;


/**
 * A system command to execute.
 */
public class AntTask extends RapidBeanBaseAntTask {

	/**
	 * The Ant task class.
	 */
	private Class<Task> anttaskclass = null;

	/**
	 * @return the anttaskclass
	 */
	public Class<Task> getAnttaskclass() {
		return anttaskclass;
	}

	public void setAnttaskname(final String name) {
		updateAnttakclass(name);
		super.setAnttaskname(name);
	}

	@SuppressWarnings("unchecked")
	private void updateAnttakclass(final String name) {
		if (this.anttaskclass == null) {
			final String classname = StringHelper.upperFirstCharacter(name);
			final String fullyClassname = "org.apache.tools.ant.taskdefs." + classname;
			try {
				final Class<?> clazz = Class.forName(fullyClassname);
				if (!ClassHelper.classOf(Task.class, clazz)) {
					throw new RapidEnvException("Class \"" + classname + "\" is not Ant task class");
				}
				this.anttaskclass = (Class<Task>) clazz;
			} catch (ClassNotFoundException e) {
				throw new RapidEnvException("Ant task class \"" + fullyClassname + "\" not found");
			}			
		}
	}

    private void setTaskProperty(final Task task, final String name,
    		final String value, final Argtype valuetype) {
		final String setterMethodName = "set"
				+ StringHelper.upperFirstCharacter(name);
		try {
			Method setterMethod = null;
			switch (valuetype) {
			case string:
				setterMethod = task.getClass().getMethod(
						setterMethodName, new Class[] {String.class});
				break;
			case bool:
				setterMethod = task.getClass().getMethod(
						setterMethodName, new Class[] {Boolean.class});
				break;
			case file:
				setterMethod = task.getClass().getMethod(
						setterMethodName, new Class[] {File.class});
				break;
			default:
				throw new RapidEnvException(
						"Argument value type \"" + valuetype.name()
						+ "\" not yet supported.");
			}
			switch (valuetype) {
			case string:
				setterMethod.invoke(task, new Object[] {value});
				break;
			case bool:
				setterMethod.invoke(task, new Object[] {Boolean.getBoolean(value)});
				break;
			case file:
				setterMethod.invoke(task, new Object[] {new File(value)});
				break;
			default:
				throw new RapidEnvException(
						"Argument value type \"" + valuetype.name()
						+ "\" not yet supported.");
			}
		} catch (SecurityException e) {
			throw new RapidEnvException(e);
		} catch (NoSuchMethodException e) {
			throw new RapidEnvException(e);
		} catch (IllegalArgumentException e) {
			throw new RapidEnvException(e);
		} catch (IllegalAccessException e) {
			throw new RapidEnvException(e);
		} catch (InvocationTargetException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
     * Runs the command in the OS environment.
     * 
     * @return the result containing the return value, stdout and stderr
     */
    public CommandExecutionResult execute() {
    	final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
    	try {
    		if (this.anttaskclass == null) {
    			updateAnttakclass(getAnttaskname());
    		}
    		final Task task = this.anttaskclass.newInstance();
    		task.setProject(new Project());
    		if (this.getArguments() != null) {
    			for (final Argument arg : this.getArguments()) {
    				setTaskProperty(task, arg.getName(), arg.getValue(), arg.getValuetype());
    			}
    		}
    		if (interpreter != null
    				&& interpreter.getOut() != null) {
    			interpreter.getOut().println("Executing Ant task \""
    					+ getAnttaskname() + "\"...");
    		}
    		task.execute();
    	} catch (InstantiationException e) {
    		throw new RapidEnvException(e);
    	} catch (IllegalAccessException e) {
    		throw new RapidEnvException(e);
    	}
    	return new CommandExecutionResult(
    			"", "", 0);
    }

    /**
     * default constructor.
     */
    public AntTask() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public AntTask(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public AntTask(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(AntTask.class);

    /**
     * @return the RapidBean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
