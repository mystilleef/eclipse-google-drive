package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.drive.FolderInserter;
import com.laboki.eclipse.plugin.googledrive.drive.Inserter;
import com.laboki.eclipse.plugin.googledrive.drive.MetadataContext;
import com.laboki.eclipse.plugin.googledrive.events.UploadedFileEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class RecursiveUploader extends EventBusInstance {

	private final List<IResource> resources;
	private final Drive drive;
	private final String rootFolderId;
	private final BiMap<String, IResource> uploadedResourcesCache = HashBiMap.create();
	private final ProjectUploader projectUploader;

	public RecursiveUploader(final ProjectUploader projectUploader, final EventBus eventBus, final Drive drive, final String rootFolderid, final List<IResource> resources) {
		super(eventBus);
		this.projectUploader = projectUploader;
		this.drive = drive;
		this.rootFolderId = rootFolderid;
		this.resources = resources;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UploadedFileEvent event) {
		new Task() {

			@Override
			protected void execute() {
				final IResource resource = event.getResource();
				if (RecursiveUploader.this.resources.contains(resource)) this.prepareToUploadNextFile(event, resource);
			}

			private void prepareToUploadNextFile(final UploadedFileEvent event, final IResource resource) {
				this.updateLocalCache(event, resource);
				this.removeUploadedFile(event);
				RecursiveUploader.this.upload();
			}

			private void updateLocalCache(final UploadedFileEvent event, final IResource resource) {
				RecursiveUploader.this.uploadedResourcesCache.forcePut(event.getDriveId(), resource);
			}

			private boolean removeUploadedFile(final UploadedFileEvent event) {
				return RecursiveUploader.this.resources.remove(event.getResource());
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		super.begin();
		this.startUploadTask();
		return this;
	}

	private void startUploadTask() {
		new Task() {

			@Override
			protected void execute() {
				RecursiveUploader.this.upload();
			};
		}.begin();
	}

	private void upload() {
		try {
			this.tryToUpload();
		} catch (final FinishedProjectsUploadException e) {
			this.projectUploader.stopService(this);
		}
	}

	private void tryToUpload() throws FinishedProjectsUploadException {
		if (this.resources.size() == 0) throw new FinishedProjectsUploadException();
		final IResource resource = this.resources.get(0);
		this.beginUpload(resource, this.newMetadata(resource));
	}

	private void beginUpload(final IResource resource, final File metadata) {
		if (resource.getType() == IResource.FILE) this.insertFileInDrive(metadata, resource);
		else this.insertFolderInDrive(metadata, resource);
	}

	private void insertFileInDrive(final File metadata, final IResource resource) {
		new Inserter(this.drive, resource, metadata).newFile();
	}

	private void insertFolderInDrive(final File metadata, final IResource resource) {
		try {
			new FolderInserter(this.drive, resource, metadata).newFolder();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private File newMetadata(final IResource resource) {
		final File metadata = new File();
		MetadataContext.insert(resource, metadata, this.getParentId(resource));
		// PropertyContext.update(resource, metadata);
		return metadata;
	}

	private String getParentId(final IResource resource) {
		if (resource.getType() == IResource.PROJECT) return this.rootFolderId;
		return this.uploadedResourcesCache.inverse().get(resource.getParent());
	}

	protected static final class FinishedProjectsUploadException extends Exception {

		private static final long serialVersionUID = 3036970565141264262L;

		public FinishedProjectsUploadException() {}

		public FinishedProjectsUploadException(final String message) {
			super(message);
		}

		public FinishedProjectsUploadException(final Throwable cause) {
			super(cause);
		}

		public FinishedProjectsUploadException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public FinishedProjectsUploadException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
	}
}
