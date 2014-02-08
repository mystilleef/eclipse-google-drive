package com.laboki.eclipse.plugin.googledrive.events;

import com.google.common.collect.ImmutableBiMap;

public final class DriveIdMapEvent {

	private final ImmutableBiMap<String, String> driveIdMap;

	public DriveIdMapEvent(final ImmutableBiMap<String, String> driveIdMap) {
		this.driveIdMap = driveIdMap;
	}

	public ImmutableBiMap<String, String> getDriveIdMap() {
		return this.driveIdMap;
	}
}
