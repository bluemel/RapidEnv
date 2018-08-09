/*
 * RapidEnv: ConfigFileXmlTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 09/10/2010
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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.RapidEnvTestHelper;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.InstallState;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.InstallunitData;
import org.rapidbeans.rapidenv.config.Project;

public class ConfigFilePropertiesTest {

	@BeforeClass
	public static void setUpClass() throws MalformedURLException {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("src/test/resources/testinstall").mkdir();
		new File("src/test/resources/testinstall/org/apache/maven/2.1.2").mkdirs();
		InstallunitData data = new InstallunitData();
		data.setFullname("org.apache.maven");
		data.setVersion(new Version("2.1.2"));
		data.setInstallstate(InstallState.installed);
		Document doc = new Document(data);
		doc.setUrl(new File("src/test/resources/testinstall/org/apache/maven/2.1.2/.renvstate.xml").toURI().toURL());
		doc.save();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project", "org.rapidbeans.rapidenv.config.Project",
				true);
	}

	@AfterClass
	public static void tearDownClass() {
		CmdRenv cmd = new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml" });
		RapidEnvTestHelper.tearDownProfile(new RapidEnvInterpreter(cmd));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
		File file1 = new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".properties");
		file1.delete();
		new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".cmd").delete();
	}

	@Test
	public void testReadConfiguration() {
		Document doc = new Document(new File("src/test/resources/env/envFileProperties01.xml"));
		Project project = (Project) doc.getRoot();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFileProperties file = (ConfigFileProperties) unit.getConfigurations().get(0);
		assertNotNull(file.getTasks());
		final ConfigFilePropertiesTaskSetpropvalue task0 = (ConfigFilePropertiesTaskSetpropvalue) file.getTasks()
				.get(0);
		assertEquals("prop.1", task0.getName());
		assertEquals("xyz", task0.getValue());
		final ConfigFilePropertiesTaskDeleteprop task1 = (ConfigFilePropertiesTaskDeleteprop) file.getTasks().get(1);
		assertEquals("prop.del", task1.getName());
	}

	/**
	 * test configuring an XML file.
	 *
	 * @throws FileNotFoundException
	 */
	@Test
	public void testConfigSetnodevalueCheckConfigRequired() throws FileNotFoundException {
		File cfgfile = null;
		try {
			RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
					new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileProperties01.xml", "s" }));
			Project project = interpreter.getProject();
			Installunit unit = project.findInstallunitConfiguration("maven");
			ConfigFile fileConfig = (ConfigFile) unit.getConfigurations().get(0);
			assertEquals("conf/settings.xml", fileConfig.getPath());
			File source = new File(fileConfig.getSourceurlAsUrl().getFile());
			cfgfile = fileConfig.getPathAsFile();
			if (cfgfile.exists()) {
				assertTrue(cfgfile.delete());
			} else {
				assertTrue(cfgfile.getParentFile().mkdirs());
			}
			assertFalse(cfgfile.exists());
			FileHelper.copyFile(source, cfgfile);
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			PrintStream sout = new PrintStream(bStream);
			interpreter.execute(System.in, sout);
			assertFalse(fileConfig.getOk());

			interpreter = new RapidEnvInterpreter(
					new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileProperties01.xml", "c" }));
			bStream = new ByteArrayOutputStream();
			sout = new PrintStream(bStream);
			interpreter.execute(System.in, sout);
			project = interpreter.getProject();
			unit = project.findInstallunitConfiguration("maven");
			/* ConfigFileEditorProperties editor = */
			new ConfigFileEditorProperties(null, cfgfile);
			ConfigFileProperties file = (ConfigFileProperties) unit.getConfigurations().get(0);
			final ConfigFilePropertiesTaskSetpropvalue task0 = (ConfigFilePropertiesTaskSetpropvalue) file.getTasks()
					.get(0);
			assertEquals("prop.1", task0.getName());
			assertEquals("xyz", task0.getValue());
		} finally {
			if (cfgfile != null && cfgfile.exists()) {
				cfgfile.delete();
			}
		}
	}
}
