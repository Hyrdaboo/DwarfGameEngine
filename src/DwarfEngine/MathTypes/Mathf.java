package DwarfEngine.MathTypes;

import java.awt.Color;

/**
 * A Math class that wraps java math methods into floats and has a couple of
 * useful methods such as {@link Mathf#lerp(Color, Color, float)}. It mostly
 * serves the purpose of storing those methods and saving me some effort from
 * explicitly converting commonly used {@link Math} methods to float
 */
public final class Mathf {
	public static final float Deg2Rad = 0.01745329251994329576923690768489f;
	public static final float Rad2Deg = 57.295779513082320876798154814105f;
	public static final float epsilon = (float) 2.2204460492503130808472633361816E-16;

	public static float lerp(float a, float b, float t) {
		t = Mathf.clamp01(t);
		return a + t * (b - a);
	}

	public static float inverseLerp(float a, float b, float t) {
		return (t - a) / (b - a);
	}

	public static Color lerp(Color a, Color b, float t) {
		int r = (int) lerp(a.getRed(), b.getRed(), t);
		int g = (int) lerp(a.getGreen(), b.getGreen(), t);
		int _b = (int) lerp(a.getBlue(), b.getBlue(), t);
		return new Color(r, g, _b);
	}

	public static float step(float a, float b) {
		if (a > b)
			return 1;
		return 0;
	}

	public static float smoothstep(float a, float b, float t) {
		t = clamp01((t - a) / (b - a));
		return t * t * (3.0f - 2.0f * t);
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(Math.min(value, max), min);
	}

	public static float clamp01(float value) {
		return clamp(value, 0, 1);
	}

	public static float abs(float a) {
		return Math.abs(a);
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
		return Math.signum(a);
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

	public static float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}
}
