/*
 * RapidEnv: ConfigExprStringLiteralTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 06/27/2010
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

public class ConfigExprStringLiteralTest {

	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null,
		        "xxx''hostname()yyy''pathconvert('/a\\b/c', '/')zzz", false);
		Assert.assertEquals("xxx" + PlatformHelper.hostname() + "yyy" + "/a/b/c" + "zzz", expr.interpret());
	}

	@Test
	public void testConstantLiteral() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx'yyy'zzz", false);
		Assert.assertEquals("xxxyyyzzz", expr.interpret());
	}

	@Test
	public void testEscapingAposthrophOuter() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx\\'yyy'\\zzz", false);
		Assert.assertEquals("xxx\\yyy\\zzz", expr.interpret());
	}

	@Test
	public void testEscapingApostrophLiteral() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "'xxx\\'yyy\\'zzz'", true);
		Assert.assertEquals("xxx'yyy'zzz", expr.interpret());
	}

	@Test
	public void testInterpretBackslashSingleOuter() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx\\yyy\\zzz", false);
		Assert.assertEquals("xxx\\yyy\\zzz", expr.interpret());
	}

	@Test
	public void testInterpretBackslashSingleLiteral() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "'xxx\\yyy\\zzz'", false);
		Assert.assertEquals("xxx\\yyy\\zzz", expr.interpret());
	}

	@Test
	public void testEscapingBackslashOuter() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx\\yyy\\zzz", false);
		Assert.assertEquals("xxx\\yyy\\zzz", expr.interpret());
	}

	@Test
	public void testEscapingBackslashLiteral() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "'xxx\\\\yyy\\\\zzz'", true);
		Assert.assertEquals("xxx\\yyy\\zzz", expr.interpret());
	}

	@Test
	public void testExprString() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "xxx${V1}'yyy${V2}zzz'", false);
		Assert.assertEquals(3, expr.getChilds().size());
		Assert.assertSame(ConfigExprStringLiteral.class, expr.getChilds().get(0).getClass());
		Assert.assertSame(ConfigExprProperty.class, expr.getChilds().get(1).getClass());
		Assert.assertEquals("xxx${V1}yyy${V2}zzz", expr.interpret());
	}
}
