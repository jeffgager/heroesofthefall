package com.hotf.client.view.control;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.Context2d.Repetition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.hotf.client.image.Images;

public class ColorField extends Composite implements HasValue<FillStrokeStyle>, HasValueChangeHandlers<FillStrokeStyle>, HasName {
    
    private Images images = GWT.create(Images.class);
    
    private class ColorPopup extends PopupPanel {
        
        private FlowPanel panel;
        private Image hueSaturation;
        private Image lightness;
        private ImageElement loadedImage;
        private ImageElement tileImage;
        private Label preview;
        private Label tilePreview;
        private boolean down = false;
        
        float h = 200;
        float s = 2/3f;
        float l = 1/3f;

        ColorPopup() {
            super(true);
            
            this.panel = new FlowPanel();
            this.hueSaturation = new Image(images.hueSaturation());
            hueSaturation.setTitle("Click to select colour hue");
            this.lightness = new Image(images.lightness());
            lightness.setTitle("Click to select colour lightness");
            this.preview = new Label();
            preview.setTitle("Click to select colour");
            this.tilePreview = new Label();
            tilePreview.setTitle("Click to select tile");
            
            panel.setSize("260px", "100px");
            preview.setSize("20px", "100px");
            tilePreview.setSize("40px", "100px");
            DOM.setStyleAttribute(preview.getElement(), "cursor", "pointer");
            DOM.setStyleAttribute(tilePreview.getElement(), "cursor", "pointer");

            panel.add(hueSaturation);
            panel.add(lightness);
            panel.add(tilePreview);
            panel.add(preview);
            setWidget(panel);
            addStyleName("fp-cp");

            DOM.setStyleAttribute(hueSaturation.getElement(), "cursor", "crosshair");
            DOM.setStyleAttribute(lightness.getElement(), "cursor", "ns-resize");
            preview.getElement().getStyle().setFloat(Float.RIGHT);
            DOM.setStyleAttribute(preview.getElement(), "cssFloat", "right");
            DOM.setStyleAttribute(preview.getElement(), "styleFloat", "right");
            tilePreview.getElement().getStyle().setFloat(Float.RIGHT);
            DOM.setStyleAttribute(tilePreview.getElement(), "cssFloat", "right");
            DOM.setStyleAttribute(tilePreview.getElement(), "styleFloat", "right");
            
            setColor();

            hueSaturation.addMouseDownHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    event.preventDefault();
                    setHueSaturation(event.getNativeEvent());
                    down = true;
                }});
            
            hueSaturation.addMouseUpHandler(new MouseUpHandler() {

                @Override
                public void onMouseUp(MouseUpEvent event) {
                    setHueSaturation(event.getNativeEvent());
                    down = false;
                }});
            
            hueSaturation.addMouseMoveHandler(new MouseMoveHandler() {

                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (down)
                        setHueSaturation(event.getNativeEvent());
                }});
            
            hueSaturation.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    down = false;
                }});
            
            /* --- */
            
            lightness.addMouseDownHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    event.preventDefault();
                    setLightness(event.getNativeEvent());
                    down = true;
                }});
            
            lightness.addMouseUpHandler(new MouseUpHandler() {

                @Override
                public void onMouseUp(MouseUpEvent event) {
                    setLightness(event.getNativeEvent());
                    down = false;
                }});
            
            lightness.addMouseMoveHandler(new MouseMoveHandler() {

                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (down)
                        setLightness(event.getNativeEvent());
                }});
            
            lightness.addMouseOutHandler(new MouseOutHandler(){

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    down = false;
                }});
            
            /* --- */
            
            preview.addMouseDownHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                	tileImage = null;
                    hide();
                }});

            tilePreview.addMouseDownHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                	tileImage = loadedImage;
                    hide();
                }});
        }
        
        public String getHex() {
            return new Color(h,s,l).toString();
        }
        
        public void setHex(String colorString) {
            if (colorString.startsWith("#") && colorString.length() == 7) {
                Color rgb = new Color(colorString);
                h = rgb.getHue();
                s = rgb.getSaturation();
                l = rgb.getLightness();
                setColor();
            }
        }
        
        private void setColor() {
            Color p = new Color(h,s,l);
            DOM.setStyleAttribute(preview.getElement(), "backgroundColor", p.toString());
            Color l = new Color(h, s, 0.5f);
            DOM.setStyleAttribute(lightness.getElement(), "backgroundColor", l.toString());
        }
        
        private void setHueSaturation(NativeEvent event) {
            int x = event.getClientX()-hueSaturation.getAbsoluteLeft();
            int y = event.getClientY()-hueSaturation.getAbsoluteTop();
            
            if (x > -1 && x < 181 && y > -1 && y < 101) {
                h = x*2;
                s = (float) (100-y) / 100f;
                
                setColor();
            } else {
                down = false;
            }
        }
        
        private void setLightness(NativeEvent event) {
            int y = event.getClientY()-lightness.getAbsoluteTop();
            setLightness(y);
        }
        
        private void setLightness(int lightness) {
            if (lightness > -1 && lightness < 101) {
                l = (float) (100-lightness) / 100f;
                setColor();
            } else {
                down = false;
            }
        }

    }

    private ColorPopup popup;
    private TextBox textbox;
    private Blotch blotch;
    private AbsolutePanel panel;
    private boolean keyPressed = false;

    public ColorField() {
        this.panel = new AbsolutePanel();
        this.popup = new ColorPopup();
        this.textbox = new TextBox();
        this.blotch = new Blotch();
        
        textbox.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                enterEditMode();
            }});
        
        textbox.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                keyPressed = true;
                popup.setHex(getValue().toString());
                DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", getValue().toString());
                DOM.setStyleAttribute(blotch.getElement(), "backgroundImage", "none");
                DOM.setStyleAttribute(blotch.getElement(), "backgroundRepeat", "norepeat");
            }});
        
        blotch.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                enterEditMode();
            }});
        
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if (keyPressed) {
                    popup.setHex(getValue().toString());
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", getValue().toString());
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundImage", "none");
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundRepeat", "norepeat");
                    keyPressed = false;
                    ValueChangeEvent.fire(ColorField.this, CssColor.make(getValue().toString()));
                } else if (popup.tileImage != null) {
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", "transparent");
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundImage", "url(" + popup.tileImage.getSrc() + ")");
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundRepeat", "repeat");
                    keyPressed = false;
                    ValueChangeEvent.fire(ColorField.this, Canvas.createIfSupported().getContext2d().createPattern(popup.tileImage, Repetition.REPEAT));
                } else {
                    setValue(CssColor.make(popup.getHex()), true);
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", popup.getHex());
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundImage", "none");
                    DOM.setStyleAttribute(blotch.getElement(), "backgroundRepeat", "norepeat");
                    textbox.setFocus(false);
                    ValueChangeEvent.fire(ColorField.this, CssColor.make(getValue().toString()));
                }
            }});
        
        textbox.setWidth("5em");
        textbox.setHeight("1em");
        panel.setWidth("5.6em");
        panel.setHeight("1.8em");
        panel.add(textbox, 0, 0);
        panel.add(blotch, 56, 4);
        initWidget(panel);
        
        addStyleName("gwt-ColorBox");

    }

    @Override
    public String getName() {
        return textbox.getName();
    }

    @Override
    public void setName(String name) {
        textbox.setName(name);
    }

    @Override
    public FillStrokeStyle getValue() {
    	if (popup.tileImage == null) {
            return CssColor.make(textbox.getValue().toString());
    	} else {
    		return Canvas.createIfSupported().getContext2d().createPattern(popup.tileImage, Repetition.REPEAT);
    	}
    }
    
	public ImageElement getTileImage() {
		return popup.tileImage;
	}
	
    @Override
    public void setValue(FillStrokeStyle value) {
    	if (value instanceof CssColor) {
            textbox.setValue(value.toString());
            DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", value.toString());
    	}
    }

	public void setTileImage(ImageElement tileImage) {
		Canvas canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceWidth(tileImage.getWidth());
		canvas.setCoordinateSpaceHeight(tileImage.getHeight());
		canvas.getContext2d().drawImage(tileImage, 0, 0);
		popup.tileImage = ImageElement.as(new Image(canvas.toDataUrl()).getElement());
	}
	
	public void setTileData(String data) {
		popup.tileImage = ImageElement.as(new Image(data).getElement());
	}

	public void setLoadedImage(ImageElement loadedImage) {
		this.popup.loadedImage = loadedImage;
        DOM.setStyleAttribute(this.popup.tilePreview.getElement(), "backgroundImage", "url(" + popup.loadedImage.getSrc() + ")");
        DOM.setStyleAttribute(this.popup.tilePreview.getElement(), "backgroundRepeat", "repeat");
        DOM.setStyleAttribute(this.popup.tilePreview.getElement(), "backgroundPosition", "0 0");
	}

	@Override
    public void setValue(FillStrokeStyle value, boolean fireEvents) {
    	if (value instanceof CssColor) {
            textbox.setValue(value.toString(), fireEvents);
            DOM.setStyleAttribute(blotch.getElement(), "backgroundColor", value.toString());
    	}
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FillStrokeStyle> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void addStyleName(String style) {
    	super.addStyleName(style);
    }
    
    private void enterEditMode() {
        popup.setHex(getValue().toString());
        popup.setPopupPosition(getAbsoluteLeft()+10, getAbsoluteTop()+20);
        popup.show();
    }

    private class Blotch extends FlowPanel implements HasClickHandlers {

    	public Blotch() {
            addStyleName("blotch");
            setWidth("1em");
            setHeight("1em");
            DOM.setStyleAttribute(getElement(), "cursor", "pointer");
            DOM.setStyleAttribute(getElement(), "display", "block");
            getElement().getStyle().setFloat(Float.RIGHT);
		}
    	@Override
    	public HandlerRegistration addClickHandler(ClickHandler handler) {
    		return addDomHandler(handler, ClickEvent.getType());
    	}
    	
    }

}
