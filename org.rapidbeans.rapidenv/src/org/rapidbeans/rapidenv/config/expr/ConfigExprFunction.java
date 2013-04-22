/*
 * RapidEnv: .java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 05/30/2010
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
package org.rapidbeans.rapidenv.config.expr;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.core.exception.UtilException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.core.type.TypePropertyString;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Used to extend configurations with dynamic values.<br>
 * <br>
 * A function has the following syntax:<br>
 * <code>&lt;function name&gt;(&lt;arg 1&gt;, &lt;arg 1&gt;, ...)</code><br>
 * For example:<br>
 * <code>source="pathconvert(${prop_path}, '/')/config.xml"</code> <br>
 * <br>
 * Empty parameter lists are also possible:<br>
 * <code>prop.hostname="hostname()"</code><br>
 * <br>
 * Function expressions basically can have sub expressions on any argument
 * value:<br>
 * <code>source="pathconvert(homedir(), '/')/config.xml"</code><br>
 * Be careful to distinct sub expression from literals by using ' characters to
 * mark literals.
 * <p>
 * A function expression must extend this abstract expression class while having
 * a class name according to the following convention:<br>
 * <code>ConfigExprFunction&lt;function name&gt;</code><br>
 * While the function name in an expression string is lower case the first
 * character should be upper case in the class name.
 * </p>
 * <p>
 * <b>please note:</b><br>
 * In order to recognize function expressions the parser needs function names
 * ether to have a prepending whitespace or another prepending expression e. g.:
 * <br>
 * <code>prop.b="${USERNAME}hostname()"</code><br>
 * <br>
 * The following Function Expression can't be recognized:<br>
 * <code>prop.c="${USERNAME}hostname()"</code><br>
 * To accomplish recognition you can prepend an empty String Expression:<br>
 * <code>prop.c="${USERNAME}''hostname()"</code><br>
 * or an empty Variable Expression:<br>
 * <code>prop.c="${USERNAME}_${empty}hostname()"</code><br>
 * </p>
 * 
 * @author Martin Bluemel
 */
public abstract class ConfigExprFunction extends RapidBeanBaseConfigExprFunction {

	// the funtions's argument type declarations
	private List<ArgumentDeclaration> argtypes;

	// the function's argument values
	private List<ConfigExpr> args = new ArrayList<ConfigExpr>();

	/**
	 * The getter for the function's argument values.
	 * 
	 * @return the function's argument values
	 */
	protected final List<ConfigExpr> getArgs() {
		return this.args;
	}

