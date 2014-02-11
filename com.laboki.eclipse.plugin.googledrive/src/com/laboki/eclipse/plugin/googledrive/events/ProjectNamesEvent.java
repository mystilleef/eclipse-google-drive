package com.laboki.eclipse.plugin.googledrive.events;

import java.util.List;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class ProjectNamesEvent {

	private final List<String> projectNames;

	public ProjectNamesEvent(final List<String> projectNames) {
		this.projectNames = Preconditions.checkNotNull(projectNames);
	}

	public List<String> getProjectNames() {
		return this.projectNames;
	}
}
