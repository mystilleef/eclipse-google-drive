package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;

public final class EclipseGoogleDriveResourcesEvent {

	private final ImmutableList<IResource> resources;

	public EclipseGoogleDriveResourcesEvent(final ImmutableList<IResource> resources) {
		this.resources = resources;
	}

	public ImmutableList<IResource> getResources() {
		return this.resources;
	}
}
