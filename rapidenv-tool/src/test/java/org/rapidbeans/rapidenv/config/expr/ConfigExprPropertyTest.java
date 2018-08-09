/*
 * RapidEnv: ConfigExprFunctionPropertyTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/29/2010
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for Property expressions.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprPropertyTest {

	private static final Installunit testUnit = new Installunit("test");

	private RapidEnvInterpreter renv = null;

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("src/test/resources/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
	}

	@Before
	public void setUp() {
		this.renv = new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
	}

	public void tearDown() {
		this.renv = null;
	}

	/**
	 * Test simple property expansion.
	 */
	@Test
	public void interpretSimple() {
		this.renv.setPropertyValue("prop1", "Abc Xyz");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(testUnit, null, "123${prop1}456", false);
		Assert.assertEquals("123Abc Xyz456", expr.interpret());
	}

	/**
	 * Test expansion of unknown property.
	 */
	@Test
	public void interpretUnknown() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(testUnit, null, "123${propunknown}456", false);
		Assert.assertEquals("123${propunknown}456", expr.interpret());
	}

	/**
	 * Test nested property expansion.
	 */
	@Test
	public void interpretNestedSimple() {
		this.renv.setPropertyValue("p1", "bingo");
		this.renv.setPropertyValue("p2", "p1");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(testUnit, null, "A${${p2}}Z", false);
		Assert.assertEquals("AbingoZ", expr.interpret());
	}

	/**
	 * Test nested property expansion.
	 */
	@Test
	public void interpretNestedComplex() {
		this.renv.setPropertyValue("pp1", "bingo");
		this.renv.setPropertyValue("p2" + PlatformHelper.getOsfamily().name(), "p");
		this.renv.setPropertyValue("p3", "1");
		this.renv.setPropertyValue("p4", "p2");
		ConfigExprTopLevel expr = new ConfigExprTopLevel(testUnit, null, "A${p${${p4}osname()}${p3}}Z", false);
		Assert.assertEquals("AbingoZ", expr.interpret());
		expr = new ConfigExprTopLevel(testUnit, null, "A${'p'${${p4}osname()}${p3}}Z", false);
		Assert.assertEquals("AbingoZ", expr.interpret());
	}
}
