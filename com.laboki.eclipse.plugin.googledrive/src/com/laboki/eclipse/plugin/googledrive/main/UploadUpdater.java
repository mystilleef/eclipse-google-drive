package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public final class UploadUpdater extends Uploader {

	private static final Logger LOGGER = Logger.getLogger(UploadUpdater.class.getName());

	public UploadUpdater(final Drive drive, final File metadata, final String filePath, final String mimeType) {
		super(drive, metadata, filePath, mimeType);
	}

	public void updateFile() {
		try {
			this.uploadFile(this.update(this.metadata.getId(), this.metadata, this.getFileContent()));
		} catch (final IOException e) {
			UploadUpdater.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
