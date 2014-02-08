package com.laboki.eclipse.plugin.googledrive.drive;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.model.File;
import com.laboki.eclipse.plugin.googledrive.events.UploadedFileEvent;
import com.laboki.eclipse.plugin.googledrive.main.EditorContext;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;

public class Uploader {

	private final Drive drive;
	protected final java.io.File ioFile;
	protected final String mimeType;
	protected final File metadata;
	private static final Logger LOGGER = Logger.getLogger(Inserter.class.getName());
	private final String filePath;
	private final IResource resource;

	protected Uploader(final Drive drive, final IResource resource, final File metadata) {
		this.drive = Preconditions.checkNotNull(drive);
		this.resource = Preconditions.checkNotNull(resource);
		this.metadata = Preconditions.checkNotNull(metadata);
		this.filePath = Preconditions.checkNotNull(resource.getLocation().toOSString());
		this.mimeType = Preconditions.checkNotNull(metadata.getMimeType());
		this.ioFile = Preconditions.checkNotNull(new java.io.File(this.filePath));
	}

	protected String getFileId() {
		return this.metadata.getId();
	}

	protected FileContent getFileContent() {
		return new FileContent(this.mimeType, this.ioFile);
	}

	protected Insert insert(final File metadata) throws IOException {
		return this.drive.files().insert(metadata);
	}

	protected Insert insert(final File metadata, final FileContent content) throws IOException {
		return this.drive.files().insert(metadata, content);
	}

	protected Update update(final String fileId, final File metadata) throws IOException {
		return this.drive.files().update(fileId, metadata);
	}

	protected Update update(final String fileId, final File metadata, final FileContent content) throws IOException {
		return this.drive.files().update(fileId, metadata, content);
	}

	protected void uploadFile(final Insert insert) {
		try {
			this.tryToUploadFile(insert);
		} catch (final IOException e) {
			this.handleUploadError(e);
		}
	}

	private void tryToUploadFile(final Insert insert) throws IOException {
		this.initUploader(insert);
		insert.execute();
	}

	protected void uploadFile(final Update update) {
		try {
			this.tryToUploadFile(update);
		} catch (final IOException e) {
			this.handleUploadError(e);
		}
	}

	private void tryToUploadFile(final Update update) throws IOException {
		this.initUploader(update);
		update.execute();
	}

	private void initUploader(final Drive.Files.Insert insert) {
		final MediaHttpUploader uploader = insert.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(false);
		uploader.setProgressListener(new FileUploadProgressListener());
		uploader.setDisableGZipContent(false);
	}

	private void initUploader(final Drive.Files.Update update) {
		final MediaHttpUploader uploader = update.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(false);
		uploader.setProgressListener(new FileUploadProgressListener());
		uploader.setDisableGZipContent(false);
	}

	private class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

		@Override
		public void progressChanged(final MediaHttpUploader uploader) throws IOException {
			switch (uploader.getUploadState()) {
				case INITIATION_STARTED:
					EditorContext.out("Upload Initiation has started for " + Uploader.this.metadata.getTitle());
					break;
				case INITIATION_COMPLETE:
					EditorContext.out("Upload Initiation is Complete for " + Uploader.this.metadata.getTitle());
					break;
				case MEDIA_IN_PROGRESS:
					EditorContext.out(Uploader.this.metadata.getTitle() + " Upload Progress: " + NumberFormat.getPercentInstance().format(uploader.getProgress()));
					break;
				case MEDIA_COMPLETE:
					EditorContext.out("Upload is Complete - " + Uploader.this.metadata.getTitle());
					EventBus.post(new UploadedFileEvent(Uploader.this.metadata.getId(), Uploader.this.resource));
					break;
				case NOT_STARTED:
					break;
				default:
					break;
			}
		}
	}

	private void handleUploadError(final IOException e) {
		EditorContext.out("Failed to insert new file" + this.metadata.getTitle());
		Uploader.LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}
}
