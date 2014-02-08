package com.laboki.eclipse.plugin.googledrive.drive;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public final class Updater extends Uploader {

	private static final Logger LOGGER = Logger.getLogger(Updater.class.getName());

	public Updater(final Drive drive, final IResource resource, final File metadata) {
		super(drive, resource, metadata);
	}

	public void updateFile() {
		try {
			this.uploadFile(this.update(this.metadata.getId(), this.metadata, this.getFileContent()));
		} catch (final IOException e) {
			Updater.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
