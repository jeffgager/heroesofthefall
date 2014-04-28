package com.hotf.client.view.control;

import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.view.client.TreeViewModel;

public class HotfCellBrowser extends CellBrowser {

	public <T> HotfCellBrowser(TreeViewModel viewModel, T rootValue) {
		super(viewModel, rootValue);
	}

	public <T> HotfCellBrowser(TreeViewModel viewModel, T rootValue, Resources resources) {
		super(viewModel, rootValue, resources);
	}
	
	@Override
	public SplitLayoutPanel getWidget() {
		return (SplitLayoutPanel)super.getWidget();
	}

}

