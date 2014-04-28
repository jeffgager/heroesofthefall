/**
 * 
 */
package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 *
 */
public class AccountsResult implements Result {

	private List<AccountResult> accounts = new ArrayList<AccountResult>();
	
	public AccountsResult() {
		super();
	}

	public List<AccountResult> getAccounts() {
		return accounts;
	}
	
}