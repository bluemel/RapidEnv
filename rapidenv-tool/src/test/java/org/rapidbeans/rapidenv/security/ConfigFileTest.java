/*
 * RapidEnv: ConfigFileTest.java
 * 
 * Copyright (C) 2011 Martin Bluemel
 * 
 * Creation Date: 12/16/2011
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

package org.rapidbeans.rapidenv.security;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigFileTest {

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Test
	public void testHashMD5() {
		Assert.assertEquals("a47d3912f1eaab08af5d01f6f6a5af46",
				Verifyer.hashValue(new File("src/test/resources/unpack/test1.zip"), EnumHashalgorithm.MD5));
	}

	@Test
	public void testHashSHA1() {
		Assert.assertEquals("364e173ff53f823d1f152403ed004c73abaae9c4",
				Verifyer.hashValue(new File("src/test/resources/unpack/test1.zip"), EnumHashalgorithm.SHA1));
	}
}
