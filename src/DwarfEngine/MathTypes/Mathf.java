package DwarfEngine.MathTypes;

import java.awt.Color;

public final class Mathf {
	public static final float Deg2Rad = 0.01745329251994329576923690768489f;
	public static final float Rad2Deg = 57.295779513082320876798154814105f;
	public static final float epsilon = (float) 2.2204460492503130808472633361816E-16;
	public static float Lerp(float a, float b, float t) {
		t = Mathf.Clamp01(t);
		return a + t * (b - a);
	}
	public static float InverseLerp(float a, float b, float t) {
		return (t - a) / (b - a);
	}
	public static Color LerpColor(Color a, Color b, float t) {
		t = Mathf.Clamp01(t);
		int r = (int)Mathf.Lerp(a.getRed(), b.getRed(), t);
		int g = (int)Mathf.Lerp(a.getGreen(), b.getGreen(), t);
		int _b = (int)Mathf.Lerp(a.getBlue(), b.getBlue(), t);
		Color c = new Color(r, g, _b);
		return c;
	}
	public static float Clamp(float value, float min, float max) {
		if (value > max) return max;
		else if (value < min) return min;
		else return value;
	}
	public static float Clamp01(float value) {
		return Clamp(value, 0, 1);
	}
	public static float abs(float a) {
		return (float) Math.abs(a);
	}
	public static float sin(float a) {
		return (float) Math.sin(a);
	}
	public static float cos(float a) {
		return (float) Math.cos(a);
	}
	public static float tan(float a) {
		return (float) Math.tan(a);
	}
	public static float atan(float a) {
		return (float) Math.atan(a);
	}
	public static float atan2(float y, float x) {
		return (float) Math.atan2(y, x);
	}
	public static float sign(float a) {
		return (float) Math.signum(a);
	}
	public static float ceil(float a) {
		return (float) Math.ceil(a);
	}
	public static float floor(float a) {
		return (float) Math.floor(a);
	}
	public static float round(float a) {
		return Math.round(a);
	}
	public static float sqrt(float a) {
		return (float) Math.sqrt(a);
	}
	public static float frac(float a) {
		return a - (int)a;
	}
}
