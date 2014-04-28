package com.hotf.model.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.hotf.model.Role;

public class RoleDAO implements Role {

	private Entity entity;
	
	public RoleDAO() {
		entity = new Entity("Role");
	}
	
	public RoleDAO(String id) throws EntityNotFoundException {
		super();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		entity = datastore.get(KeyFactory.stringToKey(id));
	}

	@Override
	public String getId() {
		return KeyFactory.keyToString(entity.getKey());
	}

	@Override
	public String getName() {
		return (String)entity.getProperty("name");
	}

	@Override
	public void setName(String name) {
		entity.setProperty("name", name);
	}

	@Override
	public void save() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(entity);
	}

}
