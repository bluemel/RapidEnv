/*
 * RapidEnv: ConfigExprFunctionHomedirTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/27/2010
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
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for class ConfigExprFunctionHomedir.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionNameTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	/**
	 * Test without install unit argument. The home directory of the enclosing
	 * install unit should be taken.
	 */
	@Test
	public void testInterpret() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "testdata/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "", "jdk" }), null,
		        "homedir()", false);
		Assert.assertEquals(new File("testdata/testinstall/jdk/1.6.0").getAbsolutePath(), expr.interpret());
		expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "org.apache", "ant" }), null, "name()",
		        false);
		Assert.assertEquals("ant", expr.interpret());
	}
}
