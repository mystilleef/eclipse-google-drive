package com.laboki.eclipse.plugin.googledrive.main;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.services.drive.DriveScopes;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.GoogleAuthorizationCodeEvent;
import com.laboki.eclipse.plugin.googledrive.events.GoogleAuthorizationCredentialEvent;
import com.laboki.eclipse.plugin.googledrive.events.ShowAuthorizationBrowserEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class GoogleAuthorization extends EventBusInstance {

	private static final Set<String> DRIVE_SCOPES = Collections.singleton(DriveScopes.DRIVE_FILE);
	private static final Logger LOGGER = Logger.getLogger(GoogleAuthorization.class.getName());

	public GoogleAuthorization(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void googleAuthorizationCodeEventHandler(final GoogleAuthorizationCodeEvent event) {
		new Task() {

			@Override
			protected void execute() {
				GoogleAuthorization.createAuthorizationCredential(event.getCode(), event.getFlow());
				GoogleAuthorization.this.initAuthorizationCredential(event.getFlow());
			}
		}.begin();
	}

	private static void createAuthorizationCredential(final String code, final GoogleAuthorizationCodeFlow flow) {
		try {
			flow.createAndStoreCredential(flow.newTokenRequest(code).setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).execute(), flow.getClientId());
		} catch (final IOException e) {
			GoogleAuthorization.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public Instance begin() {
		try {
			this.initAuthorizationCredential(GoogleAuthorization.createAuthorizationCodeFlow());
		} catch (final IOException e) {
			GoogleAuthorization.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return super.begin();
	}

	private void initAuthorizationCredential(final GoogleAuthorizationCodeFlow flow) {
		try {
			this.checkCredential(flow);
		} catch (final IOException e) {
			GoogleAuthorization.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void checkCredential(final GoogleAuthorizationCodeFlow flow) throws IOException {
		final Credential credential = flow.loadCredential(flow.getClientId());
		if (credential != null) this.emitAuhorizationCredential(credential);
		else this.requestUserAuthorization(flow);
	}

	private void emitAuhorizationCredential(final Credential credential) {
		this.getEventBus().post(new GoogleAuthorizationCredentialEvent(credential));
	}

	private void requestUserAuthorization(final GoogleAuthorizationCodeFlow flow) {
		this.getEventBus().post(new ShowAuthorizationBrowserEvent(flow));
	}

	private static GoogleAuthorizationCodeFlow createAuthorizationCodeFlow() throws IOException {
		return GoogleAuthorization.newAuthorizationCodeFlowBuilder().setApprovalPrompt("force").setAccessType("offline").setDataStoreFactory(GoogleAuthorizationContext.DATA_STORE_FACTORY).build();
	}

	private static GoogleAuthorizationCodeFlow.Builder newAuthorizationCodeFlowBuilder() {
		return new GoogleAuthorizationCodeFlow.Builder(GoogleAuthorizationContext.HTTP_TRANSPORT, GoogleAuthorizationContext.JSON_FACTORY, GoogleAuthorizationContext.CLIENT_SECRETS, GoogleAuthorization.DRIVE_SCOPES);
	}
}
