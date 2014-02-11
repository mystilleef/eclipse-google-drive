package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdResourceMapperEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.events.LocalFolderModificationStampUpdatedEvent;
import com.laboki.eclipse.plugin.googledrive.exception.DriveIdMapperNotReadyException;
import com.laboki.eclipse.plugin.googledrive.exception.DriveServiceNotReadyException;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class RemoteModificationStampUpdater extends EventBusInstance {

	private static final Logger LOGGER = Logger.getLogger(RemoteModificationStampUpdater.class.getName());
	private DriveIdResourceMapper mapper;
	private Drive drive;

	public RemoteModificationStampUpdater(final EventBus eventBus) {
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
	public void eventHandler(final LocalFolderModificationStampUpdatedEvent event) {
		new Task() {

			@Override
			public void execute() {
				try {
					if (this.isNotMapped(event.getResource())) return;
					this.tryToUpdateRemoteModificationStamp(event.getResource());
				} catch (final IOException e) {
					RemoteModificationStampUpdater.LOGGER.log(Level.WARNING, e.getMessage(), e);
				} catch (final DriveIdMapperNotReadyException e) {
					RemoteModificationStampUpdater.LOGGER.info(e.getMessage());
				} catch (final DriveServiceNotReadyException e) {
					RemoteModificationStampUpdater.LOGGER.info(e.getMessage());
				}
			}

			private boolean isNotMapped(final IResource resource) throws DriveIdMapperNotReadyException {
				return !this.isMapped(resource);
			}

			private boolean isMapped(final IResource resource) throws DriveIdMapperNotReadyException {
				if (RemoteModificationStampUpdater.this.mapper == null) throw new DriveIdMapperNotReadyException("Error: Mapper service is not ready");
				if (!RemoteModificationStampUpdater.this.mapper.hasResource(resource)) return false;
				if (!RemoteModificationStampUpdater.this.mapper.resourceHasId(resource)) return false;
				return true;
			}

			private void tryToUpdateRemoteModificationStamp(final IResource resource) throws IOException, DriveServiceNotReadyException {
				final String fileId = this.getFileId(resource);
				final File metadata = this.getMetadata(fileId);
				this.updateModificationStamp(resource, metadata);
				this.updateRemoteFile(fileId, metadata);
			}

			private String getFileId(final IResource resource) {
				return RemoteModificationStampUpdater.this.mapper.getIdFromResource(resource);
			}

			private File getMetadata(final String fileId) throws IOException, DriveServiceNotReadyException {
				if (RemoteModificationStampUpdater.this.drive == null) throw new DriveServiceNotReadyException("Error: Drive service is not ready.");
				return RemoteModificationStampUpdater.this.drive.files().get(fileId).execute();
			}

			private void updateModificationStamp(final IResource resource, final File metadata) {
				final long lastModified = resource.getModificationStamp();
				metadata.setModifiedByMeDate(new DateTime(lastModified));
				metadata.setModifiedDate(new DateTime(lastModified));
			}

			private void updateRemoteFile(final String fileId, final File metadata) throws IOException {
				RemoteModificationStampUpdater.this.drive.files().update(fileId, metadata).execute();
			}
		}.begin();
	}
}
