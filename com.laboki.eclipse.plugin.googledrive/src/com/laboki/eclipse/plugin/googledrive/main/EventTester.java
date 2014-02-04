package com.laboki.eclipse.plugin.googledrive.main;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.EclipseGoogleDriveResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;

public final class EventTester extends EventBusInstance {

	public EventTester(final EventBus eventBus) {
		super(eventBus);
		this.fireEvent();
	}

	private void fireEvent() {
		final List<String> projectNames = new ArrayList<>();
		projectNames.add("XMLExamples");
		projectNames.add("drive-cmdline-sample");
		projectNames.add("com.laboki.eclipse.plugin.responsiveness");
		projectNames.add("com.laboki.eclipse.feature.responsiveness");
		this.getEventBus().post(new ProjectNamesEvent(projectNames));
		// this.getEventBus().post(new ScanProjectsForResourcesEvent(null));
		// this.getEventBus().post(new ScanProjectsForResourcesEvent(new ArrayList<String>()));
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void genericEventHandler(final EclipseGoogleDriveResourcesEvent event) {
		for (final IResource resource : event.getResources())
			EditorContext.out(resource.getFullPath());
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void genericEventHandler(final DeserializedProjectNamesEvent event) {
		for (final String name : event.getProjectNames())
			name.toString();
	}
}
