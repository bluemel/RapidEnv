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
import static org.junit.Assert.assertNull;
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
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.core.util.XmlHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.RapidEnvTestHelper;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.InstallState;
import org.rapidbeans.rapidenv.config.Installations;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.InstallunitData;
import org.rapidbeans.rapidenv.config.Project;
import org.w3c.dom.Node;

public class ConfigFileXmlTest {

	@BeforeClass
	public static void setUpClass() throws MalformedURLException {
		TypePropertyCollection.setDefaultCharSeparator(',');
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
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
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileXml01.xml", "s" }));
		if (interpreter.getEnvironmentInstallationsFile().exists()) {
			assertTrue(interpreter.getEnvironmentInstallationsFile().delete());
		}

		Installations is = interpreter.getInstallations();
		InstallunitData data1 = new InstallunitData();
		data1.setFullname("org.apache.maven");
		data1.setVersion(new Version("2.1.2"));
		data1.setInstallstate(InstallState.installed);
		is.addInstallunit(data1);
		interpreter.saveInstallations();
	}

	@AfterClass
	public static void tearDownClass() {
		CmdRenv cmd = new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml" });
		RapidEnvTestHelper.tearDownProfile(new RapidEnvInterpreter(cmd));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
		File file1 = new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".properties");
		file1.delete();
		new File("renv_" + PlatformHelper.username() + "_" + PlatformHelper.hostname() + ".cmd").delete();
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileXml01.xml", "s" }));
		if (interpreter.getEnvironmentInstallationsFile().exists()) {
			assertTrue(interpreter.getEnvironmentInstallationsFile().delete());
		}
		new File("../../env.dtd").delete();
	}

	@Test
	public void testReadConfiguration() {
		Document doc = new Document(new File("src/test/resources/env/envFileXml01.xml"));
		Project project = (Project) doc.getRoot();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFileXml file = (ConfigFileXml) unit.getConfigurations().get(0);
		assertNotNull(file.getTasks());
		final ConfigFileXmlTaskSetnodevalue task0 = (ConfigFileXmlTaskSetnodevalue) file.getTasks().get(0);
		assertEquals("//settings/proxies/proxy/active", task0.getPath());
		assertEquals("false", task0.getValue());
	}

	/**
	 * test configuring an XML file.
	 *
	 * @throws FileNotFoundException
	 */
	@Test
	public void testConfigSetnodevalueCheckConfigRequired() throws FileNotFoundException {
		RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileXml01.xml", "s" }));
		Project project = interpreter.getProject();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFile fileConfig = (ConfigFile) unit.getConfigurations().get(0);
		assertEquals("conf/settings.xml", fileConfig.getPath());
		File source = new File(fileConfig.getSourceurlAsUrl().getFile());
		System.out.println("source = \"" + source.getAbsolutePath() + "\"");
		File cfgfile = fileConfig.getPathAsFile();
		if (cfgfile.exists()) {
			assertTrue(cfgfile.delete());
		} else if (!cfgfile.getParentFile().exists()) {
			assertTrue(cfgfile.getParentFile().mkdirs());
		}
		assertFalse(cfgfile.exists());
		assertTrue(cfgfile.getParentFile().exists());
		assertTrue(cfgfile.getParentFile().isDirectory());
		FileHelper.copyFile(source, cfgfile);
		System.out.println("cfgfile = \"" + cfgfile.getAbsolutePath() + "\"");
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		PrintStream sout = new PrintStream(bStream);
		interpreter.execute(System.in, sout);
		assertFalse(fileConfig.getOk());

		interpreter = new RapidEnvInterpreter(
				new CmdRenv(new String[] { "-env", "src/test/resources/env/envFileXml01.xml", "c" }));
		bStream = new ByteArrayOutputStream();
		sout = new PrintStream(bStream);
		interpreter.execute(System.in, sout);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, cfgfile);
		assertEquals("false", editor.retrieveNode("//settings/proxies/proxy/active").getFirstChild().getNodeValue());
		assertEquals("myproto",
				editor.retrieveNode("//settings/proxies/proxy/protocol").getFirstChild().getNodeValue());
		assertEquals("myserver1",
				editor.retrieveNode("//settings/servers/server[1]/id").getFirstChild().getNodeValue());
		assertEquals("yyy", editor.retrieveNode("//settings/testnode02/@attr1").getNodeValue());
		assertEquals("hello1", editor.retrieveNode("//settings/localRepository/@testattr").getNodeValue());
		assertEquals("hello2", editor.retrieveNode("//settings/localRepository/@testattr2").getNodeValue());
		assertEquals("hello first",
				editor.retrieveNode("//settings/emptytestnode01/firstnewsubelement").getFirstChild().getNodeValue());
		assertEquals("hello attr", editor.retrieveNode("//settings/emptytestnode01/firstnewsubelement/@test1")
				.getFirstChild().getNodeValue());
		assertNotNull(editor.retrieveNode("//settings/testnode01"));
		assertNull(editor.retrieveNode("//settings/testnode01/subnodetodelete"));
		assertNull(editor.retrieveNode("//settings/testnode01/@attr1"));
	}

	@Test
	public void testConfigSetnodevalueCreateNodeWithId01() {
		File fileToConfigure = new File("src/test/resources/conf/workbenchTest.xml");
		try {
			FileHelper.copyFile(new File("src/test/resources/conf/workbench.xml"), fileToConfigure);
			String path = "//workbench/window/coolbarLayout/coolItem" + "[@id='org.eclipse.wst.server.ui.editor']"
					+ "/@itemType";
			ConfigFileXml configFile = new ConfigFileXml();
			configFile.setPath(fileToConfigure.getAbsolutePath());
			final ConfigFileEditorXml editor = new ConfigFileEditorXml(configFile, fileToConfigure);
			editor.setNodeValue(path, "typePlaceholder", true);
			editor.save();
			Node root = XmlHelper.getDocumentTopLevel(fileToConfigure);
			assertEquals("typePlaceholder", XmlHelper.getNodeValue(root,
					"window/coolbarLayout/coolItem" + "[@id='org.eclipse.wst.server.ui.editor']" + "/@itemType"));
		} finally {
			if (fileToConfigure.exists()) {
				assertTrue(fileToConfigure.delete());
			}
		}
	}
}
