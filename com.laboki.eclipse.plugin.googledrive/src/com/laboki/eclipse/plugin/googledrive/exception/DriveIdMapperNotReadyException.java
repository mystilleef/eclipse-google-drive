package com.laboki.eclipse.plugin.googledrive.exception;

public final class DriveIdMapperNotReadyException extends Exception {

	private static final long serialVersionUID = -4087362345215589363L;

	public DriveIdMapperNotReadyException() {}

	public DriveIdMapperNotReadyException(final String message) {
		super(message);
	}

	public DriveIdMapperNotReadyException(final Throwable cause) {
		super(cause);
	}

	public DriveIdMapperNotReadyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DriveIdMapperNotReadyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}