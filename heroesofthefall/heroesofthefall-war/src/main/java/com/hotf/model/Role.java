package com.hotf.model;

public interface Role {

	public abstract String getId();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract void save();

}