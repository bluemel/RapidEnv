package org.rapidbeans.rapidenv.config.eclipseupdatsite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rapidbeans.rapidenv.config.Artifact;

public class RepositoryTest {

	@Test
	public void testRead() throws FileNotFoundException {
		InputStream is = new FileInputStream(
				new File("testdata/unpack/artifacts.xml"));
		final List<Artifact> artifacts = Artifact.parse(is);
		Assert.assertEquals(17, artifacts.size());
		for (Artifact art : artifacts) {
			if (art.getId().equals("org.maven.ide.eclipse.site")) {
				Assert.assertEquals("org.eclipse.update.feature", art.getClassifier());
				Assert.assertEquals("0.12.1.20110112-1712", art.getVersion().toString());
				Assert.assertEquals(6519, art.getArtifactSize());
				Assert.assertEquals(6519, art.getDownloadSize());
				Assert.assertEquals("a972bc083ca0968896671e6c88146e45", art.getDownloadMd5());
				Assert.assertEquals("application/zip", art.getDownloadContentType());
			} else if (art.getId().equals("org.maven.ide.eclipse.editor.xml")) {
				Assert.assertEquals("osgi.bundle", art.getClassifier());
				Assert.assertEquals("0.12.1.20110112-1712", art.getVersion().toString());
				Assert.assertEquals(91938, art.getArtifactSize());
				Assert.assertEquals(91938, art.getDownloadSize());
				Assert.assertEquals("da250af77f42e6a0372194e8e1dc1ebc", art.getDownloadMd5());
				Assert.assertNull(art.getDownloadContentType());
			}
		}
	}
}
