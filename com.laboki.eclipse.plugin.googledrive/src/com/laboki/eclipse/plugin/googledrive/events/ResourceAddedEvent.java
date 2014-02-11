package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class ResourceAddedEvent {

	private final IResource resource;

	public ResourceAddedEvent(final IResource resource) {
		this.resource = Preconditions.checkNotNull(resource);
	}

	public IResource getResource() {
		return this.resource;
	}
}
