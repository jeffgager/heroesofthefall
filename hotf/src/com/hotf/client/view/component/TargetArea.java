package com.hotf.client.view.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TargetArea extends Composite {

	private static TargetAreaUiBinder uiBinder = GWT.create(TargetAreaUiBinder.class);

	interface TargetAreaUiBinder extends UiBinder<Widget, TargetArea> {
	}

	public TargetArea() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
}
