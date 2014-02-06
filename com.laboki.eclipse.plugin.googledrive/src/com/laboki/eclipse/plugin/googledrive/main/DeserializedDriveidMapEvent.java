package com.laboki.eclipse.plugin.googledrive.main;

import com.google.common.collect.ImmutableBiMap;

public final class DeserializedDriveidMapEvent {

	private final ImmutableBiMap<String, String> driveIdMap;

	public DeserializedDriveidMapEvent(final ImmutableBiMap<String, String> driveIdMap) {
		this.driveIdMap = driveIdMap;
	}

	public ImmutableBiMap<String, String> getDriveIdMap() {
		return this.driveIdMap;
	}
}
