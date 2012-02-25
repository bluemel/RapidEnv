/*
 * RapidEnv: ConfigFileEditorXml.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/11/2010
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.XmlHelper;
import org.rapidbeans.rapidenv.RapidEnvException;
import org.rapidbeans.rapidenv.RapidEnvInterpreter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.dom.DeferredAttrImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;

/**
 * @author Martin Bluemel
 */
public class ConfigFileEditorXml extends ConfigFileEditor {

	private Document document = null;

	/**
	 * the only constructor to be be used for ConfigFile objects.
	 * 
	 * @param cfile
	 *            - the XML file to process by the editor instance
	 * @param file
	 *            the file to edit (may be null)
	 */
	public ConfigFileEditorXml(final ConfigFile cfile, final File file) {
		super(cfile, file);
	}

	/**
	 * retrieves a node in an XML DOM Tree traversing it from top.
	 * 
	 * @param pattern
	 *            - the pattern
	 * 
	 * @return the attribute XML node
	 */
	public final Node retrieveNode(final String pattern) {
		this.load();
		if (!pattern.startsWith("//")) {
			throw new RapidEnvException("wrong pattern \"" + pattern + "\" for retrieveNode(String).\n"
			        + "Pattern has to start with \"//\"");
		}
		return XmlHelper.getNode(this.document, pattern);
	}

	/**
	 * retrieves a node in an XML DOM Tree traversing the tree from a given
	 * node.
	 * 
	 * @param node
	 *            - the node
	 * @param pattern
	 *            - the pattern
	 * @return the attribute XML node
	 */
	public final Node retrieveNode(final Node node, final String pattern) {
		this.load();
		if (pattern.startsWith("/")) {
			throw new RapidEnvException("wrong pattern \"" + pattern + "\" for retrieveNode(Node, String).\n"
			        + "Pattern must not start with start with \"/\"");
		}
		return XmlHelper.getNode(node, pattern);
	}

	/**
	 * retrieves a node value in an XML DOM Tree.
	 * 
	 * @param pattern
	 *            - the pattern
	 * @return the attribute value string
	 */
	public final String retrieveNodeValue(final String pattern) {
		this.load();
		return XmlHelper.getNodeValue(this.document, pattern);
	}

	/**
	 * retrieves an element or attribute node in an XML DOM Tree and sets its
	 * value.
	 * 
	 * @param pattern
	 *            - the pattern
	 * @param value
	 *            - the value string
	 * @param createIfNotExist
	 *            - if the node should be created
	 */
	public final void setNodeValue(final String pattern, final String value, final boolean createIfNotExist) {
		this.load();
		if (isAttributePath(pattern)) {
			setAttributeValue(pattern, value, createIfNotExist);
		} else {
			setElementValue(pattern, value, createIfNotExist);
		}
	}

