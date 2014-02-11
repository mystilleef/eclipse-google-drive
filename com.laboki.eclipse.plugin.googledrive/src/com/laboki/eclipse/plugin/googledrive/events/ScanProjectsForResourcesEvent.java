package com.laboki.eclipse.plugin.googledrive.events;

import java.util.List;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class ScanProjectsForResourcesEvent {

	private final List<String> names;

	public ScanProjectsForResourcesEvent(final List<String> projectNames) {
		this.names = Preconditions.checkNotNull(projectNames);
	}

	public List<String> getProjectNames() {
		return this.names;
	}
}
