package com.laboki.eclipse.plugin.googledrive.drive;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.laboki.eclipse.plugin.googledrive.main.MimeTypeContext;

public enum MetadataContext {
	INSTANCE;

	public static void insert(final IResource resource, final File metadata, final String parentId) {
		if (resource.getType() == IResource.FILE) MetadataContext.insertFile(resource, metadata, parentId);
		else MetadataContext.insertFolder(resource, metadata, parentId);
	}

	private static void insertFolder(final IResource resource, final File metadata, final String parentId) {
		metadata.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		metadata.setTitle(resource.getName());
		metadata.setMimeType(MimeTypeContext.getMimeType(resource));
		metadata.setDescription(resource.getFullPath().toString());
	}

	private static void insertFile(final IResource resource, final File metadata, final String parentId) {
		metadata.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		metadata.setDescription(resource.getFullPath().toString());
		metadata.setMimeType(MimeTypeContext.getMimeType(resource));
		metadata.setFileExtension(MetadataContext.getFileExtension(resource));
		metadata.setOriginalFilename(resource.getName());
		metadata.setTitle(resource.getName());
		final long lastModified = resource.getLocation().toFile().lastModified();
		metadata.setModifiedByMeDate(new DateTime(lastModified));
		metadata.setModifiedDate(new DateTime(lastModified));
	}

	private static String getFileExtension(final IResource resource) {
		final String extension = resource.getFileExtension();
		if (extension == null) return "";
		return extension;
	}

	public static void update(final IResource resource, final File metadata) {
		final long lastModified = resource.getLocation().toFile().lastModified();
		metadata.setModifiedByMeDate(new DateTime(lastModified));
		metadata.setModifiedDate(new DateTime(lastModified));
	}
}
