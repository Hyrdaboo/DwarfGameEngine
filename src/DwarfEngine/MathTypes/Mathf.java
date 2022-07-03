package DwarfEngine.MathTypes;

public final class Mathf {
	public static final float Deg2Rad = (float) ((1 / 180.0f) * Math.PI);
	public static final float epsilon = (float) 2.2204460492503130808472633361816E-16;
	public static float Lerp(float a, float b, float t) {
		return a + t * (b - a);
	}
	public static float InverseLerp(float a, float b, float value) {
		return (value - a) / (b - a);
	}
	public static float Clamp(float value, float min, float max) {
		if (value > max) return max;
		else if (value < min) return min;
		else return value;
	}
}
