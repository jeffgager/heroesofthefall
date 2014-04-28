package com.hotf.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.view.control.MapEditor;

public class PlaceDrawImpl extends Composite implements PlaceDraw {

	private static LocationDrawImplUiBinder uiBinder = GWT.create(LocationDrawImplUiBinder.class);

	interface LocationDrawImplUiBinder extends UiBinder<Widget, PlaceDrawImpl> {
	}

	private Presenter presenter;

	@UiField(provided = true) final Resources resources;
	@UiField(provided = true) MapEditor mapEditor;

	public PlaceDrawImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();

		mapEditor = new MapEditor(clientFactory);

		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setImageData(String map, String overlay) {
		mapEditor.setImageData(map, overlay);
	}

	@Override
	public String getImageData() {
		return mapEditor.getImageData();
	}
	
	@UiHandler ("closeLink")
	public void close(ClickEvent e) {
		presenter.close();
	}
		
	@UiHandler ("saveLink")
	public void save(ClickEvent e) {
		presenter.save();
	}

}