	/**
	 * Determine if the XPath like expression determines an attribute or not
	 * (element).
	 * 
	 * @param path
	 *            the XPath like expression to support
	 * 
	 * @return false if the path determines an element true if the path
	 *         determines an attribute
	 */
	protected static boolean isAttributePath(final String path) {
		if (!path.contains("@")) {
			return false;
		}
		if (path.matches(".*@[A-Za-z0-9\\.\\-_]*\\z")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * retrieves an element node in an XML DOM Tree and sets its value.
	 * 
	 * @param nodePath
	 *            the path to this node
	 * @param value
	 *            the value string
	 */
	public final void setElementValue(final String nodePath, final String value, final boolean createIfNotExist) {
		final ConfigFileXml fileCfg = (ConfigFileXml) getConfigfile();
		final Node node = XmlHelper.getNode(this.document, nodePath);
		if (node == null) {
			if (createIfNotExist) {
				appendNewElementNode(nodePath, value, fileCfg);
			} else {
				throw new RapidEnvException("XML node \"" + nodePath + "\" not found in file "
				        + getConfigfile().getPathAsFile().getAbsolutePath());
			}
		} else {
			node.getFirstChild().setNodeValue(value);
		}
		this.setChangedSomething();
	}

	/**
	 * @param nodePath
	 * @param value
	 * @param fileCfg
	 */
	private void appendNewElementNode(final String nodePath, final String value, final ConfigFileXml fileCfg) {
		final String parentNodePath = StringHelper.splitBeforeLast(nodePath, "/");
		Node parentNode = XmlHelper.getNode(this.document, parentNodePath);
		if (parentNode == null) {
			appendNewElementNode(parentNodePath, null, fileCfg);
			parentNode = XmlHelper.getNode(this.document, parentNodePath);
		}
		if (parentNode == null) {
			throw new RapidEnvException("Failed to create parent node" + " of XML node \"" + nodePath + "\" in file "
			        + getConfigfile().getPathAsFile().getAbsolutePath());
		} else {
			indentXmlAppendBefore(fileCfg, parentNode);
			final Map<String, String> idAttrs = XmlHelper.parseIdAttrs(nodePath);
			String newChildName = null;
			newChildName = StringHelper.splitLast(nodePath, "/");
			if (idAttrs.size() > 0) {
				newChildName = StringHelper.splitBeforeLast(newChildName, "[");
			}
			final Node newChild = this.document.createElement(newChildName);
			if (value != null) {
				final Node textnode = this.document.createTextNode(value);
				newChild.appendChild(textnode);
			}
			for (final Entry<String, String> entry : idAttrs.entrySet()) {
				final Attr attribute = this.document.createAttribute(entry.getKey());
				attribute.setValue(entry.getValue());
				newChild.getAttributes().setNamedItem(attribute);
			}
			parentNode.appendChild(newChild);
			indentXmlAppendAfter(fileCfg, parentNode, newChild);
		}
	}

	/**
	 * @param fileCfg
	 *            the XML file configuration
	 * @param parentNode
	 *            the parent node
	 * @param newNode
	 *            the new node if called after or null if called before
	 */
	private void indentXmlAppendBefore(final ConfigFileXml fileCfg, Node parentNode) {
		if (fileCfg.getAddeleminnewline()) {

			int indent = countIndentBeforeAccordingToParent(fileCfg, parentNode, 0);

			final Node lastChild = parentNode.getLastChild();
			if (lastChild != null && lastChild instanceof TextImpl) {
				final int indentBefore = countIndent(((TextImpl) lastChild).getData(), fileCfg.getIndent());
				if (indentBefore < indent) {
					if (fileCfg.getIndent() != null && fileCfg.getIndent().length() > 0) {
						lastChild.setTextContent(indentLf(indent, fileCfg));
					}
					indent = -1;
				}
			}

			if (indent > -1) {
				parentNode.appendChild(this.document.createTextNode(indentLf(indent, fileCfg)));
			}
		}
	}

	/**
	 * @param fileCfg
	 * @param parentNode
	 *            the parent node to investigate
	 * @param depth
	 *            recursion depth
	 * 
	 * @return the indent count
	 */
	private int countIndentBeforeAccordingToParent(final ConfigFileXml fileCfg, Node parentNode, final int depth) {
		int indent = -1;
		final Node siblingBeforeParent = parentNode.getPreviousSibling();
		if (siblingBeforeParent instanceof TextImpl) {
			if (fileCfg.getIndent() != null && fileCfg.getIndent().length() > 0) {
				indent = countIndent(((TextImpl) siblingBeforeParent).getData(), fileCfg.getIndent()) + depth + 1;
			} else {
				indent = 0;
			}
		} else if (parentNode.getParentNode() == null) {
			indent = depth;
		} else if (siblingBeforeParent == parentNode.getParentNode()) {
			indent = countIndentBeforeAccordingToParent(fileCfg, parentNode.getParentNode(), depth + 1);
		}
		return indent;
	}

	/**
	 * @param fileCfg
	 *            the XML file configuration
	 * @param parentNode
	 *            the parent node
	 * @param newNode
	 *            the new node if called after or null if called before
	 */
	private void indentXmlAppendAfter(final ConfigFileXml fileCfg, final Node parentNode, final Node newNode) {

		if (fileCfg.getAddeleminnewline()) {
			int indent = -1;
			final Node siblingBeforeParent = parentNode.getPreviousSibling();
			if (siblingBeforeParent instanceof TextImpl) {
				indent = countIndent(((TextImpl) siblingBeforeParent).getData(), fileCfg.getIndent());
			}
			final Node lastChild = parentNode.getLastChild();
			if (lastChild instanceof TextImpl) {
				final int indentLastChild = countIndent(siblingBeforeParent.getTextContent(), fileCfg.getIndent());
				if (indentLastChild > indent) {
					if (fileCfg.getIndent() != null && fileCfg.getIndent().length() > 0) {
						lastChild.setTextContent(indentLf(indent, fileCfg));
					}
					indent = -1;
				}
			}
			if (indent > -1) {
				parentNode.appendChild(this.document.createTextNode(indentLf(indent, fileCfg)));
			}
		}
	}

	private int countIndent(final String text, final String indent) {
		int count = 0;
		final int index = text.indexOf('\n');
		if (index != -1 && text.trim().length() == 0) {
			final String indentAfterLf = text.substring(index + 1);
			count = indentAfterLf.length() / indent.length();
		}
		return count;
	}

	private String indentLf(final int iIndent, final ConfigFileXml fileCfg) {
		final StringBuffer sbIndent = new StringBuffer();
		sbIndent.append('\n');
		for (int i = 0; i < iIndent; i++) {
			sbIndent.append(fileCfg.getIndent());
		}
		return sbIndent.toString();

	}

	/**
	 * retrieves an attribute node in an XML DOM Tree and sets its value. If the
	 * attribute is not found, but it's node is found it is created.
	 * 
	 * @param path
	 *            - the attribute node path
	 * @param value
	 *            - the value string
	 */
	public final void setAttributeValue(final String path, final String value, final boolean createIfNotExist) {
		this.load();
		final Node attr = XmlHelper.getNode(this.document, path);
		if (attr == null) {
			final ConfigFileXml fileCfg = (ConfigFileXml) getConfigfile();
			final String parentNodePath = XmlHelper.getParentNodePattern(path);
			final String attname = XmlHelper.getAttributeName(path);
			Node parentNode = XmlHelper.getNode(this.document, parentNodePath);
			if (parentNode == null) {
				if (createIfNotExist) {
					setElementValue(parentNodePath, null, createIfNotExist);
				} else {
					throw new RapidEnvException("could not find parent node" + " for non existing attribute \"" + path
					        + "\"");
				}
				parentNode = XmlHelper.getNode(this.document, parentNodePath);
				if (parentNode == null) {
					throw new RapidEnvException("could not create parent node" + " for non existing attribute \""
					        + path + "\"");
				}
			}
			final Element element = (Element) parentNode;
			if (fileCfg.getAddattrinnewline()) {
				throw new RapidEnvException("Sorry! addattrinnemwline is not yet supported.");
				// parentNode.appendChild(this.document.createTextNode("\n"));
				// final Attr newAttrNode =
				// this.document.createAttribute(attname);
				// newAttrNode.setNodeValue(value);
				// element.setAttributeNode(newAttrNode);
			} else {
				element.setAttribute(attname, value);
			}
		} else {
			attr.setNodeValue(value);
		}
		this.setChangedSomething();
	}

	public void deleteNode(final String path) {
		this.load();
		final Node node = XmlHelper.getNode(this.document, path);
		if (node == null) {
			RapidEnvInterpreter.log(Level.WARNING, "XML node \"" + path + "\" already deleted.");
		}
		final Node parentNode = XmlHelper.getNode(this.document, StringHelper.splitBeforeLast(path, "/"));
		if (parentNode == null) {
			throw new RapidEnvException("Refused to delete the top level element \"" + node.getNodeName()
			        + "\" of XML file \"" + getFile().getAbsolutePath() + "\"");
		}
		final Node previousTextnode = node.getPreviousSibling();
		if (node instanceof DeferredAttrImpl) {
			parentNode.getAttributes().removeNamedItem(node.getNodeName());
		} else {
			parentNode.removeChild(node);
		}
		if (previousTextnode != null && previousTextnode.getNodeType() == Node.TEXT_NODE) {
			// final String text = previousTextnode.getNodeValue();
			parentNode.removeChild(previousTextnode);
		}
		this.setChangedSomething();
	}

	/**
	 * loads the file to be processed. Load implement a "lazy load" mechanism.
	 * Therefore it must be called before every operation but the load itself
	 * only is executed the first time load() is called.
	 */
	protected final void load() {
		if (this.document != null) {
			return;
		}

		try {
			final InputStream is = new FileInputStream(this.getFile());
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			this.document = db.parse(is);
		} catch (IOException e) {
			throw new RapidEnvException(e);
		} catch (ParserConfigurationException e) {
			throw new RapidEnvException(e);
		} catch (SAXException e) {
			throw new RapidEnvException(e);
		}
	}

	/**
	 * save only is perfomed if the changed flag is set.
	 */
	protected final void save() {
		if (!this.getChangedSomething()) {
			return;
		}

		try {
			final Transformer xformer = TransformerFactory.newInstance().newTransformer();

			// Write the DOM document to a file
			final Source source = new DOMSource(this.document);
			final Result result = new StreamResult(this.getFile());
			xformer.transform(source, result);

		} catch (TransformerException e) {
			throw new RapidEnvException(e);
		}
	}
}
