package com.laboki.eclipse.plugin.googledrive.exception;

public final class DriveServiceNotReadyException extends Exception {

	private static final long serialVersionUID = -915299085359903255L;

	public DriveServiceNotReadyException() {}

	public DriveServiceNotReadyException(final String message) {
		super(message);
	}

	public DriveServiceNotReadyException(final Throwable cause) {
		super(cause);
	}

	public DriveServiceNotReadyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DriveServiceNotReadyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}