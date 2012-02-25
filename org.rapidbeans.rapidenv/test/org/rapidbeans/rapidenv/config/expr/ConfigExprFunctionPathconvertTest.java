/*
 * RapidEnv: ConfigExprFunctionPathconvertTest.java
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
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Tests for class ConfigExprFunctionPathconvert.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionPathconvertTest {

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
	 * Test variable expansion within the argument
	 */
	@Test
	public void interpretWithVarExtension() {
		Installunit unit = new Installunit("test");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(unit, null, "pathconvert(${test.dir}'/xxx/yyy/zzz.txt', '/')",
		        false);
		Assert.assertEquals("/home/martin/xxx/yyy/zzz.txt", expr.interpret());
	}

	/**
	 * Test without separator char given.<br>
	 * The platform specific separator character should be taken.
	 */
	@Test
	public void interpretPlatformSpecific() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "a pathconvert('C:\\a\\b\\c')", false);
		Assert.assertEquals("a C:" + File.separator + "a" + File.separator + "b" + File.separator + "c",
		        expr.interpret());
	}

	/**
	 * Test with a '/' separator char given.<br>
	 */
	@Test
	public void interpretSlash() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "a pathconvert('C:\\a/b\\c', '/')", false);
		Assert.assertEquals("a C:/a/b/c", expr.interpret());
	}

	/**
	 * Test with a '\' separator char given.<br>
	 */
	@Test
	public void interpretBackslash() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "a pathconvert('C:/a\\b/c', '\\')", false);
		Assert.assertEquals("a C:\\a\\b\\c", expr.interpret());
	}

	/**
	 * Test with a '\' separator char given.<br>
	 */
	@Test
	public void interpretBackslashEsc() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "a pathconvert('C:/a\\\\b/c', '\\\\')", true);
		Assert.assertEquals("a C:\\a\\b\\c", expr.interpret());
	}

	/**
	 * Test with an empty separator char ("") given.<br>
	 * Only a string with one character is valid.
	 */
	@Test(expected = RapidEnvException.class)
	public void interpretSeparatorCharEmpty() {
		try {
			new ConfigExprTopLevel(new Installunit("xyz"), null, "a pathconvert('C:/a\\b/c', '')", false);
		} catch (RapidEnvException e) {
			Exception eNest1 = (Exception) e.getCause();
			Assert.assertSame(InvocationTargetException.class, eNest1.getClass());
			Exception eNest2 = (Exception) eNest1.getCause();
			Assert.assertSame(RapidEnvConfigurationException.class, eNest2.getClass());
			throw e;
		}
	}

	/**
	 * Test with a to long separator char ("##") given.<br>
	 * Only a string with one character is valid.
	 */
	@Test(expected = RapidEnvException.class)
	public void interpretSeparatorCharTooLong() {
		try {
			new ConfigExprTopLevel(new Installunit("xyz"), null, "a pathconvert('C:/a\\b/c','##')", false);
		} catch (RapidEnvException e) {
			Exception eNest1 = (Exception) e.getCause();
			Assert.assertSame(InvocationTargetException.class, eNest1.getClass());
			Exception eNest2 = (Exception) eNest1.getCause();
			Assert.assertSame(RapidEnvConfigurationException.class, eNest2.getClass());
			throw e;
		}
	}
}
