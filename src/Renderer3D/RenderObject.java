package Renderer3D;

public final class RenderObject {
	public final Transform transform;
	Triangle[] triangles;
	
	public RenderObject(Mesh mesh) {
		transform = new Transform();
		
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
}
