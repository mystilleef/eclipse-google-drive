package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.CreateRootParentFolderEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.events.RootParentIdEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class RootParentIdFinder extends EventBusInstance {

	private static final String QUERY = "title = " + "'" + EditorContext.GOOGLE_DRIVE_ROOT_FOLDER + "'" + " and mimeType = 'application/vnd.google-apps.folder'";
	private static final Logger LOGGER = Logger.getLogger(RootParentIdFinder.class.getName());
	private Drive drive;

	public RootParentIdFinder(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveServiceEvent event) {
		this.drive = event.getDriveService();
		this.findRootIdTask();
	}

	private void findRootIdTask() {
		new Task() {

			@Override
			protected void execute() {
				try {
					this.trytofindRootParentId();
				} catch (final IOException e) {
					RootParentIdFinder.LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			private void trytofindRootParentId() throws IOException {
				this.findRootParentId(QUERY);
			}

			private void findRootParentId(final String query) throws IOException {
				final List<File> results = RootParentIdFinder.this.drive.files().list().setQ(query).execute().getItems();
				if (results.size() == 0) EventBus.post(new CreateRootParentFolderEvent());
				else this.getIdFromResult(results);
			}

			private void getIdFromResult(final List<File> results) {
				final String id = this.getId(results);
				if (id == null) EventBus.post(new CreateRootParentFolderEvent());
				else EventBus.post(new RootParentIdEvent(id));
			}

			private String getId(final List<File> results) {
				for (final File file : results)
					if (EditorContext.GOOGLE_DRIVE_ROOT_FOLDER.equals(file.getTitle())) return file.getId();
				return null;
			}
		}.begin();
	}
}
