package com.hotf.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * IE7 + IE8 fix for setHTML bug. 
 */
public class HasHtml implements HasHTML {

	private HasHTML hashtml;
	private Widget widget;
	
	public HasHtml(HasHTML hashtml) {
		this.hashtml = hashtml;
		if (hashtml instanceof Widget) {
			widget = (Widget)hashtml;
		}
	}

	@Override
	public String getText() {
		return hashtml.getText();
	}

	@Override
	public void setText(String text) {
		hashtml.setText(text);
	}

	@Override
	public String getHTML() {
		return hashtml.getHTML();
	}

	@Override
	public void setHTML(String html) {
		if (widget != null) {
			Document doc = Document.get();
			Element div =  doc.createElement("div");
			div.setInnerHTML(html);
			for (int i = 0; i < widget.getElement().getChildCount(); i++) {
				widget.getElement().getChild(i).removeFromParent();
			}
			widget.getElement().appendChild(div);
		} else {
			hashtml.setHTML(html);
		}
	}

}
