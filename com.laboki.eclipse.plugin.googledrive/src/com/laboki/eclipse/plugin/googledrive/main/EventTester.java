package com.laboki.eclipse.plugin.googledrive.main;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.EclipseGoogleDriveResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScanProjectsForResourcesEvent;
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
		this.getEventBus().post(new ScanProjectsForResourcesEvent(projectNames));
		// this.getEventBus().post(new ScanProjectsForResourcesEvent(null));
		// this.getEventBus().post(new ScanProjectsForResourcesEvent(new ArrayList<String>()));
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void genericEventHandler(final EclipseGoogleDriveResourcesEvent resources) {
		for (final IResource resource : resources.getResources())
			EditorContext.out(resource.getFullPath());
	}
}
