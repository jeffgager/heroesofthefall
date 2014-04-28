package com.hotf.client;

import java.util.logging.Logger;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.UmbrellaException;
import com.hotf.client.event.LoginEvent;
import com.hotf.client.event.LoginEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.PlayActivity.PlayPlace;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Hotf implements EntryPoint {

	private ClientFactory clientFactory = GWT.create(ClientFactory.class);
	private HandlerRegistration registration;
	private Logger logger = Logger.getLogger("");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// install AppWindowView in display
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(clientFactory.getAppWindowView());
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));

		// login
		HotfActivityMapper activityMapper = new HotfActivityMapper(
				clientFactory);
		HotfHistoryMapper historyMapper = new HotfHistoryMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper,
				clientFactory.getEventBus());
		activityManager.setDisplay(clientFactory.getAppWindowView());
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
				historyMapper);
		historyHandler.register(clientFactory.getPlaceController(),
				clientFactory.getEventBus(), new PlayPlace());

		registration = clientFactory.getEventBus().addHandler(LoginEvent.TYPE,
				new LoginEventHandler() {
					@Override
					public void onLogin(LoginEvent event) {
						historyHandler.handleCurrentHistory();
						registration.removeHandler();
					}
				});
		clientFactory.getGameController().login();

		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				logger.log(java.util.logging.Level.SEVERE, "Uncaught Exception: ", e);
				unwrap(e);
			}
		});

	}

	public void unwrap(Throwable e) {
		if (e instanceof UmbrellaException) {
			UmbrellaException ue = (UmbrellaException) e;
			if(ue.getCauses().size() == 1) {
				Throwable ce = ue.getCauses().iterator().next();
				logger.log(java.util.logging.Level.SEVERE, "Uncaught Exception Cause: ", ce);
				unwrap(ce);
			}
		}
	}

}
