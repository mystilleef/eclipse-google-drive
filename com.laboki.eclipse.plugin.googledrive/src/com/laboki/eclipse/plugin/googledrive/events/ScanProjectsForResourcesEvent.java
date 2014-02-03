package com.laboki.eclipse.plugin.googledrive.events;

import java.util.List;

public final class ScanProjectsForResourcesEvent {

	private final List<String> names;

	public ScanProjectsForResourcesEvent(final List<String> projectNames) {
		this.names = projectNames;
	}

	public List<String> getProjectNames() {
		return this.names;
	}
}
