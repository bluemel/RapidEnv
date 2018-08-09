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

/**
 * The DTD generator is a unit test instead of a Maven plugin from rapidenv-sdk.
 * The reason is that the DTD generator has dependencies to rapidenv-tool's
 * domain classes.
 * 
 * @author Martin Bluemel
 */
public final class GenerateDtds {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void generateDTDs() throws IOException {
		generatDtd("org.rapidbeans.rapidenv.config.Project", "env.dtd");
		generatDtd("org.rapidbeans.rapidenv.config.ProjectPart", "envPartProject.dtd");
		generatDtd("org.rapidbeans.rapidenv.config.InstallunitPart", "envPartInstallunit.dtd");
	}

	private void generatDtd(String typename, String outfileName) throws IOException {
		TypeRapidBean roottype = TypeRapidBean.forName(typename);
		File outfile = new File("target/generated-dtds/" + outfileName);
		if (!outfile.getParentFile().exists()) {
			assertTrue(outfile.getParentFile().mkdirs());
		}
		if (outfile.exists()) {
			assertTrue(outfile.delete());
		}
		try (final FileWriter writer = new FileWriter(outfile)) {
			TaskGenDtd.generateDTD(roottype, writer, null);
		}
		assertTrue(outfile.exists());
	}
}
