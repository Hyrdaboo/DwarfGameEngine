package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Shader;
import Renderer3D.Vertex;

/**
 * The Phong class represents a Phong shading model shader implementation.
 */
public class Phong extends Shader {
	public float shininess = 1;
	public Vector3 specularColor = Vector3.one();
	private Shader baseColor;

	/**
	 * Uses another unlit shader and applies phong lighting on it
	 *
	 * @param shader a Shader that doesn't implement lighting for example
	 *               {@link Unlit}
	 */
	public Phong(Shader shader) {
		baseColor = shader;
	}

	private Vector3 white = Vector3.one();

	@Override
	public Vector3 Fragment(Vertex in) {

		Vector3 finalCol = Vector3.zero();
		Vector3 finalSpecular = Vector3.zero();
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

			Vector3 cameraDir = Vector3.subtract2Vecs(cameraTransform.position, in.worldPos).normalized();

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

			Vector3 halfVector = Vector3.add2Vecs(lightDir, cameraDir).normalized();
			float specular = Vector3.Dot(normal, halfVector);
			specular = Mathf.pow(specular, shininess);
			specular = Mathf.clamp01(specular);
			specular *= light.intensity * attenuation;
			finalSpecular.addTo(Vector3.mul2Vecs(Vector3.mulVecFloat(specularColor, specular), light.getColor()));

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Mathf.clamp01(diffuse);
			diffuse *= light.intensity * attenuation;
			finalCol.addTo(Vector3.mulVecFloat(light.getColor(), diffuse));
		}
		Vector3 surfaceColor = baseColor == null ? white : baseColor.Fragment(in);
		finalCol = Vector3.mul2Vecs(finalCol, surfaceColor);
		finalCol.addTo(finalSpecular);

		return finalCol;
	}
}
