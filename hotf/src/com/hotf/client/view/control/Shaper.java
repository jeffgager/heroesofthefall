/**
 * 
 */
package com.hotf.client.view.control;


/**
 * @author Jeff
 *
 */
public interface Shaper {

	void draw(double x, double y);

	void stop();

	void redraw(Handle handle);

}
