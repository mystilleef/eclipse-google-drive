package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.services.drive.Drive;

public final class DriveServiceEvent {

	private final Drive driveService;

	public DriveServiceEvent(final Drive drive) {
		this.driveService = Preconditions.checkNotNull(drive);
	}

	public Drive getDriveService() {
		return this.driveService;
	}
}
