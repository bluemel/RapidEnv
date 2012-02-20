/*
 * RapidEnv: PropertyTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/02/2010
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

package org.rapidbeans.rapidenv.config;


import java.io.File;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;

public class PropertyTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding(
				"project", "org.rapidbeans.rapidenv.config.Project", true);
		CmdRenv cmd = new CmdRenv(new String[]{
				"-env",
				"testdata/env/env.xml"
		});
		new RapidEnvInterpreter(cmd);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("profile"));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	@Test
	public void testLoadProperties() {
		Document doc = new Document(new File("testdata/env/envWithPathext.xml"));
		Project project = (Project) doc.getRoot();
		Property cmdPath = project.findPropertyConfiguration("cmd.path");
		Assert.assertEquals(2, cmdPath.getSpecificvalues().size());
		switch (PlatformHelper.getOs()) {
		case windows:
			Assert.assertTrue(cmdPath.getValue(),
					cmdPath.getValue().startsWith("D:\\h\\opt\\maven\\bin;"));
			break;
		case linux:
			String expected = cmdPath.getValue();
			Assert.assertTrue(expected,
					expected.startsWith("/h/opt/maven/bin:"));
			break;
		}
	}
}
