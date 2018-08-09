/*
 * RapidEnv: ConfigFilePropertiesTest.java
 * 
 * Copyright (C) 2010 Martin Bluemel
 * 
 * Creation Date: 09/17/2010
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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.Project;

public class ConfigFileEditorPropertiesTest {

	@BeforeClass
	public static void setUpClass() {
		FileHelper.deleteDeep(new File("profile"));
		new File("profile").mkdir();
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
		new File("src/test/resources/testinstall").mkdir();
		new File("src/test/resources/testinstall/org/apache/maven/2.1.2").mkdirs();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding(
				"project", "org.rapidbeans.rapidenv.config.Project", true);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("profile"));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
	}

	@Test
	public void testReadConfiguration() {
		Document doc = new Document(new File("src/test/resources/env/envFileProperties01.xml"));
		Project project = (Project) doc.getRoot();
		Installunit unit = project.findInstallunitConfiguration("maven");
		ConfigFileProperties file = (ConfigFileProperties) unit.getConfigurations().get(0);
		Assert.assertNotNull(file.getTasks());
		final ConfigFilePropertiesTaskSetpropvalue task0 =
				(ConfigFilePropertiesTaskSetpropvalue) file.getTasks().get(0);
		Assert.assertEquals("prop.1", task0.getName());
		Assert.assertEquals("xyz", task0.getValue());
	}

	@Test
	public void createAndWriteNewPropertiesFile() throws IOException {
		if (new File("src/test/resources/conf/test.properties").exists()) {
			Assert.assertTrue(new File("src/test/resources/conf/test.properties").delete());
		}
		ConfigFileEditorProperties propedit = new ConfigFileEditorProperties(new ConfigFileProperties(), new File(
				"src/test/resources/conf/test.properties"));
		propedit.setCreateIfNotExists(true);
		propedit.setProperty("[sect1]", "prop1", "val11");
		propedit.setProperty("[sect1]", "prop2", "val12");
		propedit.setProperty("[sect2]", "prop1", "val21");
		propedit.setProperty("[sect2]", "prop2", "val22");
		propedit.save();
		Assert.assertTrue(FileHelper.filesEqual(new File("src/test/resources/conf/test.properties"), new File(
				"src/test/resources/conf/testref01.properties"), true, true));
		Assert.assertTrue(new File("src/test/resources/conf/test.properties").delete());
	}

	@Test
	public void readAndChangePropertiesFile() throws IOException {
		FileHelper.copyFile(new File("src/test/resources/conf/testref01.properties"), new File("src/test/resources/conf/test.properties"));
		ConfigFileEditorProperties propedit = new ConfigFileEditorProperties(new ConfigFileProperties(), new File(
				"src/test/resources/conf/test.properties"));
		Assert.assertEquals("val22", propedit.getProperty("[sect2]", "prop2"));
		Assert.assertEquals("val11", propedit.getProperty("[sect1]", "prop1"));
		Assert.assertEquals("val21", propedit.getProperty("[sect2]", "prop1"));
		Assert.assertEquals("val12", propedit.getProperty("[sect1]", "prop2"));
		propedit.setProperty("[sect1]", "prop1", "val11a");
		propedit.setProperty("[sect1]", "prop2", "val12a");
		propedit.setProperty("[sect2]", "prop1", "val21a");
		propedit.setProperty("[sect2]", "prop2", "val22a");
		propedit.setProperty("[sect3]", "prop1", "val31");
		propedit.setProperty("[sect3]", "prop2", "val32");
		propedit.save();
		Assert.assertTrue(FileHelper.filesEqual(new File("src/test/resources/conf/test.properties"), new File(
				"src/test/resources/conf/testref02.properties"), true, true));
		Assert.assertTrue(new File("src/test/resources/conf/test.properties").delete());
	}
}
