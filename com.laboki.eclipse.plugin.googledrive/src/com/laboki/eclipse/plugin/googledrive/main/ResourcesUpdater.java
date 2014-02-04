package com.laboki.eclipse.plugin.googledrive.main;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.ScannedResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceAddedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceRemovedEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserDeSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;

public final class ResourcesUpdater extends EventBusInstance {

	private final List<IResource> resources = Lists.newArrayList();
	private List<String> projectNames;

	public ResourcesUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ProjectNamesEvent event) {
		this.projectNames = event.getProjectNames();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ScannedResourcesEvent event) {
		this.addUpdate(event.getResources());
		this.emitUpdatedProjectResources();
	}

	private synchronized void addUpdate(final ImmutableList<IResource> resources) {
		this.resources.removeAll(resources);
		this.resources.addAll(resources);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ResourceAddedEvent event) {
		this.addUpdate(event.getResource());
		this.emitUpdatedProjectResources();
	}

	private synchronized void addUpdate(final IResource resource) {
		if (!this.projectNames.contains(resource.getProject().getName())) return;
		this.resources.remove(resource);
		this.resources.add(resource);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ResourceRemovedEvent event) {
		this.removeUpdate(event.getResource());
		this.emitUpdatedProjectResources();
	}

	private void emitUpdatedProjectResources() {
		this.getEventBus().post(new UpdatedProjectResourcesEvent(this.getResources()));
	}

	private synchronized void removeUpdate(final IResource resources) {
		this.resources.remove(resources);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UserDeSelectedProjectNamesEvent event) {
		this.removeUpdate(this.getResourcesBelongingTo(event.getProjectNames()));
		this.emitUpdatedProjectResources();
	}

	private synchronized void removeUpdate(final ImmutableList<IResource> resources) {
		this.resources.removeAll(resources);
	}

	private ImmutableList<IResource> getResourcesBelongingTo(final ImmutableList<String> projectNames) {
		final List<IResource> resources = Lists.newArrayList();
		for (final String projectName : projectNames)
			this.filterResourcesBelongingTo(projectName, resources);
		return ImmutableList.copyOf(resources);
	}

	private void filterResourcesBelongingTo(final String projectName, final List<IResource> resources) {
		for (final IResource resource : this.getResources())
			if (resource.getProject().getName().equals(projectName)) resources.add(resource);
	}

	private synchronized ImmutableList<IResource> getResources() {
		return ImmutableList.copyOf(this.resources);
	}
}
