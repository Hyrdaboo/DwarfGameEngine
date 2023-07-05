package Renderer3D;

import DwarfEngine.MathTypes.Vector3;

/**
 * Represents a light source in the 3D scene.
 */
public final class Light {
	/**
	 * The type of light.
	 */
	public enum LightType {
		Directional, Point, Ambient
	}

	/** The type of current light, defaults to directional */
	public LightType type = LightType.Directional;
	public final Transform transform = new Transform();
	/** The color of the light, defaults to white */
	private Vector3 color = Vector3.one();
	/** The radius of the light, applicable for point lights */
	public float radius = 1;
	/** The intensity of the light */
	public float intensity = 1;

	/**
	 * Sets the color of the light.
	 *
	 * @param color The color to set.
	 */
	public void setColor(Vector3 color) {
		if (color == null)
			return;
		this.color = color;
	}

	public Vector3 getColor() {
		return this.color;
	}
}
