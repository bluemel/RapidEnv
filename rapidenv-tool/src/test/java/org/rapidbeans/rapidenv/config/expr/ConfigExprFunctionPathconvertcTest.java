/*
 * RapidEnv: ConfigExprPathconvertcTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 06/28/2010
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

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for class ConfigExprFunctionPathconvert.
 *
 * @author Martin Bluemel
 */
public class ConfigExprFunctionPathconvertcTest {

	@BeforeClass
	public static void setUpClass() throws InterruptedException {
		FileHelper.deleteDeep(new File("profile"));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
		Thread.sleep(200);
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("profile").mkdir();
		new File("src/test/resources/testinstall").mkdir();
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" })).setPropertyValue(
				"test.dir", "/home/martin");
		int i = 0;
		while (!(new File("profile").exists()))
		{
			Thread.sleep(10);
			if ((i % 5) == 0)
			{
				System.out.println("@@@ mkdir: " + new File("profile").getAbsolutePath());
				new File("profile").mkdir();
			}
			i++;
		}
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("profile"));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
	}

	@Before
	public void setUp() throws InterruptedException
	{
		int i = 0;
		while (!(new File("profile").exists()))
		{
			Thread.sleep(10);
			if ((i % 5) == 0)
			{
				System.out.println("@@@ mkdir: " + new File("profile").getAbsolutePath());
				new File("profile").mkdir();
			}
			i++;
		}
	}

	/**
	 * Test without separator char given.<br>
	 * The platform specific separator character should be taken.
	 */
	@Test
	public void interpretAbsolute() {
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			ConfigExprTopLevel exprWin = new ConfigExprTopLevel(null, null, "a pathconvertc('C:\\a\\b\\c')", false);
			Assert.assertEquals("a C:" + File.separator + "a" + File.separator + "b" + File.separator + "c",
					exprWin.interpret());
			break;
		case linux:
			ConfigExprTopLevel exprUnix = new ConfigExprTopLevel(null, null, "a pathconvertc('/tmp/a/b/c')", false);
			Assert.assertEquals("a /tmp" + File.separator + "a" + File.separator + "b" + File.separator + "c",
					exprUnix.interpret());
			break;
		default:
			Assert.fail("Operating system \"" + PlatformHelper.getOsfamily().name() + "\" not yet supported.");
			break;
		}
	}

	/**
	 * Test without separator char given.<br>
	 * The platform specific separator character should be taken.
	 */
	@Test
	public void interpretNormalized() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "a pathconvertc('x/../a/b')", false);
		String workingDirectory = System.getProperty("user.dir");
		Assert.assertEquals("a " + workingDirectory + File.separator + "a" + File.separator + "b", expr.interpret());
	}

	/**
	 * Test variable expansion within the argument
	 *
	 * @throws IOException
	 *             in case of IO problems
	 */
	@Test
	public void interpretWithVarExtension() throws IOException {
		Installunit unit = new Installunit("test");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(unit, null, "pathconvertc(${test.dir}'/xxx/yyy/zzz.txt')",
				false);
		Assert.assertEquals(new File("/home/martin/xxx/yyy/zzz.txt").getCanonicalPath(), expr.interpret());
	}
}
