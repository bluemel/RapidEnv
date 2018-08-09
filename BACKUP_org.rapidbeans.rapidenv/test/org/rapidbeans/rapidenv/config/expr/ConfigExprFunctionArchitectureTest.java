/*
 * RapidEnv: ConfigExprFunctionArchitectureTest.java
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Tests for class ConfigExprFunctionArchitecture.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionArchitectureTest {

	@BeforeClass
	public static void setUpClass() {
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	/**
	 * Simply test the happy day.
	 */
	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "architecture()", false);
		Assert.assertEquals(PlatformHelper.getArchName(), expr.interpret());
	}

	/**
	 * Check with too much arguments.
	 */
	@Test(expected = RapidEnvException.class)
	public void testInterpretWithArgs() {
		try {
			new ConfigExprTopLevel(new Installunit("xxx"), null, "architecture('xxx', 'yy')", false);
		} catch (RapidEnvException e) {
			Assert.assertTrue(e.getCause().getCause() instanceof RapidEnvConfigurationException);
			throw e;
		}
	}
}
