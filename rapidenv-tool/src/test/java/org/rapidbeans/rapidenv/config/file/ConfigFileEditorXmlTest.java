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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.ComparisonFailure;
import org.junit.Test;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.Version;
import org.rapidbeans.core.util.XmlHelper;
import org.rapidbeans.datasource.CharsetsAvailable;
import org.rapidbeans.rapidenv.FileTestHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Martin Bluemel
 */
public class ConfigFileEditorXmlTest {

	/**
	 * Test method for
	 * {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}
	 * .
	 */
	@Test
	public void testRetrieveNodeTopLevel() {
		File testfile = new File("src/test/resources/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		editor.load();
		Node node = editor.retrieveNode("//workbench");
		assertEquals("workbench", node.getNodeName());
	}

	/**
	 * Test method for
	 * {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}
	 * .
	 */
	@Test
	public void testRetrieveSubnode() {
		File testfile = new File("src/test/resources/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		editor.load();
		Node node = editor.retrieveNode("//workbench/window/page/views/view");
		assertEquals("view", node.getNodeName());
	}

	@Test
	public void testRetrieveSubnodeWithSpecNodeText() {
		File testfile = new File("src/test/resources/conf/web.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//web-app/context-param/param-name[text()='config_home']");
		assertEquals("param-name", node.getNodeName());
		assertEquals("config_home", node.getFirstChild().getNodeValue());
	}

	@Test
	public void testRetrieveSubnodeWithSpecNodeTextDot() {
		File testfile = new File("src/test/resources/conf/web.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//web-app/context-param/param-name[.='config_home']");
		assertEquals("param-name", node.getNodeName());
		assertEquals("config_home", node.getFirstChild().getNodeValue());
	}

	/**
	 * Test method for
	 * {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}
	 * .
	 */
	@Test
	public void testRetrieveSubnodeWithSpecAttribute() {
		File testfile = new File("src/test/resources/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//workbench/window/page/views/view[@id='org.eclipse.jdt.ui.PackageExplorer']");
		assertEquals("view", node.getNodeName());
	}

	/**
	 * Test method for
	 * {@link org.rapidbeans.rapidenv.config.file.ConfigFileEditorXml#retrieveNode(java.lang.String)}
	 * .
	 */
	@Test
	public void testRetrieveSubnodeWithSpecAttributeComplex() {
		File testfile = new File("src/test/resources/conf/textXmlComplex01.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//workbench/window/page/views"
				+ "/view[@id='org.eclipse.jdt.ui.PackageExplorer']" + "/viewState/customFilters/xmlDefinedFilters"
				+ "/child[@filterId='org.eclipse.jdt.ui.PackageExplorer_patternFilterId_.*']" + "/@isEnabled");
		assertEquals("false", node.getNodeValue());
	}

	@Test
	public void testRetrieveSubnodeWithSpecSubnode() {
		File testfile = new File("src/test/resources/conf/web.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		Node node = editor.retrieveNode("//web-app/context-param[param-name/text()='config_home']");
		assertEquals("context-param", node.getNodeName());
		assertEquals("param-name", node.getChildNodes().item(1).getNodeName());
	}

	@Test
	public void testJaxpDomFormatting()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		File testfile = setupTestfile("web1.xml", "web01.xml");

		InputStream is = new FileInputStream(testfile);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(is);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, CharsetsAvailable.ISO_8859_1.name());
		transformer.setOutputProperty(OutputKeys.STANDALONE, EnumXmlStandalone.no.name());
		Version version = new Version(System.getProperty("java.version"));
		System.out.println(String.format("Java version = \"%s\"", version.toString()));
		if (Integer.parseInt(version.getComponents().get(0)) < 10) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-", "\t");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		final Source source = new DOMSource(document);
		final Result result = new StreamResult(testfile);
		transformer.transform(source, result);

		if (Integer.parseInt(version.getComponents().get(0)) < 10) {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/web01.xml"), testfile, true, true);
		} else {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/web01a.xml"), testfile, true, true);
		}
	}

	@Test
	public void testSetElementValueSimple() {
		File testfile = setupTestfile("web1.xml", "web01.xml");
		ConfigFileXml fileConfiguration = new ConfigFileXml();
		fileConfiguration.setPath(testfile.getAbsolutePath());
		fileConfiguration.setEncoding(CharsetsAvailable.ISO_8859_1);
		fileConfiguration.setStandalone(EnumXmlStandalone.no);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(fileConfiguration, testfile);
		editor.setElementValue("//web-app/context-param/param-name[text()='config_home']", "xxx", false);
		editor.save();
		FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webChangedNodeValue.xml"), testfile, true,
				true);
	}

