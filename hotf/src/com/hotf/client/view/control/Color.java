package com.hotf.client.view.control;

public class Color {

	public final static Color WHITE = new Color(255, 255, 255);
	public final static Color LIGHT_GRAY = new Color(192, 192, 192);
	public final static Color GRAY = new Color(128, 128, 128);
	public final static Color DARK_GRAY = new Color(64, 64, 64);
	public final static Color BLACK = new Color(0, 0, 0);
	public final static Color RED = new Color(255, 0, 0);
	public final static Color PINK = new Color(255, 175, 175);
	public final static Color ORANGE = new Color(255, 200, 0);
	public final static Color YELLOW = new Color(255, 255, 0);
	public final static Color GREEN = new Color(0, 255, 0);
	public final static Color MAGENTA = new Color(255, 0, 255);
	public final static Color CYAN = new Color(0, 255, 255);
	public final static Color BLUE = new Color(0, 0, 255);

	private int r, g, b;

	public Color(String hex) {
		int rgb = Integer.decode(hex);
		r = (rgb & 0xff0000) >> 16;
		g = (rgb & 0x00ff00) >> 8;
		b = rgb & 0x0000ff;
	}

	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(float h, float s, float l) {
		float q = 0;
		if (l < 0.5f)
			q = l * (s + 1.0f);
		else
			q = l + s - (l * s);

		float p = 2.0f * l - q;
		float hk = h / 360.0f;

		float tr = hk + 1.0f / 3;
		float tg = hk;
		float tb = hk - 1.0f / 3;

		this.r = (int) (getComponent(tr, q, p) * 255f);
		this.g = (int) (getComponent(tg, q, p) * 255f);
		this.b = (int) (getComponent(tb, q, p) * 255f);
	}

	private float getComponent(float tc, float q, float p) {
		if (tc < 0)
			tc += 1;
		else if (tc > 1)
			tc -= 1;

		if (tc < (1f / 6f))
			tc = p + ((q - p) * 6f * tc);
		else if ((1f / 6f) <= tc && tc < 0.5f)
			tc = q;
		else if (0.5f <= tc && tc < (2f / 3f))
			tc = p + ((q - p) * 6.0f * (2f / 3f - tc));
		else
			tc = p;

		return tc;
	}

	public int getRed() {
		return r;
	}

	public int getGreen() {
		return g;
	}

	public int getBlue() {
		return b;
	}

	public float getHue() {
		float r = this.r / 255f;
		float g = this.g / 255f;
		float b = this.b / 255f;

		float max = getMax(r, g, b);
		float min = getMin(r, g, b);

		if (max == min) {
			return 0;
		} else if (max == r) {
			return (60f * ((float) (g - b) / max - min) + 360) % 360;
		} else if (max == g) {
			return (60f * ((float) (b - r) / max - min) + 120);
		} else if (max == b) {
			return (60f * ((float) (r - g) / max - min) + 240);
		} else {
			return 0;
		}
	}

	public float getSaturation() {
		float r = this.r / 255f;
		float g = this.g / 255f;
		float b = this.b / 255f;

		float l = getLightness();
		float max = getMax(r, g, b);
		float min = getMin(r, g, b);

		if (max == min) {
			return 0;
		} else if (l <= 0.5) {
			return (max - min) / (2f * l);
		} else if (l > 0.5) {
			return (max - min) / (2f - 2f * l);
		} else {
			return 0;
		}
	}

	public float getLightness() {
		float r = this.r / 255f;
		float g = this.g / 255f;
		float b = this.b / 255f;

		return (getMax(r, g, b) + getMin(r, g, b)) / 2f;
	}

	private float getMax(float r, float g, float b) {
		return r > g ? (r > b ? r : b) : (g > b ? g : b);
	}

	private float getMin(float r, float g, float b) {
		return r < g ? (r < b ? r : b) : (g < b ? g : b);
	}

	private String pad(String in) {
		if (in.length() == 0) {
			return "00";
		}
		if (in.length() == 1) {
			return "0" + in;
		}
		return in;
	}

	public String toString() {
		return "#" + pad(Integer.toHexString(r)) + pad(Integer.toHexString(g))
				+ pad(Integer.toHexString(b));
	}

}
