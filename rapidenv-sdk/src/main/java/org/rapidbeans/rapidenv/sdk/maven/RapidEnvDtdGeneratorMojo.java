/*
 * Rapid Beans Framework, SDK, Maven Plugin: RapidBeansGeneratorMojo.java
 *
 * Copyright (C) 2013 Martin Bluemel
 *
 * Creation Date: 08/31/2018
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
package org.rapidbeans.rapidenv.sdk.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;
import org.rapidbeans.rapidenv.sdk.ant.TaskGenDtd;

/**
 * Goal to generate a DTD out of a model description
 */
@Mojo(name = "rapidenv-dtd-generator")
public class RapidEnvDtdGeneratorMojo extends AbstractMojo {

	/**
	 * force flag.
	 */
	@Parameter(required = false, defaultValue = "false")
	private boolean force;

	/**
	 * the (root) bean type to analyze
	 */
	@Parameter(required = true)
	private String type = null;

	/**
	 * the output DTD file
	 */
	@Parameter(required = true)
	private File dtd = null;

	/**
	 * The model root directory (folder).
	 */
	@Parameter(required = true)
	private String modelroot = null;

	/**
	 * The header.
	 */
	@Parameter(required = true)
	private String header = null;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException {
		try {
			getLog().info("Running RapidEnv DTD generator...");
			TaskGenDtd task = new TaskGenDtd();
			final Project project = new Project();
			project.setBaseDir(project.getBaseDir());
			task.setProject(project);
			task.setForce(this.force);
			task.setType(this.type);
			task.setDtd(this.dtd);
			task.setModelroot(this.modelroot);
			task.setHeader(this.header);
			task.execute();
			getLog().info("Finished RapidBEans DTD generator successfully.");
		} catch (RuntimeException e) {
			getLog().error("Finished RapidBEans DTD generator with Exception.", e);
			e.printStackTrace();
			throw e;
		}
	}
}
