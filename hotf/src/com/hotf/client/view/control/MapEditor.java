package com.hotf.client.view.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageLoader;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.view.dialog.TextInputDialog;
import com.hotf.client.view.dialog.UploadDialog;

public class MapEditor extends Composite {

	private static final String UPGRADE_MESSAGE = "Please upgrade or change your browser to one that supports the HTML5 Canvas element";
	private static final String SHIFT_MESSAGE = "Press SHIFT key and click again to create a straight line to another node, or press CTRL for a curved line.";
	private static final String PLACE_SIZE_MESSAGE = "A place must be between 100 and 1000 pixels in width and height";
	private static final double MAX_RADIANS = 2.0D * Math.PI;
	private static final double ROTATION_DRAG_LENGTH = 300.0D;
	private static final int MIN_SIZE = 600;
	
	interface MyUiBinder extends UiBinder<Widget, MapEditor> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private ClientFactory clientFactory;
	private PencilShaper pencilShaper = new PencilShaper();
	private LineShaper lineShaper = new LineShaper();
	private PolyShaper polyShaper = new PolyShaper();
	private FilledPolyShaper filledPolyShaper = new FilledPolyShaper();
	private ImageShaper imageShaper = new ImageShaper();
	private TextShaper textShaper = new TextShaper();
	private ImageElement image;
	private boolean drawing = false;
	private boolean control = false;
	private List<Handle> handles = new ArrayList<Handle>();
	private List<Boolean> curverHandles = new ArrayList<Boolean>();
	private String text;

	@UiField Label imageSize;
	@UiField Image imageField;
	@UiField Image paletteField;
	@UiField ToggleButton pencilButton;
	@UiField AbsolutePanel handlePanel;
	@UiField (provided=true) Canvas canvas;
	@UiField TextBox widthField;
	@UiField TextBox heightField;
	@UiField TextBox strokeSizeField;
	@UiField ColorField strokeStyleField;
	@UiField ColorField fillStyleField;
	@UiField ColorField textStyleField;
	@UiField ListBox strokeCapField;
	@UiField ListBox strokeJoinField;
	
	public MapEditor(ClientFactory clientFactory) {

		super();

		this.clientFactory = clientFactory;

		canvas = Canvas.createIfSupported();

		if (canvas == null) {
			initWidget(new Label(UPGRADE_MESSAGE));
			return;
		}

		initWidget(uiBinder.createAndBindUi(this));

		for (LineCap c : LineCap.values()) {
			strokeCapField.addItem(c.getValue());
		}
		for (LineJoin j : LineJoin.values()) {
			strokeJoinField.addItem(j.getValue());
		}
		setWidth(MIN_SIZE);
		setHeight(MIN_SIZE);
		widthField.setValue(Integer.toString(MIN_SIZE));
		heightField.setValue(Integer.toString(MIN_SIZE));
		strokeStyleField.setValue(CssColor.make("#000000"));
		fillStyleField.setValue(CssColor.make("#000000"));
		textStyleField.setValue(CssColor.make("#000000"));
		strokeJoinField.setSelectedIndex(getLineJoinIndex("miter"));
		strokeCapField.setSelectedIndex(getLineCapIndex("square"));
		strokeSizeField.setValue("6");
		configureCanvas();

	}

	private void configureCanvas() {
		canvas.getContext2d().setFillStyle(fillStyleField.getValue());
		canvas.getContext2d().setStrokeStyle(strokeStyleField.getValue());
		canvas.getContext2d().setLineJoin(strokeJoinField.getValue(strokeJoinField.getSelectedIndex()));
		canvas.getContext2d().setLineCap(strokeCapField.getValue(strokeCapField.getSelectedIndex()));
		setStrokeSize();
		updatePalette();
	}
	
