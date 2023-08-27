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

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		dst.x = dst.y = dst.z = 0f;

		for (int i = 0; i < lightCount(); i++) {
			Light light = GetLight(i);
			if (light == null)
				continue;

			if (light.type == LightType.Ambient) {
				dst.addTo(light.getColor());
				continue;
			}

			Vector3 normal = in.normal.normalized();
			normal = rotationMatrix.MultiplyByVector(normal);

			Vector3 lightDir;
			float attenuation = 1;

			if (light.type == LightType.Directional) {
				lightDir = new Vector3(light.transform.forward.normalized());
				lightDir.multiplyBy(-1);
			} else {
				Vector3 difference = Vector3.subtract2Vecs(light.transform.position, in.worldPos);
				lightDir = difference.normalized();
				float lightDist = difference.magnitude();
				attenuation = Mathf.clamp01(lightDist / light.radius);
				attenuation = 1 - attenuation;
			}

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Mathf.clamp01(diffuse);
			diffuse *= light.intensity * attenuation;
			dst.addTo(Vector3.mulVecFloat(light.getColor(), diffuse));
		}

		if (baseColor != null) {
			Vector3 surfaceColor = baseColor.Fragment(in, new Vector3());
			Vector3.mul2Vecs(dst, surfaceColor, dst);
		}

		return dst;
	}
}
