package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PostsResult;

public class GetPostsAction implements Action<PostsResult> {

	private Boolean important;
	private String createdOrder;
	private String locationId;
	
	public GetPostsAction() {
		super();
	}

	/**
	 * @param createdOrder
	 */
	public GetPostsAction(Boolean important, String createdOrder) {
		super();
		this.important = important;
		this.createdOrder = createdOrder;
		this.locationId = null;
	}

	/**
	 * @param createdOrder
	 */
	public GetPostsAction(Boolean important, String createdOrder, String locationId) {
		super();
		this.important = important;
		this.createdOrder = createdOrder;
		this.locationId = locationId;
	}

	/**
	 * @return the important
	 */
	public Boolean isImportant() {
		return important;
	}

	/**
	 * @return the createdOrder
	 */
	public String getCreatedOrder() {
		return createdOrder;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}
	
}
