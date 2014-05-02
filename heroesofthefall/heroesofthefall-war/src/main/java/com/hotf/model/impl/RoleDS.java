package com.hotf.model.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.hotf.model.Role;

public class RoleDS implements Role {

	public static final String KIND_ROLE = "Role";
	public static final String PROPERTY_NAME = "name";

	private Entity entity;
	
	public RoleDS(AccountDS account) {
		String accountId = account.getId();
		if (accountId == null) {
			throw new IllegalArgumentException("Cannot create Role in an un-saved Account");
		}
		entity = new Entity(KIND_ROLE, KeyFactory.stringToKey(accountId));
	}
	
	public RoleDS(String roleId) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		entity = datastore.get(KeyFactory.stringToKey(roleId));
	}

	@Override
	public String getId() {
		return KeyFactory.keyToString(entity.getKey());
	}

	@Override
	public String getName() {
		return (String)entity.getProperty(PROPERTY_NAME);
	}

	@Override
	public void setName(String name) {
		entity.setProperty(PROPERTY_NAME, name);
	}

	@Override
	public void save() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(entity);
	}

}
