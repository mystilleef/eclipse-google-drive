package com.laboki.eclipse.plugin.googledrive.resources;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScanProjectsForResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ScannedResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class ResourcesScanner extends EventBusInstance {

	public ResourcesScanner(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ScanProjectsForResourcesEvent event) {
		Preconditions.checkNotNull(event.getProjectNames(), "ERROR: List of project names expected, not NULL");
		ResourcesScanner.startScanTask(event.getProjectNames());
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ProjectNamesEvent event) {
		Preconditions.checkNotNull(event.getProjectNames(), "ERROR: List of project names expected, not NULL");
		ResourcesScanner.startScanTask(event.getProjectNames());
	}

	private static void startScanTask(final List<String> projectNames) {
		new Task() {

			@Override
			public void execute() {
				this.emitScannedResourcesEvent(new FolderScanner().scanProjects(projectNames));
			}

			private void emitScannedResourcesEvent(final List<IResource> resources) {
				EventBus.post(new ScannedResourcesEvent(ImmutableList.copyOf(resources)));
			}
		}.begin();
	}
}
