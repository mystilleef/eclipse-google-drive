package com.laboki.eclipse.plugin.googledrive.main;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.laboki.eclipse.plugin.googledrive.instance.Instance;

public enum Factory implements Instance, IResourceVisitor {
	INSTANCE;

	private static final IPartService PART_SERVICE = EditorContext.getPartService();

	@Override
	public Instance begin() {
		Factory.emitPartActivationEvent(Factory.PART_SERVICE.getActivePart());
		return this;
	}

	@Override
	public Instance end() {
		return this;
	}

	public static void emitPartActivationEvent(@SuppressWarnings("unused") final IWorkbenchPart part) {
		final IProject project = EditorContext.getFile(EditorContext.getEditor()).getProject();
		System.out.println(project.getName());
		Factory.scan(project);
	}

	private static void scan(final IProject project) {
		try {
			project.accept(INSTANCE);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean visit(final IResource resource) throws CoreException {
		System.out.println(resource.getFullPath());
		return true;
	}
}
