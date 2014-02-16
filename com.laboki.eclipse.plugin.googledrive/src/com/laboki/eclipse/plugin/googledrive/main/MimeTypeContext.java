package com.laboki.eclipse.plugin.googledrive.main;

import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.net.MediaType;

public enum MimeTypeContext {
	INSTANCE;

	private static final String GOOGLE_DRIVE_FOLDER_MIMETYPE = "application/vnd.google-apps.folder";
	private static final String GOOGLE_DRIVE_UNKNOWN_MIMETYPE = "application/vnd.google-apps.unknown";

	public static String getMimeType(final IResource file) {
		try {
			Preconditions.checkNotNull(file, "Resource should not be null");
			if (file.getType() != IResource.FILE) return MimeTypeContext.GOOGLE_DRIVE_FOLDER_MIMETYPE;
			return MimeTypeContext.getMediaType((IFile) file).toString();
		} catch (final Exception e) {
			return MimeTypeContext.GOOGLE_DRIVE_UNKNOWN_MIMETYPE;
		}
	}

	private static MediaType getMediaType(final IFile file) throws Exception {
		return MediaType.parse(Files.probeContentType(FileSystems.getDefault().getPath(MimeTypeContext.getURIPath(file))));
	}

	private static String getURIPath(final IResource resource) {
		return resource.getLocationURI().getPath();
	}
}
