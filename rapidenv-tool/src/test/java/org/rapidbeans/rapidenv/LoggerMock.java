/*
 * RapidEnv: LoggerMock.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 07/12/2010
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Mock class for java.util.Logger. Extends the Logger class and so acts like a
 * logger. Encapsulates an original logger but keeps all log records in memory
 */
public class LoggerMock extends Logger {

	private Logger logger = null;

	private List<LogRecord> logRecords = new ArrayList<LogRecord>();

	private boolean suppressLogging = true;

	/**
	 * @param suppressLogging the suppressLogging to set
	 */
	public void setSuppressLogging(boolean suppressLogging) {
		this.suppressLogging = suppressLogging;
	}

	/**
	 * @return the mock's logRecords.
	 */
	public List<LogRecord> getLogRecords() {
		return logRecords;
	}

	private LoggerMock(final Logger internalLogger) {
		super(internalLogger.getName(), internalLogger.getResourceBundleName());
		this.logger = internalLogger;
	}

	/**
	 * Find or create a logger for a named subsystem. If a logger has already been
	 * created with the given name it is returned. Otherwise a new logger is
	 * created.
	 * <p>
	 * If a new logger is created its log level will be configured based on the
	 * LogManager configuration and it will configured to also send logging output
	 * to its parent's handlers. It will be registered in the LogManager global
	 * namespace.
	 *
	 * @param name A name for the logger. This should be a dot-separated name and
	 *             should normally be based on the package name or class name of the
	 *             subsystem, such as java.net or javax.swing
	 * @return a suitable Logger
	 * @throws NullPointerException if the name is null.
	 */
	public static synchronized LoggerMock getLogger(String name) {
		return new LoggerMock(Logger.getLogger(name));
	}

	/**
	 * Find or create a logger for a named subsystem. If a logger has already been
	 * created with the given name it is returned. Otherwise a new logger is
	 * created.
	 * <p>
	 * If a new logger is created its log level will be configured based on the
	 * LogManager and it will configured to also send logging output to its parent
	 * loggers Handlers. It will be registered in the LogManager global namespace.
	 * <p>
	 * If the named Logger already exists and does not yet have a localization
	 * resource bundle then the given resource bundle name is used. If the named
	 * Logger already exists and has a different resource bundle name then an
	 * IllegalArgumentException is thrown.
	 * <p>
	 *
	 * @param name               A name for the logger. This should be a
	 *                           dot-separated name and should normally be based on
	 *                           the package name or class name of the subsystem,
	 *                           such as java.net or javax.swing
	 * @param resourceBundleName name of ResourceBundle to be used for localizing
	 *                           messages for this logger. May be <CODE>null</CODE>
	 *                           if none of the messages require localization.
	 * @return a suitable Logger
	 * @throws MissingResourceException if the named ResourceBundle cannot be found.
	 * @throws IllegalArgumentException if the Logger already exists and uses a
	 *                                  different resource bundle name.
	 * @throws NullPointerException     if the name is null.
	 */
	public static synchronized LoggerMock getLogger(String name, String resourceBundleName) {
		return new LoggerMock(Logger.getLogger(name, resourceBundleName));
	}

	/**
	 * Create an anonymous Logger. The newly created Logger is not registered in the
	 * LogManager namespace. There will be no access checks on updates to the
	 * logger.
	 * <p>
	 * This factory method is primarily intended for use from applets. Because the
	 * resulting Logger is anonymous it can be kept private by the creating class.
	 * This removes the need for normal security checks, which in turn allows
	 * untrusted applet code to update the control state of the Logger. For example
	 * an applet can do a setLevel or an addHandler on an anonymous Logger.
	 * <p>
	 * Even although the new logger is anonymous, it is configured to have the root
	 * logger ("") as its parent. This means that by default it inherits its
	 * effective level and handlers from the root logger.
	 * <p>
	 *
	 * @return a newly created private Logger
	 */
	public static synchronized LoggerMock getAnonymousLogger() {
		return new LoggerMock(Logger.getAnonymousLogger());
	}

