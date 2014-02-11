package com.laboki.eclipse.plugin.googledrive.events;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;

public final class GoogleAuthorizationCredentialEvent {

	private final Credential credential;

	public GoogleAuthorizationCredentialEvent(final Credential credential) {
		this.credential = Preconditions.checkNotNull(credential);
		System.out.println(this.getClass().getName() + " " + credential);
	}

	public Credential getCredential() {
		return this.credential;
	}
}
