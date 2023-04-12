package Renderer3D;

import java.awt.Color;

public final class RenderObject {
	public final Transform transform;
	public Color color = Color.white;
	
	Triangle[] triangles;
	
	public RenderObject(Mesh mesh) {
		transform = new Transform();
		
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
}
