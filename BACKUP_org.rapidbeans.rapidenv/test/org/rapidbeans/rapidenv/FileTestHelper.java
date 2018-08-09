package org.rapidbeans.rapidenv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.junit.Assert;
import org.rapidbeans.core.exception.UtilException;

public class FileTestHelper {

	public static void verifyFilesEqual(final File file1, final File file2, final boolean differentNamesAllowed,
	        final boolean compareLineByLine) {
		if (!file1.exists()) {
			throw new UtilException("file \"" + file1.getAbsolutePath() + "\" not found.");
		}
		if (!file2.exists()) {
			throw new UtilException("file \"" + file2.getAbsolutePath() + "\" not found.");
		}
		if (!file1.isFile()) {
			throw new UtilException("file \"" + file1.getAbsolutePath() + "\" not a normal file.");
		}
		Assert.assertTrue("File to compare \"" + file2.getAbsolutePath() + "\" is no file.", file2.isFile());

		if (!differentNamesAllowed) {
			Assert.assertEquals("files have different names:\n"
			        + "- file 1: " + file1.getAbsolutePath() + "\n"
			        + "- file 2: " + file2.getAbsolutePath(), file1.getName(), file2.getName());
		}

		FileInputStream is1 = null;
		FileInputStream is2 = null;
		LineNumberReader r1 = null;
		LineNumberReader r2 = null;
		try {
			if (compareLineByLine) {
				r1 = new LineNumberReader(new InputStreamReader(new FileInputStream(file1)));
				r2 = new LineNumberReader(new InputStreamReader(new FileInputStream(file2)));

				String l1 = r1.readLine();
				String l2 = r2.readLine();
				while (l1 != null) {
					Assert.assertNotNull("files have different number of lines:\n"
					        + "- file 1: " + file1.getAbsolutePath() + "\n"
					        + "- file 2: " + file2.getAbsolutePath() + "\n"
					        + "line " + r1.getLineNumber() + " of file 1 not" + " found in file 2",
					        l2);
					Assert.assertEquals("files differ:\n"
					        + "- file 1: " + file1.getAbsolutePath() + "\n"
					        + "  line " + r1.getLineNumber() + ": \"" + l1 + "\"\n"
					        + "- file 2: " + file2.getAbsolutePath() + "\n"
					        + "  line " + r2.getLineNumber() + ": \"" + l2 + "\"",
					        l1, l2);
					l1 = r1.readLine();
					l2 = r2.readLine();
				}
				Assert.assertNull("files have different number of lines:\n"
				        + "- file 1: " + file1.getAbsolutePath() + "\n"
				        + "- file 2: " + file2.getAbsolutePath(), l2);
			} else {
				is1 = new FileInputStream(file1);
				is2 = new FileInputStream(file2);

				int i1 = is1.read();
				int i2 = is2.read();
				while (i1 != -1) {
					Assert.assertTrue("files differ:\n"
					        + "- file 1: " + file1.getAbsolutePath() + "\n"
					        + "- file 2: " + file2.getAbsolutePath(), i1 == i2);
					i1 = is1.read();
					i2 = is2.read();
				}
				Assert.assertTrue("files have different length:\n"
				        + "- file 1: " + file1.getAbsolutePath() + "\n"
				        + "- file 2: " + file2.getAbsolutePath(), i2 != -1);
			}
		} catch (IOException e) {
			throw new UtilException(e);
		} finally {
			try {
				if (is1 != null) {
					is1.close();
				}
				if (is2 != null) {
					is2.close();
				}
				if (r1 != null) {
					r1.close();
				}
				if (r2 != null) {
					r2.close();
				}
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
	}
}
