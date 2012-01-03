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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
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
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.InstallunitData;
import org.rapidbeans.rapidenv.config.Project;

public class ConfigFileTest {

    @BeforeClass
    public static void setUpClass() throws MalformedURLException {
        FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
        new File("testdata/testinstall").mkdir();
        new File("testdata/testinstall/org/apache/maven/2.1.2").mkdirs();
		InstallunitData data = new InstallunitData();
		data.setFullname("org.apache.maven");
		data.setVersion(new Version("2.1.2"));
		data.setInstallstate(InstallState.installed);
		Document doc = new Document(data);
		doc.setUrl(new File("testdata/testinstall/org/apache/maven/2.1.2/.renvstate.xml").toURI().toURL());
		doc.save();
        RapidBeansTypeLoader.getInstance().addXmlRootElementBinding(
                "project", "org.rapidbeans.rapidenv.config.Project", true);
     }

    @AfterClass
    public static void tearDownClass() {
        CmdRenv cmd = new CmdRenv(new String[]{
                "-env",
                "testdata/env/env.xml"
        });
        RapidEnvTestHelper.tearDownProfile(new RapidEnvInterpreter(cmd));
        FileHelper.deleteDeep(new File("../../env.dtd"));
        FileHelper.deleteDeep(new File("testdata/testinstall"));
        new File("renv_" + PlatformHelper.username() + "_"
                + PlatformHelper.hostname() + ".cmd").delete();
        new File("renv_" + PlatformHelper.username() + "_"
                + PlatformHelper.hostname() + ".properties").delete();
    }

    @Test
    public void testReadConfiguration() {
        if (!new File("profile").exists()) {
            new File("profile").mkdir();
        }
        Document doc = new Document(new File("testdata/env/envFile01.xml"));
        Project project = (Project) doc.getRoot();
        Installunit unit = project.findInstallunitConfiguration("maven");
        ConfigFile file = (ConfigFile) unit.getConfigurations().get(0);
        Assert.assertEquals("conf/settings.xml", file.getPath());
        Assert.assertEquals("file:testdata/conf/mavensettings1.xml",
                file.getSourceurlAsUrl().toString());
    }

    @Test
    public void testConfigAddFileCheckCopyNotexistNotExistent() {
        RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envFile01.xml", "s"}));
        Project project = interpreter.getProject();
        Installunit unit = project.findInstallunitConfiguration("maven");
        Assert.assertSame(InstallStatus.configurationrequired, unit.getInstallationStatus(CmdRenvCommand.stat));
        ConfigFile file = (ConfigFile) unit.getConfigurations().get(0);
        final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        final PrintStream sout = new PrintStream(bStream);
        interpreter.execute(System.in, sout);
        Assert.assertEquals(false, file.check(false));
        String issue = file.getIssue();
        Assert.assertTrue("Issue does not start with \"File\"",
            issue.startsWith("File to configure "));
        Assert.assertTrue(issue.endsWith(" does not exist."));
    }

    @Test
    public void testConfigAddFileCheckCopyNotexistExistent()
        throws IOException {
        try {
        RapidEnvInterpreter interpreter = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envFile01.xml", "s"}));
        Project project = interpreter.getProject();
        Installunit unit = project.findInstallunitConfiguration("maven");
        ConfigFile fileConfiguration = (ConfigFile) unit.getConfigurations().get(0);
        final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        final PrintStream sout = new PrintStream(bStream);
        // create the file before testing if configuration is necessary
        new File("testdata/testinstall/org/apache/maven/2.1.2/conf").mkdirs();
        Assert.assertTrue(new File(
                "testdata/testinstall/org/apache/maven/2.1.2/conf/settings.xml").createNewFile());
        interpreter.execute(System.in, sout);
        Assert.assertEquals("configuration should be ok",
                true, fileConfiguration.check(false));
        Assert.assertNull(fileConfiguration.getIssue());
        } finally {
            FileHelper.deleteDeep(new File("testdata/testinstall/org/apache/maven/2.1.2/conf"));
        }
    }
}
