package com.laboki.eclipse.plugin.googledrive.main;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.DeserializedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.task.Task;

public final class ProjectNamesSerializer extends EventBusInstance {

	public static final String SERIALIZABLE_FILE_PATH = Serializer.getSerializableFilePath("eclipse.google.drive.project.names.ser");

	public ProjectNamesSerializer(final EventBus eventBus) {
		super(eventBus);
		EditorContext.emptyFile(ProjectNamesSerializer.SERIALIZABLE_FILE_PATH);
	}

	@Override
	public Instance begin() {
		this.emitDeserializedProjectNamesEvent();
		return super.begin();
	}

	private void emitDeserializedProjectNamesEvent() {
		this.getEventBus().post(new DeserializedProjectNamesEvent(ImmutableList.copyOf(ProjectNamesSerializer.deserialize())));
	}

	@SuppressWarnings("unchecked")
	private static List<String> deserialize() {
		final Object files = Serializer.deserialize(ProjectNamesSerializer.SERIALIZABLE_FILE_PATH);
		if (files == null) return Lists.newArrayList();
		return (List<String>) files;
	}

	@Subscribe
	@AllowConcurrentEvents
	public static synchronized void serializeProjectNamesEventHandler(final ProjectNamesEvent event) {
		new Task() {

			@Override
			public void execute() {
				this.serialize(event.getProjectNames());
			}

			private void serialize(final Collection<String> projectNames) {
				Serializer.serialize(ProjectNamesSerializer.SERIALIZABLE_FILE_PATH, projectNames);
			}
		}.begin();
	}
}
