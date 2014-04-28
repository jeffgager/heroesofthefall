package com.hotf.client.image;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * This {@link ClientBundle} is used for all the button icons. Using a bundle
 * allows all of these images to be packed into a single image, which saves a
 * lot of HTTP requests, drastically improving startup time.
 */
public interface Images extends ClientBundle {

	ImageResource editor();

	ImageResource text();

	ImageResource bold();

	ImageResource hr();

	ImageResource indent();

	ImageResource italic();

	ImageResource justifyCenter();

	ImageResource justifyLeft();

	ImageResource justifyRight();

	ImageResource ol();

	ImageResource outdent();

	ImageResource removeFormat();

	ImageResource deleteobj();

	ImageResource strikeThrough();

	ImageResource subscript();

	ImageResource superscript();

	ImageResource ul();

	ImageResource underline();

	ImageResource marker();

	ImageResource markerSmall();

	ImageResource personalMarker();

	ImageResource clear();

	ImageResource erase();

	ImageResource pencil();

	ImageResource rectangle();

	ImageResource filledRectangle();

	ImageResource undo();

	ImageResource redo();

	ImageResource drawImage();

	ImageResource colorChooser();

	ImageResource line();

	ImageResource polygon();

	ImageResource filledPolygon();

	ImageResource sizer();

	ImageResource mover();

	ImageResource rotater();

	ImageResource loadImage();
	
    ImageResource lightness();
    
    ImageResource hueSaturation();
    
    ImageResource finish();

    ImageResource html();

    ImageResource postMarker();

    ImageResource background();

}
