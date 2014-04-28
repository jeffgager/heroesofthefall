/**
 * 
 */
package com.hotf.client.view.dialog;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * @author Jeff
 * 
 */
public class UploadDialog extends DialogBox implements HasClickHandlers, HasValueChangeHandlers<String> {

	private VerticalPanel pWidget = new VerticalPanel();
	private Image iFullSize = new Image();
	private Button bCancel = new Button("Cancel");
	private Button bSave = new Button("Save");
	private FormPanel uploadForm = new FormPanel();
	private HorizontalPanel formPanel = new HorizontalPanel();
	private TextBox uploadType = new TextBox();
	private TextBox uploadId = new TextBox();
	private FileUpload fileUpload = new FileUpload();
	private String imageData;
	private String fileName;
	private String contentType;

	/**
	 * constructor
	 * 
	 */
	public UploadDialog() {

		uploadType.setName("uploadType");
		uploadType.setVisible(false);
		formPanel.add(uploadType);

		uploadId.setName("uploadId");
		uploadId.setVisible(false);
		formPanel.add(uploadId);

		fileUpload.setName("upload");
		formPanel.add(fileUpload);

		uploadForm.setWidget(formPanel);
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

		pWidget.add(uploadForm);

		//add image
		iFullSize.setSize("200px", "200px");
		pWidget.add(new ScrollPanel(iFullSize));

		//add button panel
		HorizontalPanel bp = new HorizontalPanel();
		bp.add(bCancel);
		bp.add(bSave);
		pWidget.add(bp);

		bCancel.setEnabled(true);
		bSave.setEnabled(false);

		setWidget(pWidget);
		setupHandlers();
		observe();
		
	}

	/*
	 * register button handlers 
	 */
	private void setupHandlers() {

		bCancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		bSave.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireChange();
			}
		});

	}

	/*
	 * start observing changes to fileUpload field 
	 */
	private void observe() {

		Element e = fileUpload.getElement();
		observeImageData(this, e);

	}

	/**
	 * observe changes on the file upload input
	 * 
	 * @param fuw
	 *            this class (widget)
	 * @param e
	 */
	private static native void observeImageData(UploadDialog fuw, Element e) /*-{
		function handleFileSelect(evt) {
			var files = evt.target.files;
			var output = [];
			for ( var i = 0, f; f = files[i]; i++) {
				var name = f.name;
				var type = f.type;
				var reader = new FileReader();
				reader.onload = function(evt) {
					var base64 = evt.target.result;
					fuw.@com.hotf.client.view.dialog.UploadDialog::setImage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(base64, name, type);
				}
				reader.readAsDataURL(files[i]);
			}
		}
		e.addEventListener('change', handleFileSelect, false);
	}-*/;

	/**
	 * process base64 mime imagedata content string
	 * 
	 * @param imageData
	 */
	public void setImage(String imageData, String fileName, String contentType) {
		
		this.imageData = imageData;
		this.fileName = fileName;
		this.contentType = contentType;

		bSave.setEnabled(false);

		iFullSize.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent event) {
				bSave.setEnabled(true);
			}
		});
		iFullSize.setUrl(imageData);

	}

	private void fireChange() {
		ValueChangeEvent.fire(this, imageData);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return bSave.addClickHandler(handler);
	}
	
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		uploadForm.addSubmitCompleteHandler(handler);
	}

	public void submit(String url, String type, String id) {
		uploadType.setText(type);
		uploadId.setText(id);
		uploadForm.setAction(url);
		uploadForm.submit();
		uploadForm.reset();
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

}
