package com.hotf.client.view.control;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

public abstract class ImageButtonCell<C> extends AbstractCell<C> {
	private static ImageResourceRenderer renderer;

	private ImageResource image;
	private String title;
	
	/**
	 * Construct a new ImageButtonCell.
	 */
	public ImageButtonCell(String title, ImageResource image) {
		super("click");
		this.title = title;
		this.image = image;
		if (renderer == null) {
			renderer = new ImageResourceRenderer();
		}
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<div style='float: right;'><a title='" + title + "'>");
			sb.append(renderer.render(image));
			sb.appendHtmlConstant("</a></div>");
		}
	}

	public void onBrowserEvent(Cell.Context context, Element parent, C value, NativeEvent event, com.google.gwt.cell.client.ValueUpdater<C> valueUpdater) {
		EventTarget eventTarget = event.getEventTarget();
		if (!Element.is(eventTarget)) {
			return;
		}
		if ("click".equals(event.getType())) {
			execute(value);
		}
	};

	public abstract void execute(C value);
	
}
