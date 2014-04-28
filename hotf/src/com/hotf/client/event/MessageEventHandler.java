package com.hotf.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MessageEventHandler extends EventHandler {
  void onMessage(MessageEvent event);
}
