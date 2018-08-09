/*
 * Rapid Beans Framework, SDK, Ant Tasks: TaskGenDtdTest.java
 * 
 * Copyright (C) 2009 Martin Bluemel
 * 
 * Creation Date: 05/11/2010
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

package org.rapidbeans.rapidenv.sdk.ant;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.core.util.FileHelper;

/**
 * Unit TestCase (Unit Tests).
 * 
 * @author Martin Bluemel
 */
public final class TaskGenDtdTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateDTD() throws IOException {
		TypeRapidBean roottype = TypeRapidBean.forName("org.rapidbeans.rapidenv.sdk.ant.Project");
		File outfile = new File("target/out.dtd");
		if (outfile.exists()) {
			assertTrue(outfile.delete());
		}
		final FileWriter writer = new FileWriter(outfile);
		TaskGenDtd.generateDTD(roottype, writer, null);
		writer.close();
		assertTrue(FileHelper.filesEqual(new File("src/test/resources/out_ref.dtd"), outfile, true, true));
	}
}
