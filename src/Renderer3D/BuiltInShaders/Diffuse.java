package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Shader;
import Renderer3D.Vertex;

/**
 * The Diffuse class represents a diffuse shading model shader implementation.
 */
public class Diffuse extends Shader {

	private final Shader baseColor;

	/**
	 * Uses another unlit shader and applies diffuse lighting on it
	 *
	 * @param shader a Shader that doesn't implement lighting for example
	 *               {@link Unlit}
	 */
	public Diffuse(Shader shader) {
		baseColor = shader;
	}

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		dst.set(ambientLight);

		Vector3 lightDir = Vector3.POOL.get();
		Vector3 normal = Vector3.POOL.get();
		for (Light light : lights) {

			in.normal.normalized(normal);
			rotationMatrix.MultiplyByVector(normal, normal);

			float attenuation = 1f;
			if (light.type == LightType.Directional) {
				light.transform.forward.normalized(-1f, lightDir);
			} else {
				Vector3.subtract2Vecs(light.transform.position, in.worldPos, lightDir);
				float lightDist = lightDir.magnitude();
				lightDir.multiplyBy(1f / lightDist);
				attenuation = 1f - Math.min(lightDist / light.radius, 1f);
			}

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Math.max(diffuse, 0f);
			diffuse *= light.intensity * attenuation;
			dst.addTo(light.getColor(), diffuse);
		}

		if (baseColor != null) {
			// normal is being abused here as a temporary variable
			Vector3 surfaceColor = baseColor.Fragment(in, normal);
			Vector3.mul2Vecs(dst, surfaceColor, dst);
		}
		Vector3.POOL.sub(2);

		return dst;
	}
}