	@Test
	public void testSetElementValueAnother() {
		File testfile = setupTestfile("mavensettings1a.xml", "mavensettings1.xml");
		ConfigFileXml fileConfiguration = new ConfigFileXml();
		fileConfiguration.setPath(testfile.getAbsolutePath());
		fileConfiguration.setStandalone(EnumXmlStandalone.no);
		fileConfiguration.setIndent("  ");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(fileConfiguration, testfile);
		editor.setElementValue("//settings/proxies/proxy/active", "true", true);
		editor.setElementValue("//settings/proxies/proxy/protocol", "myproto", true);
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/mavensettings1_res.xml"), testfile, true,
					true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/mavensettings1_resAlt.xml"), testfile,
					true, true);
		}
	}

	@Test
	public void testSetNodeValueAnother() {
		File testfile = setupTestfile("mavensettings1a.xml", "mavensettings1.xml");
		ConfigFileXml fileConfiguration = new ConfigFileXml();
		fileConfiguration.setSourceurl(null);
		fileConfiguration.setPath(testfile.getAbsolutePath());
		fileConfiguration.setStandalone(EnumXmlStandalone.no);
		fileConfiguration.setIndent("  ");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(fileConfiguration, testfile);
		editor.setNodeValue("//settings/proxies/proxy/active", "false", true);
		editor.setNodeValue("//settings/proxies/proxy/protocol", "myproto", true);
		editor.setNodeValue("//settings/servers/server[1]/id", "myserver1", true);
		editor.setNodeValue("//settings/testnode02/@attr1", "yyy", true);
		editor.setNodeValue("//settings/localRepository/@testattr", "hello1", true);
		editor.setNodeValue("//settings/localRepository/@testattr2", "hello2", true);
		editor.setNodeValue("//settings/emptytestnode01/firstnewsubelement", "hello first", true);
		editor.setNodeValue("//settings/emptytestnode01/firstnewsubelement/@test1", "hello attr", true);
		editor.setNodeValue("//settings/emptytestnode01/secondsubelement", "hello second", true);
		editor.setNodeValue("//settings/emptytestnode02/firstnewsubelement/firstnewsubsubelement", "hello first subsub",
				true);
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/mavensettings2_res.xml"), testfile, true,
					true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/mavensettings2_resAlt.xml"), testfile,
					true, true);
		}
	}

	@Test
	public void testSetElementValueCreateNewNode() {
		File testfile = setupTestfile("web1.xml", "web.xml");
		ConfigFileXml configuration = new ConfigFileXml();
		configuration.setSourceurl(null);
		configuration.setPath(testfile.getAbsolutePath());
		configuration.setIndent("    ");
		configuration.setEncoding(CharsetsAvailable.ISO_8859_1);
		configuration.setStandalone(EnumXmlStandalone.no);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(configuration, testfile);
		editor.setElementValue("//web-app/mime-mapping[extension/.='xyz']/extension", "xyz", true);
		editor.setElementValue("//web-app/mime-mapping[extension/.='xyz']/mime-type", "my/mimetype", true);
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webNewNode.xml"), testfile, true, true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webNewNodeAlt.xml"), testfile, true,
					true);
		}
		assertTrue(testfile.delete());
	}

	@Test
	public void testDeleteNode() {
		File testfile = setupTestfile("web1.xml", "web.xml");
		ConfigFileXml configuration = new ConfigFileXml();
		configuration.setSourceurl(null);
		configuration.setPath(testfile.getAbsolutePath());
		configuration.setIndent("    ");
		configuration.setEncoding(CharsetsAvailable.ISO_8859_1);
		configuration.setStandalone(EnumXmlStandalone.no);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(configuration, testfile);
		editor.deleteNode("//web-app/context-param/param-name[.='config_home']");
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webDeletedNode.xml"), testfile, true,
					true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webDeletedNodeAlt.xml"), testfile, true,
					true);
		}
		assertTrue(testfile.delete());
	}

	@Test
	public void testDeleteNodeWithSubnodeCondition() {
		File testfile = setupTestfile("web1.xml", "web.xml");
		ConfigFileXml configuration = new ConfigFileXml();
		configuration.setSourceurl(null);
		configuration.setPath(testfile.getAbsolutePath());
		configuration.setIndent("    ");
		configuration.setEncoding(CharsetsAvailable.ISO_8859_1);
		configuration.setStandalone(EnumXmlStandalone.no);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(configuration, testfile);
		editor.deleteNode("//web-app/context-param[param-name/text()='config_home']");
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webDeletedNodeWithSubnodeCondition.xml"),
					testfile, true, true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(
					new File("src/test/resources/conf/webDeletedNodeWithSubnodeConditionAlt.xml"), testfile, true,
					true);
		}
		assertTrue(testfile.delete());
	}

	@Test
	public void testDeleteNodeTwice() {
		File testfile = setupTestfile("web1.xml", "web.xml");
		ConfigFileXml configuration = new ConfigFileXml();
		configuration.setSourceurl(null);
		configuration.setPath(testfile.getAbsolutePath());
		configuration.setIndent("    ");
		configuration.setEncoding(CharsetsAvailable.ISO_8859_1);
		configuration.setStandalone(EnumXmlStandalone.no);
		ConfigFileEditorXml editor = new ConfigFileEditorXml(configuration, testfile);
		editor.deleteNode("//web-app/context-param[param-name/text()='config_home']");
		editor.deleteNode("//web-app/context-param[param-name/text()='config_home']");
		editor.save();
		try {
			FileTestHelper.verifyFilesEqual(new File("src/test/resources/conf/webDeletedNodeWithSubnodeCondition.xml"),
					testfile, true, true);
		} catch (ComparisonFailure e) {
			FileTestHelper.verifyFilesEqual(
					new File("src/test/resources/conf/webDeletedNodeWithSubnodeConditionAlt.xml"), testfile, true,
					true);
		}
		assertTrue(testfile.delete());
	}

	@Test(expected = RapidEnvException.class)
	public void testDeleteNodeTopLevel() {
		File testfile = setupTestfile("web1.xml", "web.xml");
		ConfigFileEditorXml editor = new ConfigFileEditorXml(null, testfile);
		try {
			editor.deleteNode("//web-app");
		} catch (RapidEnvException e) {
			assertTrue(testfile.delete());
			throw e;
		}
	}

	/**
	 * Test isAttributePath().
	 */
	@Test
	public void testIsAttributePath() {
		assertTrue(ConfigFileEditorXml.isAttributePath("//xxx/yyy/@zXw-120"));
	}

	/**
	 * Test parseIdAttrs()
	 */
	@Test
	public void testParseIdAttrs() {
		XmlHelper.parseIdAttrs(
				"//xxx/yyy[@id='org.eclipse.wst.server.ui.editor'" + " and @name='JBoss 6.0 Runtime Server']");
	}

	private File setupTestfile(String testFileName, String testFileSourceName) {
		File testfile = new File("target/conf/" + testFileName);
		if (testfile.exists()) {
			assertTrue(testfile.delete());
		} else {
			if (!testfile.getParentFile().exists()) {
				assertTrue(testfile.getParentFile().mkdirs());
			}
		}
		FileHelper.copyFile(new File("src/test/resources/conf/" + testFileSourceName), testfile);
		return testfile;
	}
}
