/*
 * RapidEnv: SystemCommandTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 10/02/2010
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

import org.junit.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Project;

public class SystemCommandTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
		new File("testdata/testinstall/org/apache/maven/2.1.2").mkdirs();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project",
				"org.rapidbeans.rapidenv.config.Project", true);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	@Test
	public void testProcessInput() throws IOException, InterruptedException {
		switch (PlatformHelper.getOsfamily()) {
		case linux:
			// cmd = new SystemCommand();
			// cmd.setExecutable("sh");
			// cmd.setSilent(true);
			// cmd.addArgument(new Argument("-c"));
			// cmd.addArgument(new
			// Argument("read x && echo \"x = \\\"${x}\\\"\""));
			// cmd.setInput("XXX\n\n\n");
			// result = cmd.execute();
			// Assert.assertEquals("x = \"XXX\"\n", result.getStdout());
			break;
		case windows:
			// cmd = new SystemCommand();
			// cmd.setExecutable("cmd.exe");
			// cmd.setSilent(true);
			// cmd.addArgument(new Argument("/C"));
			// cmd.addArgument(new
			// Argument("FOR /F \"tokens=*\" %%A IN ('TYPE CON') DO SET INPUT=%%A && echo x = \"%INPUT%\""));
			// cmd.setInput("XXX\n\n\n");
			// result = cmd.execute();
			// Assert.assertEquals("x = \"XXX\"\n", result.getStdout());
			break;
		default:
			Assert.fail("Unexpected os familiy: " + PlatformHelper.getOsfamily().name());
			break;
		}
	}

	@Test
	public void testReadConfiguration() {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/envSystemCommand01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("unit01");
		Assert.assertNotNull(unit);
		SystemCommand cmd = null;
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			cmd = (SystemCommand) unit.getConfigurations().get(0);
			break;
		case linux:
			cmd = (SystemCommand) unit.getConfigurations().get(3);
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
		final File exe = cmd.getExecutableAsFile();
		Assert.assertTrue(exe.isAbsolute());
		Assert.assertTrue(exe.exists());
		Assert.assertTrue(exe.canExecute());
	}

	@Test
	public void testExecuteSynchronously() {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/envSystemCommand01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("unit01");
		Assert.assertNotNull(unit);
		SystemCommand cmd = null;
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			cmd = (SystemCommand) unit.getConfigurations().get(0);
			break;
		case linux:
			cmd = (SystemCommand) unit.getConfigurations().get(3);
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
		CommandExecutionResult result = cmd.execute();
		Assert.assertEquals("Hallo Martin! \n", result.getStdout());
		Assert.assertEquals("", result.getStderr());
		Assert.assertEquals(0, result.getReturncode());
		File testfile = null;
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			testfile = new File(System.getenv("TMP"), "test.txt");
			break;
		case linux:
			testfile = new File("/tmp/test.txt");
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
		Assert.assertTrue(testfile.exists());
		testfile.delete();
	}

	@Test
	public void testExecuteSynchronouslyBatch() {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/envSystemCommand01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("unit01");
		Assert.assertNotNull(unit);
		SystemCommand cmd = null;
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			cmd = (SystemCommand) unit.getConfigurations().get(1);
			break;
		case linux:
			cmd = (SystemCommand) unit.getConfigurations().get(4);
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
		CommandExecutionResult result = cmd.execute();
		Assert.assertEquals("Hallo Ulrike!\n", result.getStdout());
		Assert.assertEquals("", result.getStderr());
		Assert.assertEquals(0, result.getReturncode());
	}

	@Test
	public void testExecuteCheckSuccessContainsmatch() {
		Assert.assertTrue("hallihallo!!!\n".matches(".*halli.*\n.*"));
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/envSystemCommand01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("unit01");
		Assert.assertNotNull(unit);
		SystemCommand cmd = null;
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			cmd = (SystemCommand) unit.getConfigurations().get(2);
			break;
		case linux:
			cmd = (SystemCommand) unit.getConfigurations().get(5);
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
		CommandExecutionResult result = cmd.execute();
		Assert.assertEquals("hallihallo!!!\n", result.getStdout());
		Assert.assertEquals("", result.getStderr());
		Assert.assertEquals(0, result.getReturncode());
	}

	/**
	 * Test passing arguments to a Windows script interpreted by cmd.exe. The
	 * SystemCommand is built by the program.
	 *
	 * Please note the limitation that cmd.exe will keep surrounding quotes in
	 * arguments. The script called in this test testArgPassing.cmd demonstrates
	 * how to strip them afterwards.
	 */
	@Test
	public void testArgumentsPassingWindowsCmdProg() {
		switch (PlatformHelper.getOsfamily()) {
		case windows:

			// test with a cmd.exe call with a programmed SystemCommand
			SystemCommand cmd = new SystemCommand();
			cmd.setExecutable("cmd.exe");
			cmd.addArgument(new Argument("/C", false));
			cmd.addArgument(new Argument("testdata\\scripts\\windows\\testArgPassing.cmd", false));
			cmd.addArgument(new Argument("xxx", false));
			cmd.addArgument(new Argument("a b c", true));
			cmd.addArgument(new Argument("\"x\" \"y\" \"z\"", true));
			cmd.setSilent(true);
			CommandExecutionResult result = cmd.execute();
			Assert.assertEquals(0, result.getReturncode());
			Assert.assertEquals("Test Argument passing:\n" + "argument 1: @xxx@\n" + "argument 2: @\"a b c\"@\n"
					+ "argument 3: @\"\\\"x\\\" \\\"y\\\" \\\"z\\\"\"@\n" + "argument 2': @a b c@\n"
					+ "argument 3': @\"x\" \"y\" \"z\"@\n", result.getStdout());
			Assert.assertEquals("", result.getStderr());
			break;
		case linux:
			// nothing to do
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
	}

	/**
	 * Test passing arguments to a Windows script interpreted by cmd.exe. The
	 * SystemCommand is read from an environment configuration file.
	 *
	 * Please note the limitation that cmd.exe will keep surrounding quotes in
	 * arguments. The script called in this test testArgPassing.cmd demonstrates
	 * how to strip them afterwards.
	 */
	@Test
	public void testArgumentsPassingWindowsCmdConf() {
		switch (PlatformHelper.getOsfamily()) {
		case windows:

			RapidEnvInterpreter interpreter = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
					"testdata/env/envSystemCommand01.xml", "s" }));
			Project project = interpreter.getProject();
			Installunit unit = project.findInstallunitConfiguration("unit01");
			SystemCommand cmd = (SystemCommand) unit.getConfigurations().get(6);
			CommandExecutionResult result = cmd.execute();
			Assert.assertEquals(0, result.getReturncode());
			Assert.assertEquals("Test Argument passing:\n" + "argument 1: @xxx@\n" + "argument 2: @\"a b c\"@\n"
					+ "argument 3: @\"\\\"x\\\" \\\"y\\\" \\\"z\\\"\"@\n" + "argument 2': @a b c@\n"
					+ "argument 3': @\"x\" \"y\" \"z\"@\n", result.getStdout());
			Assert.assertEquals("", result.getStderr());
			break;
		case linux:
			// Do nothing
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
	}

	/**
	 * Test passing arguments to a Windows VBS script interpreted by the Windows
	 * Scripting Host (cscript.exe). The SystemCommand is built by the programm
	 *
	 * Please note the limitation that cscript.exe ist not capable to receive
	 * quotes in arguments. The VBS script called in this test
	 * testArgPassing.vbs demonstrates how to overcome this problem.
	 */
	@Test
	public void testArgumentsPassingWindowsCscriptProg() {
		switch (PlatformHelper.getOsfamily()) {
		case windows:

			// test with a cscript.exe call with a programmed SystemCommand
			SystemCommand cmd = new SystemCommand();
			cmd.setExecutable("cscript.exe");
			cmd.addArgument(new Argument(new File("testdata\\scripts\\windows\\testArgPassing.vbs").getAbsolutePath(),
					false));
			cmd.addArgument(new Argument("//Nologo", false));
			cmd.addArgument(new Argument("xxx", false));
			cmd.addArgument(new Argument("a b c", false));
			cmd.addArgument(new Argument("/C &quot;echo Hello&quot;", false));
			cmd.setSilent(true);
			CommandExecutionResult result = cmd.execute();
			Assert.assertEquals(0, result.getReturncode());
			Assert.assertEquals("Test Argument passing:\n" + "argument 1: @xxx@\n" + "argument 2: @a b c@\n"
					+ "argument 3: @/C &quot;echo Hello&quot;@\n" + "argument 3': @/C \"echo Hello\"@\n",
					result.getStdout());
			Assert.assertEquals("", result.getStderr());
			break;

		case linux:
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" currently not supported");
			break;
		}
	}
}
