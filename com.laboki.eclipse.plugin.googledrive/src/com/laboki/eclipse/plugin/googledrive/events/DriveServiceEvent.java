package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.services.drive.Drive;

public final class DriveServiceEvent {

	private final Drive driveService;

	public DriveServiceEvent(final Drive drive) {
		this.driveService = drive;
	}

	public Drive getDriveService() {
		return this.driveService;
	}
}
