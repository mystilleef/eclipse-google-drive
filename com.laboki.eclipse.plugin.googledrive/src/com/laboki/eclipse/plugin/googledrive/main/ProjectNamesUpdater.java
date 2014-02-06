package com.laboki.eclipse.plugin.googledrive.main;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectDeletedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserDeSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;

public final class ProjectNamesUpdater extends EventBusInstance {

	private final List<String> projectNames = Lists.newArrayList();

	public ProjectNamesUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DeserializedProjectNamesEvent event) {
		this.refreshUpdate(event.getProjectNames());
		this.emitProjectNamesEvent();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UserSelectedProjectNamesEvent event) {
		this.refreshUpdate(event.getProjectNames());
		this.emitProjectNamesEvent();
	}

	private synchronized void refreshUpdate(final ImmutableList<String> names) {
		this.projectNames.clear();
		this.projectNames.addAll(names);
	}

	@SuppressWarnings("unused")
	private synchronized void addUpdate(final ImmutableList<String> names) {
		this.projectNames.removeAll(names);
		this.projectNames.addAll(names);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UserDeSelectedProjectNamesEvent event) {
		this.removeUpdate(event.getProjectNames());
		this.emitProjectNamesEvent();
	}

	private synchronized void removeUpdate(final ImmutableList<String> names) {
		this.projectNames.removeAll(names);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ProjectDeletedEvent event) {
		this.removeUpdate(event.getResource().getName());
		this.emitProjectNamesEvent();
	}

	private synchronized void removeUpdate(final String name) {
		this.projectNames.remove(name);
	}

	private synchronized void emitProjectNamesEvent() {
		this.removeMissingProjects();
		EventBus.post(new ProjectNamesEvent(this.getProjectNames()));
		EditorContext.out(this.getProjectNames());
	}

	private void removeMissingProjects() {
		this.removeUpdate(ImmutableList.copyOf(this.findMissingProjects()));
	}

	private ArrayList<String> findMissingProjects() {
		final ArrayList<String> missingProjects = Lists.newArrayList();
		for (final String projectName : this.getProjectNames())
			if (ProjectNamesUpdater.projectDoesNotExist(projectName)) missingProjects.add(projectName);
		return missingProjects;
	}

	private static boolean projectDoesNotExist(final String projectName) {
		return !ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists();
	}

	private synchronized ImmutableList<String> getProjectNames() {
		return ImmutableList.copyOf(this.projectNames);
	}
}
