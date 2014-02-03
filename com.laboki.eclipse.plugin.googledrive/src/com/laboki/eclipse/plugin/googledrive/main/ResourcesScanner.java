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
import com.laboki.eclipse.plugin.googledrive.events.EclipseGoogleDriveResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScanProjectsForResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class ResourcesScanner extends EventBusInstance implements IResourceVisitor {

	private static final Logger LOGGER = Logger.getLogger(ResourcesScanner.class.getName());
	private final List<IResource> resources = Lists.newArrayList();

	public ResourcesScanner(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scanProjectsForResourcesHandler(final ScanProjectsForResourcesEvent event) {
		Preconditions.checkNotNull(event.getProjectNames(), "ERROR: List of project names expected, not NULL");
		this.startScanTask(event.getProjectNames());
	}

	private void startScanTask(final List<String> projectNames) {
		new Task() {

			@Override
			public void execute() {
				ResourcesScanner.this.scanProjectsForResources(this.getProjects(projectNames));
				this.emitScannedResourcesEvent();
				ResourcesScanner.this.resources.clear();
			}

			private List<IProject> getProjects(final List<String> projectNames) {
				final ArrayList<IProject> projects = new ArrayList<>();
				for (final String name : projectNames)
					projects.add(this.getProject(name));
				return projects;
			}

			private IProject getProject(final String projectName) {
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}

			private void emitScannedResourcesEvent() {
				ResourcesScanner.this.getEventBus().post(new EclipseGoogleDriveResourcesEvent(ImmutableList.copyOf(ResourcesScanner.this.resources)));
			}
		}.begin();
	}

	private void scanProjectsForResources(final List<IProject> projects) {
		for (final IProject project : projects)
			ResourcesScanner.this.scan(project);
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
}
