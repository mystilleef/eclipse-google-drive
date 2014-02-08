package com.laboki.eclipse.plugin.googledrive.main;

public final class RootParentIdEvent {

	private final String RootParentId;

	public RootParentIdEvent(final String id) {
		this.RootParentId = id;
	}

	public String getRootParentId() {
		return this.RootParentId;
	}
}
