package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

public final class ShowAuthorizationBrowserEvent {

	private final GoogleAuthorizationCodeFlow flow;

	public ShowAuthorizationBrowserEvent(final GoogleAuthorizationCodeFlow flow) {
		this.flow = flow;
		System.out.println(this.getClass().getName() + " " + flow);
	}

	public GoogleAuthorizationCodeFlow getFlow() {
		return this.flow;
	}
}
