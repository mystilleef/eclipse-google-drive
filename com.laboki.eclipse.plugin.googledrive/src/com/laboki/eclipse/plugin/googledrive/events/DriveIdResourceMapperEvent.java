package com.laboki.eclipse.plugin.googledrive.events;

import com.laboki.eclipse.plugin.googledrive.main.DriveIdResourceMapper;

public final class DriveIdResourceMapperEvent {

	private final DriveIdResourceMapper driveIdResourceMapper;

	public DriveIdResourceMapperEvent(final DriveIdResourceMapper driveIdResourceMapper) {
		this.driveIdResourceMapper = driveIdResourceMapper;
	}

	public DriveIdResourceMapper getDriveIdResourceMapper() {
		return this.driveIdResourceMapper;
	}
}
