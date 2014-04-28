package com.hotf.client.view;

import com.google.gwt.user.cellview.client.CellList;

public interface PostListResources extends CellList.Resources {

	@Source(value = {"postlist.css"})
	CellList.Style cellListStyle();

}
