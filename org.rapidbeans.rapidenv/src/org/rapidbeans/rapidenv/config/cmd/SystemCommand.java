/*
 * RapidEnv: SystemCommand.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/30/2010
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.EscapeMap;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.Installunit;


/**
 * A system command to execute.
 */
public class SystemCommand extends RapidBeanBaseSystemCommand {


	public static File getExecutableAsFileStat(final String exe,
			final String pathextension,
			final RapidEnvInterpreter interpreter,
			final Installunit enclosingUnit) {
		File file = null;
		if (exe != null) {
			String path = exe;
			if (interpreter != null) {
				path = interpreter.interpret(enclosingUnit, null, exe);
			}
			file = new File(path);
			String pathEnvvarname = null;
			switch (PlatformHelper.getOs()) {
			case windows:
				pathEnvvarname = "Path";
				break;
			case linux:
				pathEnvvarname = "PATH";
				break;
			default:
				throw new RapidEnvException("Platform \""
						+ PlatformHelper.getOs().name() + "\" not supported!");
			}
			String cmdpathstring = System.getenv(pathEnvvarname);
			if (pathextension != null) {
				cmdpathstring = pathextension
						+ File.pathSeparatorChar + cmdpathstring;
			}
			if (!file.isAbsolute()) {
				final List<String> cmdpath = StringHelper.split(
						cmdpathstring, File.pathSeparator);
				for (final String cmdpathelem : cmdpath) {
					if (new File(cmdpathelem + File.separator + file.getPath()).exists()) {
						file = new File(cmdpathelem + File.separator + file.getPath());
						break;
					}
				}
			}
			if (!file.exists()) {
				throw new RapidEnvException("Executable file \""
						+ file.getAbsolutePath() + "\" not found");
			}
			if (!file.canExecute()) {
				throw new RapidEnvException("No execution rights"
						+ " for executable file \""
						+ file.getAbsolutePath() + "\"");
			}
		}
		return file;
	}

	/**
	 * Check if the file configuration has been performed properly or not
	 *
	 * @param execute if false only execute the check if the configuration is neccessary
	 *                if true execute the configuration if necessary
	 *
	 * @return if the configuration has been performed properly or not
	 */
	public boolean check(final boolean execute) {
		try {
			boolean ok = false;
			if (getVerifycmds() != null && getVerifycmds().size() > 0) {
				ok = true;
				for (final SystemCommand cmd : getVerifycmds()) {
					try {
						RapidEnvInterpreter.log(Level.FINER, "Executing verify command "
								+ cmd.getClass().getName() + "::" + cmd.getCommandline());
						cmd.execute();
						RapidEnvInterpreter.log(Level.FINER, "verify command succeeded: "
								+ cmd.getClass().getName() + "::" + cmd.getCommandline());
					} catch (RuntimeException e) {
						RapidEnvInterpreter.log(Level.FINER, "verify command failed: "
								+ cmd.getClass().getName() + "::" + cmd.getCommandline());
						ok = false;
						break;
					}
				}
			}
			if (execute) {
				if (ok) {
					final String msg = "command: " + getCommandline() + "\n    does not need to be executed";
					RapidEnvInterpreter.log(Level.FINE, msg);
				} else {
					execute();
					// if no exception has been thrown
					ok = true;
					final String msg = "command: " + getCommandline() + "\n    has been executed successfully";
					RapidEnvInterpreter.log(Level.FINE, msg);
				}
			} else {
				if (ok) {
					final String msg = "command: " + getCommandline() + "\n    does not need to be executed";
					RapidEnvInterpreter.log(Level.FINE, msg);
				} else {
					final String msg = "command: " + getCommandline() + "\n    should be executed";
					setIssue(msg);
					RapidEnvInterpreter.log(Level.FINE, msg);
				}
			}
			this.setOk(ok);
			return ok;
		} catch (RuntimeException e) {
			this.setOk(false);
			throw e;
		}
	}