	/**
	 * Create an anonymous Logger. The newly created Logger is not registered in the
	 * LogManager namespace. There will be no access checks on updates to the
	 * logger.
	 * <p>
	 * This factory method is primarily intended for use from applets. Because the
	 * resulting Logger is anonymous it can be kept private by the creating class.
	 * This removes the need for normal security checks, which in turn allows
	 * untrusted applet code to update the control state of the Logger. For example
	 * an applet can do a setLevel or an addHandler on an anonymous Logger.
	 * <p>
	 * Even although the new logger is anonymous, it is configured to have the root
	 * logger ("") as its parent. This means that by default it inherits its
	 * effective level and handlers from the root logger.
	 * <p>
	 *
	 * @param resourceBundleName name of ResourceBundle to be used for localizing
	 *                           messages for this logger. May be null if none of
	 *                           the messages require localization.
	 * @return a newly created private Logger
	 * @throws MissingResourceException if the named ResourceBundle cannot be found.
	 */
	public static synchronized LoggerMock getAnonymousLogger(String resourceBundleName) {
		return new LoggerMock(Logger.getAnonymousLogger(resourceBundleName));
	}

	/**
	 * Retrieve the localization resource bundle for this logger for the current
	 * default locale. Note that if the result is null, then the Logger will use a
	 * resource bundle inherited from its parent.
	 *
	 * @return localization bundle (may be null)
	 */
	public ResourceBundle getResourceBundle() {
		return logger.getResourceBundle();
	}

	/**
	 * Retrieve the localization resource bundle name for this logger. Note that if
	 * the result is null, then the Logger will use a resource bundle name inherited
	 * from its parent.
	 *
	 * @return localization bundle name (may be null)
	 */
	public String getResourceBundleName() {
		return logger.getResourceBundleName();
	}

	/**
	 * Set a filter to control output on this Logger.
	 * <P>
	 * After passing the initial "level" check, the Logger will call this Filter to
	 * check if a log record should really be published.
	 *
	 * @param newFilter a filter object (may be null)
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public void setFilter(Filter newFilter) throws SecurityException {
		logger.setFilter(newFilter);
	}

	/**
	 * Get the current filter for this Logger.
	 *
	 * @return a filter object (may be null)
	 */
	public Filter getFilter() {
		return logger.getFilter();
	}

	/**
	 * Log a LogRecord.
	 * <p>
	 * All the other logging methods in this class call through this method to
	 * actually perform any logging. Subclasses can override this single method to
	 * capture all log activity.
	 *
	 * @param record the LogRecord to be published
	 */
	public void log(LogRecord record) {
		if (record.getLevel().intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		synchronized (this) {
			if (logger.getFilter() != null && !logger.getFilter().isLoggable(record)) {
				return;
			}
		}
		this.logRecords.add(record);
		if (!this.suppressLogging) {
			logger.log(record);
		}
	}

	// ================================================================
	// Start of convenience methods WITHOUT className and methodName
	// ================================================================

	/**
	 * Log a message, with no arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param level One of the message level identifiers, e.g. SEVERE
	 * @param msg   The string message (or a key in the message catalog)
	 */
	public void log(Level level, String msg) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		doLog(lr);
	}

