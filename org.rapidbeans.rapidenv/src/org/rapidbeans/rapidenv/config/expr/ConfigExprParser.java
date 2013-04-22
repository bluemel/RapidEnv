/*
 * RapidEnv: ConfigExprParser.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 03/31/2011
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

import java.util.logging.Level;

import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.EnvProperty;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * The parser for configuration expressions.
 * 
 * @author Martin Bluemel
 */
public final class ConfigExprParser {

	// integer constants for the expression perser's state
	private enum State {
		basic, envvar, function, literal
	}

	/**
	 * the parser method for a ConfigfileChange Expression. Builds a tree of
	 * configuration expression instances.
	 * 
	 * @param expr
	 *            the configuration expression to construct
	 * @param enclosingUnit
	 *            the enclosing install unit
	 * @param enclosingProp
	 *            the enclosing property
	 * @param expressionString
	 *            - the string with the expression to parse.
	 * @param escapeLiterals
	 *            if escaping literals is desired or not
	 */
	public static final void parse(final ConfigExpr expr, final Installunit enclosingUnit,
	        final EnvProperty enclosingProp, final String expressionString, final boolean escapeLiterals) {
		int len = expressionString.length();
		char c;
		ConfigExprSplitResult splitResult = null;
		StringBuffer text = new StringBuffer();
		StringBuffer funcContent = new StringBuffer();
		StringBuffer literal = new StringBuffer();
		int braceCounter = 0;
		State state = State.basic;
		for (int i = 0; i < len; i++) {
			c = expressionString.charAt(i);
			switch (state) {

			case basic:
				switch (c) {
				case '$':
					if ((i + 1 < len) && expressionString.charAt(i + 1) != '{') {
						text.append(c);
						break;
					}
					if (text.length() > 0) {
						expr.addChild(new ConfigExprStringLiteral(enclosingUnit, enclosingProp, text.toString()));
						text.setLength(0);
					}
					state = State.envvar;
					break;
				case '(':
					if (text.length() > 0) {
						splitResult = splitFuncname(text.toString());
						if (splitResult.getFuncname() == null || splitResult.getFuncname().length() == 0) {
							throw new RapidEnvException("parsed '(' without preceeding function name");
						}
						if ((splitResult.getBefore() != null) && splitResult.getBefore().length() > 0) {
							expr.addChild(new ConfigExprStringLiteral(enclosingUnit, enclosingProp, splitResult
							        .getBefore()));
						}
						text.setLength(0);
					} else {
						throw new RapidEnvException("parsed '(' without preceeding function name");
					}
					braceCounter = 0;
					state = State.function;
					break;
				case '\'':
					if (text.length() > 0) {
						expr.addChild(new ConfigExprStringLiteral(enclosingUnit, enclosingProp, text.toString()));
						text.setLength(0);
					}
					state = State.literal;
					break;
				default:
					text.append(c);
					break;
				}
				break;

			case envvar:
				switch (c) {
				case '{':
					final int iEnd = countToClosingBrace('{', '}', expressionString, i + 1);
					final ConfigExpr subexpression = new ConfigExprTopLevel(enclosingUnit, enclosingProp,
					        expressionString.substring(i + 1, i + iEnd), escapeLiterals);
					final ConfigExprProperty propExpression = new ConfigExprProperty(enclosingUnit, enclosingProp,
					        subexpression, escapeLiterals);
					expr.addChild(propExpression);
					state = State.basic;
					i += iEnd;
					break;
				default:
					// this case never should happen as it is excluded before
					throw new AssertionError("Unexpected character '" + c + "' in expression \"" + expressionString
					        + "\" at position " + i);
				}
				break;

			case function:
				switch (c) {
				case ')':
					if (braceCounter > 0) {
						braceCounter--;
						funcContent.append(c);
					} else {
						try {
							final ConfigExprFunction funcExpr = ConfigExprFunction.createInstance(
							        expr.getEnclosingInstallUnit(), expr.getEnclosingProperty(),
							        splitResult.getFuncname(), funcContent.toString(), escapeLiterals);
							expr.addChild(funcExpr);
						} catch (RapidEnvException e) {
							if (e.getCause() != null && e.getCause() instanceof ClassNotFoundException) {
								StringBuffer literalPushback = new StringBuffer();
								literalPushback.append(splitResult.getFuncname());
								if (splitResult.getWhitespacesAfter() != null) {
									literalPushback.append(splitResult.getWhitespacesAfter());
								}
								literalPushback.append('(');
								literalPushback.append(funcContent.toString());
								literalPushback.append(')');
								final ConfigExprStringLiteral literalExpr = new ConfigExprStringLiteral(
								        expr.getEnclosingInstallUnit(), expr.getEnclosingProperty(),
								        literalPushback.toString());
								expr.addChild(literalExpr);
								String funcname = e.getCause().getMessage();
								final String msg = e.getCause().getMessage();
								final int pos = msg.indexOf("ConfigExprFunction");
								if (pos != -1) {
									funcname = msg.substring(pos + "ConfigExprFunction".length());
								}
								final String message = "No interpreter class found for function \"" + funcname
								        + "()\".";
								RapidEnvInterpreter.log(Level.FINE, message);
							} else {
								throw e;
							}
						}
						funcContent.setLength(0);
						state = State.basic;
					}
					break;
				case '(':
					braceCounter++;
					funcContent.append(c);
					break;
				default:
					funcContent.append(c);
					break;
				}
				break;

			case literal:
				switch (c) {
				case '\'':
					if (literal.length() > 0) {
						expr.addChild(new ConfigExprStringLiteral(enclosingUnit, enclosingProp, literal.toString()));
						literal.setLength(0);
					}
					state = State.basic;
					break;
				case '\\':
					if (escapeLiterals) {
						if (i < len - 1) {
							final char c1 = expressionString.charAt(i + 1);
							switch (c1) {
							case '\'':
							case '\\':
								literal.append(c1);
								i++;
								break;
							case 'n':
								literal.append('\n');
								i++;
								break;
							case 'r':
								literal.append('\r');
								i++;
								break;
							case 't':
								literal.append('\t');
								i++;
								break;
							default:
								literal.append(c);
								break;
							}
						} else {
							literal.append(c);
						}
					} else {
						literal.append(c);
					}
					break;
				default:
					literal.append(c);
					break;
				}
				break;
			default:
				throw new RuntimeException("unexpected state " + state);
			}
		}
		if (text.length() > 0) {
			expr.addChild(new ConfigExprStringLiteral(enclosingUnit, enclosingProp, text.toString()));
			text.setLength(0);
		}
	}

