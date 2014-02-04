package com.laboki.eclipse.plugin.googledrive.main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.ScannedResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScanProjectsForResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class ResourcesScanner extends EventBusInstance {

	private static final Logger LOGGER = Logger.getLogger(ResourcesScanner.class.getName());

	public ResourcesScanner(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ScanProjectsForResourcesEvent event) {
		Preconditions.checkNotNull(event.getProjectNames(), "ERROR: List of project names expected, not NULL");
		this.startScanTask(event.getProjectNames());
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ProjectNamesEvent event) {
		Preconditions.checkNotNull(event.getProjectNames(), "ERROR: List of project names expected, not NULL");
		this.startScanTask(event.getProjectNames());
	}

	private void startScanTask(final List<String> projectNames) {
		new Task() {

			@Override
			public void execute() {
				this.emitScannedResourcesEvent(this.scanProjectsForResources(projectNames));
			}

			private void emitScannedResourcesEvent(final List<IResource> resources) {
				ResourcesScanner.this.getEventBus().post(new ScannedResourcesEvent(ImmutableList.copyOf(resources)));
			}

			private List<IResource> scanProjectsForResources(final List<String> projectNames) {
				return new Scanner(this.getProjects(projectNames)).getResources();
			}

			private List<IProject> getProjects(final List<String> projectNames) {
				final ArrayList<IProject> projects = Lists.newArrayList();
				for (final String name : projectNames)
					projects.add(this.getProject(name));
				return projects;
			}

			private IProject getProject(final String projectName) {
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}.begin();
	}

	private final class Scanner implements IResourceVisitor {

		private final List<IResource> resources = Lists.newArrayList();
		private final List<IProject> projects;

		public Scanner(final List<IProject> projects) {
			this.projects = projects;
		}

		private void scanProjectsForResources() {
			for (final IProject project : this.projects)
				this.scan(project);
		}

		private void scan(final IProject project) {
			try {
				project.accept(this);
			} catch (final CoreException e) {
				ResourcesScanner.LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			this.resources.add(resource);
			return true;
		}

		public List<IResource> getResources() {
			this.scanProjectsForResources();
			return this.resources;
		}
	}
}
