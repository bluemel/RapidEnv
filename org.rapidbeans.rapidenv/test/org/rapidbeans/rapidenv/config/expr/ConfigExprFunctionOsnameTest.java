/*
 * RapidEnv: .java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 05/30/2010
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

import org.junit.Assert;
import org.junit.Test;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.rapidenv.config.Installunit;

public class ConfigExprFunctionOsnameTest {

	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(
				new Installunit("jdk"), null, "xxx osname()yyy", false);
		Assert.assertEquals("xxx " + PlatformHelper.getOs().name() + "yyy", expr
				.interpret());
	}

	@Test
	public void testExprFunctionOSnameSepWithLiteral() {
		Installunit jdk = new Installunit("jdk");
		jdk.setVersion(new Version("1.2.3"));
		Assert.assertEquals("xxx-" + PlatformHelper.getOs().name() + "-"
				+ PlatformHelper.getArchName() + "yyy", ConfigExpr.expand(
				        jdk, null, "xxx-''osname()-''architecture()yyy", false));
	}

	@Test
	public void testConversionMap() {
        Installunit eclipse = new Installunit("eclipse");
        eclipse.setVersion(new Version("3.7.0"));
        switch (PlatformHelper.getOs()) {
            case windows:
                if (PlatformHelper.getArchName().equals("x86")) {
                    Assert.assertEquals("eclipse-modeling-indigo-win32.zip",
                            ConfigExpr.expand(eclipse, null,
                                    "eclipse-modeling-indigo-"
                                    + "''osname('windows=win32;linux=linux-gtk')"
                                    + "''architecture('x86=;x86_64=-x86_64')"
                                    + ".zip", false));
                } else {
                    Assert.fail("Architecture \"" + PlatformHelper.getArchName() + "\" not yet tested");
                }
                break;
            case linux:
                if (PlatformHelper.getArchName().equals("i386")) {
                    Assert.assertEquals("eclipse-modeling-indigo-linux-gtk.zip",
                            ConfigExpr.expand(eclipse, null,
                                    "eclipse-modeling-indigo-"
                                    + "''osname('windows=win32;linux=linux-gtk')"
                                    + "''architecture('i386=;x86_64=-x86_64')"
                                    + ".zip", false));
                } else {
                    Assert.fail("Architecture \"" + PlatformHelper.getArchName() + "\" not yet tested");
                }
                break;
            default:
                Assert.fail("OS Platform \"" + PlatformHelper.getOs().name() + "\" not yet tested");
                break;
        }
	}
}
