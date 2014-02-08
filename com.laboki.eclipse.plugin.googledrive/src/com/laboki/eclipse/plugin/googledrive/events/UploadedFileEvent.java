package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

public final class UploadedFileEvent {

	private final String driveId;
	private final IResource resource;

	public UploadedFileEvent(final String driveId, final IResource resource) {
		this.driveId = driveId;
		this.resource = resource;
	}

	public String getDriveId() {
		return this.driveId;
	}

	public IResource getResource() {
		return this.resource;
	}
}
