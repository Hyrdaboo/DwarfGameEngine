package Renderer3D;

import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

import java.util.List;

/**
 * The Shader class represents a shader used for rendering objects in a 3D
 * scene. It provides an abstract base class that can be extended to implement
 * specific shader functionality. Shaders are responsible for determining the
 * appearance of objects by defining the color of each fragment or in other
 * words pixels that make up this object.
 */
public abstract class Shader {
	protected Light[] lights = null;
	protected final Vector3 ambientLight = new Vector3();
	protected Transform objectTransform = null;
	protected Matrix4x4 rotationMatrix = null;
	protected Transform cameraTransform = null;
	/**
	 * Specifies whether backface culling is enabled or disabled. Backface culling
	 * determines whether the backside of objects is rendered.
	 */
	public boolean cull = true;

	/**
	 * If the colors don't vary a lot, reduce the shader sampling resolution here.
	 */
	public int pixelation = 1;

	/**
	 * Passes object data of this Shader to another shader. Use this if you wanna
	 * use ouput of another shader and modify it and if the other shader uses
	 * lighting or ({@link #objectTransform}, {@link #cameraTransform}). Call this
	 * function before getting the output of another shader. Example:
	 *
	 * <pre>
	 * //inside Fragment function of your shader:
	 * passObjectData(base);
	 * Vector3 baseCol = base.Fragment(in);
	 * </pre>
	 *
	 * @param other destination object
	 */
	protected void passObjectData(Shader other) {
		if (other == null)
			return;
		if (other.lights == null)
			other.lights = this.lights;
		if (other.objectTransform == null)
			other.objectTransform = this.objectTransform;
		if (other.cameraTransform == null)
			other.cameraTransform = this.cameraTransform;
		if (other.rotationMatrix == null)
			other.rotationMatrix = this.rotationMatrix;
	}

	/**
	 * This method is called for each fragment/pixel in the rendered object.
	 * Implementations of this method should perform calculations and return the
	 * final color of the fragment.
	 *
	 * @param in The input vertex information for the fragment.
	 * @return The calculated color of the fragment.
	 */
	public abstract Vector3 Fragment(Vertex in, Vector3 dst);

	public void SetLights(List<Light> lights) {
		this.lights = lights.stream().filter((l) -> l.type != Light.LightType.Ambient).toArray(Light[]::new);
		ambientLight.set(0f, 0f, 0f);
		for (Light l : lights) {
			if (l.type == Light.LightType.Ambient) {
				ambientLight.addTo(l.getColor());
			}
		}
	}
}
