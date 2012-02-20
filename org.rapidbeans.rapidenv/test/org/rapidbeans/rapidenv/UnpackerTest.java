package org.rapidbeans.rapidenv;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;

public class UnpackerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if (new File("testdata/unpack/testunpack").exists()) {
			FileHelper.deleteDeep(new File("testdata/unpack/testunpack"), true);
		}
	}

	@Test
	public void testUnpackZip() {
		new Unpacker().unpack(new File("testdata/unpack/test1.zip"), new File("testdata/unpack/testunpack"));
		assertUnpack();
	}

	@Test
	public void testUnpackTar() {
		new Unpacker().unpack(new File("testdata/unpack/test1.tar"), new File("testdata/unpack/testunpack"));
		assertUnpack();
	}

	@Test
	public void testUnpackTargz() {
		new Unpacker().unpack(new File("testdata/unpack/test1.tar.gz"), new File("testdata/unpack/testunpack"));
		assertUnpack();
	}

	private void assertUnpack() {
		Assert.assertTrue("Contents of folders \"" + new File("testdata/unpack/testunpack/test1").getAbsolutePath()
		        + "\"\n" + "and \"" + new File("testdata/unpack/testunpackref/test1").getAbsolutePath() + "\"\n"
		        + "is different.", FileHelper.dirsEqualExcludeDotDirs(new File("testdata/unpack/testunpack/test1"),
		        "testdata/unpack/testunpackref/test1"));
	}
}
