/*
 * RapidEnv: ConfigExprFunctionContentsOfFileTest.java
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

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for class ConfigExprFunctionContentsOfFile.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionFilecontentsTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	/**
	 * Read a file simply preserving line feed characters of the original file
	 * (default behavior).
	 */
	@Test
	public final void intepretSimple() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null,
				"filecontents('testdata/ant/ant_win.properties')", true);
		String expected = "V1=Test1\r\n" + "V2=Test2\r\n"
				+ "test.dev.location=ismaning\r\n";
		String interpreted = expr.interpret();
		Assert.assertEquals(expected, interpreted);
	}

	/**
	 * Read a file and convert line endings platform specificly.
	 */
	@Test
	public final void intepretSimplePlatformSpecific() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(
				null,
				null,
				"filecontents('testdata/ant/ant_win.properties', '', 'platform')",
				true);
		String expected = null;
		switch (PlatformHelper.getOs()) {
		case windows:
			expected = "V1=Test1\r\n" + "V2=Test2\r\n"
					+ "test.dev.location=ismaning\r\n";
			break;
		default:
			expected = "V1=Test1\n" + "V2=Test2\n"
					+ "test.dev.location=ismaning\n";
			break;
		}
		String interpreted = expr.interpret();
		Assert.assertEquals(expected, interpreted);
	}

	/**
	 * Simply read a file a one single line.
	 */
	@Test
	public final void intepretAsOneLine() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null,
				"filecontents('testdata/ant/ant_win.properties', '\\n\\r')",
				true);
		Assert.assertEquals("V1=Test1\\r\\n" + "V2=Test2\\r\\n"
				+ "test.dev.location=ismaning\\r\\n", expr.interpret());
	}

	/**
	 * Simply read a file a one single line.
	 */
	@Test
	public final void intepretAsOneLinePlatform() {
		ConfigExprTopLevel expr = null;
		switch (PlatformHelper.getOs()) {
		case windows:
			expr = new ConfigExprTopLevel(
					null,
					null,
					"filecontents('testdata/ant/ant_win.properties', '\\n\\r', 'platform')",
					true);
			Assert.assertEquals("V1=Test1\\r\\n" + "V2=Test2\\r\\n"
					+ "test.dev.location=ismaning\\r\\n", expr.interpret());
			break;
		default:
			expr = new ConfigExprTopLevel(
					null,
					null,
					"filecontents('testdata/ant/ant_win.properties', '\\n', 'platform')",
					true);
			Assert.assertEquals("V1=Test1\\n" + "V2=Test2\\n"
					+ "test.dev.location=ismaning\\n", expr.interpret());
			break;
		}
	}

	/**
	 * Simply read a file a one single line with additional escaping.
	 */
	@Test
	public final void intepretAsOneLineAdditionEsc() {
		ConfigExprTopLevel expr = null;
		switch (PlatformHelper.getOs()) {
		default:
			expr = new ConfigExprTopLevel(
					null,
					null,
					"filecontents('testdata/ant/ant_win.properties', '\\n\\r=')",
					true);
			Assert.assertEquals("V1\\=Test1\\r\\n" + "V2\\=Test2\\r\\n"
					+ "test.dev.location\\=ismaning\\r\\n", expr.interpret());
			break;
		// default:
		// expr = new ConfigExprTopLevel(
		// null,
		// null,
		// "filecontents('testdata/ant/ant_win.properties', '\\n=', 'platform')",
		// true);
		// Assert.assertEquals("V1\\=Test1\\n" + "V2\\=Test2\\n"
		// + "test.dev.location\\=ismaning\\n", expr.interpret());
		// break;
		}
	}

	/**
	 * Simply read a file a one single line with additional escaping.
	 */
	@Test
	public final void intepretAsOneLineAdditionEscPlatform() {
		ConfigExprTopLevel expr = null;
		switch (PlatformHelper.getOs()) {
		case windows:
			expr = new ConfigExprTopLevel(
					null,
					null,
					"filecontents('testdata/ant/ant_win.properties', '\\n\\r=', 'platform')",
					true);
			Assert.assertEquals("V1\\=Test1\\r\\n" + "V2\\=Test2\\r\\n"
					+ "test.dev.location\\=ismaning\\r\\n", expr.interpret());
			break;
		default:
			expr = new ConfigExprTopLevel(
					null,
					null,
					"filecontents('testdata/ant/ant_win.properties', '\\n=', 'platform')",
					true);
			Assert.assertEquals("V1\\=Test1\\n" + "V2\\=Test2\\n"
					+ "test.dev.location\\=ismaning\\n", expr.interpret());
			break;
		}
	}

	/**
	 * Test variable expansion within the argument.
	 */
	@Test
	public void interpretWithVarExtension() {
		String x = new File("x").getAbsolutePath();
		String path = x.substring(0, x.length() - 2);
		(new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/env.xml", "s" }))).setPropertyValue("wd", path);
		Installunit tool = new Installunit("test");
		ConfigExprTopLevel expr = null;
		switch (PlatformHelper.getOs()) {
		case windows:
			expr = new ConfigExprTopLevel(
					tool,
					null,
					"filecontents(${wd}'/testdata/ant/ant_win.properties', '\\n\\r=')",
					true);
			Assert.assertEquals("V1\\=Test1\\r\\n" + "V2\\=Test2\\r\\n"
					+ "test.dev.location\\=ismaning\\r\\n", expr.interpret());
			break;
		default:
			expr = new ConfigExprTopLevel(
					tool,
					null,
					"filecontents(${wd}'/testdata/ant/ant_win.properties', '\\n=', 'platform')",
					true);
			Assert.assertEquals("V1\\=Test1\\n" + "V2\\=Test2\\n"
					+ "test.dev.location\\=ismaning\\n", expr.interpret());
			break;
		}
	}

	/**
	 * Test variable expansion within the argument.
	 */
	@Test
	public void interpretWithVarExtensionNormalized() {
		String x = new File("x").getAbsolutePath();
		String path = x.substring(0, x.length() - 2);
		(new RapidEnvInterpreter(new CmdRenv(new String[] { "-env",
				"testdata/env/env.xml", "s" }))).setPropertyValue("wd", path);
		Installunit tool = new Installunit("test");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(
				tool,
				null,
				"filecontents(${wd}'/testdata/ant/ant_win.properties', '\n\r=', 'normalize')",
				true);
		Assert.assertEquals("V1\\=Test1\\n" + "V2\\=Test2\\n"
				+ "test.dev.location\\=ismaning\\n", expr.interpret());
	}
}
