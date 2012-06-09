/*
 * RapidEnv: ConfigExprFunctionUserhomeTest.java
 *
 * Copyright (C) 2012 Martin Bluemel
 *
 * Creation Date: 06/09/2012
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.config.Installunit;

public class ConfigExprFunctionUserhomeTest {

	@BeforeClass
	public static void setUpClass() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(new Installunit("jdk"), null, "userhome()", false);
		Assert.assertEquals(PlatformHelper.userhome(), expr.interpret());
	}
}
