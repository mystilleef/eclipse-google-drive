package com.laboki.eclipse.plugin.googledrive.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Serializer {

	private static final Logger LOGGER = Logger.getLogger(Serializer.class.getName());

	public static String getSerializableFilePath(final String fileName) {
		return Paths.get(EditorContext.getPluginFolderPath(), fileName).toString();
	}

	public static void serialize(final String filePath, final Object serializable) {
		try {
			Serializer.writeSerializableToFile(serializable, Serializer.getObjectOutput(filePath));
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void writeSerializableToFile(final Object serializable, final ObjectOutput output) {
		Serializer.writeOutput(serializable, output);
		Serializer.closeOutput(output);
	}

	private static void writeOutput(final Object serializable, final ObjectOutput output) {
		try {
			output.writeObject(serializable);
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void closeOutput(final ObjectOutput output) {
		try {
			output.close();
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static ObjectOutput getObjectOutput(final String filePath) throws Exception {
		return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
	}

	public static Object deserialize(final String filePath) {
		return Serializer.readObjectInput(Serializer.getNewObjectInputStream(filePath));
	}

	private static Object readObjectInput(final ObjectInput input) {
		try {
			return input.readObject();
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.FINEST, e.getMessage(), e);
		} finally {
			Serializer.closeObjectInput(input);
		}
		return null;
	}

	private static void closeObjectInput(final ObjectInput input) {
		try {
			if (input != null) input.close();
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.OFF, e.getMessage(), e);
		}
	}

	private static ObjectInput getNewObjectInputStream(final String filePath) {
		try {
			return Serializer.newObjectInputStream(filePath);
		} catch (final Exception e) {
			Serializer.LOGGER.log(Level.FINEST, e.getMessage(), e);
		}
		return null;
	}

	private static ObjectInput newObjectInputStream(final String filePath) throws FileNotFoundException, IOException {
		return new ObjectInputStream(Serializer.newBufferInputStream(filePath));
	}

	private static InputStream newBufferInputStream(final String filePath) throws FileNotFoundException {
		return new BufferedInputStream(Serializer.newFileInputStream(filePath));
	}

	private static InputStream newFileInputStream(final String filePath) throws FileNotFoundException {
		return new FileInputStream(filePath);
	}
}
