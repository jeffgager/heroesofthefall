package com.hotf.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoadPostsEventHandler extends EventHandler {
  void onLoadPosts(LoadPostsEvent event);
}
