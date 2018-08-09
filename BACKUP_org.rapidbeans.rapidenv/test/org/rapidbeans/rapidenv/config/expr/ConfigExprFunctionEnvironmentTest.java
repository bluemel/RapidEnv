/*
 * RapidEnv: ConfigExprFunctionEnvironmentTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 07/03/2010
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
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;

/**
 * Tests for class ConfigExprFunctionHomedir.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionEnvironmentTest {

	static Level platformHelperLevel = null;

	@BeforeClass
	public static void setUpClass() {
		platformHelperLevel = PlatformHelper.getLogger().getLevel();
		PlatformHelper.getLogger().setLevel(Level.WARNING);
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		TypePropertyCollection.setDefaultCharSeparator(',');
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
		PlatformHelper.getLogger().setLevel(platformHelperLevel);
	}

	/**
	 * Test without install unit argument. The home directory of the enclosing
	 * install unit should be taken.
	 */
	@Test
	public void testInterpret() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "testdata/env/env.xml", "s" }));
		// AntGateway ant = new AntGateway(new
		// File("testdata/ant/ant_win.properties"));
		switch (PlatformHelper.getOsfamily()) {
		case windows:
			ConfigExprTopLevel expr1 = new ConfigExprTopLevel(new Installunit(new String[] { "", "jdk" }), null,
			        "environment('USERNAME')", false);
			Assert.assertEquals(PlatformHelper.username(), expr1.interpret());
			break;
		case linux:
			ConfigExprTopLevel expr2 = new ConfigExprTopLevel(new Installunit(new String[] { "", "jdk" }), null,
			        "environment('LOGNAME')", false);
			Assert.assertEquals(PlatformHelper.username(), expr2.interpret());
			break;
		default:
			Assert.fail("Operating system (family) \"" + PlatformHelper.getOsfamily().name() + "\" not yet tested.");
		}
	}
}
