/**
 * 
 */
package org.rapidbeans.rapidenv.config.expr;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Martin Bluemel
 */
public class ConfigExprTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSplitFuncnameNormal() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname("homedir");
		Assert.assertEquals("homedir", result.getFuncname());
		Assert.assertNull(result.getBefore());
		Assert.assertEquals("", result.getWhitespacesAfter());
	}

	@Test
	public void testSplitFuncnameWsBefore() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname("   hd");
		Assert.assertEquals("hd", result.getFuncname());
		Assert.assertEquals("   ", result.getBefore());
		Assert.assertEquals("", result.getWhitespacesAfter());
	}

	@Test
	public void testSplitFuncnameWsAfter() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname("homedir  ");
		Assert.assertEquals("homedir", result.getFuncname());
		Assert.assertNull(result.getBefore());
		Assert.assertEquals("  ", result.getWhitespacesAfter());
	}

	@Test
	public void testSplitFuncnameComplex1() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname(" a bb ccc hd     ");
		Assert.assertEquals("hd", result.getFuncname());
		Assert.assertEquals(" a bb ccc ", result.getBefore());
		Assert.assertEquals("     ", result.getWhitespacesAfter());
	}

	@Test
	public void testSplitFuncnameComplex2() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname("123 func");
		Assert.assertEquals("123 ", result.getBefore());
		Assert.assertEquals("func", result.getFuncname());
		Assert.assertEquals("", result.getWhitespacesAfter());
	}

	@Test
	public void testSplitFuncnameComplex3() {
		ConfigExprSplitResult result = ConfigExprParser.splitFuncname(
				"D:\\h\\opt\\maven\\bin;D:\\Projects\\RapidBeans\\rapidenv-tool\\src\\test\\resources\\testinstall\\jdk\\1.6.0\\bintest01;test02 ");
		Assert.assertEquals(
				"D:\\h\\opt\\maven\\bin;D:\\Projects\\RapidBeans\\rapidenv-tool\\src\\test\\resources\\testinstall\\jdk\\1.6.0\\bintest01;test02",
				result.getFuncname());
		Assert.assertNull(result.getBefore());
		Assert.assertEquals(" ", result.getWhitespacesAfter());
	}
}
