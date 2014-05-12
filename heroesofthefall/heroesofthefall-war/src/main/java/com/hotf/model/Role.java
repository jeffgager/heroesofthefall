package com.hotf.model;

import com.hotf.model.val.ValidationException;

public interface Role {

	public static final String KIND_ROLE = "Role";
	public static final String PROPERTY_NAME = "name";

	public abstract String getId();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract void save() throws ValidationException;

}