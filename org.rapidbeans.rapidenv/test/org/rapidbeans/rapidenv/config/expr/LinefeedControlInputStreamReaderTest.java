/*
 * RapidEnv: LinefeedControlInputStreamReaderTest.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 07/10/2011
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

package org.rapidbeans.rapidenv.config.expr;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.rapidbeans.core.util.OperatingSystem;
import org.rapidbeans.core.util.PlatformHelper;

/**
 * Tests for class LinefeedControlInputStreamReader.
 * 
 * @author Martin Bluemel
 */
public class LinefeedControlInputStreamReaderTest {

    /**
     * Platform specific line feed.
     */
    private static String LF_PF = "\n";

    static {
        if (PlatformHelper.getOs() == OperatingSystem.windows) {
            LF_PF = "\r\n";
        }
    }

    /**
     * Read a file with windows line feeds while preserving.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readPreserveWin() throws IOException {
    	if (!(PlatformHelper.getOs() == OperatingSystem.windows)) {
    		return;
    	}
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_win.properties"),
                    LinefeedControl.preserve);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1\r\n"
                    + "V2=Test2\r\n"
                    + "test.dev.location=ismaning\r\n";
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read a file with windows line feeds while normalizing.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readNormalizeWin() throws IOException {
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_win.properties"),
                    LinefeedControl.normalize);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1\n"
                    + "V2=Test2\n"
                    + "test.dev.location=ismaning\n";
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read a file with windows line feeds while normalizing.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readPlatformWin() throws IOException {
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_win.properties"),
                    LinefeedControl.platform);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1" + LF_PF
                    + "V2=Test2"  + LF_PF
                    + "test.dev.location=ismaning" + LF_PF;
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read a file with windows line feeds while preserving.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readPreserveNorm() throws IOException {
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_norm.properties"),
                    LinefeedControl.preserve);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1\n"
                    + "V2=Test2\n"
                    + "test.dev.location=ismaning\n";
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read a file with windows line feeds while normalizing.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readNormalizeNorm() throws IOException {
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_norm.properties"),
                    LinefeedControl.normalize);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1\n"
                    + "V2=Test2\n"
                    + "test.dev.location=ismaning\n";
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Read a file with windows line feeds while normalizing.
     * @throws IOException in case of IO problem
     */
    @Test
    public final void readPlatformNorm() throws IOException {
        LinefeedControlInputStreamReader reader = null;
        try {
            reader = new LinefeedControlInputStreamReader(
                    new File("testdata/ant/ant_norm.properties"),
                    LinefeedControl.platform);
            StringBuffer buf = new StringBuffer();
            int c;
            while ((c = reader.read()) != -1) {
                buf.append((char) c);
            }
            String read = buf.toString();
            String expected = "V1=Test1" + LF_PF
                    + "V2=Test2"  + LF_PF
                    + "test.dev.location=ismaning" + LF_PF;
            Assert.assertEquals(expected, read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
