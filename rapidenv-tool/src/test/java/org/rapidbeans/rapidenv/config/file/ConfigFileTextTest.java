/*
 * RapidEnv: ConfigFileTextTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 09/22/2010
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

package org.rapidbeans.rapidenv.config.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Project;

public class ConfigFileTextTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("src/test/resources/testinstall").mkdir();
		new File("src/test/resources/testinstall/org/apache/maven/2.1.2").mkdirs();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project", "org.rapidbeans.rapidenv.config.Project",
				true);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
	}

	@Test
	public void testReadConfiguration() {
		Document doc = new Document(new File("src/test/resources/env/envFileText01.xml"));
		Project project = (Project) doc.getRoot();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFileText file = (ConfigFileText) unit.getConfigurations().get(0);
		assertNotNull(file.getTasks());
		final ConfigFileTextTaskInsert task0 = (ConfigFileTextTaskInsert) file.getTasks().get(0);
		assertEquals("1 1 1", task0.getLine());
		assertSame(InsertMode.prepend, task0.getMode());
	}

	@Test
	public void testConfigInsertLine() {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileText01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFile fileConfig = (ConfigFile) unit.getConfigurations().get(0);
		File source = new File(fileConfig.getSourceurlAsUrl().getFile());
		File cfgfile = fileConfig.getPathAsFile();
		if (!cfgfile.getParentFile().exists()) {
			assertTrue(cfgfile.getParentFile().mkdirs());
		}
		if (cfgfile.exists()) {
			assertTrue(cfgfile.delete());
		}
		assertFalse(cfgfile.exists());
		FileHelper.copyFile(source, cfgfile);
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		PrintStream sout = new PrintStream(bStream);
		interpreter.setOut(sout);
		ConfigFileTextTaskInsert insertTask = (ConfigFileTextTaskInsert) fileConfig.getTasks().get(0);
		assertNotNull(insertTask);
		fileConfig.check(true);
		assertFalse(fileConfig.getOk());
	}
}