	private static int countToClosingBrace(final char openingBrace, final char closingBrace,
	        final String exprSubstring, final int start) {
		int indexCount = 0;
		int braceCount = 0;
		char c;
		final int len = exprSubstring.length();
		for (int i = start; i < len; i++) {
			indexCount++;
			c = exprSubstring.charAt(i);
			if (c == openingBrace) {
				braceCount++;
			} else if (c == closingBrace) {
				if (braceCount > 0) {
					braceCount--;
				} else {
					i = len;
				}
			}
		}
		if (braceCount > 0) {
			throw new RapidEnvConfigurationException("exected \"" + closingBrace + "\"");
		}
		return indexCount;
	}

	/**
	 * Extracts the function name out of a whole function expression string. For
	 * instance: "xxx(yyy, zzz)" -> "xxx".
	 * 
	 * @param s
	 *            - function name
	 * 
	 * @return - splitted String list
	 */
	protected static ConfigExprSplitResult splitFuncname(final String s) {
		final int len = s.length();
		final StringBuffer sbBefore = new StringBuffer();
		final StringBuffer sbToken = new StringBuffer();
		final StringBuffer sbWs = new StringBuffer();
		int state = 0;
		for (int i = 0; i < len; i++) {
			final char c = s.charAt(i);
			switch (state) {
			case 0:
				if (c == ' ' || c == '\n' || c == '\t') {
					if (sbWs.length() > 0) {
						sbBefore.append(sbWs);
					}
					sbWs.setLength(0);
					sbWs.append(c);
					state = 1;
				} else {
					sbToken.append(c);
				}
				break;
			case 1:
				if (c == ' ' || c == '\n' || c == '\t') {
					sbWs.append(c);
				} else {
					if (sbToken.length() > 0) {
						sbBefore.append(sbToken);
					}
					sbToken.setLength(0);
					sbToken.append(c);
					state = 0;
				}
				break;
			}
		}
		final ConfigExprSplitResult splitResult = new ConfigExprSplitResult();
		if (sbWs.length() > 0) {
			if (state == 0) {
				sbBefore.append(sbWs.toString());
			} else {
				splitResult.setWhitespacesAfter(sbWs.toString());
			}
		}
		if (sbBefore.length() > 0) {
			splitResult.setBefore(sbBefore.toString());
		}
		if (sbToken.length() > 0) {
			splitResult.setFuncname(sbToken.toString());
		}
		return splitResult;
	}
}
