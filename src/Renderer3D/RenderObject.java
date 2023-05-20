package Renderer3D;

import java.awt.Color;

class ErrorShader implements Shader {

	@Override
	public Color Fragment(Vertex in) {
		return Color.magenta;
	}
	
}

public final class RenderObject {
	public final Transform transform;
	public Shader shader = new ErrorShader();
	
	Triangle[] triangles;
	
	public RenderObject(Mesh mesh) {
		transform = new Transform();
		
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
}
