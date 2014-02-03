package com.laboki.eclipse.plugin.googledrive.main;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.googledrive.events.GoogleAuthorizationCodeEvent;
import com.laboki.eclipse.plugin.googledrive.events.ShowAuthorizationBrowserEvent;
import com.laboki.eclipse.plugin.googledrive.instance.EventBusInstance;
import com.laboki.eclipse.plugin.googledrive.task.AsyncTask;

public final class AuthorizationBrower extends EventBusInstance {

	private static final Shell SHELL = AuthorizationBrower.createShell();
	private static final Logger LOGGER = Logger.getLogger(AuthorizationBrower.class.getName());

	public AuthorizationBrower(final EventBus eventBus) {
		super(eventBus);
	}

	private static Shell createShell() {
		final Shell shell = new Shell(EditorContext.getShell(), SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setLayout(new FillLayout());
		shell.setText("Authorize Eclipse Drive Plugin");
		return shell;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void showAuthorizationBrowserEventHandler(final ShowAuthorizationBrowserEvent event) {
		new AsyncTask() {

			@Override
			protected void asyncExecute() {
				AuthorizationBrower.this.show(event.getFlow());
			}
		}.begin();
	}

	protected void show(final GoogleAuthorizationCodeFlow flow) {
		this.showAuthorizationPage(flow);
	}

	private void showAuthorizationPage(final GoogleAuthorizationCodeFlow flow) {
		final Browser browser = AuthorizationBrower.createBrowser(flow);
		this.addBrowserTitleListener(browser, flow);
		AuthorizationBrower.SHELL.open();
	}

	private static Browser createBrowser(final GoogleAuthorizationCodeFlow flow) {
		final Browser browser = new Browser(AuthorizationBrower.SHELL, SWT.NONE);
		browser.setUrl(AuthorizationBrower.getAuthorizationUrl(flow));
		return browser;
	}

	private static String getAuthorizationUrl(final GoogleAuthorizationCodeFlow flow) {
		return flow.newAuthorizationUrl().setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).build();
	}

	private void addBrowserTitleListener(final Browser browser, final GoogleAuthorizationCodeFlow flow) {
		browser.addTitleListener(new TitleListener() {

			@Override
			public void changed(final TitleEvent event) {
				AuthorizationBrower.SHELL.setText(event.title);
				this.handleResponse(event.title);
			}

			private void handleResponse(final String title) {
				if (!title.contains("=")) return;
				this.emitAuthorizationResult(title.split("=")[1]);
			}

			private void emitAuthorizationResult(final String result) {
				if (!result.equals("access_denied")) this.emitAuthorizationCode(result);
				else this.checkForAuthorizationFailure(result);
			}

			private void emitAuthorizationCode(final String result) {
				AuthorizationBrower.this.getEventBus().post(new GoogleAuthorizationCodeEvent(result, flow));
			}

			private void checkForAuthorizationFailure(final String result) {
				AuthorizationBrower.LOGGER.info(result);
			}
		});
	}
}
