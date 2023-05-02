package Renderer3D.TriangleRenderer;

import java.awt.Color;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;

public final class Vertex {
	public Vector3 position = Vector3.zero();
	public Vector3 color = Vector3.zero();
	public float depth;
	
	static Vertex Lerp(Vertex a, Vertex b, float t) {
		Vertex v = new Vertex();
		v.position = Vector3.Lerp(a.position, b.position, t);
		v.color = Vector3.Lerp(a.color, b.color, t);
		v.depth = Mathf.Lerp(a.depth, b.depth, t);
		return v;
	}
}
