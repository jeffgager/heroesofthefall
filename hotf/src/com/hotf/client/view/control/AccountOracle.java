/**
 * 
 */
package com.hotf.client.view.control;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.event.LoadAccountsEvent;
import com.hotf.client.event.LoadAccountsEventHandler;

/**
 * @author Jeff
 *
 */
public class AccountOracle extends SuggestOracle implements LoadAccountsEventHandler {

	public class AccountSuggestion implements Suggestion {
		
		private AccountResult account;
		
		public AccountSuggestion(AccountResult account) {
			this.account = account;
		}
		
		@Override
		public String getDisplayString() {
			return account.getName();
		}
		
		@Override
		public String getReplacementString() {
			return account.getName();
		}

		public AccountResult getAccount() {
			return account;
		}

	}
	
	private ClientFactory clientFactory;

	public AccountOracle(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		clientFactory.getEventBus().addHandler(LoadAccountsEvent.TYPE, this);
	}
	
	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
		requestSuggestions(request, callback);
	}

	private Request request;
	private Callback callback;

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {

		this.request = request;
		this.callback = callback;
		String query = request.getQuery() == null ? null : request.getQuery().toUpperCase();
		clientFactory.getGameController().getAccounts(query);

	}

	@Override
	public void onLoadAccounts(LoadAccountsEvent event) {
		final Response r = new Response();
		final ArrayList<AccountSuggestion> l = new ArrayList<AccountSuggestion>();
		r.setSuggestions(l);
		for (AccountResult ii : event.getAccounts()) {
			l.add(new AccountSuggestion(ii));
		}
		callback.onSuggestionsReady(request, r);
	}
	
	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}

}
