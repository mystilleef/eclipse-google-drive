package com.laboki.eclipse.plugin.googledrive.resources;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.ResourceAddedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceRemovedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScannedResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.UpdatedProjectResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserDeSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.main.EditorContext;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;

public final class ResourcesUpdater extends EventBusInstance {

	private final List<IResource> resources = Lists.newArrayList();

	public ResourcesUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ScannedResourcesEvent event) {
		this.refreshUpdate(event.getResources());
		this.emitUpdatedProjectResources();
	}

	private void refreshUpdate(final ImmutableList<IResource> resources) {
		this.resources.clear();
		this.resources.addAll(resources);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ResourceAddedEvent event) {
		this.addUpdate(event.getResource());
		this.emitUpdatedProjectResources();
	}

	private synchronized void addUpdate(final IResource resource) {
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
		EventBus.post(new UpdatedProjectResourcesEvent(this.getResources()));
		this.printResources();
	}

	private void printResources() {
		for (final IResource resource : this.getResources())
			EditorContext.out(resource);
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

	@SuppressWarnings("unused")
	private synchronized void addUpdate(final ImmutableList<IResource> resources) {
		this.resources.removeAll(resources);
		this.resources.addAll(resources);
	}
}
