package com.laboki.eclipse.plugin.googledrive.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.FileEditorInput;

import com.laboki.eclipse.plugin.googledrive.Activator;
import com.laboki.eclipse.plugin.googledrive.task.AsyncTask;

public enum EditorContext {
	INSTANCE;

	public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
	public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
	public static final IJobManager JOB_MANAGER = Job.getJobManager();
	public static final String APPLICATION_NAME = "Eclipse Google Drive";
	public static final MessageConsole CONSOLE = EditorContext.getConsole("Eclipse Google Drive Console");
	private static final Logger LOGGER = Logger.getLogger(EditorContext.class.getName());

	public static String getPluginFolderPath() {
		return Activator.getInstance().getStateLocation().toOSString();
	}

	public static void flushEvents() {
		try {
			EditorContext.tryToFlushEvent();
		} catch (final Exception e) {
			EditorContext.LOGGER.log(Level.FINEST, e.getMessage(), e);
		}
	}

	private static void tryToFlushEvent() {
		while (EditorContext.DISPLAY.readAndDispatch())
			EditorContext.DISPLAY.update();
	}

	public static void asyncExec(final Runnable runnable) {
		if (EditorContext.isInvalidDisplay()) return;
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void syncExec(final Runnable runnable) {
		if (EditorContext.isInvalidDisplay()) return;
		EditorContext.DISPLAY.syncExec(runnable);
	}

	private static boolean isInvalidDisplay() {
		return (EditorContext.DISPLAY == null) || EditorContext.DISPLAY.isDisposed();
	}

	public static Shell getShell() {
		return EditorContext.WORKBENCH.getModalDialogShellProvider().getShell();
	}

	public static IEditorPart getEditor() {
		return EditorContext.WORKBENCH.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static IFile getFile(final IEditorPart editor) {
		return ((FileEditorInput) editor.getEditorInput()).getFile();
	}

	public static IPartService getPartService() {
		return (IPartService) EditorContext.WORKBENCH.getActiveWorkbenchWindow().getService(IPartService.class);
	}

	public static Control getControl(final IEditorPart editor) {
		return (Control) editor.getAdapter(Control.class);
	}

	public static void cancelJobsBelongingTo(final String... jobNames) {
		for (final String jobName : jobNames)
			EditorContext.JOB_MANAGER.cancel(jobName);
	}

	public static boolean taskDoesNotExist(final String name) {
		return EditorContext.JOB_MANAGER.find(name).length == 0;
	}

	public static void out(final Object message) {
		EditorContext.CONSOLE.newMessageStream().println(String.valueOf(message));
		EditorContext.startShowConsoleTask();
	}

	private static void startShowConsoleTask() {
		new AsyncTask() {

			@Override
			protected void asyncExecute() {
				EditorContext.showConsole();
			};
		}.begin();
	}

	private static void showConsole() {
		try {
			EditorContext.tryToShowConsole();
		} catch (final PartInitException e) {
			EditorContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void tryToShowConsole() throws PartInitException {
		((IConsoleView) EditorContext.WORKBENCH.getActiveWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW)).display(EditorContext.CONSOLE);
	}

	private static MessageConsole getConsole(final String name) {
		final MessageConsole console = EditorContext.findConsole(name);
		if (console != null) return console;
		return EditorContext.newConsole(name);
	}

	private static MessageConsole findConsole(final String name) {
		final IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (final IConsole console : consoles)
			if (name.equals(console.getName())) return (MessageConsole) console;
		return null;
	}

	private static MessageConsole newConsole(final String name) {
		final MessageConsole myConsole = new MessageConsole(name, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static void emptyFile(final String filePath) {
		final File f = new File(filePath);
		if (f.exists()) return;
		EditorContext.createEmptyFile(f);
	}

	private static void createEmptyFile(final File f) {
		try {
			EditorContext.tryToCreateEmptyFile(f);
		} catch (final IOException e) {
			EditorContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void tryToCreateEmptyFile(final File f) throws IOException {
		final BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write("");
		out.close();
	}
}
