/*
 * RapidEnv: RapidEnvInterpreterDependencyTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 10/21/2010
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

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.rapidenv.cmd.CmdRenv;
import org.rapidbeans.rapidenv.config.Installunit;
import org.rapidbeans.rapidenv.config.RapidEnvConfigurationException;

/**
 * Unit tests for the RapidEnv command interpreter.
 *
 * @author Martin Bluemel
 */
public class RapidEnvInterpreterDependencyTest {

    @BeforeClass
    public static void setUpClass() {
        if (!new File("profile").exists()) {
            new File("profile").mkdir();
        }
        TypePropertyCollection.setDefaultCharSeparator(',');
        FileHelper.copyFile(new File("env.dtd"), new File("../../env.dtd"));
        new File("testdata/testinstall").mkdir();
    }

    @AfterClass
    public static void tearDownClass() {
		FileHelper.deleteDeep(new File("profile"));
        FileHelper.deleteDeep(new File("../../env.dtd"));
        FileHelper.deleteDeep(new File("testdata/testinstall"));
    }

    /**
     * All install units are taken by default (if no install unit is specified).
     * The sequence of the units is as they are defined in the environment
     * definition.
     */
    @Test
    public void testInstallUnitsToProcessDefaultStatusSimple() {
        checkInstallUnitsToProcessSorting("testdata/env/env.xml", "s",
                new String[]{
                "jdk",
                "org.apache.ant",
                "org.apache.ant/xalan.serializer",
                "org.apache.maven",
                "org.rapidbeans.ambitool",
                "org.rapidbeans.alt.ambitool"
        });
    }

