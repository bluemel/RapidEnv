/*
 * RapidEnv: ConfigFileEditorXmlTest.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 10/29/2011
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

package org.rapidbeans.rapidenv.config.file;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.rapidbeans.core.util.XmlHelper;
import org.w3c.dom.Node;

/**
 * @author Martin Bluemel
 */
public class ConfigFileEditorXmlTest {

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}.
	 */
	@Test
	public void testRetrieveNodeTopLevel() {
		File testfile = new File("testdata/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		editor.load();
		Node node = editor.retrieveNode("//workbench");
		Assert.assertEquals("workbench", node.getNodeName());
	}

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}.
	 */
	@Test
	public void testRetrieveSubnode() {
		File testfile = new File("testdata/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		editor.load();
		Node node = editor.retrieveNode("//workbench/window/page/views/view");
		Assert.assertEquals("view", node.getNodeName());
	}

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}.
	 */
	@Test
	public void testRetrieveSubnodeWithSpecAttribute() {
		File testfile = new File("testdata/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//workbench/window/page/views/view[@id='org.eclipse.jdt.ui.PackageExplorer']");
		Assert.assertEquals("view", node.getNodeName());
	}

	/**
	 * Test method for {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}.
	 */
	@Test
	public void testRetrieveSubnodeWithSpecAttributeComplex() {
		File testfile = new File("testdata/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode(
				"//workbench/window/page/views"
				+ "/view[@id='org.eclipse.jdt.ui.PackageExplorer']"
				+ "/viewState/customFilters/xmlDefinedFilters"
				+ "/child[@filterId='org.eclipse.jdt.ui.PackageExplorer_patternFilterId_.*']"
				+ "/@isEnabled");
		Assert.assertEquals("false", node.getNodeValue());
	}

	/**
	 * Test isAttributePath().
	 */
	@Test
	public void testIsAttributePath() {
		Assert.assertTrue(ConfigFileEditorXml.isAttributePath(
				"//xxx/yyy/@zXw-120"));
	}

	/**
	 * Test parseIdAttrs()
	 */
	@Test
	public void testParseIdAttrs() {
		XmlHelper.parseIdAttrs(
				"//xxx/yyy[@id='org.eclipse.wst.server.ui.editor'"
				+ " and @name='JBoss 6.0 Runtime Server']");
	}
}
