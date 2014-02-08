package com.laboki.eclipse.plugin.googledrive.events;

import com.google.common.collect.ImmutableList;

public final class UploadProjectsEvent {

	private final ImmutableList<String> projectNames;

	public UploadProjectsEvent(final ImmutableList<String> projectNames) {
		this.projectNames = projectNames;
	}

	public ImmutableList<String> getProjectNames() {
		return this.projectNames;
	}
}
