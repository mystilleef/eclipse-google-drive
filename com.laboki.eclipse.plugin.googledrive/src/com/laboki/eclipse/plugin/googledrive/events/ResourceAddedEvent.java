package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

public final class ResourceAddedEvent {

	private final IResource resource;

	public ResourceAddedEvent(final IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return this.resource;
	}
}
