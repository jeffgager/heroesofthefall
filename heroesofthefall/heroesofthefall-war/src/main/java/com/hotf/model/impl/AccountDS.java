package com.hotf.model.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.hotf.model.Account;

public class AccountDS implements Account {

	private static final String KIND_ACCOUNT = "Account";
	private Entity entity;
	
	public AccountDS(String accountId) {
		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			entity = datastore.get(KeyFactory.stringToKey(accountId));
		} catch (EntityNotFoundException e) {
			entity = new Entity(KIND_ACCOUNT, accountId);
		}
	}
	
	@Override
	public String getId() {
		return KeyFactory.keyToString(entity.getKey());
	}

	@Override
	public void save() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(entity);
	}

}
