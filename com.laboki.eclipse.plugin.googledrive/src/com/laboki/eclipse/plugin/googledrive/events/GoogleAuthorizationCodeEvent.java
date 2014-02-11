package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class GoogleAuthorizationCodeEvent {

	private final String code;
	private final GoogleAuthorizationCodeFlow flow;

	public GoogleAuthorizationCodeEvent(final String code, final GoogleAuthorizationCodeFlow flow) {
		this.code = Preconditions.checkNotNull(code);
		this.flow = Preconditions.checkNotNull(flow);
	}

	public String getCode() {
		return this.code;
	}

	public GoogleAuthorizationCodeFlow getFlow() {
		return this.flow;
	}
}
