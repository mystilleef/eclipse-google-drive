package com.laboki.eclipse.plugin.googledrive.main;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableList;

public final class UpdatedProjectResourcesEvent {

	private final ImmutableList<IResource> resources;

	public UpdatedProjectResourcesEvent(final ImmutableList<IResource> resources) {
		this.resources = resources;
	}

	public ImmutableList<IResource> getResources() {
		return this.resources;
	}
}