	/**
	 * Runs the command in the OS environment.
	 * 
	 * @return the result containing the return value, stdout and stderr
	 */
	public CommandExecutionResult execute() {

		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		RapidEnvInterpreter.log(Level.FINE,
				"executing system command: " + this.getClass());
		RapidEnvInterpreter.log(Level.FINE,
				"command.silent = " + getSilent());
		final String cmdline = getCommandline();
		final String[] cmdarray = getCommandArray();
		RapidEnvInterpreter.log(Level.FINE,
				"executing system command: " + cmdline);
		final StringBuffer bufOut = new StringBuffer();
		final StringBuffer bufErr = new StringBuffer();
		int ret = 0;
		try {
			if (getMessagestart() != null && interpreter != null) {
				interpreter.getOut().println(getMessagestart());
			}
			final Process proc = Runtime.getRuntime().exec(
					cmdarray, null, getWorkingdirAsFile());
			if (this.getInput() != null) {
				final OutputStream out = proc.getOutputStream();
				out.write(this.getInput().getBytes());
				out.close();
			}
			if (!getAsync()) {
				final CommandStreamReader rdOut = new CommandStreamReader(
						cmdline, proc.getInputStream(), System.out, bufOut);
				final CommandStreamReader rdErr = new CommandStreamReader(
						cmdline, proc.getErrorStream(), System.err, bufErr);
				rdOut.start();
				rdErr.start();
				ret = proc.waitFor();
				while (rdOut.isAlive() || rdErr.isAlive()) {
					Thread.sleep(100);
				}
				if (getMessagesuccess() != null && interpreter != null) {
					interpreter.getOut().println(getMessagesuccess());
				}
				checkReturn(ret, cmdline);
				checkStdout(bufOut.toString(), cmdline);
				checkStderr(bufErr.toString(), cmdline);
				RapidEnvInterpreter.log(Level.FINE,
						"finished execution of system command successfully: "
								+ cmdline);
			}
		} catch (InterruptedException e) {
			if (getMessagefailure() != null && interpreter != null) {
				interpreter.getErr().println(
						"Exceptional problem during command execution:\n"
								+ getMessagefailure());
			}
			throw new RapidEnvException("Exception during execution of"
					+ " system command \"" + cmdline + "\"", e);
		} catch (IOException e) {
			if (getMessagefailure() != null && interpreter != null) {
				interpreter.getErr().println(
						"Exceptional problem during command execution:\n"
								+ getMessagefailure());
			}
			throw new RapidEnvException("Exception during execution of"
					+ " system command \"" + cmdline + "\"", e);
		} catch (RuntimeException e) {
			if (getMessagefailure() != null && interpreter != null) {
				interpreter.getErr().println(
						"Exceptional problem during command execution:\n"
								+ getMessagefailure());
			}
			throw e;
		}
		return new CommandExecutionResult(
				bufOut.toString(), bufErr.toString(), ret);
	}

	/**
	 * Check the return value.
	 *
	 * @param ret the return value to check
	 * @param cmdline the command line entered
	 */
	private void checkReturn(final int ret, final String cmdline) {
		final RapidEnvInterpreter env = RapidEnvInterpreter.getInstance();
		if (env != null) {
			RapidEnvInterpreter.log(Level.FINER, "cheking return value " + ret);
		}
		if (getReturns() != null) {
			boolean success = false;
			for (final CmdCondReturn returnValueCondition : getReturns()) {
				if (returnValueCondition.getMatches() != null) {
					if (Integer.toString(ret).matches(returnValueCondition.getMatches())) {
						success = true;
						break;
					}
				}
				if (returnValueCondition.getEquals() != null) {
					final int retcond = new Integer(returnValueCondition.getEquals());
					if (ret == retcond) {
						success = true;
						break;
					}
				}
			}
			if (!success) {
				throw new RapidEnvException("ERROR during execution of system command: " + cmdline
						+ "\n  return code = " + ret);
			}
		}
	}

	/**
	 * Check the standard out output.
	 *
	 * @param out the output to check
	 * @param cmdline the command line entered
	 */
	private void checkStdout(final String out, final String cmdline) {
		if (getStdouts() != null) {
			boolean success = false;
			for (final CmdCondStdout stdCondition : getStdouts()) {
				if (stdCondition.getMatches() != null) {
					if (out.matches(stdCondition.getMatches())) {
						success = true;
						break;
					}
				}
				if (stdCondition.getContains() != null) {
					if (out.contains(stdCondition.getContains())) {
						success = true;
						break;
					}
				}
				if (stdCondition.getContainsmatch() != null) {
					final String regexp = ".*" + stdCondition.getContainsmatch() + ".*";
					RapidEnvInterpreter.log(Level.FINE,
							"Checking if stdout = \"" + out
							+ "\" matches \"" + regexp + "\"");
					if (out.matches(regexp)) {
						success = true;
						break;
					}
				}
				if (stdCondition.getEquals() != null) {
					if (out.equals(stdCondition.getEquals())) {
						success = true;
						break;
					}
				}
			}
			if (!success) {
				throw new RapidEnvException("ERROR during execution of system command: " + cmdline
						+ "\n  stdout = \"" + out + "\"");
			}
		}
	}

