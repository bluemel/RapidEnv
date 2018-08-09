/*
 * RapidEnv: CmdRenvTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 05/23/2010
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

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Test;
import org.rapidbeans.rapidenv.cmd.CmdRenv;

/**
 * Unit tests for the renv command line parser.
 * 
 * @author Martin Bluemel
 */
public class CmdRenvTest {

	/**
	 * renv without argument should print all the tool's status.
	 */
	@Test
	public void testParseZero() {
		CmdRenv cmd = new CmdRenv(new String[0]);
		assertSame(CmdRenvCommand.stat, cmd.getCommand());
		Assert.assertEquals(0, cmd.getInstallunitOrPropertyNames().size());
	}

	/**
	 * renv stat.
	 */
	@Test
	public void testParseOneStat() {
		CmdRenv cmd = new CmdRenv(new String[] { "stat" });
		Assert.assertSame(CmdRenvCommand.stat, cmd.getCommand());
		Assert.assertEquals(0, cmd.getInstallunitOrPropertyNames().size());
	}

	/**
	 * renv s.
	 */
	@Test
	public void testParseOneStatShort() {
		CmdRenv cmd = new CmdRenv(new String[] { "s" });
		Assert.assertSame(CmdRenvCommand.stat, cmd.getCommand());
		Assert.assertEquals(0, cmd.getInstallunitOrPropertyNames().size());
	}

	/**
	 * renv deinstall.
	 */
	@Test
	public void testParseOneDeinstall() {
		CmdRenv cmd = new CmdRenv(new String[] { "deinstall" });
		Assert.assertSame(CmdRenvCommand.deinstall, cmd.getCommand());
		Assert.assertEquals(0, cmd.getInstallunitOrPropertyNames().size());
	}

	/**
	 * renv d.
	 */
	@Test
	public void testParseOneDeinstallShort() {
		CmdRenv cmd = new CmdRenv(new String[] { "d" });
		Assert.assertSame(CmdRenvCommand.deinstall, cmd.getCommand());
		Assert.assertEquals(0, cmd.getInstallunitOrPropertyNames().size());
	}

	/**
	 * renv update java.
	 */
	@Test
	public void testParseOneTool() {
		CmdRenv cmd = new CmdRenv(new String[] { "update", "java" });
		Assert.assertSame(CmdRenvCommand.update, cmd.getCommand());
		Assert.assertEquals(1, cmd.getInstallunitOrPropertyNames().size());
		Assert.assertEquals("java", cmd.getInstallunitOrPropertyNames().get(0));
	}

	/**
	 * renv update java ant eclipse.
	 */
	@Test
	public void testParseMoreTools() {
		CmdRenv cmd = new CmdRenv(new String[] { "update", "java", "ant", "eclipse" });
		Assert.assertSame(CmdRenvCommand.update, cmd.getCommand());
		Assert.assertEquals(3, cmd.getInstallunitOrPropertyNames().size());
		Assert.assertEquals("ant", cmd.getInstallunitOrPropertyNames().get(1));
	}

	/**
	 * renv -v -env env.xml deinstall.
	 */
	@Test
	public void testParseOptions() {
		CmdRenv cmd = new CmdRenv(new String[] { "-v", "-env", "env.xml", "deinstall", "java", "ant", "eclipse" });
		Assert.assertSame(CmdRenvCommand.deinstall, cmd.getCommand());
		Assert.assertEquals(3, cmd.getInstallunitOrPropertyNames().size());
		Assert.assertTrue(cmd.getOptions().containsKey(CmdRenvOption.verbose));
		Assert.assertFalse(cmd.getOptions().containsKey(CmdRenvOption.debug));
		Assert.assertEquals("env.xml", ((String[]) cmd.getOptions().get(CmdRenvOption.env))[0]);
		Assert.assertEquals("java", cmd.getInstallunitOrPropertyNames().get(0));
		Assert.assertEquals("ant", cmd.getInstallunitOrPropertyNames().get(1));
		Assert.assertEquals("eclipse", cmd.getInstallunitOrPropertyNames().get(2));
	}

	/**
	 * Help should be recognized from a various range of strings (case
	 * insensitively).
	 */
	@Test
	public void testParseHelpOk() {
		CmdRenv cmd = new CmdRenv(new String[] { "h" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
		cmd = new CmdRenv(new String[] { "HelP" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
		cmd = new CmdRenv(new String[] { "-h" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
		cmd = new CmdRenv(new String[] { "--h" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
		cmd = new CmdRenv(new String[] { "-HeLp" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
		cmd = new CmdRenv(new String[] { "--hElP" });
		Assert.assertSame(CmdRenvCommand.help, cmd.getCommand());
	}

	/**
	 * Test if a typo is recognized.
	 */
	@Test(expected = RapidEnvCmdException.class)
	public void testParseIllegalCommand1() {
		try {
			new CmdRenv(new String[] { "helpx" });
		} catch (RapidEnvCmdException e) {
			Assert.assertEquals("Illegal command: helpx", e.getMessage());
			throw e;
		}
	}

	/**
	 * Test if a typo is recognized.
	 */
	@Test(expected = RapidEnvCmdException.class)
	public void testParseIllegalCommandWithOptions() {
		try {
			new CmdRenv(new String[] { "-env", "xxx.xml", "x" });
		} catch (RapidEnvCmdException e) {
			Assert.assertEquals("Illegal command: x", e.getMessage());
			throw e;
		}
	}

	/**
	 * Test if an illegal argument is recognized. renv -asdf install java.
	 * Should throw a RapidEnvException because of illegal argument "-asdf".
	 */
	@Test(expected = RapidEnvException.class)
	public void testParseIllegalOption() {
		try {
			new CmdRenv(new String[] { "-asdf", "install", "java" });
		} catch (RapidEnvCmdException e) {
			Assert.assertEquals("Illegal option: -asdf", e.getMessage());
			throw e;
		}
	}

	@Test
	public void testGetConfigfileDefault() {
		CmdRenv cmd = new CmdRenv(new String[] { "c" });
		Assert.assertEquals("env.xml", cmd.getConfigfile().getName());
	}
}
