package org.rapidbeans.rapidenv;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;

public class UnpackerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if (new File("src/test/resources/unpack/testunpack").exists()) {
			FileHelper.deleteDeep(new File("src/test/resources/unpack/testunpack"), true);
		}
	}

	@Test
	public void testUnpackZip() {
		new Unpacker().unpack(new File("src/test/resources/unpack/test1.zip"),
				new File("src/test/resources/unpack/testunpack"));
		assertUnpack();
	}

	@Test
	public void testUnpackTar() {
		new Unpacker().unpack(new File("src/test/resources/unpack/test1.tar"),
				new File("src/test/resources/unpack/testunpack"));
		assertUnpack();
	}

	@Test
	public void testUnpackTargz() {
		new Unpacker().unpack(new File("src/test/resources/unpack/test1.tar.gz"),
				new File("src/test/resources/unpack/testunpack"));
		assertUnpack();
	}

	private void assertUnpack() {
		Assert.assertTrue(
				"Contents of folders \"" + new File("src/test/resources/unpack/testunpack/test1").getAbsolutePath()
						+ "\"\n" + "and \""
						+ new File("src/test/resources/unpack/testunpackref/test1").getAbsolutePath() + "\"\n"
						+ "is different.",
				FileHelper.dirsEqualExcludeDotDirs(new File("src/test/resources/unpack/testunpack/test1"),
						"src/test/resources/unpack/testunpackref/test1"));
	}
}
