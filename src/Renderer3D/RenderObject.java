package Renderer3D;

public final class RenderObject {
	public final Transform transform;
	public DwarfShader shader;
	
	Triangle[] triangles;
	
	public RenderObject(Mesh mesh) {
		transform = new Transform();
		
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
}
