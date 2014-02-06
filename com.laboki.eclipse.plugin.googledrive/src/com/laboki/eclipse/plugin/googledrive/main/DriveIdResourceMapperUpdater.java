package com.laboki.eclipse.plugin.googledrive.main;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdMapEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdResourceMapperEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;

public final class DriveIdResourceMapperUpdater extends EventBusInstance {

	private ImmutableList<IResource> resources;

	public DriveIdResourceMapperUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ProjectResourcesEvent event) {
		this.resources = event.getResources();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveIdMapEvent event) {
		EventBus.post(new DriveIdResourceMapperEvent(new DriveIdResourceMapper(event.getDriveIdMap(), this.resources)));
	}
}
