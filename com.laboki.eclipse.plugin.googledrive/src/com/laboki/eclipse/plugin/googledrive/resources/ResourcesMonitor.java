package com.laboki.eclipse.plugin.googledrive.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.laboki.eclipse.plugin.googledrive.events.ProjectDeletedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceAddedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceChangedEvent;
import com.laboki.eclipse.plugin.googledrive.events.ResourceRemovedEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.instance.Instance;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;

public final class ResourcesMonitor extends EventBusInstance implements IResourceChangeListener {

	private static final Logger LOGGER = Logger.getLogger(ResourcesMonitor.class.getName());

	public ResourcesMonitor(final EventBus eventBus) {
		super(eventBus);
	}

	@Override
	public Instance begin() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
		return super.begin();
	}

	@Override
	public Instance end() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		return super.end();
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		this.emitChangedResourceEvent(event, event.getResource());
	}

	private void emitChangedResourceEvent(final IResourceChangeEvent event, final IResource resource) {
		switch (event.getType()) {
			case IResourceChangeEvent.PRE_DELETE:
				this.getEventBus().post(new ProjectDeletedEvent(resource));
				break;
			case IResourceChangeEvent.POST_CHANGE:
				this.newResourceDeltaMonitor(event);
				break;
			default:
				break;
		}
	}

	private void newResourceDeltaMonitor(final IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new ResourceDeltaMonitor());
		} catch (final CoreException e) {
			ResourcesMonitor.LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private final class ResourceDeltaMonitor implements IResourceDeltaVisitor {

		@Override
		public boolean visit(final IResourceDelta delta) throws CoreException {
			this.emitChangedResourceEvent(delta, delta.getResource());
			return true;
		}

		private void emitChangedResourceEvent(final IResourceDelta delta, final IResource resource) {
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					ResourcesMonitor.this.getEventBus().post(new ResourceAddedEvent(resource));
					break;
				case IResourceDelta.REMOVED:
					ResourcesMonitor.this.getEventBus().post(new ResourceRemovedEvent(resource));
					break;
				case IResourceDelta.CHANGED:
					if (resource.getType() == IResource.FILE) ResourcesMonitor.this.getEventBus().post(new ResourceChangedEvent(resource));
					break;
				default:
					break;
			}
		}
	}
}
