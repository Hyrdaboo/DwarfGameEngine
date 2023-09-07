package Renderer3D;

import DwarfEngine.MathTypes.Vector3;

import java.util.List;

class ErrorShader extends Shader {

	Vector3 magenta = new Vector3(1, 0, 1);

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		return magenta;
	}

}

/**
 * The <code>Prop</code> class represents an entity in the 3D scene that can be
 * rendered. It holds a reference to the {@link Mesh} of the object and manages
 * its transformation in the 3D scene and the shader used for rendering.
 */
public final class Prop {
	public final Transform transform;
	Shader shader = new ErrorShader();

	Triangle[] triangles;

	/**
	 * Creates a new <code>Prop</code> instance with the provided mesh.
	 *
	 * @param mesh The mesh representing the geometry of the prop.
	 * @throws NullPointerException if the provided mesh is null.
	 */
	public Prop(Mesh mesh) {
		if (mesh == null) {
			throw new NullPointerException("The Mesh you provided for this object is null");
		}
		transform = new Transform();

		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}

	/**
	 * Sets the shader used for rendering the prop.
	 *
	 * @param shader The shader to set.
	 */
	public void setShader(Shader shader) {
		if (shader == null)
			return;
		this.shader = shader;
	}

	/**
	 * Retrieves the shader used for rendering the prop.
	 *
	 * @return The shader used for rendering.
	 */
	public Shader getShader() {
		return this.shader;
	}

	/**
	 * Set the lights for the Shader of this object
	 *
	 * @param lights List of lights to set
	 */
	public void SetLights(List<Light> lights) {
		shader.SetLights(lights);
	}
}