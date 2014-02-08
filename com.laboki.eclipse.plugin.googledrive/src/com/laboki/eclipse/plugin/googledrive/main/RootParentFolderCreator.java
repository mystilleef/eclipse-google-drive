package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.drive.FolderInserter;
import com.laboki.eclipse.plugin.googledrive.events.CreateRootParentFolderEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class RootParentFolderCreator extends EventBusInstance {

	private Drive drive;

	public RootParentFolderCreator(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveServiceEvent event) {
		this.drive = event.getDriveService();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(@SuppressWarnings("unused") final CreateRootParentFolderEvent event) {
		new Task() {

			@Override
			protected void execute() {
				final String id = this.getId();
				if (id == null) EditorContext.out("FAILED TO CREATE ROOT PARENT FOLDER");
				else EventBus.post(new RootParentIdEvent(id));
			}

			private String getId() {
				try {
					return new FolderInserter(RootParentFolderCreator.this.drive, this.getMetadata()).getId();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			private File getMetadata() {
				final File metadata = new File();
				metadata.setTitle(EditorContext.GOOGLE_DRIVE_ROOT_FOLDER);
				metadata.setMimeType(EditorContext.GOOGLE_DRIVE_FOLDER_MIMETYPE);
				return metadata;
			}
		}.begin();
	}
}
