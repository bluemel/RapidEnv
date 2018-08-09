/*
 * RapidEnv: Artifact.java
 *
 * Copyright (C) 2011 Martin Bluemel
 *
 * Creation Date: 11/29/2011
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

package org.rapidbeans.rapidenv.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.rapidbeans.core.util.Version;
import org.rapidbeans.core.util.XmlHelper;
import org.w3c.dom.Node;

/**
 * An Artifact of an Eclipse update site.
 */
public class Artifact {

	private String classifier = null;

	private String id = null;

	private Version version = null;

	private long artifactSize = -1;

	private long downloadSize = -1;

	private String downloadMd5 = null;

	private String downloadContentType = null;

	private final Properties properties = new Properties();

	/**
	 * Parse an Eclipse update site artifact file.
	 * 
	 * @param is
	 *            the input stream to parse.
	 * 
	 * @return a list of artifacts found.
	 */
	public static List<Artifact> parse(final InputStream is) {
		final List<Artifact> artifacts = new ArrayList<Artifact>();
		final Node root = XmlHelper.getDocumentTopLevel(is, true).getNextSibling();
		final Node nartifacts = XmlHelper.getFirstSubnode(root, "artifacts");
		final Node[] nartifactArray = XmlHelper.getSubnodes(nartifacts, "artifact");
		for (final Node nartifact : nartifactArray) {
			final Artifact artifact = new Artifact();
			artifact.classifier = XmlHelper.getNodeValue(nartifact, "@classifier");
			artifact.id = XmlHelper.getNodeValue(nartifact, "@id");
			artifact.version = new Version(XmlHelper.getNodeValue(nartifact, "@version"));
			final Node nproperties = XmlHelper.getFirstSubnode(nartifact, "properties");
			for (final Node prop : XmlHelper.getSubnodes(nproperties, "property")) {
				if (XmlHelper.getNodeValue(prop, "@name").equals("artifact.size")) {
					artifact.artifactSize = new Long(XmlHelper.getNode(prop, "@value").getNodeValue()).longValue();
				} else if (XmlHelper.getNodeValue(prop, "@name").equals("download.size")) {
					artifact.downloadSize = new Long(XmlHelper.getNode(prop, "@value").getNodeValue()).longValue();
				} else if (XmlHelper.getNodeValue(prop, "@name").equals("download.size")) {
					artifact.downloadSize = new Long(XmlHelper.getNode(prop, "@value").getNodeValue()).longValue();
				} else if (XmlHelper.getNodeValue(prop, "@name").equals("download.md5")) {
					artifact.downloadMd5 = XmlHelper.getNode(prop, "@value").getNodeValue();
				} else if (XmlHelper.getNodeValue(prop, "@name").equals("download.contentType")) {
					artifact.downloadContentType = XmlHelper.getNode(prop, "@value").getNodeValue();
				}
			}
			artifacts.add(artifact);
		}
		return artifacts;
	}

	/**
	 * @return the classifier
	 */
	public String getClassifier() {
		return classifier;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @return the artifactSize
	 */
	public long getArtifactSize() {
		return artifactSize;
	}

	/**
	 * @return the downloadSize
	 */
	public long getDownloadSize() {
		return downloadSize;
	}

	/**
	 * @return the downloadMd5
	 */
	public String getDownloadMd5() {
		return downloadMd5;
	}

	/**
	 * @return the downloadContentType
	 */
	public String getDownloadContentType() {
		return downloadContentType;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

}