	private void setWidth() {
		String value = widthField.getValue();
		Integer width = null;
		try {
			width = Integer.parseInt(value);
			setWidth(width);
		} catch (NumberFormatException e) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Width must be a number"));
			return;
		}
	}


	private void setWidth(int w) {
		savePoint();
		canvas.setWidth(w + "px");
		handlePanel.setWidth(w + "px");
		canvas.setCoordinateSpaceWidth(w);
		configureCanvas();
		undoMap();
	}

	private void setHeight() {
		String value = heightField.getValue();
		Integer height = null;
		try {
			height = Integer.parseInt(value);
			setHeight(height);
		} catch (NumberFormatException e) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Height must be a number"));
			return;
		}
	}

	private void setHeight(int h) {
		savePoint();
		canvas.setHeight(h + "px");
		handlePanel.setHeight(h + "px");
		canvas.setCoordinateSpaceHeight(h);
		configureCanvas();
		undoMap();
	}
	
	private void setStrokeSize() {
		String s = strokeSizeField.getValue();
		try {
			int size = Integer.parseInt(s);
			canvas.getContext2d().setLineWidth((double)size);
			redraw();
		} catch (NumberFormatException e) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Stroke size must be a number"));
			return;
		}
	}

	private int getLineJoinIndex(String join) {
		LineJoin[] v = LineJoin.values();
		String j = join.toUpperCase();
		for (int i = 0; i < v.length; i++) {
			if (v[i].toString().equals(j)) {
				return i;
			}
		}
		return 0;
	}
	
	private int getLineCapIndex(String cap) {
		LineCap[] v = LineCap.values();
		String c = cap.toUpperCase();
		for (int i = 0; i < v.length; i++) {
			if (v[i].toString().equals(c)) {
				return i;
			}
		}
		return 0;
	}

	public String getImageData() {
		return canvas.toDataUrl("image/png");
	};

	public void setImageData(final String map, final String overlay) {
		
		if (canvas == null) {
			return;
		}
		
		//Default map size
		widthField.setValue(Integer.toString(MIN_SIZE));
		heightField.setValue(Integer.toString(MIN_SIZE));
		setWidth();
		setHeight();
		
		//clear to transparent overlay
		clearMap();

		//set image size
		setImageSize();

		//initialise map
		DOM.setStyleAttribute(canvas.getElement(), "backgroundImage", "none");
		DOM.setStyleAttribute(canvas.getElement(), "backgroundRepeat", "noRepeat");
		undo.clear();
		redo.clear();
		clearHandles();
		configureCanvas();

		if (map != null) {

			ImageLoader.loadImages(new String[] { map + "&ts=" + new Date().getTime() }, new ImageLoader.CallBack() {
				public void onImagesLoaded(ImageElement[] imageHandles) {
					widthField.setValue(Integer.toString(imageHandles[0].getWidth()));
					heightField.setValue(Integer.toString(imageHandles[0].getHeight()));
					setWidth();
					setHeight();
					DOM.setStyleAttribute(canvas.getElement(), "backgroundImage", "url('" + map + "')");
					clearMap();
					if (overlay != null) {

						ImageLoader.loadImages(new String[] { overlay + "&ts=" + new Date().getTime() }, new ImageLoader.CallBack() {
							public void onImagesLoaded(ImageElement[] imageHandles) {
								ImageElement img = imageHandles[0];
								widthField.setValue(Integer.toString(img.getWidth()));
								heightField.setValue(Integer.toString(img.getHeight()));
								setWidth();
								setHeight();
								canvas.getContext2d().drawImage(img, 0, 0);
								setImageSize();
							}
						});

					}
				}
			});

		} else {
			
			if (overlay != null) {

				ImageLoader.loadImages(new String[] { overlay + "&ts=" + new Date().getTime() }, new ImageLoader.CallBack() {
					public void onImagesLoaded(ImageElement[] imageHandles) {
						ImageElement img = imageHandles[0];
						widthField.setValue(Integer.toString(img.getWidth()));
						heightField.setValue(Integer.toString(img.getHeight()));
						setWidth();
						setHeight();
						canvas.getContext2d().drawImage(img, 0, 0);
						setImageSize();
					}
				});

			}

		}

	};
	
	@UiHandler("resizeButton")
	public void resizeButton(ClickEvent event) {
		if (widthField.getValue() == null || widthField.getValue().length() < 100 || widthField.getValue().length() > 1000) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, PLACE_SIZE_MESSAGE));
			return;
		}
		if (heightField.getValue() == null || heightField.getValue().length() < 100 || heightField.getValue().length() > 1000) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, PLACE_SIZE_MESSAGE));
			return;
		}
		configureCanvas();
		setWidth();
		setHeight();
		redraw();
	}
	
	@UiHandler("strokeJoinField")
	public void strokeJoinField(ChangeEvent event) {
		configureCanvas();
		redraw();
	}
	
	@UiHandler("strokeCapField")
	public void strokeCapField(ChangeEvent event) {
		configureCanvas();
		redraw();
	}
	
	@UiHandler("strokeSizeField")
	public void strokeSize(KeyUpEvent event) {
		if (strokeSizeField.getValue() == null || strokeSizeField.getValue().length() <= 0) {
			return;
		}
		configureCanvas();
		redraw();
	}
	
	@UiHandler("strokeSizeField")
	public void strokeSize(ValueChangeEvent<String> event) {
		configureCanvas();
		redraw();
	}
	
	private void redraw() {
		if (shaper != null && handles.size() > 0) {
			shaper.redraw(null);
		}
	}

	@UiHandler("strokeStyleField")
	public void strokeStyleField(ValueChangeEvent<FillStrokeStyle> e) {
		configureCanvas();
		redraw();
	}
	
	@UiHandler("fillStyleField")
	public void fillStyleField(ValueChangeEvent<FillStrokeStyle> e) {
		configureCanvas();
		redraw();
	}

	@UiHandler("undoButton")
	public void undoButton(ClickEvent e) {
		undoMap();
	}

	@UiHandler("redoButton")
	public void redoButton(ClickEvent e) {
		redoMap();
	}

	@UiHandler("clearButton")
	public void clearButton(ClickEvent e) {
		clearMap();
	}

	@UiHandler("pencilButton")
	public void pencilButton(ClickEvent e) {
		toggleShaper(e, pencilShaper);
	}

	@UiHandler("lineButton")
	public void lineButton(ClickEvent e) {
		toggleShaper(e, lineShaper);
	}

	@UiHandler("fillAllButton")
	public void rectangleButton(ClickEvent e) {
		fillMap();
	}

	@UiHandler("polygonButton")
	public void polygonButton(ClickEvent e) {
		toggleShaper(e, polyShaper);
	}

	@UiHandler("filledPolygonButton")
	public void filledPolygonButton(ClickEvent e) {
		toggleShaper(e, filledPolyShaper);
	}

	private boolean erasing = false;

	@UiHandler("eraseButton")
	public void eraseButton(ClickEvent e) {
		toggleShaper(e, pencilShaper);
		erasing = true;
	}

	@UiHandler("erasePolygonButton")
	public void erasePolygonButton(ClickEvent e) {
		toggleShaper(e, filledPolyShaper);
		erasing = true;
	}

	@UiHandler("imageButton")
	public void imageButton(ClickEvent e) {
		toggleShaper(e, imageShaper);
	} 

	@UiHandler("textButton")
	public void textButton(ClickEvent e) {
		toggleShaper(e, textShaper);
		final TextInputDialog d = GWT.create(TextInputDialog.class);
		d.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String txt = event.getValue();
				if (txt != null && txt.length() > 0) {
					text = txt;
				}
			}
		});
		d.center();
	} 
	
	@UiHandler("imageField")
	public void imageField(ClickEvent e) {
		final UploadDialog d = GWT.create(UploadDialog.class);
		d.setText("Upload Image");
		d.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				ImageLoader.loadImages(new String[] {event.getValue()}, new ImageLoader.CallBack() {
					public void onImagesLoaded(ImageElement[] imageHandles) {

						imageField.setUrl(imageHandles[0].getSrc());
						image = imageHandles[0];
						strokeStyleField.setLoadedImage(imageHandles[0]);
						fillStyleField.setLoadedImage(imageHandles[0]);
						imageShaper.width = image.getWidth();
						imageShaper.height = image.getHeight();
						imageShaper.rotation = 0.0D;
						d.hide();

					}
				});
			}
		});
		d.center();
	}
	
	private void updatePalette() {
		Canvas pc = Canvas.createIfSupported();
		pc.setCoordinateSpaceHeight(60);
		pc.setCoordinateSpaceWidth(60);
		pc.getContext2d().setFillStyle(CssColor.make(255,255,255));
		pc.getContext2d().fillRect(0.0D, 0.0D, 60.0D, 60.0D);
		pc.getContext2d().setStrokeStyle(canvas.getContext2d().getStrokeStyle());
		pc.getContext2d().setFillStyle(canvas.getContext2d().getFillStyle());
		pc.getContext2d().setLineWidth(5.0D);
		pc.getContext2d().fillRect(20.0D, 20.0D, 30.0D, 30.D);
		pc.getContext2d().strokeRect(20.0D, 20.0D, 30.0D, 30.D);
		pc.getContext2d().setFont("14pt Calibri");
		pc.getContext2d().setFillStyle(textStyleField.getValue());
		pc.getContext2d().setTextAlign(TextAlign.CENTER);
		pc.getContext2d().setTextBaseline(TextBaseline.MIDDLE);
		pc.getContext2d().fillText("Text", 20.0D, 20.0D, 60.0D);
		paletteField.setUrl(pc.toDataUrl());
	}

	@UiHandler("canvas")
	public void canvas(MouseDownEvent e) {
		e.preventDefault();
		e.stopPropagation();
		int mouseX = e.getRelativeX(canvas.getElement());
		int mouseY = e.getRelativeY(canvas.getElement());
		control = e.getNativeEvent().getCtrlKey();
		if (!e.getNativeEvent().getShiftKey() && !control) {
			savePoint();
			clearHandles();
		}
		shaper.draw(mouseX, mouseY);
		drawing = true;
	}
	
	@UiHandler("finishButton")
	public void finishButton(ClickEvent e) {
		savePoint();
		clearHandles();
	}
	
	private void clearHandles() {
		for (int i = 0; i < handles.size(); i++) {
			handles.get(i).removeFromParent();
		}
		handles.clear();
		curverHandles.clear();
	}

	@UiHandler("canvas")
	public void canvas(MouseMoveEvent e) {
		e.preventDefault();
		int mouseX = e.getRelativeX(canvas.getElement());
		int mouseY = e.getRelativeY(canvas.getElement());
		if (drawing) {
			shaper.draw(mouseX, mouseY);
		}
	}
	
	@UiHandler("canvas")
	public void canvas(MouseUpEvent e) {
		e.preventDefault();
		int mouseX = e.getRelativeX(canvas.getElement());
		int mouseY = e.getRelativeY(canvas.getElement());
		if (drawing) {
			shaper.draw(mouseX, mouseY);
			shaper.stop();
			drawing = false;
			setImageSize();
		}
		control = false;
	}
	
	@UiHandler("canvas")
	public void canvas(MouseOutEvent e) {
		e.preventDefault();
		int mouseX = e.getRelativeX(canvas.getElement());
		int mouseY = e.getRelativeY(canvas.getElement());
		if (drawing) {
			shaper.draw(mouseX, mouseY);
			shaper.stop();
			drawing = false;
			setImageSize();
		}
		control = false;
	}
	
	private void setImageSize() {
		int size = (canvas.toDataUrl().getBytes().length * 2) / 1024;
		imageSize.setText("Size " + size + "K");
	}

	private void clearMap() {
		savePoint();
		canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		setImageSize();
		clearHandles();
	}

	private void fillMap() {
		savePoint();
		canvas.getContext2d().fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		setImageSize();
		clearHandles();
	}

	private HasValue<Boolean> shaperButton = null;
	private Shaper shaper = pencilShaper;

	@SuppressWarnings("unchecked")
	private void toggleShaper(ClickEvent e, Shaper s) {
		if (e.getSource() instanceof HasValue<?>) {
			HasValue<Boolean> v = (HasValue<Boolean>)e.getSource();
			if (!v.getValue()) {
				v.setValue(true, false);
				return;
			}
			if (shaperButton == null) {
				pencilButton.setValue(false, false);
			} else {
				shaperButton.setValue(false, false);
			}
			shaperButton = v; 
			shaper = s;
		}
		erasing = false;
	}

	private Stack<ImageData> undo = new Stack<ImageData>();
	private Stack<ImageData> redo = new Stack<ImageData>();

	private void savePoint() {
		undo.push(canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight()));
		redo.clear();
	}

	private void saveRedo() {
		redo.push(canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight()));
	}

	private void undoMap() {
		clearHandles();
		if (hasUndoPoints()) {
			saveRedo();
			ImageData data = undo.pop();
			canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			canvas.getContext2d().putImageData(data, 0, 0);
		}
	}
	
	private void redoMap() {
		clearHandles();
		if (hasRedoPoints()) {
			ImageData data = redo.pop();
			undo.push(canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceHeight(), canvas.getCoordinateSpaceHeight()));
			canvas.getContext2d().putImageData(data, 0, 0);
		}
	}

	private boolean hasUndoPoints() {
		return !undo.isEmpty();
	}
	
	private boolean hasRedoPoints() {
		return !redo.isEmpty();
	}

	private ImageData imageData;

	private void saveImage() {
		imageData = canvas.getContext2d().getImageData(0.0D, 0.0D, (int)Math.round(canvas.getCoordinateSpaceWidth()), (int)Math.round(canvas.getCoordinateSpaceHeight()));
	}

	private void restoreImage() {
		if (imageData == null) {
			GWT.log("Restored image before saved");
			return;
		}
		canvas.getContext2d().clearRect(0.0D, 0.0D, (int)Math.round(canvas.getCoordinateSpaceWidth()), (int)Math.round(canvas.getCoordinateSpaceHeight()));
		canvas.getContext2d().putImageData(imageData, 0.0D, 0.0D);
	}

	private void styleCanvas(Canvas c) {
		c.getContext2d().setStrokeStyle(canvas.getContext2d().getStrokeStyle());
		c.getContext2d().setFillStyle(canvas.getContext2d().getFillStyle());
		c.getContext2d().setLineWidth(canvas.getContext2d().getLineWidth());
	}

	private class PencilShaper implements Shaper {

		@Override
		public void draw(double x, double y) {

			if (!drawing) {

				canvas.getContext2d().beginPath();
				canvas.getContext2d().moveTo(x, y);

			} else {

				canvas.getContext2d().save();
				canvas.getContext2d().setLineCap(LineCap.ROUND);
				canvas.getContext2d().setLineJoin(LineJoin.ROUND);
				canvas.getContext2d().lineTo(x, y);
				if (erasing) {
					canvas.getContext2d().setLineWidth(1);
					canvas.getContext2d().clip();
					canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				} else {
					canvas.getContext2d().stroke();
				}
				canvas.getContext2d().restore();
				
			}

		}

		@Override
		public void stop() {
		}

		@Override
		public void redraw(Handle handle) {
		}

	}

	private class LineShaper implements Shaper {

		private double startx = 0.0D;
		private double starty = 0.0D;
		private double finishx = 0.0D;
		private double finishy = 0.0D;
		private double cpx = 0.0D;
		private double cpy = 0.0D;

		@Override
		public void draw(double x, double y) {

			if (!drawing) {
				
				if (handles.size() == 0) {
					
					startx = x;
					starty = y;

					saveImage();

				}

			}

			if (handles.size() > 0) {
				
				redraw(null);

			} else {
				
				restoreImage();
				
			}

			finishx = x;
			finishy = y;
			
			canvas.getContext2d().beginPath();
			canvas.getContext2d().moveTo(startx, starty);
			if (control) {
				cpx = startx + ((finishx - startx) / 2);
				cpy = starty + ((finishy - starty) / 2);
				canvas.getContext2d().quadraticCurveTo(cpx, cpy, finishx, finishy);
			} else {
				cpx = 0.0D;
				cpy = 0.0D;
				canvas.getContext2d().lineTo(finishx, finishy);
			}
			canvas.getContext2d().save();
			if (erasing) {
				canvas.getContext2d().setLineWidth(1);
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			} else {
				canvas.getContext2d().stroke();
			}
			canvas.getContext2d().restore();

		}

		@Override
		public void stop() {

			if (handles.size() == 0) {
				
				Handle start = new Handle(Handle.Type.Move, handlePanel, startx, starty, LineShaper.this);
				canvas.addMouseMoveHandler(start);
				handles.add(start);
				curverHandles.add(false);

			}

			if (control) {
				
				Handle cpnt = new Handle(Handle.Type.Move, handlePanel, cpx, cpy, LineShaper.this);
				canvas.addMouseMoveHandler(cpnt);
				handles.add(cpnt);
				curverHandles.add(true);
				
			}

			Handle finish = new Handle(Handle.Type.Move, handlePanel, finishx, finishy, LineShaper.this);
			canvas.addMouseMoveHandler(finish);
			curverHandles.add(false);
			handles.add(finish);

			redraw(null);

			startx = finishx;
			starty = finishy;

			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, SHIFT_MESSAGE));

		}

		@Override
		public void redraw(Handle handle) {

			restoreImage();

			canvas.getContext2d().beginPath();

			for (int i = 0; i < handles.size(); i++) {
				
				if (i == 0) {
					canvas.getContext2d().moveTo(handles.get(i).getX(), handles.get(i).getY());
				} else if (curverHandles.get(i)) {
					double cx = handles.get(i).getX();
					double cy = handles.get(i).getY();
					i++;
					canvas.getContext2d().quadraticCurveTo(cx, cy, handles.get(i).getX(), handles.get(i).getY());
				} else {
					canvas.getContext2d().lineTo(handles.get(i).getX(), handles.get(i).getY());
				}

			}
			canvas.getContext2d().save();
			if (erasing) {
				canvas.getContext2d().setLineWidth(1);
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			} else {
				canvas.getContext2d().stroke();
			}
			canvas.getContext2d().restore();

		}

	}

	private class PolyShaper implements Shaper {

		private double startx = 0.0D;
		private double starty = 0.0D;
		private double finishx = 0.0D;
		private double finishy = 0.0D;
		private double cpx = 0.0D;
		private double cpy = 0.0D;

		@Override
		public void draw(double x, double y) {

			if (!drawing) {
				
				if (handles.size() == 0) {
					
					startx = x;
					starty = y;

					saveImage();

				}

			}

			if (handles.size() > 0) {
				
				redraw(null);

			} else {
				
				restoreImage();
				
			}

			finishx = x;
			finishy = y;
			
			canvas.getContext2d().beginPath();
			canvas.getContext2d().moveTo(startx, starty);
			if (control) {
				cpx = startx + ((finishx - startx) / 2);
				cpy = starty + ((finishy - starty) / 2);
				canvas.getContext2d().quadraticCurveTo(cpx, cpy, finishx, finishy);
			} else {
				cpx = 0.0D;
				cpy = 0.0D;
				canvas.getContext2d().lineTo(finishx, finishy);
			}
			canvas.getContext2d().closePath();
			canvas.getContext2d().save();
			if (erasing) {
				canvas.getContext2d().setLineWidth(1);
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			} else {
				canvas.getContext2d().stroke();
			}
			canvas.getContext2d().restore();

		}

		@Override
		public void stop() {

			if (handles.size() == 0) {
				
				Handle start = new Handle(Handle.Type.Move, handlePanel, startx, starty, PolyShaper.this);
				canvas.addMouseMoveHandler(start);
				handles.add(start);
				curverHandles.add(false);

			}

			if (control) {
				
				Handle cpnt = new Handle(Handle.Type.Move, handlePanel, cpx, cpy, PolyShaper.this);
				canvas.addMouseMoveHandler(cpnt);
				handles.add(cpnt);
				curverHandles.add(true);
				
			}

			Handle finish = new Handle(Handle.Type.Move, handlePanel, finishx, finishy, PolyShaper.this);
			canvas.addMouseMoveHandler(finish);
			handles.add(finish);
			curverHandles.add(false);

			redraw(null);

			startx = finishx;
			starty = finishy;

			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, SHIFT_MESSAGE));

		}

		@Override
		public void redraw(Handle handle) {

			restoreImage();

			canvas.getContext2d().beginPath();

			for (int i = 0; i < handles.size(); i++) {
				
				if (i == 0) {
					canvas.getContext2d().moveTo(handles.get(i).getX(), handles.get(i).getY());
				} else if (curverHandles.get(i)) {
					double cx = handles.get(i).getX();
					double cy = handles.get(i).getY();
					i++;
					canvas.getContext2d().quadraticCurveTo(cx, cy, handles.get(i).getX(), handles.get(i).getY());
				} else {
					canvas.getContext2d().lineTo(handles.get(i).getX(), handles.get(i).getY());
				}

			}
			canvas.getContext2d().closePath();
			canvas.getContext2d().save();
			if (erasing) {
				canvas.getContext2d().setLineWidth(1);
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			} else {
				canvas.getContext2d().stroke();
			}
			canvas.getContext2d().restore();

		}

	}

	private class FilledPolyShaper implements Shaper {

		private double startx = 0.0D;
		private double starty = 0.0D;
		private double finishx = 0.0D;
		private double finishy = 0.0D;
		private double cpx = 0.0D;
		private double cpy = 0.0D;

		@Override
		public void draw(double x, double y) {

			if (!drawing) {
				
				if (handles.size() == 0) {
					
					startx = x;
					starty = y;

					saveImage();

				}

			}

			if (handles.size() > 0) {
				
				redraw(null);

			} else {
				
				restoreImage();
				
			}

			finishx = x;
			finishy = y;

			canvas.getContext2d().beginPath();
			canvas.getContext2d().moveTo(startx, starty);
			if (control) {
				cpx = startx + ((finishx - startx) / 2);
				cpy = starty + ((finishy - starty) / 2);
				canvas.getContext2d().quadraticCurveTo(cpx, cpy, finishx, finishy);
			} else {
				cpx = 0.0D;
				cpy = 0.0D;
				canvas.getContext2d().lineTo(finishx, finishy);
			}
			canvas.getContext2d().closePath();
			if (erasing) {
				canvas.getContext2d().save();
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				canvas.getContext2d().restore();
			} else {
				canvas.getContext2d().fill();
				if (Integer.parseInt(strokeSizeField.getValue()) > 0) {
					canvas.getContext2d().stroke();
				}
			}

		}

		@Override
		public void stop() {

			if (handles.size() == 0) {
				
				Handle start = new Handle(Handle.Type.Move, handlePanel, startx, starty, FilledPolyShaper.this);
				canvas.addMouseMoveHandler(start);
				handles.add(start);
				curverHandles.add(false);

			}

			if (control) {
				
				Handle cpnt = new Handle(Handle.Type.Move, handlePanel, cpx, cpy, FilledPolyShaper.this);
				canvas.addMouseMoveHandler(cpnt);
				handles.add(cpnt);
				curverHandles.add(true);
				
			}

			Handle finish = new Handle(Handle.Type.Move, handlePanel, finishx, finishy, FilledPolyShaper.this);
			canvas.addMouseMoveHandler(finish);
			handles.add(finish);
			curverHandles.add(false);

			redraw(null);

			startx = finishx;
			starty = finishy;

			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, SHIFT_MESSAGE));

		}

		@Override
		public void redraw(Handle handle) {

			restoreImage();

			canvas.getContext2d().beginPath();

			for (int i = 0; i < handles.size(); i++) {
				
				if (i == 0) {
					canvas.getContext2d().moveTo(handles.get(i).getX(), handles.get(i).getY());
				} else if (curverHandles.get(i)) {
					double cx = handles.get(i).getX();
					double cy = handles.get(i).getY();
					i++;
					canvas.getContext2d().quadraticCurveTo(cx, cy, handles.get(i).getX(), handles.get(i).getY());
				} else {
					canvas.getContext2d().lineTo(handles.get(i).getX(), handles.get(i).getY());
				}

			}
			canvas.getContext2d().closePath();

			if (erasing) {
				canvas.getContext2d().save();
				canvas.getContext2d().clip();
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				canvas.getContext2d().restore();
			} else {
				canvas.getContext2d().fill();
				if (Integer.parseInt(strokeSizeField.getValue()) > 0) {
					canvas.getContext2d().stroke();
				}
			}

		}

	}

	private class TextShaper implements Shaper {
		
		private Handle mover;
		private Handle rotater;
		private double moverX = 0.0D;
		private double moverY = 0.0D;

		private double rotaterX = 0.0D;
		private double rotaterY = 0.0D;

		private double rotation;

		@Override
		public void draw(double x, double y) {
			
			if (text == null || text.length() <= 0) {
				return;
			}
			if (!drawing) {
				
				saveImage();

				draw(null, x, y);
				
			}

		}

		private void draw(Handle handle, double startX, double startY) {

			restoreImage();

			Canvas transformCanvas = Canvas.createIfSupported(); 
			transformCanvas.getContext2d().setFont("14pt Calibri");
			transformCanvas.getContext2d().setTextAlign(TextAlign.CENTER);
			transformCanvas.getContext2d().setTextBaseline(TextBaseline.MIDDLE);
			TextMetrics m = transformCanvas.getContext2d().measureText(text);
			double width = m.getWidth();
			double height = width;
			moverX = startX;
			moverY = startY;
			rotaterX = startX;
			rotaterY = startY - 50;
		
			//rotate and draw image in new position.
			int imagBoxsize = (int)(Math.sqrt(Math.pow(width, 2.0D) + Math.pow(height, 2.0D)) + 24.0D);
			
			transformCanvas.setCoordinateSpaceWidth(imagBoxsize);
			transformCanvas.setCoordinateSpaceHeight(imagBoxsize);
			transformCanvas.getContext2d().setFont("12pt Calibri");
			transformCanvas.getContext2d().setFillStyle(textStyleField.getValue());
			transformCanvas.getContext2d().setTextAlign(TextAlign.CENTER);
			transformCanvas.getContext2d().setTextBaseline(TextBaseline.MIDDLE);

			transformCanvas.getContext2d().translate(imagBoxsize / 2, imagBoxsize / 2);
			if (handle != null && handle == rotater) {
				rotation = Math.min(rotaterX - handle.getX(), ROTATION_DRAG_LENGTH) / ROTATION_DRAG_LENGTH;
				transformCanvas.getContext2d().rotate(-rotation * MAX_RADIANS);
			}
			transformCanvas.getContext2d().fillText(text, 0, 0);

			double tlx = startX - (imagBoxsize / 2);
			double tly = startY - (imagBoxsize / 2);
			canvas.getContext2d().drawImage(transformCanvas.getCanvasElement(), tlx, tly, imagBoxsize, imagBoxsize);
		}

		@Override
		public void stop() {

			mover = new Handle(Handle.Type.Move, handlePanel, moverX, moverY, TextShaper.this);
			canvas.addMouseMoveHandler(mover);
			canvas.addMouseUpHandler(mover);
			canvas.addMouseOutHandler(mover);
			handles.add(mover);

			rotater = new Handle(Handle.Type.Rotate, handlePanel, rotaterX, rotaterY, TextShaper.this);
			canvas.addMouseMoveHandler(rotater);
			canvas.addMouseUpHandler(rotater);
			canvas.addMouseOutHandler(rotater);
			handles.add(rotater);

			mover.addMouseMoveHandler(rotater);
			rotater.addMouseMoveHandler(mover);

		}

		@Override
		public void redraw(Handle handle) {

			double offsetX = 0.0D;
			double offsetY = 0.0D;
			if (mover != null) {
				offsetX = mover.getX() - moverX;
				offsetY = mover.getY() - moverY;
			}

			//work out new top left and bottom right corners, allowing for movement of topleft
			//and re-sizing of bottom right.
			double newtopleftX = moverX + offsetX;
			double newtopleftY = moverY + offsetY;
			double newbottomrightX = rotaterX + offsetX;
			double newbottomrightY = rotaterY + offsetY;
			
			//re-draw
			draw(handle, newtopleftX, newtopleftY);

			//adjust position of handles around newly positioned and sized image
			mover.setPosition(newtopleftX, newtopleftY);
			moverX = mover.getX();
			moverY = mover.getY();
			rotater.setPosition(newbottomrightX, newbottomrightY);
			mover.setPosition(newtopleftX, newtopleftY);

		}
		
	}


	private class ImageShaper implements Shaper {
		
		private Handle topleft;
		private Handle bottomright;
		private Handle rotater;
		private Handle mover;
		private double moverX = 0.0D;
		private double moverY = 0.0D;

		private double startX = 0.0D;
		private double startY = 0.0D;
		private double finishX = 0.0D;
		private double finishY = 0.0D;

		private double rotation;
		private double width;
		private double height;

		@Override
		public void draw(double x, double y) {
			
			if (image == null) {
				return;
			}
			if (!drawing) {
				
				saveImage();
				
			} else {
				
				startX = x;
				startY = y;
				finishX = x + width;
				finishY = y + height;

				draw(null, startX, startY, finishX, finishY);
				
			}

		}

		private void draw(Handle handle, double newtopleftX, double newtopleftY, double newbottomrightX, double newbottomrightY) {

			restoreImage();

			double width = Math.abs(newbottomrightX - newtopleftX);
			double height = Math.abs(newbottomrightY - newtopleftY);

			//rotate and draw image in new position.
			int imagBoxsize = (int)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) + 24;
			
			Canvas transformCanvas = Canvas.createIfSupported(); 
			transformCanvas.setCoordinateSpaceWidth(imagBoxsize);
			transformCanvas.setCoordinateSpaceHeight(imagBoxsize);

			transformCanvas.getContext2d().translate(imagBoxsize / 2, imagBoxsize / 2);
			if (handle != null && handle == rotater) {
				rotation = Math.min(bottomright.getX() - handle.getX(), ROTATION_DRAG_LENGTH) / ROTATION_DRAG_LENGTH;
				transformCanvas.getContext2d().rotate(-rotation * MAX_RADIANS);
			}
			styleCanvas(transformCanvas);
			transformCanvas.getContext2d().drawImage(image, -width / 2, -height / 2, width, height);
			
			double tlx = Math.min(newtopleftX, newbottomrightX) - ((imagBoxsize - width) / 2);
			double tly = Math.min(newtopleftY, newbottomrightY) - ((imagBoxsize - height) / 2);
			canvas.getContext2d().drawImage(transformCanvas.getCanvasElement(), tlx, tly, imagBoxsize, imagBoxsize);
			
		}

		@Override
		public void stop() {

			if (image == null) {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Load an image first"));
				return;
			}
			moverX = startX;
			moverY = finishY;
			
			mover = new Handle(Handle.Type.Move, handlePanel, startX, finishY, ImageShaper.this);
			canvas.addMouseMoveHandler(mover);
			canvas.addMouseUpHandler(mover);
			canvas.addMouseOutHandler(mover);
			handles.add(mover);

			rotater = new Handle(Handle.Type.Rotate, handlePanel, finishX, startY, ImageShaper.this);
			canvas.addMouseMoveHandler(rotater);
			canvas.addMouseUpHandler(rotater);
			canvas.addMouseOutHandler(rotater);
			handles.add(rotater);

			topleft = new Handle(Handle.Type.Size, handlePanel, startX, startY, ImageShaper.this);
			canvas.addMouseMoveHandler(topleft);
			canvas.addMouseUpHandler(topleft);
			canvas.addMouseOutHandler(topleft);
			handles.add(topleft);

			bottomright = new Handle(Handle.Type.Size, handlePanel, finishX, finishY, ImageShaper.this);
			canvas.addMouseMoveHandler(bottomright);
			canvas.addMouseUpHandler(bottomright);
			canvas.addMouseOutHandler(bottomright);
			handles.add(bottomright);

			topleft.addMouseMoveHandler(bottomright);
			topleft.addMouseMoveHandler(mover);
			topleft.addMouseMoveHandler(rotater);
			bottomright.addMouseMoveHandler(topleft);
			bottomright.addMouseMoveHandler(mover);
			bottomright.addMouseMoveHandler(rotater);
			mover.addMouseMoveHandler(bottomright);
			mover.addMouseMoveHandler(topleft);
			mover.addMouseMoveHandler(rotater);
			rotater.addMouseMoveHandler(topleft);
			rotater.addMouseMoveHandler(bottomright);
			rotater.addMouseMoveHandler(mover);

		}

		@Override
		public void redraw(Handle handle) {

			double offsetX = 0.0D;
			double offsetY = 0.0D;
			if (mover != null) {
				offsetX = mover.getX() - moverX;
				offsetY = mover.getY() - moverY;
			}

			//work out new top left and bottom right corners, allowing for movement of topleft
			//and re-sizing of bottom right.
			double newtopleftX = topleft.getX() + offsetX;
			double newtopleftY = topleft.getY() + offsetY;
			double newbottomrightX = bottomright.getX() + offsetX;
			double newbottomrightY = bottomright.getY() + offsetY;
			
			width = Math.abs(newbottomrightX - newtopleftX);
			height = Math.abs(newbottomrightY - newtopleftY);

			//re-draw
			draw(handle, newtopleftX, newtopleftY, newbottomrightX, newbottomrightY);

			//adjust position of handles around newly positioned and sized image
			mover.setPosition(newtopleftX, newbottomrightY);
			moverX = mover.getX();
			moverY = mover.getY();
			rotater.setPosition(newbottomrightX, newtopleftY);
			if (handle != bottomright) {
				topleft.setPosition(newtopleftX, newtopleftY);
			} 
			if (handle != topleft) {
				bottomright.setPosition(newbottomrightX, newbottomrightY);
			}

		}
		
	}

}