    /**
     * All install units are taken by default
     * if no install unit is specified.
     * The sequence of the units is as they are defined in the environment
     * definition.
     */
    @Test
    public void testInstallUnitsToProcessDefaultStatusComplex() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub.xml", "s",
                new String[]{
                "jdk",
                "org.apache.ant",
                "org.apache.maven",
                "org.rapidenv.test01",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test02"
        });
    }

    /**
     * All install units are taken by default.
     * The sequence of the units regards dependencies
     * but in this case is as defined.
     */
    @Test
    public void testInstallUnitsToProcessDefaultInstallComplex() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub.xml", "i",
                new String[]{
                "jdk",
                "org.apache.ant",
                "org.apache.maven",
                "org.rapidenv.test01",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test02"
        });
    }

    /**
     * All install units are taken by default.
     * The sequence of the units is as they are defined in the environment
     * definition but depth fist.
     */
    @Test
    public void testInstallUnitsToProcessDefaultDeinstallComplex() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub.xml", "d",
                new String[]{
                "org.rapidenv.test02",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test01",
                "org.apache.ant",
                "org.apache.maven",
                "jdk",
        });
    }

    /**
     * All install units are taken by default
     * if no install unit is specified.
     * The sequence of the units is as they are defined in the environment
     * definition.
     */
    @Test
    public void testInstallUnitsToProcessDefaultStatusComplex2() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub02.xml", "s",
                new String[]{
                "jdk",
                "org.apache.ant",
                "org.apache.maven",
                "org.rapidenv.test01",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test02"
        });
    }

    /**
     * All install units are taken by default.
     * The sequence of the units regards dependencies.
     * So since ant requires maven it is installed afterwards.
     */
    @Test
    public void testInstallUnitsToProcessDefaultInstallComplex2() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub02.xml", "i",
                new String[]{
                "jdk",
                "org.apache.maven",
                "org.apache.ant",
                "org.rapidenv.test01",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test02"
        });
    }

    /**
     * All install units are taken by default.
     * The sequence of the units is as they are defined in the environment
     * definition but depth fist.
     */
    @Test
    public void testInstallUnitsToProcessDefaultDeinstallComplex2() {
        checkInstallUnitsToProcessSorting("testdata/env/envDepSub02.xml", "d",
                new String[]{
                "org.rapidenv.test02",
                "org.rapidenv.test01/test1",
                "org.rapidenv.test01/test2/test1",
                "org.rapidenv.test01/test2/testx",
                "org.rapidenv.test01/test2",
                "org.rapidenv.test01/test3",
                "org.rapidenv.test01/test5",
                "org.rapidenv.test01/test4",
                "org.rapidenv.test01",
                "org.apache.ant",
                "org.apache.maven",
                "jdk",
        });
    }

    @Test (expected = RapidEnvCmdException.class)
    public void testCheckDependenciesInstallSonWithoutParent() {
        try {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {"-env", "testdata/env/envDepSub.xml", "i",
                        "org.rapidenv.test01/test2/test1"}));
        env.initPropertiesAndInstallunitsToProcess(CmdRenvCommand.install);
        List<Installunit> installUnits = env.getInstallunitsToProcess();
        env.checkDependencies(installUnits, CmdRenvCommand.install);
        } catch (RapidEnvCmdException e) {
            Assert.assertEquals(
                    "Can not install unit \"org.rapidenv.test01/test2/test1\""
                    + " because parent unit \"org.rapidenv.test01/test2\" is not installed.",
                    e.getMessage());
            throw e;
        }
    }

    @Test (expected = RapidEnvConfigurationException.class)
    public void testCheckDependenciesIntraTreeNodeDeps1() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubIntraTreeNodes01.xml", "i", "jdk"}));
        try {
            env.checkDependenciesAll();
        } catch (RapidEnvConfigurationException e) {
            Assert.assertEquals(
                    "Invalid dependency defined between install unit "
                    + "\"org.rapidenv.test01/test2/testx\" and install unit "
                    + "\"org.rapidenv.test01/test4\" because these install units "
                    + "are defined  on different subunit tree node levels.",
                    e.getMessage());
            throw e;
        }
    }

    @Test (expected = RapidEnvConfigurationException.class)
    public void testCheckDependenciesIntraTreeNodeDeps2() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubIntraTreeNodes02.xml", "i", "jdk"}));
        try {
            env.checkDependenciesAll();
        } catch (RapidEnvConfigurationException e) {
            Assert.assertEquals(
                    "Invalid dependency defined between install unit "
                    + "\"org.rapidenv.test02\" and install unit "
                    + "\"org.rapidenv.test01/test5\" because these install units "
                    + "are defined  on different subunit tree node levels.",
                    e.getMessage());
            throw e;
        }
    }

    @Test (expected = RapidEnvConfigurationException.class)
    public void testCheckDependenciesCyclicDepsSimple() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubCyc01.xml", "i", "jdk"}));
        try {
            env.checkDependenciesAll();
        } catch (RapidEnvConfigurationException e) {
            Assert.assertEquals(
                    "Invalid dependency cycle defined for install units "
                    + "\"org.rapidenv.test01/test4\", "
                    + "\"org.rapidenv.test01/test5\"",
                    e.getMessage());
            throw e;
        }
    }

    @Test (expected = RapidEnvConfigurationException.class)
    public void testCheckDependenciesCyclicDepsMore() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubCyc02.xml", "i", "jdk"}));
        try {
            env.checkDependenciesAll();
        } catch (RapidEnvConfigurationException e) {
            Assert.assertEquals(
                    "Invalid dependency cycle defined for install units "
                    + "\"jdk\", \"org.rapidenv.test02\", "
                    + "\"org.rapidenv.test01\", \"org.apache.ant\"",
                    e.getMessage());
            throw e;
        }
    }

    @Test (expected = RapidEnvConfigurationException.class)
    public void testCheckDependenciesCyclicDepsSelf() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubCyc03.xml", "i", "jdk"}));
        try {
            env.checkDependenciesAll();
        } catch (RapidEnvConfigurationException e) {
            Assert.assertEquals(
                    "Invalid self dependency defined for install unit "
                    + "\"org.rapidenv.test01/test3\"",
                    e.getMessage());
            throw e;
        }
    }

    public void testCheckDependenciesCyclicDepsNoCycles() {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {
                        "-env", "testdata/env/envDepSubCyc04.xml", "i", "jdk"}));
        env.checkDependenciesAll();
    }

    private void checkInstallUnitsToProcessSorting(
            String envFilePath, String command,
            String[] unitNames) {
        RapidEnvInterpreter env = new RapidEnvInterpreter(
                new CmdRenv(new String[] {"-env", envFilePath, command}));
        env.initPropertiesAndInstallunitsToProcess(CmdRenvCommand.stat);
        List<Installunit> installUnits = env.getInstallunitsToProcess();
        int size = installUnits.size();
        Assert.assertEquals(unitNames.length, size);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals("Failure on list pos " + i + ": ",
                    unitNames[i], installUnits.get(i).getFullyQualifiedName());
        }
    }
}
