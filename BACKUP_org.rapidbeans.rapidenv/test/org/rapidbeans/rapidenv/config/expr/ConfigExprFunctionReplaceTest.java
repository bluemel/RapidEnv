/*
 * RapidEnv: ConfigExprFunctionReplaceTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 10/14/2011
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
 * Tests for class ConfigExprFunctionReplace.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionReplaceTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
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
	 * Test variable expansion within the argument
	 */
	@Test
	public void testSimple() {
		Installunit unit = new Installunit("test");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(unit, null, "replace('@a@b@c@', '@', '-')", false);
		Assert.assertEquals("-a-b-c-", expr.interpret());
	}

	/**
	 * Test variable expansion within the argument
	 */
	@Test
	public void testRegex01() {
		Installunit unit = new Installunit("test");
		Assert.assertEquals("aXXXf", new ConfigExprTopLevel(unit, null, "replace('a@@@@@@bcde@@f', '@.*@', 'XXX')",
		        false).interpret());
	}

	@Test
	public void testComplexNesting() {
		Installunit unit = new Installunit("test");
		Assert.assertEquals("a\\:b/c/d", new ConfigExprTopLevel(unit, null,
		        "replace(pathconvert('a:b\\c\\d', '/'), ':', '\\\\\\\\\\:')", true).interpret());
	}

	@Test
	public void testReplaceAll() {
		Assert.assertEquals("x\\:y", "x:y".replaceAll(":", "\\\\:"));
	}
}
