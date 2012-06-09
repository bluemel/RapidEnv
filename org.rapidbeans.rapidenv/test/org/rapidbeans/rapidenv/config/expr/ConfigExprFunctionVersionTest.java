/*
 * RapidEnv: ConfigExprFunctionVersionTest.java
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
import org.rapidbeans.core.util.Version;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for class ConfigExprFunctionToolversion.
 * 
 * @version initial
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionVersionTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "testdata/env/env.xml", "s" })).setPropertyValue(
		        "test.dir", "/home/martin");
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	/**
	 * Test without separator char given.<br>
	 * The platform specific separator character should be taken.
	 */
	@Test
	public final void testInterpret() {
		Installunit tool = new Installunit(new String[] { "testspace", "testtool" });
		tool.setVersion(new Version("1.2.3"));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(tool, null, "a version() b", false);
		Assert.assertEquals("a 1.2.3 b", expr.interpret());
	}

	/**
	 * Test without separator char given.<br>
	 * The platform specific separator character should be taken.
	 */
	@Test
	public final void testInterpretInstallunit() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "testdata/env/env.xml", "s" }));
		Installunit tool = new Installunit(new String[] { "testspace", "testtool" });
		tool.setVersion(new Version("1.2.3"));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(tool, null, "version('jdk')", false);
		Assert.assertEquals("1.6.0", expr.interpret());
	}
}
