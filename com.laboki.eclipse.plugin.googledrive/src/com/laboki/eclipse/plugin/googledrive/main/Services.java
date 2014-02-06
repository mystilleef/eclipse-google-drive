package com.laboki.eclipse.plugin.googledrive.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.googledrive.authorization.AuthorizationBrower;
import com.laboki.eclipse.plugin.googledrive.authorization.GoogleAuthorization;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.resources.ProjectNamesSerializer;
import com.laboki.eclipse.plugin.googledrive.resources.ProjectNamesUpdater;
import com.laboki.eclipse.plugin.googledrive.resources.ResourcesMonitor;
import com.laboki.eclipse.plugin.googledrive.resources.ResourcesScanner;
import com.laboki.eclipse.plugin.googledrive.resources.ResourcesUpdater;
import com.laboki.eclipse.plugin.googledrive.ui.ProjectSelectionDialog;

public final class Services implements Instance {

	private final List<Instance> instances = Lists.newArrayList();
	private final EventBus eventBus = new EventBus();

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.startService(new ProjectSelectionDialog(this.eventBus));
		this.startService(new DriveIdResourceMapperUpdater(this.eventBus));
		this.startService(new DriveIdMapUpdater(this.eventBus));
		this.startService(new DriveIdMapUpdaterSerializer(this.eventBus));
		this.startService(new ResourcesUpdater(this.eventBus));
		this.startService(new ResourcesScanner(this.eventBus));
		this.startService(new ProjectNamesUpdater(this.eventBus));
		this.startService(new ProjectNamesSerializer(this.eventBus));
		this.startService(new ResourcesMonitor(this.eventBus));
		this.startService(new AuthorizationBrower(this.eventBus));
		this.startService(new GoogleAuthorization(this.eventBus));
	}

	private void startService(final Instance instance) {
		instance.begin();
		this.instances.add(instance);
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

	private void stopService(final Instance instance) {
		instance.end();
		this.instances.remove(instance);
	}
}
