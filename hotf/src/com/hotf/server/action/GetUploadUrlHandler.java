package com.hotf.server.action;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.hotf.client.action.GetUploadUrlAction;
import com.hotf.client.action.result.UploadUrlResult;

public class GetUploadUrlHandler implements ActionHandler<GetUploadUrlAction, UploadUrlResult> {

	private static final Logger log = Logger.getLogger(GetUploadUrlHandler.class.getName());

	@Override
	public UploadUrlResult execute(GetUploadUrlAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetUploadUrlAction");

		UploadUrlResult logoutResult = new UploadUrlResult();
		logoutResult.setUrl(getUrl(action.getUrlbase()));
		return logoutResult;

	}

	/**
	 * Get URL upload URL.
	 * 
	 * @param urlbase page to return to when logging back in
	 * @return URL to logout client from GAE
	 */
	public String getUrl(String urlbase) {

		try {

			log.info("upload destination=" + urlbase);
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			String url = blobstoreService.createUploadUrl("/data/upload").replace("WH1736", "127.0.0.1");
			log.info("upload url=" + url);

			return url;

		} catch (RuntimeException t) {

			log.severe(t.getMessage());
			throw t;

		}

	}

	@Override
	public Class<GetUploadUrlAction> getActionType() {
		return GetUploadUrlAction.class;
	}

	@Override
	public void rollback(GetUploadUrlAction action, UploadUrlResult result, ExecutionContext context) throws DispatchException {
		
	}

}