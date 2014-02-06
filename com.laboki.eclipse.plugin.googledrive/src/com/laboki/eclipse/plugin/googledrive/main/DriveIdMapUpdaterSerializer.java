package com.laboki.eclipse.plugin.googledrive.main;

import java.util.HashMap;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedDriveIdMapEvent;
import com.laboki.eclipse.plugin.googledrive.events.DriveIdMapEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class DriveIdMapUpdaterSerializer extends EventBusInstance {

	public static final String SERIALIZABLE_FILE_PATH = Serializer.getSerializableFilePath("eclipse.google.drive.id.map.ser");

	public DriveIdMapUpdaterSerializer(final EventBus eventBus) {
		super(eventBus);
		EditorContext.emptyFile(DriveIdMapUpdaterSerializer.SERIALIZABLE_FILE_PATH);
	}

	@Override
	public Instance begin() {
		DriveIdMapUpdaterSerializer.emitDeserializedDriveIdMapEvent();
		return super.begin();
	}

	private static void emitDeserializedDriveIdMapEvent() {
		EventBus.post(new DeserializedDriveIdMapEvent(DriveIdMapUpdaterSerializer.deserialize()));
	}

	@SuppressWarnings("unchecked")
	private static ImmutableBiMap<String, String> deserialize() {
		final Object files = Serializer.deserialize(DriveIdMapUpdaterSerializer.SERIALIZABLE_FILE_PATH);
		if (files == null) return ImmutableBiMap.copyOf(HashBiMap.create(new HashMap<String, String>()));
		return (ImmutableBiMap<String, String>) files;
	}

	@Subscribe
	@AllowConcurrentEvents
	public static synchronized void eventHandler(final DriveIdMapEvent event) {
		new Task() {

			@Override
			public void execute() {
				this.serialize(event.getDriveIdMap());
			}

			private void serialize(final ImmutableBiMap<String, String> driveIdMap) {
				Serializer.serialize(DriveIdMapUpdaterSerializer.SERIALIZABLE_FILE_PATH, driveIdMap);
			}
		}.begin();
	}
}