	/**
	 * Log a message, with one object parameter.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 *
	 * @param level  One of the message level identifiers, e.g. SEVERE
	 * @param msg    The string message (or a key in the message catalog)
	 * @param param1 parameter to the message
	 */
	public void log(Level level, String msg, Object param1) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		Object params[] = { param1 };
		lr.setParameters(params);
		doLog(lr);
	}

	/**
	 * Log a message, with an array of object arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 *
	 * @param level  One of the message level identifiers, e.g. SEVERE
	 * @param msg    The string message (or a key in the message catalog)
	 * @param params array of parameters to the message
	 */
	public void log(Level level, String msg, Object params[]) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setParameters(params);
		doLog(lr);
	}

	/**
	 * Log a message, with associated Throwable information.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * arguments are stored in a LogRecord which is forwarded to all registered
	 * output handlers.
	 * <p>
	 * Note that the thrown argument is stored in the LogRecord thrown property,
	 * rather than the LogRecord parameters property. Thus is it processed specially
	 * by output Formatters and is not treated as a formatting parameter to the
	 * LogRecord message property.
	 * <p>
	 *
	 * @param level  One of the message level identifiers, e.g. SEVERE
	 * @param msg    The string message (or a key in the message catalog)
	 * @param thrown Throwable associated with log message.
	 */
	public void log(Level level, String msg, Throwable thrown) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setThrown(thrown);
		doLog(lr);
	}

	// ================================================================
	// Start of convenience methods WITH className and methodName
	// ================================================================

	/**
	 * Log a message, specifying source class and method, with no arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param msg          The string message (or a key in the message catalog)
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		doLog(lr);
	}

	/**
	 * Log a message, specifying source class and method, with a single object
	 * parameter to the log message.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param msg          The string message (or a key in the message catalog)
	 * @param param1       Parameter to the log message.
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		Object params[] = { param1 };
		lr.setParameters(params);
		doLog(lr);
	}

	/**
	 * Log a message, specifying source class and method, with an array of object
	 * arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param msg          The string message (or a key in the message catalog)
	 * @param params       Array of parameters to the message
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object params[]) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		lr.setParameters(params);
		doLog(lr);
	}

	/**
	 * Log a message, specifying source class and method, with associated Throwable
	 * information.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * arguments are stored in a LogRecord which is forwarded to all registered
	 * output handlers.
	 * <p>
	 * Note that the thrown argument is stored in the LogRecord thrown property,
	 * rather than the LogRecord parameters property. Thus is it processed specially
	 * by output Formatters and is not treated as a formatting parameter to the
	 * LogRecord message property.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param msg          The string message (or a key in the message catalog)
	 * @param thrown       Throwable associated with log message.
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		lr.setThrown(thrown);
		doLog(lr);
	}

	// =========================================================================
	// Start of convenience methods WITH className, methodName and bundle name.
	// =========================================================================

	/**
	 * Log a message, specifying source class, method, and resource bundle name with
	 * no arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 * The msg string is localized using the named resource bundle. If the resource
	 * bundle name is null, or an empty String or invalid then the msg string is not
	 * localized.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param bundleName   name of resource bundle to localize msg, can be null
	 * @param msg          The string message (or a key in the message catalog)
	 */

	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		doLog(lr, bundleName);
	}

	/**
	 * Log a message, specifying source class, method, and resource bundle name,
	 * with a single object parameter to the log message.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 * The msg string is localized using the named resource bundle. If the resource
	 * bundle name is null, or an empty String or invalid then the msg string is not
	 * localized.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param bundleName   name of resource bundle to localize msg, can be null
	 * @param msg          The string message (or a key in the message catalog)
	 * @param param1       Parameter to the log message.
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
			Object param1) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		Object params[] = { param1 };
		lr.setParameters(params);
		doLog(lr, bundleName);
	}

	/**
	 * Log a message, specifying source class, method, and resource bundle name,
	 * with an array of object arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then a
	 * corresponding LogRecord is created and forwarded to all the registered output
	 * Handler objects.
	 * <p>
	 * The msg string is localized using the named resource bundle. If the resource
	 * bundle name is null, or an empty String or invalid then the msg string is not
	 * localized.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param bundleName   name of resource bundle to localize msg, can be null.
	 * @param msg          The string message (or a key in the message catalog)
	 * @param params       Array of parameters to the message
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
			Object params[]) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		lr.setParameters(params);
		doLog(lr, bundleName);
	}

	/**
	 * Log a message, specifying source class, method, and resource bundle name,
	 * with associated Throwable information.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * arguments are stored in a LogRecord which is forwarded to all registered
	 * output handlers.
	 * <p>
	 * The msg string is localized using the named resource bundle. If the resource
	 * bundle name is null, or an empty String or invalid then the msg string is not
	 * localized.
	 * <p>
	 * Note that the thrown argument is stored in the LogRecord thrown property,
	 * rather than the LogRecord parameters property. Thus is it processed specially
	 * by output Formatters and is not treated as a formatting parameter to the
	 * LogRecord message property.
	 * <p>
	 *
	 * @param level        One of the message level identifiers, e.g. SEVERE
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that issued the logging request
	 * @param bundleName   name of resource bundle to localize msg, can be null
	 * @param msg          The string message (or a key in the message catalog)
	 * @param thrown       Throwable associated with log message.
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
			Throwable thrown) {
		if (level.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(level, msg);
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		lr.setThrown(thrown);
		doLog(lr, bundleName);
	}

	// ======================================================================
	// Start of convenience methods for logging method entries and returns.
	// ======================================================================

	/**
	 * Log a method entry.
	 * <p>
	 * This is a convenience method that can be used to log entry to a method. A
	 * LogRecord with message "ENTRY", log level FINER, and the given sourceMethod
	 * and sourceClass is logged.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that is being entered
	 */
	public void entering(String sourceClass, String sourceMethod) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		logp(Level.FINER, sourceClass, sourceMethod, "ENTRY");
	}

	/**
	 * Log a method entry, with one parameter.
	 * <p>
	 * This is a convenience method that can be used to log entry to a method. A
	 * LogRecord with message "ENTRY {0}", log level FINER, and the given
	 * sourceMethod, sourceClass, and parameter is logged.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that is being entered
	 * @param param1       parameter to the method being entered
	 */
	public void entering(String sourceClass, String sourceMethod, Object param1) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		Object params[] = { param1 };
		logp(Level.FINER, sourceClass, sourceMethod, "ENTRY {0}", params);
	}

	/**
	 * Log a method entry, with an array of parameters.
	 * <p>
	 * This is a convenience method that can be used to log entry to a method. A
	 * LogRecord with message "ENTRY" (followed by a format {N} indicator for each
	 * entry in the parameter array), log level FINER, and the given sourceMethod,
	 * sourceClass, and parameters is logged.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of method that is being entered
	 * @param params       array of parameters to the method being entered
	 */
	public void entering(String sourceClass, String sourceMethod, Object params[]) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		String msg = "ENTRY";
		if (params == null) {
			logp(Level.FINER, sourceClass, sourceMethod, msg);
			return;
		}
		for (int i = 0; i < params.length; i++) {
			msg = msg + " {" + i + "}";
		}
		logp(Level.FINER, sourceClass, sourceMethod, msg, params);
	}

	/**
	 * Log a method return.
	 * <p>
	 * This is a convenience method that can be used to log returning from a method.
	 * A LogRecord with message "RETURN", log level FINER, and the given
	 * sourceMethod and sourceClass is logged.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of the method
	 */
	public void exiting(String sourceClass, String sourceMethod) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		logp(Level.FINER, sourceClass, sourceMethod, "RETURN");
	}

	/**
	 * Log a method return, with result object.
	 * <p>
	 * This is a convenience method that can be used to log returning from a method.
	 * A LogRecord with message "RETURN {0}", log level FINER, and the gives
	 * sourceMethod, sourceClass, and result object is logged.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of the method
	 * @param result       Object that is being returned
	 */
	public void exiting(String sourceClass, String sourceMethod, Object result) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		logp(Level.FINER, sourceClass, sourceMethod, "RETURN {0}", result);
	}

	/**
	 * Log throwing an exception.
	 * <p>
	 * This is a convenience method to log that a method is terminating by throwing
	 * an exception. The logging is done using the FINER level.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given
	 * arguments are stored in a LogRecord which is forwarded to all registered
	 * output handlers. The LogRecord's message is set to "THROW".
	 * <p>
	 * Note that the thrown argument is stored in the LogRecord thrown property,
	 * rather than the LogRecord parameters property. Thus is it processed specially
	 * by output Formatters and is not treated as a formatting parameter to the
	 * LogRecord message property.
	 * <p>
	 *
	 * @param sourceClass  name of class that issued the logging request
	 * @param sourceMethod name of the method.
	 * @param thrown       The Throwable that is being thrown.
	 */
	public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
		if (Level.FINER.intValue() < this.logger.getLevel().intValue()
				|| this.logger.getLevel().intValue() == Level.OFF.intValue()) {
			return;
		}
		LogRecord lr = new LogRecord(Level.FINER, "THROW");
		lr.setSourceClassName(sourceClass);
		lr.setSourceMethodName(sourceMethod);
		lr.setThrown(thrown);
		doLog(lr);
	}

	// =======================================================================
	// Start of simple convenience methods using level names as method names
	// =======================================================================

	/**
	 * Log a SEVERE message.
	 * <p>
	 * If the logger is currently enabled for the SEVERE message level then the
	 * given message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void severe(String msg) {
		if (Level.SEVERE.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.SEVERE, msg);
	}

	/**
	 * Log a WARNING message.
	 * <p>
	 * If the logger is currently enabled for the WARNING message level then the
	 * given message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void warning(String msg) {
		if (Level.WARNING.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.WARNING, msg);
	}

	/**
	 * Log an INFO message.
	 * <p>
	 * If the logger is currently enabled for the INFO message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void info(String msg) {
		if (Level.INFO.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.INFO, msg);
	}

	/**
	 * Log a CONFIG message.
	 * <p>
	 * If the logger is currently enabled for the CONFIG message level then the
	 * given message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void config(String msg) {
		if (Level.CONFIG.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.CONFIG, msg);
	}

	/**
	 * Log a FINE message.
	 * <p>
	 * If the logger is currently enabled for the FINE message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void fine(String msg) {
		if (Level.FINE.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.FINE, msg);
	}

	/**
	 * Log a FINER message.
	 * <p>
	 * If the logger is currently enabled for the FINER message level then the given
	 * message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void finer(String msg) {
		if (Level.FINER.intValue() < logger.getLevel().intValue()) {
			return;
		}
		log(Level.FINER, msg);
	}

	/**
	 * Log a FINEST message.
	 * <p>
	 * If the logger is currently enabled for the FINEST message level then the
	 * given message is forwarded to all the registered output Handler objects.
	 * <p>
	 *
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void finest(String msg) {
		if (Level.FINEST.intValue() < this.logger.getLevel().intValue()) {
			return;
		}
		log(Level.FINEST, msg);
	}

	// ================================================================
	// End of convenience methods
	// ================================================================

	/**
	 * Set the log level specifying which message levels will be logged by this
	 * logger. Message levels lower than this value will be discarded. The level
	 * value Level.OFF can be used to turn off logging.
	 * <p>
	 * If the new level is null, it means that this node should inherit its level
	 * from its nearest ancestor with a specific (non-null) level value.
	 *
	 * @param newLevel the new value for the log level (may be null)
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public void setLevel(final Level newLevel) throws SecurityException {
		logger.setLevel(newLevel);
	}

	/**
	 * Get the log Level that has been specified for this Logger. The result may be
	 * null, which means that this logger's effective level will be inherited from
	 * its parent.
	 *
	 * @return this Logger's level
	 */
	public Level getLevel() {
		return logger.getLevel();
	}

	/**
	 * Check if a message of the given level would actually be logged by this
	 * logger. This check is based on the Loggers effective level, which may be
	 * inherited from its parent.
	 *
	 * @param level a message logging level
	 * @return true if the given message level is currently being logged.
	 */
	public boolean isLoggable(Level level) {
		return logger.isLoggable(level);
	}

	/**
	 * Get the name for this logger.
	 *
	 * @return logger name. Will be null for anonymous Loggers.
	 */
	public String getName() {
		return logger.getName();
	}

	/**
	 * Add a log Handler to receive logging messages.
	 * <p>
	 * By default, Loggers also send their output to their parent logger. Typically
	 * the root Logger is configured with a set of Handlers that essentially act as
	 * default handlers for all loggers.
	 *
	 * @param handler a logging Handler
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public synchronized void addHandler(final Handler handler) throws SecurityException {
		logger.addHandler(handler);
	}

	/**
	 * Remove a log Handler.
	 * <P>
	 * Returns silently if the given Handler is not found or is null
	 *
	 * @param handler a logging Handler
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public synchronized void removeHandler(Handler handler) throws SecurityException {
	}

	/**
	 * Get the Handlers associated with this logger.
	 * <p>
	 *
	 * @return an array of all registered Handlers
	 */
	public synchronized Handler[] getHandlers() {
		return logger.getHandlers();
	}

	/**
	 * Specify whether or not this logger should send its output to it's parent
	 * Logger. This means that any LogRecords will also be written to the parent's
	 * Handlers, and potentially to its parent, recursively up the namespace.
	 *
	 * @param useParentHandlers true if output is to be sent to the logger's parent.
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public synchronized void setUseParentHandlers(boolean useParentHandlers) {
		logger.setUseParentHandlers(useParentHandlers);
	}

	/**
	 * Discover whether or not this logger is sending its output to its parent
	 * logger.
	 *
	 * @return true if output is to be sent to the logger's parent
	 */
	public synchronized boolean getUseParentHandlers() {
		return logger.getUseParentHandlers();
	}

	/**
	 * Return the parent for this Logger.
	 * <p>
	 * This method returns the nearest extant parent in the namespace. Thus if a
	 * Logger is called "a.b.c.d", and a Logger called "a.b" has been created but no
	 * logger "a.b.c" exists, then a call of getParent on the Logger "a.b.c.d" will
	 * return the Logger "a.b".
	 * <p>
	 * The result will be null if it is called on the root Logger in the namespace.
	 *
	 * @return nearest existing parent Logger
	 */
	public Logger getParent() {
		return this.logger.getParent();
	}

	/**
	 * Set the parent for this Logger. This method is used by the LogManager to
	 * update a Logger when the namespace changes.
	 * <p>
	 * It should not be called from application code.
	 * <p>
	 *
	 * @param parent the new parent logger
	 * @exception SecurityException if a security manager exists and if the caller
	 *                              does not have LoggingPermission("control").
	 */
	public void setParent(Logger parent) {
		this.logger.setParent(parent);
	}

	// private support method for logging.
	// We fill in the logger name, resource bundle name, and
	// resource bundle and then call "void log(LogRecord)".
	private void doLog(final LogRecord lr) {
		lr.setLoggerName(this.logger.getName());
		String ebname = getEffectiveResourceBundleName();
		if (ebname != null) {
			lr.setResourceBundleName(ebname);
			lr.setResourceBundle(findResourceBundle(ebname));
		}
		log(lr);
	}

	// Private support method for logging for "logrb" methods.
	// We fill in the logger name, resource bundle name, and
	// resource bundle and then call "void log(LogRecord)".
	private void doLog(LogRecord lr, String rbname) {
		lr.setLoggerName(this.logger.getName());
		if (rbname != null) {
			lr.setResourceBundleName(rbname);
			lr.setResourceBundle(findResourceBundle(rbname));
		}
		log(lr);
	}

	// Private utility method to map a resource bundle name to an
	// actual resource bundle, using a simple one-entry cache.
	// Returns null for a null name.
	// May also return null if we can't find the resource bundle and
	// there is no suitable previous cached value.

	private ResourceBundle catalog; // Cached resource bundle

	private String catalogName; // name associated with catalog

	private Locale catalogLocale; // locale associated with catalog

	private synchronized ResourceBundle findResourceBundle(String name) {

		// Return a null bundle for a null name.
		// (garbage in - garbage out
		if (name == null) {
			return null;
		}

		Locale currentLocale = Locale.getDefault();

		// Normally we should hit on our simple one entry cache.
		if (catalog != null && currentLocale == catalogLocale && name == catalogName) {
			return catalog;
		}

		// Use the thread's context ClassLoader. If there isn't one,
		// use the SystemClassloader.
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		try {
			catalog = ResourceBundle.getBundle(name, currentLocale, cl);
			catalogName = name;
			catalogLocale = currentLocale;
			return catalog;
		} catch (MissingResourceException ex) {
			// Woops. We can't find the ResourceBundle in the default
			// ClassLoader. Drop through.
		}

		// Fall back to searching up the call stack and trying each
		// calling ClassLoader.
		for (int ix = 0;; ix++) {
			@SuppressWarnings({ "deprecation", "restriction" })
			Class<?> clz = sun.reflect.Reflection.getCallerClass(ix);
			if (clz == null) {
				break;
			}
			ClassLoader cl2 = clz.getClassLoader();
			if (cl2 == null) {
				cl2 = ClassLoader.getSystemClassLoader();
			}
			if (cl == cl2) {
				// We've already checked this class loader.
				continue;
			}
			cl = cl2;
			try {
				catalog = ResourceBundle.getBundle(name, currentLocale, cl);
				catalogName = name;
				catalogLocale = currentLocale;
				return catalog;
			} catch (MissingResourceException ex) {
				// Ok, this one didn't work either.
				// Drop through, and try the next one.
			}
		}

		if (name.equals(catalogName)) {
			// Return the previous cached value for that name.
			// This may be null.
			return catalog;
		}
		// Sorry, we're out of luck.
		return null;
	}

	// Private method to get the potentially inherited
	// resource bundle name for this Logger.
	// May return null
	private String getEffectiveResourceBundleName() {
		Logger target = this;
		while (target != null) {
			String rbn = target.getResourceBundleName();
			if (rbn != null) {
				return rbn;
			}
			target = target.getParent();
		}
		return null;
	}
}
