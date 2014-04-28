/**
 * 
 */
package com.hotf.client;

import java.util.Date;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.PlaceResult;

/**
 * @author Jeff
 *
 */
public class ImageUtils {

	private static final ImageUtils UTILITIES = new ImageUtils();
	
	public static ImageUtils get() {
		return UTILITIES;
	}

	public String getMapOverlayUrl(PlaceResult location) {
		if (location == null) {
			return getDefaultMap();
		}
		if ((location.getHasMap() != null && location.getHasMap()) ||
			(location.getHasOverlay() != null && location.getHasOverlay())) {
			Date ts = location.getUpdated();
			if (ts == null) {
				ts = location.getCreated();
			}
			if (ts == null) {
				return getDefaultMap();
			}
			return getHomeUrl() + "/data/images?mapOverlayId=" + location.getId() + "&dt=" + ts.getTime();
		}
		return getDefaultMap();
	}
	
	public String getMapUrl(PlaceResult location) {
		if (location == null) {
			return getDefaultMap();
		}
		if ((location.getHasMap() != null && location.getHasMap())) {
			Date ts = location.getUpdated();
			if (ts == null) {
				ts = location.getCreated();
			}
			if (ts == null) {
				return getDefaultMap();
			}
			return getHomeUrl() + "/data/images?mapId=" + location.getId() + "&dt=" + ts.getTime();
		}
		return getDefaultMap();
	}
	
	public String getOverlayUrl(PlaceResult location) {
		if (location == null) {
			return getDefaultMap();
		}
		if ((location.getHasOverlay() != null && location.getHasOverlay())) {
			Date ts = location.getUpdated();
			if (ts == null) {
				ts = location.getCreated();
			}
			if (ts == null) {
				return getDefaultMap();
			}
			return getHomeUrl() + "/data/images?overlayId=" + location.getId() + "&dt=" + ts.getTime();
		}
		return getDefaultMap();
	}

	public String getTileUrl(PlaceResult location) {
		if (location == null) {
			return null;
		}
		Date ts = location.getUpdated();
		if (ts == null) {
			ts = location.getCreated();
		}
		if (ts == null) {
			return null;
		}
		return getHomeUrl() + "/data/images?tileId=" + location.getId() + "&dt=" + ts.getTime();
	}
	
	public String getPortraitUrl(CharacterResult character) {
		if (character == null) {
			return getDefaultPortrait();
		}
		Date ts = character.getUpdated();
		if (ts == null) {
			ts = character.getCreated();
		}
		if (ts == null) {
			return getDefaultPortrait();
		}
		String dt = "&dt=" + ts.getTime();
		return getHomeUrl() + "/data/images?characterId=" + character.getId() + dt;
	}

	public String getPortraitUrlLazilly(String characterId) {
		if (characterId == null) {
			return getDefaultPortrait();
		}
		return getHomeUrl() + "/data/images?characterId=" + characterId + "&i=1";
	}

	public String getImage(String image) {
		return Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/images/" + image;
	}

	public String getDefaultMap() {
		return getImage("nomap.png");
	}
	
	public String getDefaultPortrait() {
		return getImage("notoken.png");
	}
	
	public String getHomeUrl() {
		return Window.Location.getProtocol() + "//" + Window.Location.getHost();
	}

	public void setOpactity(Widget w, double opacity) {
		if (opacity <= 90.0D) {
			String s = Integer.toString((int) Math.round(opacity * 100));
			String sDecimal = (s.length() == 1 ? ".0" : ".") + s;
			w.getElement().getStyle().setProperty("opacity", sDecimal);
			w.getElement().getStyle().setProperty("filter", "alpha(opacity=" + s + ")");
			w.getElement().getStyle().setProperty("MozMpacity", sDecimal);
			w.getElement().getStyle().setProperty("KhtmlOpacity", sDecimal);
		} else {
			w.getElement().getStyle().setProperty("opacity", "");
			w.getElement().getStyle().setProperty("filter", "");
			w.getElement().getStyle().setProperty("MozMpacity", "");
			w.getElement().getStyle().setProperty("KhtmlOpacity", "");
		}
	}

	public int distance(int x1, int y1, int x2, int y2) {
		return (int)((Math.sqrt(
				Math.pow(Math.abs(x1 - x2), 2) + 
				Math.pow(Math.abs(y1 - y2), 2)
				) / 30.0D) * 2.0);
	}
}
