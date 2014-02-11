package com.laboki.eclipse.plugin.googledrive.drive;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Property;
import com.google.common.collect.Lists;

public enum PropertyContext {
	INSTANCE;

	public static void update(final IResource resource, final File metadata) {
		final List<Property> properties = Lists.newArrayList();
		// properties.add(PropertyContext.newResourcePathProperty(resource));
		properties.add(PropertyContext.newProjectTypeProperty(resource));
		metadata.setProperties(properties);
	}

	@SuppressWarnings("unused")
	private static Property newResourcePathProperty(final IResource resource) {
		final Property property = new Property();
		property.setKey("resourcePath");
		property.setValue(resource.getFullPath().toString());
		property.setVisibility("PRIVATE");
		return property;
	}

	private static Property newProjectTypeProperty(final IResource resource) {
		final Property property = new Property();
		property.setKey("projectType");
		property.setValue(PropertyContext.getProjectType(resource));
		property.setVisibility("PRIVATE");
		return property;
	}

	private static String getProjectType(final IResource resource) {
		if (resource.getType() == IResource.PROJECT) return "PROJECT";
		if (resource.getType() == IResource.FOLDER) return "FOLDER";
		return "FILE";
	}
}
