package Renderer3D;

import java.awt.Color;

import DwarfEngine.MathTypes.Vector3;

class ErrorShader extends Shader {

	Vector3 magenta = new Vector3(1, 0, 1);
	
	@Override
	public Vector3 Fragment(Vertex in) {
		return magenta;
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