	/**
	 * the constructor of a function expression.
	 * 
	 * @param enclosingUnit
	 *            - the enclosing install unit
	 * @param enclosingProp
	 *            - the enclosing property
	 * @param argumentsString
	 *            - the string containing all the function's arguments.
	 * @param escapeLiterals
	 *            - determines if literals should be escaped or not
	 */
	protected void init(final Installunit enclosingUnit, final EnvProperty enclosingProp, final String argumentsString,
	        final boolean escapeLiterals) {
		setEnclosingInstallUnit(enclosingUnit);
		setEnclosingProperty(enclosingProp);
		this.argtypes = determineArgDeclFromModel();
		int minArgs = 0;
		int maxArgs = 0;
		for (final ArgumentDeclaration decl : this.argtypes) {
			maxArgs++;
			if (!decl.isOptional()) {
				minArgs++;
			}
		}
		final List<String> argumentStrings = splitArguments(argumentsString, getEscapeLitrals());
		if (argumentStrings.size() < minArgs) {
			if (getEnclosingInstallUnit() != null) {
				throw new RapidEnvConfigurationException("To less arguments for function \"" + this.getName()
				        + "\" in configuration" + " of install unit \""
				        + this.getEnclosingInstallUnit().getFullyQualifiedName() + "\".\n" + "Need at minimum "
				        + minArgs + " arguments.");
			} else {
				throw new RapidEnvConfigurationException("To less arguments for function \"" + this.getName()
				        + "\" in configuration" + " of property \""
				        + this.getEnclosingProperty().getFullyQualifiedName() + "\".\n" + "Need at minimum " + minArgs
				        + " arguments.");
			}
		}
		if (argumentStrings.size() > maxArgs) {
			if (getEnclosingInstallUnit() != null) {
				throw new RapidEnvConfigurationException("To much arguments (" + argumentStrings.size()
				        + ") for function \"" + this.getName() + "\" in configuration" + " of install unit \""
				        + getEnclosingInstallUnit().getFullyQualifiedName() + "\".\n" + "Use at maximum " + maxArgs
				        + " arguments.");
			} else {
				throw new RapidEnvConfigurationException("To much arguments (" + argumentStrings.size()
				        + ") for function \"" + this.getName() + "\" in configuration" + " of property \""
				        + getEnclosingProperty().getFullyQualifiedName() + "\".\n" + "Use at maximum " + maxArgs
				        + " arguments.");
			}
		}
		for (String argumentString : argumentStrings) {
			argumentString = argumentString.trim();
			this.args.add(new ConfigExprTopLevel(enclosingUnit, enclosingProp, argumentString, escapeLiterals));
		}
		int i = 0;
		for (final ConfigExpr expr : this.args) {
			final String arg = expr.interpret();
			final ArgumentDeclaration decl = this.argtypes.get(i++);
			if (arg.length() < decl.getMinLength()) {
				if (getEnclosingInstallUnit() != null) {
					throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
					        + "\" of function \"" + this.getName() + "\" in configuration of install unit \""
					        + getEnclosingInstallUnit().getFullyQualifiedName() + "\".\n"
					        + "The value has to be at least " + decl.getMinLength() + " characters long.");
				} else if (getEnclosingProperty() != null) {
					throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
					        + "\" of function \"" + this.getName() + "\" in configuration of install unit \""
					        + getEnclosingProperty().getFullyQualifiedName() + "\".\n"
					        + "The value has to be at least " + decl.getMinLength() + " characters long.");
				} else {
					throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
					        + "\" of function \"" + this.getName() + "\".\n" + "The value has to be at least "
					        + decl.getMinLength() + " characters long.");
				}
			}
			if (decl.getMaxLength() != ArgumentDeclaration.UNLIMITED && arg.length() > decl.getMaxLength()) {
				if (getEnclosingInstallUnit() != null) {
					throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
					        + "\" of function \"" + this.getName() + "\" in configuration of install unit \""
					        + getEnclosingInstallUnit().getFullyQualifiedName() + "\".\n"
					        + "The value may not be more than " + decl.getMaxLength() + " characters long.");
				} else {
					if (getEnclosingProperty() != null) {
						throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
						        + "\" of function \"" + this.getName() + "\" in configuration of install unit \""
						        + getEnclosingProperty().getFullyQualifiedName() + "\".\n"
						        + "The value may not be more than " + decl.getMaxLength() + " characters long.");
					} else {
						throw new RapidEnvConfigurationException("Too short value for argument \"" + decl.getName()
						        + "\" of function \"" + this.getName() + "\" in configuration.\n"
						        + "The value may not be more than " + decl.getMaxLength() + " characters long.");
					}
				}
			}
		}
	}

	/**
	 * Scan all model properties != "installUnit", "childs", "returnval" and
	 * build up the argument declaration list from them.
	 * 
	 * @return the argument declaration list
	 */
	private List<ArgumentDeclaration> determineArgDeclFromModel() {
		final List<ArgumentDeclaration> argDeclList = new ArrayList<ArgumentDeclaration>();
		for (final org.rapidbeans.core.basic.Property prop : this.getPropertyList()) {
			if (!prop.getName().equals("enclosingInstallUnit") && !prop.getName().equals("enclosingProperty")
			        && !prop.getName().equals("childs") && !prop.getName().equals("returnval")) {
				final TypeProperty proptype = prop.getType();
				final boolean optional = !proptype.getMandatory();
				if (proptype instanceof TypePropertyString) {
					final TypePropertyString stringtype = (TypePropertyString) proptype;
					final int minlen = stringtype.getMinLength();
					final int maxlen = stringtype.getMaxLength();
					final ArgumentDeclaration argdecl = new ArgumentDeclaration(prop.getName(), optional, minlen,
					        maxlen);
					argDeclList.add(argdecl);
				} else {
					throw new RapidEnvException("Invalid type \"" + proptype.getClass().getName() + "\"for property \""
					        + prop.getName() + "\" for function declaration type \""
					        + prop.getBean().getType().getName() + "\": only string is a valid type.");
				}
			}
		}
		return argDeclList;
	}

	/**
	 * the factory method for a Function Expression instance.
	 * 
	 * @param enclosingUnit
	 *            the enclosing install unit if any
	 * @param enclosingProp
	 *            the enclosing property if any
	 * @param funcName
	 *            the function's name.
	 * @param funcContent
	 *            the string containing the function's arguments
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 * @return the created Function Expression instance.
	 */
	public static ConfigExprFunction createInstance(final Installunit enclosingUnit, final EnvProperty enclosingProp,
	        final String funcName, final String funcContent, final boolean escapeLiterals) {
		try {
			final Class<?> configClass = Class.forName("org.rapidbeans.rapidenv.config.expr.ConfigExprFunction"
			        + StringHelper.upperFirstCharacter(funcName));
			final Class<?>[] constructorArgTypes = { Installunit.class, EnvProperty.class, String.class, Boolean.class };
			final Constructor<?> constructor = configClass.getConstructor(constructorArgTypes);
			final Object[] constructorArgs = { enclosingUnit, enclosingProp, funcContent, escapeLiterals };
			final ConfigExprFunction instance = (ConfigExprFunction) constructor.newInstance(constructorArgs);
			return instance;
		} catch (ClassNotFoundException e) {
			throw new RapidEnvException(e);
		} catch (NoSuchMethodException e) {
			throw new RapidEnvException(e);
		} catch (InstantiationException e) {
			throw new RapidEnvException(e);
		} catch (InvocationTargetException e) {
			throw new RapidEnvException(e);
		} catch (IllegalAccessException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
	 * Splitting argument values from function content is not trivial because
	 * each argument values could also contain ',' characters. Rules: - leave
	 * together string literals ' (obey escaping) - leave together sub function
	 * content (count braces) - filter whitespace after argument separating ','
	 * characters
	 * 
	 * @param string
	 *            the function content string that will be split
	 * 
	 * @return a list with split argument values
	 */
	public static List<String> splitArguments(final String string, final boolean escapeLiterals) {
		final List<String> list = new ArrayList<String>();
		final StringBuffer buffer = new StringBuffer();
		int state = 0;
		int braceCount = 0;
		final int len = string.length();
		for (int i = 0; i < len; i++) {
			final char c = string.charAt(i);
			switch (state) {

			// between argument values
			// ignore leading whitespace(s)
			case 0:
				switch (c) {
				case ' ':
				case '\t':
				case '\n':
					// state stays 0
					break;
				case '\'':
					buffer.append(c);
					state = 2;
					break;
				default:
					buffer.append(c);
					state = 1;
				}
				break;

			// within unquoted argument value
			case 1:
				switch (c) {
				case ',':
					list.add(buffer.toString());
					buffer.setLength(0);
					state = 0;
					break;
				case '\'':
					buffer.append(c);
					state = 2;
					break;
				case '(':
					buffer.append(c);
					braceCount = 0;
					state = 4;
					break;
				default:
					buffer.append(c);
					state = 1;
				}
				break;

			// within quoted token
			case 2:
				switch (c) {
				case '\\':
					if (escapeLiterals) {
						state = 3;
					} else {
						buffer.append(c);
					}
					break;
				case '\'':
					buffer.append(c);
					state = 1;
					break;
				default:
					buffer.append(c);
					break;
				}
				break;

			// within quoted token after \
			case 3:
				switch (c) {
				case '\\':
					buffer.append('\\');
					state = 2;
					break;
				case '\'':
					buffer.append('\'');
					state = 2;
					break;
				default:
					buffer.append('\\');
					buffer.append(c);
					state = 2;
					break;
				}
				break;

			// within quoted token
			case 4:
				switch (c) {
				case ')':
					buffer.append(c);
					if (braceCount == 0) {
						state = 1;
					} else {
						braceCount--;
					}
					break;
				default:
					buffer.append(c);
					break;
				}
				break;

			default:
				throw new UtilException("wrong state " + state);
			}
		}

		switch (state) {
		case 0:
			break;
		case 1:
			list.add(buffer.toString());
			break;
		case 2:
			throw new UtilException("Missing qouote at the end of string\"" + string + "\"");
		}

		return list;
	}

	/**
	 * adding child expressions to a Function Expression accomplishes building
	 * the expression tree.
	 * 
	 * @param child
	 *            - the child expression to add
	 */
	public final void addChild(final ConfigExpr child) {
		throw new AssertionError("Do not use for funtions. Use arguments instead");
	}

	public String getName() {
		String name = this.getClass().getName();
		name = StringHelper.splitLast(name, ".");
		name = name.substring(18).toLowerCase();
		return name;
	}

	/**
	 * default constructor.
	 */
	public ConfigExprFunction() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s
	 *            the string
	 */
	public ConfigExprFunction(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa
	 *            the string array
	 */
	public ConfigExprFunction(final String[] sa) {
		super(sa);
	}
}
