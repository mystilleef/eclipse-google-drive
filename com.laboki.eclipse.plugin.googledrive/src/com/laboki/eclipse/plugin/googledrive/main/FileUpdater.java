package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.drive.MetadataContext;
import com.laboki.eclipse.plugin.googledrive.drive.PropertyContext;
import com.laboki.eclipse.plugin.googledrive.drive.Updater;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdResourceMapperEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceChangedEvent;
import com.laboki.eclipse.plugin.googledrive.exception.DriveIdMapperNotReadyException;
import com.laboki.eclipse.plugin.googledrive.exception.DriveServiceNotReadyException;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class FileUpdater extends EventBusInstance {

	private static final Logger LOGGER = Logger.getLogger(FileUpdater.class.getName());
	private DriveIdResourceMapper mapper;
	private Drive drive;

	public FileUpdater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveIdResourceMapperEvent event) {
		this.mapper = event.getDriveIdResourceMapper();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveServiceEvent event) {
		this.drive = event.getDriveService();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final ResourceChangedEvent event) {
		new Task() {

			@Override
			public void execute() {
				try {
					if (this.isNotMapped(event.getResource())) return;
					this.tryToUpdateFile(event.getResource());
				} catch (final IOException e) {
					FileUpdater.LOGGER.log(Level.SEVERE, e.getMessage(), e);
				} catch (final DriveIdMapperNotReadyException e) {
					FileUpdater.LOGGER.info(e.getMessage());
				} catch (final DriveServiceNotReadyException e) {
					FileUpdater.LOGGER.info(e.getMessage());
				}
			}

			private boolean isNotMapped(final IResource resource) throws DriveIdMapperNotReadyException {
				return !this.isMapped(resource);
			}

			private boolean isMapped(final IResource resource) throws DriveIdMapperNotReadyException {
				if (FileUpdater.this.mapper == null) throw new DriveIdMapperNotReadyException("Error: Mapper service is not ready");
				if (!FileUpdater.this.mapper.hasResource(resource)) return false;
				if (!FileUpdater.this.mapper.resourceHasId(resource)) return false;
				return true;
			}

			private void tryToUpdateFile(final IResource resource) throws IOException, DriveServiceNotReadyException {
				final File metadata = this.getMetadata(this.getFileId(resource));
				MetadataContext.update(resource, metadata);
				PropertyContext.update(resource, metadata);
				new Updater(FileUpdater.this.drive, resource, metadata).updateFile();
			}

			private String getFileId(final IResource resource) {
				return FileUpdater.this.mapper.getIdFromResource(resource);
			}

			private File getMetadata(final String fileId) throws IOException, DriveServiceNotReadyException {
				if (FileUpdater.this.drive == null) throw new DriveServiceNotReadyException("Error: Drive service is not ready.");
				return FileUpdater.this.drive.files().get(fileId).execute();
			}
		}.begin();
	}
}
