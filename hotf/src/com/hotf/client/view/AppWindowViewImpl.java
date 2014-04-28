package com.hotf.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.view.component.MessagePanel;

public class AppWindowViewImpl extends Composite implements AppWindowView {

	private static final String[] MOBILE_SPECIFIC_SUBSTRING = {
	      "iPhone","Android","MIDP","Opera Mobi",
	      "Opera Mini","BlackBerry","HP iPAQ","IEMobile",
	      "MSIEMobile","Windows Phone","HTC","LG",
	      "MOT","Nokia","Symbian","Fennec",
	      "Maemo","Tear","Midori","armv",
	      "Windows CE","WindowsCE","Smartphone","240x320",
	      "176x220","320x320","160x160","webOS",
	      "Palm","Sagem","Samsung","SGH",
	      "SIE","SonyEricsson","MMP","UCWEB"};
	
	interface MyUiBinder extends UiBinder<Widget, AppWindowViewImpl> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private Presenter presenter;
	
	@UiField (provided=true) Resources resources;
	@UiField HTMLPanel page;
	@UiField Anchor strongholdLink;
	@UiField Anchor playLink;
	@UiField Anchor gamesLink;
	@UiField Anchor logoutLink;
	@UiField (provided=true) MessagePanel messagePanel;
	@UiField SimplePanel pageBody;
	@UiField FlowPanel mainPanel;

	public AppWindowViewImpl(ClientFactory clientFactory) {

		this.resources = clientFactory.getResources();
		this.messagePanel = clientFactory.getMessagePanel();

		initWidget(uiBinder.createAndBindUi(this));

		strongholdLinkHidden = new HasVisibilityImpl(strongholdLink);
		playLinkHidden = new HasVisibilityImpl(playLink);
		gamesLinkHidden = new HasVisibilityImpl(gamesLink);

		if (checkMobile()) {
			page.setWidth("640px");
			DOM.setStyleAttribute(page.getElement(), "marginLeft", "5px");
			DOM.setStyleAttribute(page.getElement(), "marginRight", "5px");
			updateViewport();
		}
	}

	private void updateViewport() {
	    NodeList<Element> tags = Document.get().getElementsByTagName("meta");
	    for (int i = 0; i < tags.getLength(); i++) {
	        MetaElement metaTag = ((MetaElement) tags.getItem(i));
	        if (metaTag.getName().equals("viewport")) {
	            metaTag.setContent("user-scalable=no, width=" + 660 + "px");
	        }
	    }
	}
	
	private boolean checkMobile() {
	      String userAgent = Window.Navigator.getUserAgent();
	     for (String mobile: MOBILE_SPECIFIC_SUBSTRING){
	           if (userAgent.contains(mobile)
	             || userAgent.contains(mobile.toUpperCase())
	             || userAgent.contains(mobile.toLowerCase())){
	                  return true;
	          }
	     }

	     return false;
	}

	@Override
	public void setWidget(IsWidget w) {
		pageBody.setWidget(w);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	private HasVisibility strongholdLinkHidden;
	
	@Override
	public HasVisibility getStrongholdLinkHidden() {
		return strongholdLinkHidden;
	}
	
	private HasVisibility playLinkHidden;
	
	@Override
	public HasVisibility getPlayLinkHidden() {
		return playLinkHidden;
	}
	
	private HasVisibility gamesLinkHidden;
	
	@Override
	public HasText getLoginOut() {
		return logoutLink;
	}
	
	@Override
	public HasVisibility getGamesLinkHidden() {
		return gamesLinkHidden;
	}
	
	@UiHandler ("playLink")
	public void playLink(ClickEvent e) {
		presenter.play();
	}

	@UiHandler ("strongholdLink")
	public void strongholdLink(ClickEvent e) {
		presenter.stronghold();
	}

	@UiHandler ("logoutLink")
	public void logoutLink(ClickEvent e) {
		presenter.loginOut();
	}

	@UiHandler ("gamesLink")
	public void gamesLink(ClickEvent e) {
		presenter.games();
	}

}
