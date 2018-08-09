/*
 * RapidEnv: PreprocessorTest.java
 *
 * Copyright (C) 2010 - 2013 Martin Bluemel
 *
 * Creation Date: 03/28/2013
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
package org.rapidbeans.rapidenv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PreprocessorTest {

	@Test
	public void testEvalIncludeStatementXMLSingleLine() throws IOException {
		File currentFile = new File("src/test/resources/env/env.xml");
		assertTrue(currentFile.exists());
		File includedFileFixture = new File("src/test/resources/env/envincl.xml").getCanonicalFile();
		assertTrue(includedFileFixture.exists());
		File includedFile = Preprocessor.evalIncludeStatementXmlSingleLine(currentFile,
				"<include file=\"envincl.xml\"/>");
		assertTrue(includedFile.exists());
		assertEquals(includedFileFixture, includedFile);
	}

	@Test
	public void testEvalIncludeStatementSame() throws IOException {
		File currentFile = new File("src/test/resources/env/env.xml");
		assertTrue(currentFile.exists());
		File includedFileFixture = new File("src/test/resources/env/envDepSimple.xml").getCanonicalFile();
		assertTrue(includedFileFixture.exists());
		File includedFile = Preprocessor.evalIncludeStatement(currentFile, "include \"envDepSimple.xml\"");
		assertTrue(includedFile.exists());
		assertEquals(includedFileFixture, includedFile);
	}

	@Test
	public void testEvalIncludeStatementAbsolute() throws IOException {
		File currentFile = new File("src/test/resources/env/env.xml");
		assertTrue(currentFile.exists());
		File includedFileFixture = new File("src/test/resources/env/envFile01.xml").getCanonicalFile();
		assertTrue(includedFileFixture.exists());
		File includedFile = Preprocessor.evalIncludeStatement(currentFile,
				"include \"" + System.getProperty("user.dir").replace(File.separatorChar, '/')
						+ "/src/test/resources/env/envFile01.xml\"");
		assertTrue(includedFile.exists());
		assertEquals(includedFileFixture, includedFile);
	}

	@Test
	public void testEvalIncludeStatementRelative() throws IOException {
		File currentFile = new File("src/test/resources/env/env.xml");
		assertTrue(currentFile.exists());
		File includedFileFixture = new File("src/test/resources/conf/workbench.xml").getCanonicalFile();
		assertTrue(includedFileFixture.exists());
		File includedFile = Preprocessor.evalIncludeStatement(currentFile, "include \"../conf/workbench.xml\"");
		assertTrue(includedFile.exists());
		assertEquals(includedFileFixture, includedFile);
	}

	@Test
	public void testReadXmlEncoding() {
		assertEquals("ISO-SCHLAGMICHTOT", Preprocessor
				.extractEncoding("<?xml version=\"1.0\" encoding=\"ISO-SCHLAGMICHTOT\" standalone=\"yes\"?>"));
		assertEquals("ISO-SCHLAGMICHTOT", Preprocessor
				.extractEncoding("<?xml version=\"1.0\" encoding =\"ISO-SCHLAGMICHTOT\" standalone=\"yes\"?>"));
		assertEquals("ISO-SCHLAGMICHTOT", Preprocessor
				.extractEncoding("<?xml version=\"1.0\" encoding = \"ISO-SCHLAGMICHTOT\" standalone=\"yes\"?>"));
	}
}
