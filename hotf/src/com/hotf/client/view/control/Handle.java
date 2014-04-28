/**
 * 
 */
package com.hotf.client.view.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.hotf.client.ImageUtils;
import com.hotf.client.image.Images;

/**
 * @author Jeff
 *
 */
public class Handle extends Composite implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, HasMouseDownHandlers, HasMouseMoveHandlers, HasMouseUpHandlers, HasMouseOutHandlers {

	public static enum Type {Move, Size, Rotate}
	
	private Images images = (Images) GWT.create(Images.class);
	private AbsolutePanel panel;
	private Image image;
	private double x;
	private double y;
	private Shaper shaper;
	private boolean moving = false;

	public Handle(Type type, final AbsolutePanel panel, double x, double y, Shaper shaper) {
		this.panel = panel;
		this.x = x;
		this.y = y;
		this.shaper = shaper;
		if (type.equals(Type.Move)) {
			image = new Image(images.mover());
		} else if (type.equals(Type.Rotate)) {
				image = new Image(images.rotater());
		} else {
			image = new Image(images.sizer());
		}
		initWidget(image);
		setTitle(type.toString());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		panel.add(this, (int)x - 12, (int)y - 12);
		image.addMouseDownHandler(this);
		image.addMouseMoveHandler(this);
		image.addMouseUpHandler(this);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		moving = true;
		ImageUtils.get().setOpactity(image, 0.0D);
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if (moving) {
			setPosition(event.getClientX() - panel.getAbsoluteLeft(), event.getClientY() - panel.getAbsoluteTop());
			Handle.this.shaper.redraw(this);
		}
	}
	@Override
	public void onMouseUp(MouseUpEvent event) {
		moving = false;
		ImageUtils.get().setOpactity(image, 100.0D);
	}
	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (Element.is(event.getRelatedTarget())) {
			if (!((Element)event.getRelatedTarget().cast()).getClassName().contains("gwt-Image")) {
				moving = false;
			}
		}
		ImageUtils.get().setOpactity(image, 100.0D);
	}
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		panel.setWidgetPosition(Handle.this, (int)x - 12, (int)y - 12);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return image.addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return image.addMouseUpHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return image.addMouseMoveHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return image.addMouseDownHandler(handler);
	}

	
}
