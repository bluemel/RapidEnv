/*
 * RapidEnv: AntTaskTest.java
 * 
 * Copyright (C) 2011 Martin Bluemel
 * 
 * Creation Date: 12/12/2011
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

package org.rapidbeans.rapidenv.config.cmd;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.tools.ant.taskdefs.Copy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.rapidenv.RapidEnvTestHelper;

public class AntTaskTest {

	@BeforeClass
	public static void setUpClass() {
		// if (!new File("profile").exists()) {
		// new File("profile").mkdir();
		// }
		// FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
		// new File("testdata/testinstall").mkdir();
		// new File("testdata/testinstall/org/apache/maven/2.1.2").mkdirs();
		// RapidBeansTypeLoader.getInstance().addXmlRootElementBinding(
		// "project", "org.rapidbeans.rapidenv.config.Project", true);
	}

	@AfterClass
	public static void tearDownClass() {
		// FileHelper.deleteDeep(new File("../../env.dtd"));
		// FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	@Test
	public void testExecuteCopy() throws IOException {
		try {
			Assert.assertFalse(new File("testdata/unpack/artifacts1.xml").exists());

			AntTask task = new AntTask();
			task.setAnttaskname("copy");
			Assert.assertSame(Copy.class, task.getAnttaskclass());
			Argument arg1 = new Argument();
			arg1.setName("file");
			arg1.setValue(new File("testdata/unpack/artifacts.xml").getAbsolutePath());
			arg1.setValuetype(Argtype.file);
			task.addArgument(arg1);
			Argument arg2 = new Argument();
			arg2.setName("tofile");
			arg2.setValue(new File("testdata/unpack/artifacts1.xml").getAbsolutePath());
			arg2.setValuetype(Argtype.file);
			task.addArgument(arg2);

			task.execute();

			Assert.assertTrue(new File("testdata/unpack/artifacts1.xml").exists());
			RapidEnvTestHelper.assertFilesEqual(new File("testdata/unpack/artifacts.xml"), new File(
					"testdata/unpack/artifacts1.xml"));
		} finally {
			if (new File("testdata/unpack/artifacts1.xml").exists()) {
				Assert.assertTrue(new File("testdata/unpack/artifacts1.xml").delete());
			}
		}
	}
}
