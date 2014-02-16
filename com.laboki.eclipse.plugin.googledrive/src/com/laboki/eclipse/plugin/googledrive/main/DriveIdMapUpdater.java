package com.laboki.eclipse.plugin.googledrive.main;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedDriveIdMapEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdMapEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectResourcesEvent;
import com.laboki.eclipse.plugin.googledrive.events.UploadedFileEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class DriveIdMapUpdater extends EventBusInstance {

	private final BiMap<String, String> driveIdMap = HashBiMap.create();

	public DriveIdMapUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UploadedFileEvent event) {
		new Task() {

			@Override
			protected void execute() {
				DriveIdMapUpdater.this.driveIdMap.forcePut(event.getDriveId(), event.getResource().getFullPath().toString());
				DriveIdMapUpdater.this.emitDriveIdMapEvent();
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DeserializedDriveIdMapEvent event) {
		new Task() {

			@Override
			protected void execute() {
				DriveIdMapUpdater.this.refreshUpdate(event.getDriveIdMap());
				DriveIdMapUpdater.this.emitDriveIdMapEvent();
			}
		}.begin();
	}

	private synchronized void refreshUpdate(final ImmutableBiMap<String, String> map) {
		this.driveIdMap.clear();
		this.driveIdMap.putAll(map);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ProjectResourcesEvent event) {
		new Task() {

			@Override
			protected void execute() {
				DriveIdMapUpdater.this.refreshUpdate(event.getResources());
				DriveIdMapUpdater.this.emitDriveIdMapEvent();
			};
		}.begin();
	}

	private synchronized void refreshUpdate(final ImmutableList<IResource> resources) {
		final List<String> staleList = Lists.newArrayList();
		this.updateStaleResourcesList(resources, staleList);
		this.removeStaleResources(staleList);
	}

	private void updateStaleResourcesList(final ImmutableList<IResource> resources, final List<String> deleteList) {
		final List<String> resourcePaths = DriveIdMapUpdater.getResourcePaths(resources);
		for (final String cachedPath : this.driveIdMap.values())
			if (!resourcePaths.contains(cachedPath)) deleteList.add(cachedPath);
	}

	private static List<String> getResourcePaths(final ImmutableList<IResource> resources) {
		final List<String> resourcePaths = Lists.newArrayList();
		for (final IResource resource : resources)
			resourcePaths.add(resource.getFullPath().toString());
		return resourcePaths;
	}

	private void removeStaleResources(final List<String> deleteList) {
		for (final String resourcePath : deleteList)
			this.driveIdMap.inverse().remove(resourcePath);
	}

	private void emitDriveIdMapEvent() {
		new Task() {

			@Override
			protected void execute() {
				EventBus.post(new DriveIdMapEvent(ImmutableBiMap.copyOf(DriveIdMapUpdater.this.driveIdMap)));
			}
		}.begin();
	}
}
