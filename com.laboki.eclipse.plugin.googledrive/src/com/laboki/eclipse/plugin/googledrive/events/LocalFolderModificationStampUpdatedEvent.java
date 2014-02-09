package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

public final class LocalFolderModificationStampUpdatedEvent {

	private final IResource resource;

	public LocalFolderModificationStampUpdatedEvent(final IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return this.resource;
	}
}
