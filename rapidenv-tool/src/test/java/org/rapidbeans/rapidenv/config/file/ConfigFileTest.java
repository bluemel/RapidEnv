/*
 * RapidEnv: ConfigFileTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 09/08/2010
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.CmdRenvCommand;
import org.rapidbeans.rapidenv.InstallStatus;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.RapidEnvTestHelper;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.InstallState;
import org.rapidbeans.rapidenv.config.Installations;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.InstallunitData;
import org.rapidbeans.rapidenv.config.Project;

public class ConfigFileTest {

	@BeforeClass
	public static void setUpClass() throws MalformedURLException {
		TypePropertyCollection.setDefaultCharSeparator(',');
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("target/testinstall").mkdir();
		new File("target/testinstall/org/apache/maven/2.1.2").mkdirs();
		InstallunitData data = new InstallunitData();
		data.setFullname("org.apache.maven");
		data.setVersion(new Version("2.1.2"));
		data.setInstallstate(InstallState.installed);
		Document doc = new Document(data);
		doc.setUrl(new File("target/testinstall/org/apache/maven/2.1.2/.renvstate.xml").toURI().toURL());
		doc.save();

		Installations insts = new Installations();
		data = new InstallunitData();
		data.setFullname("org.apache.maven");
		data.setVersion(new Version("2.1.2"));
		data.setInstallstate(InstallState.installed);
		insts.addInstallunit(data);
		doc = new Document(insts);
		doc.setUrl(new File("src/test/resources/env/.renvinstall.xml").toURI().toURL());
		doc.save();

		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project", "org.rapidbeans.rapidenv.config.Project",
				true);
	}

	@AfterClass
	public static void tearDownClass() {
		CmdRenv cmd = new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml" });
		RapidEnvTestHelper.tearDownProfile(new RapidEnvInterpreter(cmd));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("target/testinstall"));
		new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".cmd").delete();
		new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".properties").delete();
		new File("src/test/resources/env/.renvinstall.xml").delete();
	}

	@Test
	public void testReadConfiguration() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		Document doc = new Document(new File("src/test/resources/env/envFile01.xml"));
		Project project = (Project) doc.getRoot();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFile file = (ConfigFile) unit.getConfigurations().get(0);
		assertEquals("conf/settings.xml", file.getPath());
		assertEquals("file:src/test/resources/conf/mavensettings1.xml", file.getSourceurlAsUrl().toString());
	}

	@Test
	public void testConfigAddFileCheckCopyNotexistNotExistent() {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFile01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("maven");
		assertSame(InstallStatus.configurationrequired, interpreter.getInstallationStatus(unit, CmdRenvCommand.stat));
		ConfigFile file = (ConfigFile) unit.getConfigurations().get(0);
		final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		final PrintStream sout = new PrintStream(bStream);
		interpreter.execute(System.in, sout);
		assertEquals(false, file.check(false));
		String issue = file.getIssue();
		assertTrue("Issue does not start with \"File\"", issue.startsWith("File to configure "));
		assertTrue(issue.endsWith(" does not exist."));
	}

	@Test
	public void testConfigAddFileCheckCopyNotexistExistent() throws IOException {
		try {
			RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
					new CmdRenv(new String[] { "-env", "src/test/resources/env/envFile01.xml", "s" }));
			Project project = interpreter.getProject();
			Installunit unit = project.findInstallunitConfiguration("maven");
			ConfigFile fileConfiguration = (ConfigFile) unit.getConfigurations().get(0);
			final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			final PrintStream sout = new PrintStream(bStream);
			if (!new File("target/testinstall/org/apache/maven/2.1.2/conf").exists()) {
				new File("target/testinstall/org/apache/maven/2.1.2/conf").mkdirs();
			}
			if (!new File("target/testinstall/org/apache/maven/2.1.2/conf/settings.xml").exists()) {
				assertTrue(new File("target/testinstall/org/apache/maven/2.1.2/conf/settings.xml").createNewFile());
			}
			assertTrue(new File("target/testinstall/org/apache/maven/2.1.2/conf/settings.xml").exists());
			interpreter.execute(System.in, sout);
			assertEquals("configuration should be ok", true, fileConfiguration.check(false));
			assertNull(fileConfiguration.getIssue());
		} finally {
			FileHelper.deleteDeep(new File("target/testinstall/org/apache/maven/2.1.2/conf"));
		}
	}
}
