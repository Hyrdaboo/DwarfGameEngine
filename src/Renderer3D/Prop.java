package Renderer3D;

import DwarfEngine.MathTypes.Vector3;

class ErrorShader extends Shader {

	Vector3 magenta = new Vector3(1, 0, 1);

	@Override
	public Vector3 Fragment(Vertex in) {
		return magenta;
	}

}

public final class Prop {
	public final Transform transform;
	Shader shader = new ErrorShader();

	Triangle[] triangles;

	public Prop(Mesh mesh) {
		if (mesh == null) {
			throw new NullPointerException("The Mesh you provided for this object is null");
		}
		transform = new Transform();

		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
	
	public void setShader(Shader shader) {
		if (shader == null) return;
		this.shader = shader;
	}
	public Shader getShader() {
		return this.shader;
	}
}