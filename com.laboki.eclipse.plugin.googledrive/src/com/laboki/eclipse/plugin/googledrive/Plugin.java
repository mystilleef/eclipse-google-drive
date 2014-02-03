package com.laboki.eclipse.plugin.googledrive;

import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.main.Services;
import com.laboki.eclipse.plugin.googledrive.task.AsyncTask;

public enum Plugin implements Instance {
	INSTANCE;

	private final static Services SERVICES = new Services();

	@Override
	public Instance begin() {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				Plugin.SERVICES.begin();
			}
		}.begin();
		return this;
	}

	@Override
	public Instance end() {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				Plugin.SERVICES.end();
			}
		}.begin();
		return this;
	}
}