	/**
	 * Check standard error output.
	 *
	 * @param out the output to check
	 * @param cmdline the command line entered
	 */
	private void checkStderr(final String out, final String cmdline) {
		if (getStderrs() != null) {
			boolean success = false;
			for (final CmdCondStderr stdCondition : getStderrs()) {
				if (stdCondition.getMatches() != null) {
					if (out.matches(stdCondition.getMatches())) {
						success = true;
						break;
					}
				}
				if (stdCondition.getContains() != null) {
					if (out.contains(stdCondition.getContains())) {
						success = true;
						break;
					}
				}
				if (stdCondition.getContainsmatch() != null) {
					if (out.matches(".*" + stdCondition.getContainsmatch() + ".*")) {
						success = true;
						break;
					}
				}
				if (stdCondition.getEquals() != null) {
					if (out.equals(stdCondition.getEquals())) {
						success = true;
						break;
					}
				}
			}
			if (!success) {
				throw new RapidEnvException("ERROR during execution of system command: " + cmdline
						+ "\n  stderr = \"" + out + "\"");
			}
		}
	}

	/**
	 * @return the system command line to execute.
	 */
	private String getCommandline() {
		final StringBuffer cmdline = new StringBuffer();
		if (getRunasbatch()) {
			switch (PlatformHelper.getOs()) {
			case windows:
				final String envvarSysdirWin = System.getenv("SystemRoot");
				if (envvarSysdirWin != null && envvarSysdirWin.length() > 0) {
					cmdline.append('"');
					cmdline.append(envvarSysdirWin);
					cmdline.append(File.separatorChar);
					cmdline.append("system32");
					cmdline.append(File.separatorChar);
					cmdline.append("cmd.exe");
					cmdline.append("\" /C");
				} else {
					cmdline.append("cmd.exe /C ");
				}
				break;
			case linux:
				// Unix commands always run in a shell
				break;
			default:
				throw new RapidEnvException("ERROR: "
						+ " system command execution currently not supported"
						+ " for OS platform \"" + PlatformHelper.getOs().name() + "\".");
			}
		}

		if (getExecutable() != null) {
			final String exe = getExecutableAsFile().getAbsolutePath();
			boolean exeQuoted = false;
			if (exe.contains(" ") || exe.contains("\t")) {
				exeQuoted = true;
			}
			if (exeQuoted) {
				cmdline.append('"');
			}
			cmdline.append(exe);
			if (exeQuoted) {
				cmdline.append('"');
			}
		}

		if (getArguments() != null) {
			for (final Argument arg : getArguments()) {
				cmdline.append(' ');
				if (arg.getQuoted()) {
					cmdline.append('"');
					final EscapeMap map = new EscapeMap(new String[]{
							"\b", "\\b",
							"\n", "\\n",
							"\r", "\\r",
							"\t", "\\t",
							"\"", "\\\"",
							"\\", "\\\\"
					});
					cmdline.append(StringHelper.escape(arg.getValue(), map));
					cmdline.append('"');
				} else {
					cmdline.append(arg.getValue());
				}
			}
		}
		return cmdline.toString();
	}

	/**
	 * @return the system command array to execute.
	 */
	private String[] getCommandArray() {
		final List<String> cmdArrayList = new ArrayList<String>();
		if (getRunasbatch()) {
			switch (PlatformHelper.getOs()) {
			case windows:
				final String envvarSysdirWin = System.getenv("SystemRoot");
				if (envvarSysdirWin != null && envvarSysdirWin.length() > 0) {
					cmdArrayList.add(envvarSysdirWin + File.separatorChar
							+ "system32" + File.separatorChar + "cmd.exe");
				} else {
					cmdArrayList.add("cmd.exe");
				}
				cmdArrayList.add("/C");
				break;
			case linux:
				// Unix commands always run in a shell
				break;
			default:
				throw new RapidEnvException("ERROR: "
						+ " system command execution currently not supported"
						+ " for OS platform \"" + PlatformHelper.getOs().name() + "\".");
			}
		}

		final String exe = getExecutableAsFile().getAbsolutePath();
		boolean exeQuoted = false;
		if (exe.contains(" ") || exe.contains("\t")) {
			exeQuoted = true;
		}
		if (exeQuoted) {
			final EscapeMap map = new EscapeMap(new String[]{
					"\b", "\\b",
					"\n", "\\n",
					"\r", "\\r",
					"\t", "\\t",
					"\"", "\\\"",
					"\\", "\\\\"
			});
			cmdArrayList.add(StringHelper.escape(exe, map));
		} else {
			cmdArrayList.add(exe);
		}

		if (getArguments() != null) {
			for (final Argument arg : getArguments()) {
				final String argvalue = this.interpret(arg.getValue());
				if (arg.getQuoted()) {
					final EscapeMap map = new EscapeMap(new String[]{
							"\b", "\\b",
							"\n", "\\n",
							"\r", "\\r",
							"\t", "\\t",
							"\"", "\\\"",
							"\\", "\\\\"
					});
					cmdArrayList.add("\"" + StringHelper.escape(argvalue, map) + "\"");
				} else {
					cmdArrayList.add(argvalue);
				}
			}
		}
		final int size = cmdArrayList.size();
		final String[] cmdArray = new String[size];
		for (int i = 0; i < size; i++) {
			cmdArray[i] = cmdArrayList.get(i);
		}
		return cmdArray;
	}

