package com.laboki.eclipse.plugin.googledrive.events;

import com.google.common.collect.ImmutableList;

public final class UserSelectedProjectNamesEvent {

	private final ImmutableList<String> projectNames;

	public UserSelectedProjectNamesEvent(final ImmutableList<String> projectNames) {
		this.projectNames = projectNames;
	}

	public ImmutableList<String> getProjectNames() {
		return this.projectNames;
	}
}