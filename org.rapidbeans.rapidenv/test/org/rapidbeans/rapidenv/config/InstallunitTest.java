/*
 * RapidEnv: InstallunitTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 08/15/2010
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;

public class InstallunitTest {

	@BeforeClass
	public static void setUpClass() {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		TypePropertyCollection.setDefaultCharSeparator(',');
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project",
				"org.rapidbeans.rapidenv.config.Project", true);
		FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("profile"));
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	@Test
	public final void testCopyFileNormal() {
		File src = new File("testdata/site/myapp/1.0.2/myapp-1.0.2.zip");
		// src = new File(
		// "F:\\ArchiveSoftware\\Develop\\Java\\eclipse\\3.7.1\\eclipse-modeling-indigo-win32.zip");
		// src = new File(
		// "\\\\bluemix\\ArchivSoftware\\Develop\\Java\\Eclipse\\3.7.1\\eclipse-modeling-indigo-win32.zip");
		File tgt = new File("testdata/testcopy.zip");
		if (tgt.exists()) {
			Assert.assertTrue(tgt.delete());
		}
		Assert.assertFalse(tgt.exists());
		Installunit.copyFile(src, tgt, true, true);
		Assert.assertTrue(FileHelper.filesEqual(src, tgt, true, false));
		tgt.delete();
	}

	@Test
	public void testCheckSemanticsOK() {
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(new File("testdata/env/env.xml"));
		PropertyInterpretedString.unlockIntepretation();
		Installunit jdk = ((Project) doc.getRoot())
				.findInstallunitConfiguration("jdk");
		jdk.checkSemantics();
	}

	@Test(expected = RapidEnvConfigurationException.class)
	public void testCheckSemanticsNoSourceURL() {
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(new File("testdata/env/envNoSourceURL.xml"));
		PropertyInterpretedString.unlockIntepretation();
		Installunit jdk = ((Project) doc.getRoot())
				.findInstallunitConfiguration("jdk");
		try {
			jdk.checkSemantics();
		} catch (RapidEnvConfigurationException e) {
			Assert.assertTrue(e.getMessage().startsWith(
					"No source URL defined for installunit"));
			throw e;
		}
	}

	@Test(expected = RapidEnvConfigurationException.class)
	public void testCheckSemanticsNoHomedir() {
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(new File("testdata/env/envNoHomedir.xml"));
		PropertyInterpretedString.unlockIntepretation();
		Installunit jdk = ((Project) doc.getRoot())
				.findInstallunitConfiguration("jdk");
		try {
			jdk.checkSemantics();
		} catch (RapidEnvConfigurationException e) {
			if (!e.getMessage().startsWith(
					"No home directory defined for installunit")) {
				Assert.fail("Unexpected RapidEnvConfigurationException: "
						+ e.getMessage());
			}
			throw e;
		}
	}

	@Test
	public void testSerialization() throws MalformedURLException {
		Project project = new Project();
		Installunit jdk = new Installunit(new String[] { "", "com.oracle",
				"jdk" });
		Installunit ant = new Installunit(new String[] { "", "org.apache",
				"ant" });
		Installunit maven = new Installunit(new String[] { "", "org.apache",
				"maven" });
		File testfile = new File("testdata/env/test.xml");
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(project);
		PropertyInterpretedString.unlockIntepretation();
		project.addInstallunit(jdk);
		project.addInstallunit(ant);
		project.addInstallunit(maven);
		ant.addDepend(jdk);
		maven.addDepend(jdk);
		doc.setUrl(new URL("file:testdata/env/test.xml"));
		doc.save();
		testfile.delete();
	}

	@Test
	public void testDependenciesSimple() {
		Document doc = new Document(new File("testdata/env/envDepSimple.xml"));
		Project project = (Project) doc.getRoot();
		Installunit jdk = project.findInstallunitConfiguration("jdk");
		Installunit maven = project.findInstallunitConfiguration("maven");
		Installunit ant = project.findInstallunitConfiguration("ant");
		Installunit test01 = project.findInstallunitConfiguration("test01");
		Installunit test02 = project.findInstallunitConfiguration("test02");
		Assert.assertEquals(1, maven.getDepends().size());
		Assert.assertSame(jdk, maven.getDepends().get(0));
		Assert.assertEquals(1, ant.getDepends().size());
		Assert.assertSame(jdk, ant.getDepends().get(0));
		Assert.assertEquals(2, jdk.getDependents().size());
		Assert.assertEquals(ant, jdk.getDependents().get(0));
		Assert.assertEquals(maven, jdk.getDependents().get(1));
		Assert.assertEquals(2, test01.getDepends().size());
		Assert.assertSame(ant, test01.getDepends().get(0));
		Assert.assertSame(maven, test01.getDepends().get(1));
		Assert.assertEquals(1, ant.getDependents().size());
		Assert.assertSame(test01, ant.getDependents().get(0));
		Assert.assertEquals(1, maven.getDependents().size());
		Assert.assertSame(test01, maven.getDependents().get(0));
		Assert.assertEquals(1, test02.getDepends().size());
		Assert.assertSame(test01, test02.getDepends().get(0));
		Assert.assertEquals(1, test01.getDependents().size());
		Assert.assertSame(test02, test01.getDependents().get(0));
	}

	@Test
	public void testSubunits() {
		Document doc = new Document(new File("testdata/env/envDepSub.xml"));
		Project project = (Project) doc.getRoot();
		Installunit test01 = project.findInstallunitConfiguration("test01");
		Assert.assertEquals("org.rapidenv.test01",
				test01.getFullyQualifiedName());
		Assert.assertEquals(new File(
				"testdata/testtargetdir/org/rapidenv/test01/1.0.0")
				.getAbsolutePath(), test01.getHomedirAsFile().getPath());
		Installunit test01_1 = test01.getSubunits().get(0);
		Assert.assertNotNull(test01_1);
		Installunit test01_2_x = project.findInstallunitConfiguration("testx");
		Assert.assertEquals("org.rapidenv.test01/test2/testx",
				test01_2_x.getFullyQualifiedName());
		Installunit test01_2_1 = project
				.findInstallunitConfiguration("org.rapidenv.test01/test2/test1");
		Assert.assertEquals("org.rapidenv.test01/test2/test1",
				test01_2_1.getFullyQualifiedName());
	}

	@Test
	public void testSwapUnits0() {
		final List<Installunit> units = new ArrayList<Installunit>();
		Installunit.swapUnits(units);
		Assert.assertEquals(0, units.size());
	}

	@Test
	public void testSwapUnits1() {
		final List<Installunit> units = new ArrayList<Installunit>();
		units.add(new Installunit(new String[] { "", "bluemel", "unit1" }));
		Installunit.swapUnits(units);
		Assert.assertEquals("bluemel.unit1", units.get(0)
				.getFullyQualifiedName());
	}

	@Test
	public void testSwapUnits3() {
		final List<Installunit> units = new ArrayList<Installunit>();
		units.add(new Installunit(new String[] { "", "bluemel", "unit1" }));
		units.add(new Installunit(new String[] { "", "bluemel", "unit2" }));
		units.add(new Installunit(new String[] { "", "bluemel", "unit3" }));
		Installunit.swapUnits(units);
		Assert.assertEquals("bluemel.unit3", units.get(0)
				.getFullyQualifiedName());
		Assert.assertEquals("bluemel.unit2", units.get(1)
				.getFullyQualifiedName());
		Assert.assertEquals("bluemel.unit1", units.get(2)
				.getFullyQualifiedName());
	}

	@Test
	public void testSwapUnits4() {
		final List<Installunit> units = new ArrayList<Installunit>();
		units.add(new Installunit(new String[] { "", "bluemel", "unit1" }));
		units.add(new Installunit(new String[] { "", "bluemel", "unit2" }));
		units.add(new Installunit(new String[] { "", "bluemel", "unit3" }));
		units.add(new Installunit(new String[] { "", "bluemel", "unit4" }));
		Installunit.swapUnits(units);
		Assert.assertEquals("bluemel.unit4", units.get(0)
				.getFullyQualifiedName());
		Assert.assertEquals("bluemel.unit3", units.get(1)
				.getFullyQualifiedName());
		Assert.assertEquals("bluemel.unit2", units.get(2)
				.getFullyQualifiedName());
		Assert.assertEquals("bluemel.unit1", units.get(3)
				.getFullyQualifiedName());
	}

	@Test(expected = RapidEnvConfigurationException.class)
	public void testAmbigoousSubunit() {
		Document doc = new Document(new File("testdata/env/envDepSub.xml"));
		Project project = (Project) doc.getRoot();
		Installunit test01_2_x = project.findInstallunitConfiguration("testx");
		Assert.assertEquals("org.rapidenv.test01/test2/testx",
				test01_2_x.getFullyQualifiedName());
		try {
			project.findInstallunitConfiguration("test1");
		} catch (RapidEnvConfigurationException e) {
			Assert.assertTrue(
					"Unexpected message text start: " + e.getMessage(), e
							.getMessage().startsWith("Ambigouus tool name"));
			throw e;
		}
	}

	@Test
	public void testGetPackaging() {
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(new File("testdata/env/env.xml"));
		PropertyInterpretedString.unlockIntepretation();
		Project project = (Project) doc.getRoot();
		Installunit ant = project.findInstallunitConfiguration("ant");
		Assert.assertEquals("http://ant.apache.org/xxx.zip", ant.getSourceurl());
		Assert.assertSame(Packaging.zip, ant.getPackaging());
	}

	@Test
	public void testGetHomedir() {
		PropertyInterpretedString.lockIntepretation();
		Document doc = new Document(new File("testdata/env/env.xml"));
		PropertyInterpretedString.unlockIntepretation();
		Project project = (Project) doc.getRoot();
		Installunit ant = project.findInstallunitConfiguration("ant");
		Assert.assertEquals("testdata/testinstall/org/apache/ant/1.8.0",
				ant.getHomedir());
		Installunit antXalanSerializer = project
				.findInstallunitConfiguration("org.apache.ant/xalan.serializer");
		Assert.assertEquals("testdata/testinstall/org/apache/ant/1.8.0/lib",
				antXalanSerializer.getHomedir());
	}

	@Test
	public void testGetHomedirDeep() {
		Document doc = new Document(new File("testdata/env/envDepSub.xml"));
		Project project = (Project) doc.getRoot();
		Installunit test01 = project
				.findInstallunitConfiguration("org.rapidenv.test01");
		Assert.assertEquals("testdata/testtargetdir/org/rapidenv/test01/1.0.0",
				test01.getHomedir());
		Assert.assertEquals(new File(
				"testdata/testtargetdir/org/rapidenv/test01/1.0.0")
				.getAbsolutePath(), test01.getHomedirAsFile().getAbsolutePath());
		Installunit test01_1 = project
				.findInstallunitConfiguration("org.rapidenv.test01/test1");
		Assert.assertEquals(
				"testdata/testtargetdir/org/rapidenv/test01/1.0.0/test1/lib",
				test01_1.getHomedir());
		Installunit test01_2_x = project
				.findInstallunitConfiguration("org.rapidenv.test01/test2/testx");
		Assert.assertEquals(
				"testdata/testtargetdir/org/rapidenv/test01/1.0.0/test2/testx",
				test01_2_x.getHomedir());
	}
}
