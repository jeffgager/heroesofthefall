package com.hotf.model.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.hotf.model.Role;
import com.hotf.model.val.PatternValidator;
import com.hotf.model.val.ValidationException;

/**
 * Simple GAE DataStore implementation for a Role.
 * 
 * @author Jeff Gager
 *
 */
public class RoleDS implements Role {

	private Entity entity;
	
	/**
	 * Create Role Constructor.
	 * @param account to create Role in
	 * @param name name of new Role
	 */
	public RoleDS(AccountDS account, String name) {
		entity = new Entity(KIND_ROLE, KeyFactory.stringToKey(account.getId()));
		setName(name);
	}
	
	/**
	 * Load Role Constructor.
	 * @param roleId of Role 
	 */
	public RoleDS(String roleId) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		entity = datastore.get(KeyFactory.stringToKey(roleId));
	}

	/**
	 * Get Id property.
	 * @return id
	 */
	@Override
	public String getId() {
		return KeyFactory.keyToString(entity.getKey());
	}

	/**
	 * Get name property.
	 * @return name
	 */
	@Override
	public String getName() {
		return (String)entity.getProperty(PROPERTY_NAME);
	}

	/**
	 * Set name property.
	 * @param name to set
	 */
	@Override
	public void setName(String name) {
		entity.setProperty(PROPERTY_NAME, name);
	}

	/**
	 * Save to data store.
	 */
	@Override
	public void save() throws ValidationException {
		getNamePatternValidator().validate(getName());
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(entity);
	}

	private static PatternValidator namePatternValidator;
	/**
	 * Lazily instantiate namePatternValidator.
	 * @return namePatternValidator
	 */
	private PatternValidator getNamePatternValidator() {
		if (namePatternValidator == null) {
			namePatternValidator = new PatternValidator(KIND_ROLE, PROPERTY_NAME); 
		}
		return namePatternValidator;
	}

}
