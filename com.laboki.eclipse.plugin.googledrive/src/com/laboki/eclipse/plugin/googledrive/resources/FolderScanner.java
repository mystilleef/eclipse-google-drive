package com.laboki.eclipse.plugin.googledrive.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class FolderScanner {

	private static final Logger LOGGER = Logger.getLogger(FolderScanner.class.getName());

	public List<IResource> scanProjects(final List<String> projectNames) {
		Preconditions.checkNotNull(projectNames);
		return this.scanProjectsForResources(projectNames);
	}

	public List<IResource> scanProjects(final String projectName) {
		Preconditions.checkNotNull(projectName);
		return this.scanProjectsForResources(ImmutableList.of(projectName));
	}

	public List<IResource> scanProject(final String projectName) {
		return this.scanProjects(projectName);
	}

	private List<IResource> scanProjectsForResources(final List<String> projectNames) {
		return new Scanner(FolderScanner.getProjects(projectNames)).getResources();
	}

	private static List<IContainer> getProjects(final List<String> projectNames) {
		final ArrayList<IContainer> projects = Lists.newArrayList();
		for (final String name : projectNames)
			projects.add(FolderScanner.getProject(name));
		return projects;
	}

	private static IContainer getProject(final String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public List<IResource> scanFolders(final List<IContainer> resources) {
		Preconditions.checkNotNull(resources);
		return new Scanner(resources).getResources();
	}

	public List<IResource> scanFolders(final IContainer resource) {
		Preconditions.checkNotNull(resource);
		return new Scanner(ImmutableList.of(resource)).getResources();
	}

	public List<IResource> scanFolder(final IContainer resource) {
		return this.scanFolders(resource);
	}

	private final class Scanner implements IResourceVisitor {

		private final List<IResource> resources = Lists.newArrayList();
		private final List<IContainer> folders;

		public Scanner(final List<IContainer> folders) {
			this.folders = folders;
		}

		private void scanFoldersForResources() {
			for (final IContainer folder : this.folders)
				this.scan(folder);
		}

		private void scan(final IContainer folder) {
			try {
				folder.accept(this);
			} catch (final CoreException e) {
				FolderScanner.LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			this.resources.add(resource);
			return true;
		}

		public List<IResource> getResources() {
			this.scanFoldersForResources();
			return this.resources;
		}
	}
}
