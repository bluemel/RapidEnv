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
import org.rapidbeans.rapidenv.config.Installunit;

public class ConfigExprTopLevelTest {

	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null,
		        "xxx''osname()yyy''pathconvert('\\a/b\\c', '/')zzz", false);
		Assert.assertEquals("xxx" + PlatformHelper.getOsfamily().name() + "yyy" + "/a/b/c" + "zzz", expr.interpret());
	}

	private static final int I5 = 5;

	/**
	 * Test of a ConfigExprTopLevel's construction (String + Envvar).
	 */
	@Test
	public final void testExprTopLevelStringEnvvar() {
		// AntGateway ant = new AntGateway(new
		// File("testdata/ant/ant_win.properties"));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx${V1}yyy${V2}zzz", false);
		Assert.assertEquals(I5, expr.getChilds().size());
		Assert.assertSame(ConfigExprStringLiteral.class, expr.getChilds().get(0).getClass());
		Assert.assertSame(ConfigExprProperty.class, expr.getChilds().get(1).getClass());
	}

	/**
	 * Test of a ConfigExprTopLevel's construction (Pathconvert + String).
	 */
	@Test
	public final void testExprTopLevelPathcovertString() {
		// AntGateway ant = new AntGateway(new
		// File("testdata/ant/ant_win.properties"));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "pathconvert('C:\\a\\b\\c')b",
		        false);
		Assert.assertEquals(2, expr.getChilds().size());
		Assert.assertSame(ConfigExprFunctionPathconvert.class, expr.getChilds().get(0).getClass());
		Assert.assertSame(ConfigExprStringLiteral.class, expr.getChilds().get(1).getClass());
	}

	/**
	 * Test expansion of unknown property.
	 */
	@Test
	public void interpretUnknownFunction() {
		// AntGateway ant = new AntGateway(new
		// File("testdata/ant/ant_win.properties"));
		ConfigExprTopLevel expr = new ConfigExprTopLevel(null, null, "123 func(x) 456", false);
		Assert.assertEquals("123 func(x) 456", expr.interpret());
	}
}
