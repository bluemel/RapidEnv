/*
 * RapidEnv: ProjectTest.java
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;

public class ProjectTest {

    @BeforeClass
    public static void setUpClass() {
        if (!new File("profile").exists()) {
            new File("profile").mkdir();
        }
        FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
        new File("testdata/testinstall").mkdir();
        RapidBeansTypeLoader.getInstance().addXmlRootElementBinding(
                "project", "org.rapidbeans.rapidenv.config.Project", true);
    }

    @AfterClass
    public static void tearDownClass() {
        FileHelper.deleteDeep(new File("../../env.dtd"));
        FileHelper.deleteDeep(new File("testdata/testinstall"));
    }

    @Test
    public void testCheckSemanticsOK() {
        Document doc = new Document(new File("testdata/env/env.xml"));
        Project project = (Project) doc.getRoot();
        project.checkSemantics();
    }
}
