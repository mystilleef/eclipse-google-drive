package com.laboki.eclipse.plugin.googledrive.events;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;

public final class ScannedResourcesEvent {

	private final ImmutableList<IResource> resources;

	public ScannedResourcesEvent(final ImmutableList<IResource> resources) {
		this.resources = resources;
	}

	public ImmutableList<IResource> getResources() {
		return this.resources;
	}
}
