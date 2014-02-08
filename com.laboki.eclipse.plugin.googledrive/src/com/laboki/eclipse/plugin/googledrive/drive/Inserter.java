package com.laboki.eclipse.plugin.googledrive.drive;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public final class Inserter extends Uploader {

	private static final Logger LOGGER = Logger.getLogger(Inserter.class.getName());

	public Inserter(final Drive drive, final IResource resource, final File metadata) {
		super(drive, resource, metadata);
	}

	public void newFile() {
		try {
			this.uploadFile(this.insert(this.metadata, this.getFileContent()));
		} catch (final IOException e) {
			Inserter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
