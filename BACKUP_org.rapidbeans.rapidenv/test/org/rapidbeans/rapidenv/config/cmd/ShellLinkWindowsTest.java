/**
 *
 */
package org.rapidbeans.rapidenv.config.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.OperatingSystemFamily;
import org.rapidbeans.core.util.PlatformHelper;

/**
 */
public class ShellLinkWindowsTest {

	@BeforeClass
	public static void setUpClass() {
		FileHelper.copyFile(new File("target/generated-dtds/env.dtd"), new File("../../env.dtd"));
		new File("testdata/testinstall").mkdir();
		new File("testdata/testinstall/org/apache/maven/2.1.2").mkdirs();
		RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("project",
				"org.rapidbeans.rapidenv.config.Project", true);
	}

	@AfterClass
	public static void tearDownClass() {
		FileHelper.deleteDeep(new File("../../env.dtd"));
		FileHelper.deleteDeep(new File("testdata/testinstall"));
	}

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.cmd.ShellLinkWindows#save()}.
	 */
	@Test
	public void testCreateProgrammatically() {
		if (PlatformHelper.getOsfamily() != OperatingSystemFamily.windows) {
			return;
		}
		File linkFile = new File("testdata/shelllink/testCreate.lnk");
		if (linkFile.exists()) {
			assertTrue(linkFile.delete());
		}
		ShellLinkWindows shellLink = new ShellLinkWindows(linkFile);
		shellLink.setDescription("This is a second \"test shortcut\".");
		shellLink.setTargetPath(new File(System.getenv("SYSTEMROOT")
				+ File.separator + "System32" + File.separator + "cmd.exe"));
		shellLink.setWorkingDirectory(new File(System.getenv("SYSTEMROOT")
				+ File.separator + "System32"));
		shellLink.addArgument(new Argument("/C"));
		shellLink.addArgument(new Argument("echo Hello \"My Folks\"!& pause",
				true));
		shellLink.setIconFile(new File("testdata/shelllink/test.ico"));
		shellLink.setIconNumber(3);
		List<Integer> hotKey = new ArrayList<Integer>();
		hotKey.add(KeyEvent.VK_ALT);
		hotKey.add(KeyEvent.VK_SHIFT);
		hotKey.add(KeyEvent.VK_A);
		shellLink.setHotKey(hotKey);
		shellLink.save();

		ShellLinkWindows shellLinkRead = new ShellLinkWindows(linkFile);
		shellLinkRead.load();
		assertEquals("This is a second \"test shortcut\".",
				shellLinkRead.getDescription());
		assertEquals(new File(System.getenv("SYSTEMROOT") + File.separator
				+ "System32"), shellLinkRead.getWorkingDirectory());
		final List<Argument> args = shellLinkRead.getArguments();
		assertEquals(2, args.size());
		assertEquals("/C", args.get(0).getValue());
		assertEquals(false, args.get(0).getQuoted());
		assertEquals("echo Hello \"My Folks\"!& pause", shellLinkRead
				.getArguments().get(1).getValue());
		assertEquals(true, args.get(1).getQuoted());
		assertEquals(new File("testdata/shelllink/test.ico").getAbsolutePath(),
				shellLinkRead.getIconFile().getAbsolutePath());
		assertEquals(3, shellLinkRead.getIconNumber());
		assertEquals(KeyEvent.VK_ALT, shellLinkRead.getHotKey().get(0)
				.intValue());
		assertEquals(KeyEvent.VK_SHIFT, shellLinkRead.getHotKey().get(1)
				.intValue());
		assertEquals(KeyEvent.VK_A, shellLinkRead.getHotKey().get(2).intValue());

		assertTrue(linkFile.delete());
	}

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.cmd.ShellLinkWindows#load()}.
	 */
	@Test
	public void testReadWindows() {
		if (PlatformHelper.getOsfamily() != OperatingSystemFamily.windows) {
			return;
		}
		ShellLinkWindows shellLink = new ShellLinkWindows(new File(
				"testdata/shelllink/testRead.lnk"));
		shellLink.load();
		assertEquals("This is a test shortcut for reading shortcuts.",
				shellLink.getDescription());
		assertEquals(new File("testdata/shelllink/testRead.lnk"),
				shellLink.getFile());
		assertEquals(new File(System.getenv("SYSTEMROOT") + File.separator
				+ "System32" + File.separator + "cmd.exe"),
				shellLink.getTargetPath());
		switch (PlatformHelper.getOs())
		{
		case windows_xp:
			assertEquals(System.getenv("SYSTEMROOT") + File.separator + "System32",
					shellLink.getWorkingDirectory().getAbsolutePath());
			break;
		default:
			assertEquals(System.getenv("SYSTEMROOT") + File.separator + "System32",
					shellLink.getWorkingDirectory().getAbsolutePath());
			break;
		}
		assertEquals(2, shellLink.getArguments().size());
		assertEquals("/C", shellLink.getArguments().get(0).getValue());
		assertFalse(shellLink.getArguments().get(0).getQuoted());
		assertEquals("echo Hello shortcut!& pause", shellLink.getArguments()
				.get(1).getValue());
		assertTrue(shellLink.getArguments().get(1).getQuoted());
		assertTrue(shellLink
				.getIconFile()
				.getAbsolutePath()
				.endsWith(
						"testdata" + File.separator + "shelllink"
								+ File.separator + "test.ico"));
		assertEquals(0, shellLink.getIconNumber());
		assertSame(ShortcutWindowStyle.maximizedFocus,
				shellLink.getWindowStyle());
		assertEquals(3, shellLink.getHotKey().size());
		assertEquals(new Integer(KeyEvent.VK_ALT), shellLink.getHotKey().get(0));
		assertEquals(new Integer(KeyEvent.VK_CONTROL), shellLink.getHotKey()
				.get(1));
		assertEquals(new Integer(KeyEvent.VK_K), shellLink.getHotKey().get(2));
	}

	@Test
	public void testGetStartMenuFolderWindows() {
		if (PlatformHelper.getOsfamily() != OperatingSystemFamily.windows) {
			return;
		}
		File startMenuFolder = ShellLinkWindows.getStartMenuFolder();
		assertTrue(startMenuFolder.exists());
	}
}
