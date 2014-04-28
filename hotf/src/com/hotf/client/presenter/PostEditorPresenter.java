package com.hotf.client.presenter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.event.ChangePostEvent;
import com.hotf.client.event.ChangePostEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.view.dialog.PostEditorDialog;

/**
 * PostViewerPresenter.
 * 
 * @author Jeff Gager
 */
public class PostEditorPresenter implements PostEditorDialog.Presenter, ChangePostEventHandler {

	private ClientFactory clientFactory;
	private PostEditorDialog postEditor;
	private PostResult post;
	private HandlerRegistration changePostRegistration;

	/**
	 * Constructor.
	 * 
	 * @param post Post
	 * @param postEditor PostEditorDialog
	 * @param clientFactory ClientFactory
	 */
	public PostEditorPresenter(GameResult game, PostResult post, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.postEditor = clientFactory.getPostEditor();
		this.post = post;
		changePostRegistration = clientFactory.getEventBus().addHandler(ChangePostEvent.TYPE, this);
		postEditor.setPresenter(this);
		postEditor.getPostEditField().setHTML(post.getText());
		StringBuffer sb = new StringBuffer();
		sb.append(post.getCharacterName());
		sb.append(" posted at ");
		DateTimeFormat dtf = clientFactory.getDateTimeFormat();
		sb.append(dtf.format(post.getCreated()));
		if (post.getUpdated() != null) {
			sb.append(" (Updated ");
			sb.append(dtf.format(post.getUpdated()));
			sb.append(")");
		}
		postEditor.setText(sb.toString());
		postEditor.center();

	}

	@Override
	public void cancel() {
		changePostRegistration.removeHandler();
		clientFactory.getPostEditor().hide();
	}
	
	@Override
	public void save() {

		//make sure there is something to post
		String postText = postEditor.getPostEditField().getHTML();
		if (postText == null || postText.length() <= 0 || postText.equals("<br>")) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "No text entered in post"));
			return;
		}
				
		//save post
		post.setText(postText);

		clientFactory.getGameController().savePost(post);
		
	}

	@Override
	public void onChange(ChangePostEvent event) {
		PostEditorPresenter.this.post = event.getPost();
		cancel();
	}

}