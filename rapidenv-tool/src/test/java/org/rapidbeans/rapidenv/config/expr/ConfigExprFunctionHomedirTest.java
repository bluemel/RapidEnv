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
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Tests for class ConfigExprFunctionHomedir.
 * 
 * @author Martin Bluemel
 */
public class ConfigExprFunctionHomedirTest {

	@BeforeClass
	public static void setUpClass() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("target/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("target/testinstall"));
	}

	/**
	 * Test without install unit argument. The home directory of the enclosing
	 * install unit should be taken.
	 */
	@Test
	public void testInterpretWithoutArgument() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "", "jdk" }), null,
				"homedir()", false);
		Assert.assertEquals(new File("target/testinstall/jdk/1.6.0").getAbsolutePath(), expr.interpret());
		expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "org.apache", "ant" }), null, "homedir()",
				false);
		Assert.assertEquals(new File("target/testinstall/org/apache/ant/1.8.0").getAbsolutePath(), expr.interpret());
	}

	@Test
	public void testInterpretWithNonDefaultHomedir() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "org.apache", "maven" }),
				null, "homedir()", false);
		Assert.assertEquals(new File("/h/opt/maven").getAbsolutePath(), expr.interpret());
	}

	/**
	 * Test with fully qualified name.
	 */
	@Test
	public void testInterpretFully() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "homedir('jdk')", false);
		Assert.assertEquals(new File("target/testinstall/jdk/1.6.0").getAbsolutePath(), expr.interpret());
		expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "homedir('org.apache.ant')", false);
		Assert.assertEquals(new File("target/testinstall/org/apache/ant/1.8.0").getAbsolutePath(), expr.interpret());
	}

	/**
	 * Test with pure name only.
	 */
	@Test
	public void testInterpretPureName() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "jdk" }), null,
				"homedir('ant')", false);
		Assert.assertEquals(new File("target/testinstall/org/apache/ant/1.8.0").getAbsolutePath(), expr.interpret());
	}

	/**
	 * Test interpret with old version.
	 */
	@Test
	public void testInterpretOldVersion() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "", "jdk" }), null,
				"homedir('ant', '1.7.1')", false);
		Assert.assertEquals(new File("target/testinstall/org/apache/ant/1.7.1").getAbsolutePath(), expr.interpret());
	}

	/**
	 * Test with ambiguous pure name.
	 */
	@Test(expected = RapidEnvConfigurationException.class)
	public void testInterpretPureNameAmbi() {
		new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/envAmbi.xml", "s" }));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit(new String[] { "org.apache", "ant" }), null,
				"homedir('ant')", false);
		try {
			expr.interpret();
		} catch (RapidEnvConfigurationException e) {
			Assert.assertEquals("Ambiguous tool name \"ant\"", e.getMessage());
			throw e;
		}
	}
}
