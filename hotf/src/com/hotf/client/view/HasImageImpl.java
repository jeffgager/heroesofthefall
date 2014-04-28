/**
 * 
 */
package com.hotf.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Jeff
 *
 */
public class HasImageImpl implements HasImage {

	private Image image;
	
	public HasImageImpl(Image image) {
		this.image = image;
		updateImageFace();
	}
	
	@Override
	public void setUrl(String url) {
		this.image.setUrl(url);
	}
	
	@Override
	public String getUrl() {
		return image.getUrl();
	}
	
	@Override
	public void setTitle(String title) {
		image.setTitle(title);
	}
	
	@Override
	public String getTitle() {
		return image.getTitle();
	}

	private boolean selected = false;
	
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateImageFace();
	}

	private Boolean transparent = null;
	
	@Override
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
		updateImageFace();
	}
	
	private void updateImageFace() {
		if (transparent == null) {
			return;
		}
		DOM.setStyleAttribute(image.getElement(), "borderWidth", "1px");
		if (selected) {
			DOM.setStyleAttribute(image.getElement(), "borderColor", "red");
		} else {
			DOM.setStyleAttribute(image.getElement(), "borderColor", "black");
		} 
		if (transparent) {
			DOM.setStyleAttribute(image.getElement(), "borderStyle", "dashed");
			String s = Integer.toString((int) Math.round(60 * 100));
			String sDecimal = (s.length() == 1 ? ".0" : ".") + s;
			image.getElement().getStyle().setProperty("opacity", sDecimal);
			image.getElement().getStyle().setProperty("filter", "alpha(opacity=" + s + ")");
			image.getElement().getStyle().setProperty("MozMpacity", sDecimal);
			image.getElement().getStyle().setProperty("KhtmlOpacity", sDecimal);
		} else {
			DOM.setStyleAttribute(image.getElement(), "borderStyle", "solid");
			image.getElement().getStyle().setProperty("opacity", "");
			image.getElement().getStyle().setProperty("filter", "");
			image.getElement().getStyle().setProperty("MozMpacity", "");
			image.getElement().getStyle().setProperty("KhtmlOpacity", "");
		}
	}

}