	/**
	 * Helper class for command line execution.
	 *
	 * @author Martin Bluemel
	 */
	class CommandStreamReader extends Thread {

		private String cmdline = null;

		private LineNumberReader reader = null;

		private PrintStream outstream = null;

		private StringBuffer outbuf = null;

		/**
		 * constructor.
		 *
		 * @param cmdl the command line
		 * @param is the input stream
		 * @param outs the output stream
		 * @param outb the buffer
		 */
		public CommandStreamReader(final String cmdl, final InputStream is,
				final PrintStream outs, final StringBuffer outb) {  
			this.cmdline = cmdl;
			InputStreamReader isr;
			try {
				isr = new InputStreamReader(is, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RapidEnvException(e);
			}
			this.reader = new LineNumberReader(isr);
			this.outstream = outs;
			this.outbuf = outb;
		}

		/**
		 * run the reader.
		 */
		public void run() {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!getSilent()) {
						outstream.println(line);
					}
					outbuf.append(line);
					outbuf.append('\n');
				}
			} catch (IOException e) {
				throw new RapidEnvException(
						"Exception during execution of "
								+ " system command \"" + cmdline + "\"", e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RapidEnvException(
							"Exception during closing reader after execution "
									+ " of system command \"" + cmdline + "\"", e);
				}
			}
		}
	}

	public File getWorkingdirAsFile() {
		File file = null;
		if (getWorkingdir() != null) {
			final String path = interpret(getWorkingdir());
			file = new File(path);
			if (!file.exists()) {
				throw new RapidEnvException("Working directory \""
						+ file.getAbsolutePath() + "\" not found");                
			}
			if (!file.isDirectory()) {
				throw new RapidEnvException("File \""
						+ file.getAbsolutePath() + "\" is no directory.");
			}
		}
		return file;
	}

	public ReadonlyListCollection<SystemCommand> getVerifycmds() {
		final ReadonlyListCollection<SystemCommand> superVerifycmds = super.getVerifycmds();
		if (superVerifycmds == null || super.getVerifycmds().size() == 0) {
			return superVerifycmds;
		}
		final ArrayList<SystemCommand> commands = new ArrayList<SystemCommand>();
		for (final SystemCommand scmd : superVerifycmds) {
			if (scmd.checkOsfamily()) {
				commands.add(scmd);
			}
		}
		return new ReadonlyListCollection<SystemCommand>(commands,
				this.getProperty("verifycmds").getType());
	}

	protected File getExecutableAsFile() {
		Installunit parentUnit = null;
		if (getParentBean() instanceof Installunit) {
			parentUnit = (Installunit) getParentBean();
		}
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		return getExecutableAsFileStat(getExecutable(), null, interpreter, parentUnit);
	}

	private String interpret(final String s) {
		final RapidEnvInterpreter interpreter = RapidEnvInterpreter.getInstance();
		if (interpreter == null) {
			return s;
		} else {
			Installunit parentUnit = null;
			if (getParentBean() instanceof Installunit) {
				parentUnit = (Installunit) getParentBean();
			}
			return interpreter.interpret(parentUnit, null, s);
		}
	}

	public String print() {
		return this.getExecutable();
	}

	/**
	 * default constructor.
	 */
	 public SystemCommand() {
		super();
	}

	/**
	 * constructor out of a string.
	 * @param s the string
	 */
	 public SystemCommand(final String s) {
		 super(s);
	 }

	 /**
	  * constructor out of a string array.
	  * @param sa the string array
	  */
	 public SystemCommand(final String[] sa) {
		 super(sa);
	 }

	 /**
	  * the bean's type (class variable).
	  */
	 private static TypeRapidBean type = TypeRapidBean.createInstance(SystemCommand.class);

	 /**
	  * @return the RapidBean's type
	  */
	 public TypeRapidBean getType() {
		 return type;
	 }
}
