package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.model.File;

class Uploader {

	private final Drive drive;
	protected final java.io.File ioFile;
	protected final String mimeType;
	protected final File metadata;
	private static final Logger LOGGER = Logger.getLogger(UploadInserter.class.getName());

	protected Uploader(final Drive drive, final File metadata, final String filePath, final String mimeType) {
		this.drive = drive;
		this.metadata = metadata;
		this.mimeType = mimeType;
		this.ioFile = new java.io.File(filePath);
	}

	protected String getFileId() {
		return this.metadata.getId();
	}

	protected FileContent getFileContent() {
		return new FileContent(this.mimeType, this.ioFile);
	}

	protected void createNewFolder() throws IOException {
		final File _metadata = this.insert(this.metadata).execute();
		System.out.println(_metadata.getTitle() + " Folder created");
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
	}

	private void initUploader(final Drive.Files.Update update) {
		final MediaHttpUploader uploader = update.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(false);
		uploader.setProgressListener(new FileUploadProgressListener());
	}

	private class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

		@Override
		public void progressChanged(final MediaHttpUploader uploader) throws IOException {
			System.out.println("===================");
			switch (uploader.getUploadState()) {
				case INITIATION_STARTED:
					System.out.println("Upload Initiation has started for " + Uploader.this.metadata.getTitle());
					break;
				case INITIATION_COMPLETE:
					System.out.println("Upload Initiation is Complete for " + Uploader.this.metadata.getTitle());
					break;
				case MEDIA_IN_PROGRESS:
					System.out.println(Uploader.this.metadata.getTitle() + " Upload Progress: " + NumberFormat.getPercentInstance().format(uploader.getProgress()));
					break;
				case MEDIA_COMPLETE:
					System.out.println("Upload is Complete - " + Uploader.this.metadata.getTitle());
					break;
				case NOT_STARTED:
					break;
				default:
					break;
			}
			System.out.println("===================");
		}
	}

	private void handleUploadError(final IOException e) {
		System.out.println("Failed to insert new file" + this.metadata.getTitle());
		Uploader.LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}
}
