package com.laboki.eclipse.plugin.googledrive;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.laboki.eclipse.plugin.googledrive.events.ShowProjectSelectionDialogEvent;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;
import com.laboki.eclipse.plugin.googledrive.task.AsyncTask;

public final class Handler extends AbstractHandler {

	public Handler() {}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				EventBus.post(new ShowProjectSelectionDialogEvent());
			}
		}.begin();
		return null;
	}
}
