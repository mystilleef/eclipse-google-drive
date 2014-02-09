package com.laboki.eclipse.plugin.googledrive.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.LocalFolderModificationStampUpdatedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceAddedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceChangedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceRemovedEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class LocalModificationStampUpdater extends EventBusInstance {

	private static final Logger LOGGER = Logger.getLogger(LocalModificationStampUpdater.class.getName());

	public LocalModificationStampUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ResourceChangedEvent event) {
		new Task() {

			@Override
			public void execute() {
				LocalModificationStampUpdater.updateTimeStamp(event.getResource());
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ResourceAddedEvent event) {
		new Task() {

			@Override
			public void execute() {
				LocalModificationStampUpdater.updateTimeStamp(event.getResource());
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ResourceRemovedEvent event) {
		new Task() {

			@Override
			public void execute() {
				final IResource parentResource = event.getResource().getParent();
				if (!parentResource.exists()) return;
				final long lastModified = new java.util.Date().getTime();
				LocalModificationStampUpdater.updateLocalTimeStamp(parentResource, lastModified);
				LocalModificationStampUpdater.updateParentTimeStamp(parentResource, lastModified);
			}
		}.begin();
	}

	private static void updateTimeStamp(final IResource resource) {
		final IContainer parentResource = resource.getParent();
		final long lastModified = resource.getLocation().toFile().lastModified();
		LocalModificationStampUpdater.updateLocalTimeStamp(resource, lastModified);
		LocalModificationStampUpdater.updateLocalTimeStamp(parentResource, lastModified);
		LocalModificationStampUpdater.updateParentTimeStamp(parentResource, lastModified);
	}

	private static void updateLocalTimeStamp(final IResource resource, final long lastModified) {
		try {
			resource.setLocalTimeStamp(lastModified);
		} catch (final CoreException e) {
			LocalModificationStampUpdater.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void updateParentTimeStamp(final IResource resource, final long lastModified) {
		resource.getLocation().toFile().setLastModified(lastModified);
		EventBus.post(new LocalFolderModificationStampUpdatedEvent(resource));
	}
}
