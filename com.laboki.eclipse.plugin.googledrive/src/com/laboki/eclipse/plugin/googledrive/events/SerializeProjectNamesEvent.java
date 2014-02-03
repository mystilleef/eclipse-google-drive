package com.laboki.eclipse.plugin.googledrive.events;

import java.util.List;

public final class SerializeProjectNamesEvent {

	private final List<String> projectNames;

	public SerializeProjectNamesEvent(final List<String> projectNames) {
		this.projectNames = projectNames;
	}

	public List<String> getProjectNames() {
		return this.projectNames;
	}
}
