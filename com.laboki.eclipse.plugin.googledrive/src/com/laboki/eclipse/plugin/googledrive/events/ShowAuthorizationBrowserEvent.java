package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class ShowAuthorizationBrowserEvent {

	private final GoogleAuthorizationCodeFlow flow;

	public ShowAuthorizationBrowserEvent(final GoogleAuthorizationCodeFlow flow) {
		this.flow = Preconditions.checkNotNull(flow);
	}

	public GoogleAuthorizationCodeFlow getFlow() {
		return this.flow;
	}
}
