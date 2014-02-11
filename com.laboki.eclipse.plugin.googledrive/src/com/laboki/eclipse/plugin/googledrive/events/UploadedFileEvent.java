package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class UploadedFileEvent {

	private final String driveId;
	private final IResource resource;

	public UploadedFileEvent(final String driveId, final IResource resource) {
		this.driveId = Preconditions.checkNotNull(driveId);
		this.resource = Preconditions.checkNotNull(resource);
	}

	public String getDriveId() {
		return this.driveId;
	}

	public IResource getResource() {
		return this.resource;
	}
}
