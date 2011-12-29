/**
 * 
 */
package org.rapidbeans.rapidenv.eclipseheadless;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Martin Bluemel
 */
public class EclipseHeadlessApplication implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws CoreException, IOException {
		final Iterator<?> iterator =  context.getArguments().values().iterator();
		final String[] args = (String[]) iterator.next();
		final String command = args[0];

		if (command.equals("project_import")) {
			final String projectPath = args[1];
			final Path projectDescrFilePath = new Path(projectPath
					+ "/" + IProjectDescription.DESCRIPTION_FILE_NAME);
			importProject(projectDescrFilePath);
		}

		return null;
	}

	/**
	 * @param projectDescrFilePath the path of the project description file (.project)
	 *
	 * @throws CoreException in case of severe problems
	 */
	private void importProject(final Path projectDescrFilePath)
			throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot workspaceRoot = workspace.getRoot();
		final IProjectDescription projectDescr = workspace.loadProjectDescription(projectDescrFilePath);
		final IProject project = workspaceRoot.getProject(projectDescr.getName());

		if (!project.exists()) {
			System.out.println("    - importing project \"" + projectDescr.getName() + "\"...");
			project.create(projectDescr, null);
			project.open(null);
		} else {
			System.out.println("  - WARNING: project = \""
					+ projectDescr.getName()
					+ "\" is already imported...");
		}

		workspace.save(false, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// do nothing on stop
	}
}
