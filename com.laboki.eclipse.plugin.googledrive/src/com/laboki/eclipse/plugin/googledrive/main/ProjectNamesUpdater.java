package com.laboki.eclipse.plugin.googledrive.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedProjectNamesEvent;
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
		this.printProjectNames();
	}

	private synchronized void refreshUpdate(final ImmutableList<String> names) {
		this.projectNames.clear();
		this.projectNames.addAll(names);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UserSelectedProjectNamesEvent event) {
		this.addUpdate(event.getProjectNames());
		this.emitProjectNamesEvent();
	}

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
		EditorContext.out(this.projectNames);
	}

	private synchronized void emitProjectNamesEvent() {
		this.getEventBus().post(new ProjectNamesEvent(this.getProjectNames()));
		this.printProjectNames();
	}

	private synchronized ImmutableList<String> getProjectNames() {
		return ImmutableList.copyOf(this.projectNames);
	}

	private void printProjectNames() {
		EditorContext.out(this.getProjectNames());
	}
}
