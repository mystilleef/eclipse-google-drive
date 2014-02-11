package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class DeserializedProjectNamesEvent {

	private final ImmutableList<String> projectNames;

	public DeserializedProjectNamesEvent(final ImmutableList<String> projectNames) {
		this.projectNames = Preconditions.checkNotNull(projectNames);
	}

	public ImmutableList<String> getProjectNames() {
		return this.projectNames;
	}
}
