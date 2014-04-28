/**
 * 
 */
package com.hotf.client.view;


/**
 * @author Jeff
 *
 */
public interface HasImage {
	
	public void setUrl(String url);
	
	String getUrl();
	
	void setTitle(String title);

	String getTitle();
	
	void setSelected(boolean selected);
	
	void setTransparent(boolean transparent);

}
