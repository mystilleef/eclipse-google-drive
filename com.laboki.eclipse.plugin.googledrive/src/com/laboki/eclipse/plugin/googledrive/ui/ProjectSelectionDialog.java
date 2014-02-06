package com.laboki.eclipse.plugin.googledrive.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.ProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.events.ShowProjectSelectionDialogEvent;
import com.laboki.eclipse.plugin.googledrive.events.UserSelectedProjectNamesEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.main.EditorContext;
import com.laboki.eclipse.plugin.googledrive.main.EventBus;
import com.laboki.eclipse.plugin.googledrive.task.AsyncTask;

public final class ProjectSelectionDialog extends EventBusInstance {

	private static final int SPACING_SIZE_IN_PIXELS = 10;
	private static final int HEIGHT = 480;
	private static final int WIDTH = ProjectSelectionDialog.HEIGHT;
	private static final Shell SHELL = new Shell(EditorContext.getShell(), SWT.RESIZE | SWT.APPLICATION_MODAL);
	private static final CheckboxTableViewer VIEWER = new CheckboxTableViewer(new Table(ProjectSelectionDialog.SHELL, SWT.BORDER | SWT.CHECK));
	private static final Table TABLE = ProjectSelectionDialog.VIEWER.getTable();
	private static List<String> projectNames;

	public ProjectSelectionDialog(final EventBus eventBus) {
		super(eventBus);
		ProjectSelectionDialog.arrangeWidgets();
		ProjectSelectionDialog.setupDialog();
		this.setupViewer();
		this.addListeners();
	}

	private static void arrangeWidgets() {
		ProjectSelectionDialog.setDialogLayout();
		ProjectSelectionDialog.setViewerLayout();
		ProjectSelectionDialog.SHELL.pack();
		ProjectSelectionDialog.TABLE.pack();
	}

	private static void setDialogLayout() {
		final GridLayout layout = new GridLayout(1, true);
		ProjectSelectionDialog.spaceDialogLayout(layout);
		ProjectSelectionDialog.SHELL.setLayout(layout);
		ProjectSelectionDialog.SHELL.setLayoutData(ProjectSelectionDialog.createFillGridData());
	}

	private static void spaceDialogLayout(final GridLayout layout) {
		layout.marginLeft = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
		layout.marginTop = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
		layout.marginRight = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
		layout.marginBottom = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
		layout.horizontalSpacing = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
		layout.verticalSpacing = ProjectSelectionDialog.SPACING_SIZE_IN_PIXELS;
	}

	private static void setViewerLayout() {
		ProjectSelectionDialog.VIEWER.getTable().setLayoutData(ProjectSelectionDialog.createFillGridData());
	}

	private static GridData createFillGridData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
	}

	private static void setupDialog() {
		ProjectSelectionDialog.SHELL.setText("Select projects to sync with Google Drive");
		ProjectSelectionDialog.SHELL.setSize(ProjectSelectionDialog.WIDTH, ProjectSelectionDialog.HEIGHT);
	}

	private void setupViewer() {
		ProjectSelectionDialog.setupTable();
		ProjectSelectionDialog.VIEWER.setContentProvider(new ContentProvider());
		ProjectSelectionDialog.VIEWER.setLabelProvider(this.new LabelProvider());
	}

	private static void setupTable() {
		ProjectSelectionDialog.TABLE.setLinesVisible(true);
		ProjectSelectionDialog.TABLE.setHeaderVisible(false);
		ProjectSelectionDialog.TABLE.setSize(ProjectSelectionDialog.TABLE.getClientArea().width, ProjectSelectionDialog.TABLE.getClientArea().height);
	}

	private final static class ContentProvider implements IStructuredContentProvider {

		public ContentProvider() {}

		@Override
		public void dispose() {}

		@Override
		public void inputChanged(final Viewer arg0, final Object oldInput, final Object newInput) {}

		@Override
		public Object[] getElements(final Object inputElement) {
			return (Object[]) inputElement;
		}
	}

	class LabelProvider implements ILabelProvider {

		@Override
		public Image getImage(final Object arg0) {
			return null;
		}

		@Override
		public String getText(final Object project) {
			return ((IProject) project).getName();
		}

		@Override
		public void addListener(final ILabelProviderListener arg0) {}

		@Override
		public void dispose() {}

		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener arg0) {}
	}

	private void addListeners() {
		ProjectSelectionDialog.SHELL.addShellListener(new DialogShellListener());
	}

	private final class DialogShellListener implements ShellListener {

		public DialogShellListener() {}

		@Override
		public void shellActivated(final ShellEvent event) {}

		@Override
		public void shellClosed(final ShellEvent event) {
			event.doit = false;
			EventBus.post(new UserSelectedProjectNamesEvent(this.getProjectNamesFrom(ProjectSelectionDialog.VIEWER.getCheckedElements())));
			ProjectSelectionDialog.SHELL.setVisible(false);
		}

		private ImmutableList<String> getProjectNamesFrom(final Object[] elements) {
			final ArrayList<String> names = Lists.newArrayList();
			for (final Object project : elements)
				names.add(((IProject) project).getName());
			return ImmutableList.copyOf(names);
		}

		@Override
		public void shellDeactivated(final ShellEvent event) {}

		@Override
		public void shellDeiconified(final ShellEvent event) {}

		@Override
		public void shellIconified(final ShellEvent event) {}
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(final ProjectNamesEvent event) {
		ProjectSelectionDialog.projectNames = event.getProjectNames();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void eventHandler(@SuppressWarnings("unused") final ShowProjectSelectionDialogEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				this.showSelectionDialog();
			}

			private void showSelectionDialog() {
				ProjectSelectionDialog.VIEWER.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
				this.setSelectedProjects();
				ProjectSelectionDialog.SHELL.open();
				ProjectSelectionDialog.SHELL.setVisible(true);
			}

			private void setSelectedProjects() {
				for (final Object project : this.getProjectsInContentProvider())
					this.updateCheckedProjects(project);
			}

			private Object[] getProjectsInContentProvider() {
				return ((IStructuredContentProvider) ProjectSelectionDialog.VIEWER.getContentProvider()).getElements(ProjectSelectionDialog.VIEWER.getInput());
			}

			private void updateCheckedProjects(final Object project) {
				if (this.inProjectNamesList(project)) this.setChecked(project, true);
				else this.setChecked(project, false);
			}

			private boolean inProjectNamesList(final Object project) {
				return ProjectSelectionDialog.projectNames.contains(((IProject) project).getName());
			}

			private boolean setChecked(final Object project, final boolean check) {
				return ProjectSelectionDialog.VIEWER.setChecked(project, check);
			}
		}.begin();
	}
}
