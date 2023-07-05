package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Shader;
import Renderer3D.Vertex;

/**
 * The Diffuse class represents a diffuse shading model shader implementation.
 */
public class Diffuse extends Shader {

	private Shader baseColor;

	/**
	 * Uses another unlit shader and applies diffuse lighting on it
	 *
	 * @param shader a Shader that doesn't implement lighting for example
	 *               {@link Unlit}
	 */
	public Diffuse(Shader shader) {
		baseColor = shader;
	}

	private Vector3 white = Vector3.one();

	@Override
	public Vector3 Fragment(Vertex in) {

		Vector3 finalCol = Vector3.zero();
		for (int i = 0; i < lightCount(); i++) {
			Light light = GetLight(i);
			if (light == null)
				continue;

			if (light.type == LightType.Ambient) {
				finalCol.addTo(light.getColor());
				continue;
			}

			Vector3 normal = in.normal.normalized();
			normal = objectTransform.getRotationMatrix().MultiplyByVector(normal);

			Vector3 lightDir = null;
			float attenuation = 1;

			if (light.type == LightType.Directional) {
				lightDir = new Vector3(light.transform.forward.normalized());
				lightDir.multiplyBy(-1);
			} else {
				Vector3 difference = Vector3.subtract2Vecs(light.transform.position, in.worldPos);
				lightDir = difference.normalized();
				float lightDist = difference.magnitude();
				attenuation = Mathf.clamp01((light.radius / lightDist) - 1);
			}

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Mathf.clamp01(diffuse);
			finalCol.addTo(diffuse);
			finalCol = Vector3.mul2Vecs(finalCol, light.getColor());
			finalCol.multiplyBy(light.intensity);
			finalCol.multiplyBy(attenuation);
		}

		Vector3 surfaceColor = baseColor == null ? white : baseColor.Fragment(in);
		finalCol = Vector3.mul2Vecs(finalCol, surfaceColor);

		return finalCol;
	}
}
