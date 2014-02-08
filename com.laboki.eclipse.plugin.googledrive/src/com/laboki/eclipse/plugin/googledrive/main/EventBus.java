package com.laboki.eclipse.plugin.googledrive.main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public enum EventBus {
	INSTANCE;

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final AsyncEventBus BUS = new AsyncEventBus(EventBus.EXECUTOR);

	public static void register(final Object object) {
		EventBus.BUS.register(object);
	}

	public static void unregister(final Object object) {
		EventBus.BUS.unregister(object);
	}

	public static void post(final Object object) {
		new Task() {

			@Override
			public void execute() {
				EventBus.BUS.post(object);
			}
		}.begin();
	}
}
