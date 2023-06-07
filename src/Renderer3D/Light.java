package Renderer3D;

import DwarfEngine.MathTypes.Vector3;

public final class Light {
	public enum LightType {Directional, Point, Ambient}
	public LightType type = LightType.Directional;
	public final Transform transform = new Transform();
	private Vector3 color = Vector3.one();
	public float radius = 1;
	public float intensity = 1;


	public void setColor(Vector3 color) {
		if (color == null) return;
		this.color = color;
	}
	public Vector3 getColor() {
		return this.color;
	}
}
