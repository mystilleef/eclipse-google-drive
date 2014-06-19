package com.laboki.eclipse.plugin.googledrive.main;

import java.util.List;

import org.eclipse.core.resources.IResource;

import com.google.api.services.drive.Drive;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.events.RootParentIdEvent;
import com.laboki.eclipse.plugin.googledrive.events.UploadProjectsEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.resources.FolderScanner;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class ProjectUploader extends EventBusInstance {

	private Drive drive;
	private String id;
	private final List<Instance> instances = Lists.newArrayList();

	public ProjectUploader(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final DriveServiceEvent event) {
		this.drive = event.getDriveService();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final RootParentIdEvent event) {
		this.id = event.getRootParentId();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final UserSelectedProjectNamesEvent event) {
		new Task() {

			@Override
			protected void execute() {
				EventBus.post(new UploadProjectsEvent(event.getProjectNames()));
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void eventHandler(final UploadProjectsEvent event) {
		new Task() {

			@Override
			protected void execute() {
				this.upload(event.getProjectNames());
			}

			private void upload(final List<String> names) {
				ProjectUploader.this.startService(this.newUpdaterService(this.scanProjects(names)));
			}

			private RecursiveUploader newUpdaterService(final List<IResource> resources) {
				return new RecursiveUploader(ProjectUploader.this, EventBus.INSTANCE, ProjectUploader.this.drive, ProjectUploader.this.id, resources);
			}

			private List<IResource> scanProjects(final List<String> names) {
				return new FolderScanner().scanProjects(names);
			}
		}.begin();
	}

	private void startService(final Instance instance) {
		this.instances.add(instance.begin());
	}

	@Override
	public Instance end() {
		this.stopServices();
		return this;
	}

	private void stopServices() {
		for (final Instance instance : ImmutableList.copyOf(this.instances))
			this.stopService(instance);
	}

	protected void stopService(final Instance instance) {
		this.instances.remove(instance.end());
	}
}
