package com.laboki.eclipse.plugin.googledrive.drive;

import java.io.IOException;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.laboki.eclipse.plugin.googledrive.events.UploadedFileEvent;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;

public final class FolderInserter {

	private final Drive drive;
	private final IResource resource;
	private final File metadata;

	public FolderInserter(final Drive drive, final File metadata) {
		this.drive = drive;
		this.metadata = metadata;
		this.resource = null;
	}

	public FolderInserter(final Drive drive, final IResource resource, final File metadata) {
		this.drive = drive;
		this.resource = resource;
		this.metadata = metadata;
	}

	public void newFolder() throws IOException {
		EventBus.post(new UploadedFileEvent(this.drive.files().insert(this.metadata).execute().getId(), this.resource));
	}

	public String getId() throws IOException {
		return this.drive.files().insert(this.metadata).execute().getId();
	}
}
