package com.laboki.eclipse.plugin.googledrive.main;

import com.google.api.services.drive.Drive;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.authorization.GoogleAuthorizationContext;
import com.laboki.eclipse.plugin.googledrive.events.DriveServiceEvent;
import com.laboki.eclipse.plugin.googledrive.events.GoogleAuthorizationCredentialEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class DriveServiceHandler extends EventBusInstance {

	public DriveServiceHandler(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final GoogleAuthorizationCredentialEvent event) {
		new Task() {

			@Override
			protected void execute() {
				EventBus.post(new DriveServiceEvent(new Drive.Builder(GoogleAuthorizationContext.HTTP_TRANSPORT, GoogleAuthorizationContext.JSON_FACTORY, event.getCredential()).setApplicationName(EditorContext.APPLICATION_NAME).build()));
			}
		}.begin();
	}
}
