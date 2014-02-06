package com.laboki.eclipse.plugin.googledrive.main;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

public final class DriveIdResourceMapper {

	private final ImmutableBiMap<String, String> driveIdMap;
	private final ImmutableList<IResource> resources;

	public DriveIdResourceMapper(final ImmutableBiMap<String, String> driveIdMap, final ImmutableList<IResource> resources) {
		this.driveIdMap = driveIdMap;
		this.resources = resources;
	}

	public IResource getResourceFromId(final String driveId) {
		return this.getResourceFromPath(this.driveIdMap.get(driveId));
	}

	public IResource getResourceFromPath(final String resourcePath) {
		for (final IResource resource : this.resources)
			if (resourcePath.equals(resource.getFullPath())) return resource;
		return null;
	}

	public String getIdFromResource(final IResource resource) {
		return this.driveIdMap.inverse().get(resource.getFullPath());
	}

	public ImmutableList<String> getIds() {
		return this.driveIdMap.keySet().asList();
	}

	public ImmutableList<String> getResourcesPaths() {
		return this.driveIdMap.values().asList();
	}

	public ImmutableList<IResource> getResources() {
		return this.resources;
	}
}
