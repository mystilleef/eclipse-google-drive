package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class RootParentIdEvent {

	private final String RootParentId;

	public RootParentIdEvent(final String id) {
		this.RootParentId = Preconditions.checkNotNull(id);
	}

	public String getRootParentId() {
		return this.RootParentId;
	}
}
