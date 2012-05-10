/*
 * RapidEnv: CommandExecutor.java
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

package org.rapidbeans.rapidenv.cmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.logging.Level;

import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.config.cmd.CommandExecutionResult;

/**
 * represents an executable system command.
 * 
 * @author Martin Bluemel
 */
public class CommandExecutor {

	/**
	 * the working directory to execute the commandline in.
	 */
	protected File dir = null;

	/**
	 * the command line.
	 */
	protected String cmd = null;

	/**
	 * the operation system family.
	 */
	protected String osfamily = null;

	/**
	 * the message.
	 */
	protected String message = null;

	/**
	 * console output flag.
	 */
	private boolean consoleOutput = true;

	/**
	 * async flag.
	 */
	private final boolean asynchronous = false;

	/**
	 * Runs the command in the OS environment.
	 * 
	 * @return the result containing the return value, stdout and stderr
	 */
	public CommandExecutionResult execute() {

		final StringBuffer bufOut = new StringBuffer();
		final StringBuffer bufErr = new StringBuffer();
		String cmdline = null;

		switch (PlatformHelper.getOsfamily()) {
		case windows:
			String cmdlineWin = "cmd.exe /C ";
			if (this.dir != null) {
				cmdlineWin += "cd /D \"" + this.dir.getAbsolutePath() + "\" & ";
			}
			cmdlineWin += this.cmd;
			cmdline = cmdlineWin;
			break;
		case linux:
			cmdline = this.cmd;
			break;
		default:
			throw new RapidEnvException("ERROR: " + " system command execution currently not supported"
			        + " for OS platform \"" + PlatformHelper.getOsfamily().name() + "\".");
		}

		// ToolDbStandalone dbTool = null;
		// if (this.dbServerRequired) {
		// dbTool = (ToolDbStandalone) this.tool;
		// dbTool.startDbms(false);
		// }

		int ret = 0;

		try {
			RapidEnvInterpreter.log(Level.FINE, "executing system command: " + cmdline);
			final Process proc = Runtime.getRuntime().exec(cmdline, null, dir);

			if (!this.asynchronous) {
				final CommandStreamReader rdOut = new CommandStreamReader(cmdline, proc.getInputStream(), System.out,
				        bufOut);
				final CommandStreamReader rdErr = new CommandStreamReader(cmdline, proc.getErrorStream(), System.err,
				        bufErr);
				rdOut.start();
				rdErr.start();
				ret = proc.waitFor();
				while (rdOut.isAlive() || rdErr.isAlive()) {
					Thread.sleep(100);
				}
			}

			// if (this.dbServerRequired) {
			// dbTool = (ToolDbStandalone) this.tool;
			// dbTool.stopDbms(false);
			// }

		} catch (InterruptedException e) {
			throw new RapidEnvException("Exception during execution of" + " system command \"" + cmdline + "\"", e);
		} catch (IOException e) {
			throw new RapidEnvException("Exception during execution of" + " system command \"" + cmdline + "\"", e);
		}
		return new CommandExecutionResult(bufOut.toString(), bufErr.toString(), ret);
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
		 * @param cmdl
		 *            the command line
		 * @param is
		 *            the input stream
		 * @param outs
		 *            the output stream
		 * @param outb
		 *            the buffer
		 */
		public CommandStreamReader(final String cmdl, final InputStream is, final PrintStream outs,
		        final StringBuffer outb) {
			this.cmdline = cmdl;
			this.reader = new LineNumberReader(new InputStreamReader(is));
			this.outstream = outs;
			this.outbuf = outb;
		}

		/**
		 * run the reader.
		 */
		@Override
		public void run() {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					if (consoleOutput) {
						outstream.println(line);
					}
					outbuf.append(line);
					outbuf.append('\n');
				}
			} catch (IOException e) {
				throw new RapidEnvException("Exception during execution of " + " system command \"" + cmdline + "\"", e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RapidEnvException("Exception during closing reader after execution "
					        + " of system command \"" + cmdline + "\"", e);
				}
			}
		}
	}

	/**
	 * @param consoleOutput
	 *            the consoleOutput to set
	 */
	public void setConsoleOutput(final boolean consoleOutput) {
		this.consoleOutput = consoleOutput;
	}
}
