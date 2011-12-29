/*
 * RapidEnv: ConfigExprFunctionUsernameTest.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 07/03/2010
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

public class ConfigExprFunctionUsernameTest {

    @BeforeClass
    public static void setUpClass() {
        TypePropertyCollection.setDefaultCharSeparator(',');
    }

	@Test
	public void testInterpret() {
		ConfigExprTopLevel expr = new ConfigExprTopLevel(
				new Installunit("jdk"), null, "username()", false);
		Assert.assertEquals(PlatformHelper.username(), expr.interpret());
	}
}
