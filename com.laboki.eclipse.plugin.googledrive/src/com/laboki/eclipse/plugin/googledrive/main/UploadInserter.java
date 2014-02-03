package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

final class UploadInserter extends Uploader {

	private static final Logger LOGGER = Logger.getLogger(UploadInserter.class.getName());

	public UploadInserter(final Drive drive, final File metadata, final String filePath, final String mimeType) {
		super(drive, metadata, filePath, mimeType);
		this.newMetadata();
	}

	public UploadInserter(final Drive drive, final File metadata, final String filePath, final String mimeType, final String parentId) {
		super(drive, metadata, filePath, mimeType);
		this.newMetadata(parentId);
	}

	private void newMetadata() {
		this.metadata.setTitle(this.ioFile.getName());
		this.metadata.setMimeType(this.mimeType);
	}

	private void newMetadata(final String parentId) {
		this.newMetadata();
		this.metadata.setParents(Arrays.asList(new ParentReference().setId(parentId)));
	}

	public void newFile() {
		try {
			this.uploadFile(this.insert(this.metadata, this.getFileContent()));
		} catch (final IOException e) {
			UploadInserter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void newFolder() {
		try {
			this.createNewFolder();
		} catch (final IOException e) {
			UploadInserter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
