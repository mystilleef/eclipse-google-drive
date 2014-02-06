package com.laboki.eclipse.plugin.googledrive.authorization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.laboki.eclipse.plugin.googledrive.Activator;
import com.laboki.eclipse.plugin.googledrive.main.EditorContext;

public enum GoogleAuthorizationContext {
	INSTANCE;

	private static final String CLIENT_SECRETS_JSON = "client_secrets.json";
	private static final java.io.File DATA_STORE_FOLDER = new java.io.File(EditorContext.getPluginFolderPath());
	public static final FileDataStoreFactory DATA_STORE_FACTORY = GoogleAuthorizationContext.createDataStoreFactory();
	public static final HttpTransport HTTP_TRANSPORT = GoogleAuthorizationContext.createHttpTransport();
	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final Reader CLIENT_SECRETS_STREAM_READER = GoogleAuthorizationContext.getInputStreamReader();
	public static final GoogleClientSecrets CLIENT_SECRETS = GoogleAuthorizationContext.getGoogleClientSecrets();

	private static FileDataStoreFactory createDataStoreFactory() {
		try {
			return new FileDataStoreFactory(GoogleAuthorizationContext.DATA_STORE_FOLDER);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static HttpTransport createHttpTransport() {
		try {
			return GoogleNetHttpTransport.newTrustedTransport();
		} catch (final GeneralSecurityException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static InputStreamReader getInputStreamReader() {
		return new InputStreamReader(GoogleAuthorizationContext.getFileInputStream());
	}

	private static FileInputStream getFileInputStream() {
		try {
			return (FileInputStream) FileLocator.openStream(Activator.getInstance().getBundle(), new Path(GoogleAuthorizationContext.CLIENT_SECRETS_JSON), false);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static GoogleClientSecrets getGoogleClientSecrets() {
		try {
			return GoogleClientSecrets.load(GoogleAuthorizationContext.JSON_FACTORY, GoogleAuthorizationContext.CLIENT_SECRETS_STREAM_READER);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
