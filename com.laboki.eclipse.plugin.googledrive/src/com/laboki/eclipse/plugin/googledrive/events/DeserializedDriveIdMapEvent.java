package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;

public final class DeserializedDriveIdMapEvent {

	private final ImmutableBiMap<String, String> driveIdMap;

	public DeserializedDriveIdMapEvent(final ImmutableBiMap<String, String> driveIdMap) {
		this.driveIdMap = Preconditions.checkNotNull(driveIdMap);
	}

	public ImmutableBiMap<String, String> getDriveIdMap() {
		return this.driveIdMap;
	}
}
