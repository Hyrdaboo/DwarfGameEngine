package Renderer3D;

import java.util.List;

import DwarfEngine.MathTypes.Vector3;

/**
 * The Shader class represents a shader used for rendering objects in a 3D
 * scene. It provides an abstract base class that can be extended to implement
 * specific shader functionality. Shaders are responsible for determining the
 * appearance of objects by defining the color of each fragment or in other
 * words pixels that make up this object.
 */
public abstract class Shader {
	List<Light> lights = null;
	protected Transform objectTransform = null;
	protected Transform cameraTransform = null;
	/**
	 * Specifies whether backface culling is enabled or disabled. Backface culling
	 * determines whether the backside of objects is rendered.
	 */
	public boolean cull = true;

	/**
	 * Retrieves a light from the list of lights of this shader
	 *
	 * @param index index of the light in the list
	 * @return the light with this index <br>
	 *         <code>null</code> if the lights for this shader have not been
	 *         initialized or the index is out of bounds
	 */
	protected Light GetLight(int index) {
		if (lights == null || index < 0 || index >= lights.size())
			return null;
		return lights.get(index);
	}

	protected int lightCount() {
		if (lights == null)
			return 0;
		return lights.size();
	}

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
	 * @param other
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
	}

	/**
	 * This method is called for each fragment/pixel in the rendered object.
	 * Implementations of this method should perform calculations and return the
	 * final color of the fragment.
	 *
	 * @param in The input vertex information for the fragment.
	 * @return The calculated color of the fragment.
	 */
	public abstract Vector3 Fragment(Vertex in);
}
