package org.rapidbeans.rapidenv.config.expr;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.rapidbeans.rapidenv.cmd.CmdRenv;

public class ConfigExprFunctionTest {

	@BeforeClass
	public static void setUpClass() throws InterruptedException {
		if (!new File("profile").exists()) {
			new File("profile").mkdir();
		}
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("src/test/resources/testinstall").mkdir();
		Thread.sleep(200);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("src/test/resources/testinstall"));
	}

	/**
	 * using an empty literal as separator is one technique to apply a function
	 * without a whitespace before.
	 */
	@Test
	public void testExprFunctionHostnameSepWithLiteral() {
		Assert.assertEquals("xxx" + PlatformHelper.hostname() + "yyy",
				ConfigExpr.expand(null, null, "xxx''hostname()yyy", false));
	}

	/**
	 * using an empty literal as separator is one technique to apply a function
	 * without a whitespace before.
	 */
	@Test
	public void testExprFunctionHostnameSepWithEmptyVariable() {
		(new RapidEnvInterpreter(new CmdRenv(new String[] { "-env", "src/test/resources/env/env.xml", "s" }))).setPropertyValue(
				"EMPTY", "");
		Assert.assertEquals("xxx" + PlatformHelper.hostname() + "yyy",
				ConfigExpr.expand(null, null, "xxx${EMPTY}hostname()yyy", false));
	}

	@Test
	public void testSplitArgumentsSimple() {
		List<String> splitResult = ConfigExprFunction.splitArguments("a, ab, abc", false);
		Assert.assertEquals("a", splitResult.get(0));
		Assert.assertEquals("ab", splitResult.get(1));
		Assert.assertEquals("abc", splitResult.get(2));
	}

	@Test
	public void testSplitArgumentsQuotedSimple() {
		List<String> splitResult = ConfigExprFunction.splitArguments("'a', 'ab', 'abc'", false);
		Assert.assertEquals("'a'", splitResult.get(0));
		Assert.assertEquals("'ab'", splitResult.get(1));
		Assert.assertEquals("'abc'", splitResult.get(2));
	}

	@Test
	public void testSplitArgumentsQuotedComplex() {
		List<String> splitResult = ConfigExprFunction.splitArguments("'a', '\\'ab\\\\\\'', 'abc'", true);
		Assert.assertEquals("'a'", splitResult.get(0));
		Assert.assertEquals("''ab\\''", splitResult.get(1));
		Assert.assertEquals("'abc'", splitResult.get(2));
	}

	@Test
	public void testSplitArgumentsFunctionSimple() {
		List<String> splitResult = ConfigExprFunction.splitArguments("'a', myfunc('a', 'b c', d), 'abc'", false);
		Assert.assertEquals("'a'", splitResult.get(0));
		Assert.assertEquals("myfunc('a', 'b c', d)", splitResult.get(1));
		Assert.assertEquals("'abc'", splitResult.get(2));
	}

	@Test
	public void testSplitArgumentsFunctionComplex() {
		List<String> splitResult = ConfigExprFunction.splitArguments("pathconvert('a:b\\c\\d', '/'), ':', '\\\\:'",
				true);
		Assert.assertEquals("pathconvert('a:b\\c\\d', '/')", splitResult.get(0));
		Assert.assertEquals("':'", splitResult.get(1));
		Assert.assertEquals("'\\:'", splitResult.get(2));
	}

	@Test
	public void testSplitArgumentsFunction02() {
		List<String> splitResult = ConfigExprFunction.splitArguments(
				"${wd}'/src/test/resources/ant/ant_win.properties', \\n\\r=", false);
		Assert.assertEquals("${wd}'/src/test/resources/ant/ant_win.properties'", splitResult.get(0));
		Assert.assertEquals("\\n\\r=", splitResult.get(1));
	}
}
