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
public class PostsResult implements Result {

	private List<PostResult> posts = new ArrayList<PostResult>();
		
	public PostsResult() {
		super();
	}

	public List<PostResult> getPosts() {
		return posts;
	}

}