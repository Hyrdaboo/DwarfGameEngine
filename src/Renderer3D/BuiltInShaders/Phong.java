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
	public Vector3 Fragment(Vertex in, Vector3 dst) {

		Vector3 finalCol = dst;
		Vector3 finalSpecular = Vector3.POOL.get();

		finalCol.x = finalCol.y = finalCol.z = 0;
		finalSpecular.x = finalSpecular.y = finalSpecular.z = 0;

		Vector3 normal = Vector3.POOL.get();
		Vector3 cameraDir = Vector3.POOL.get();
		Vector3 halfVector = Vector3.POOL.get();
		Vector3 lightDir = Vector3.POOL.get();

		in.normal.normalized(normal);
		rotationMatrix.MultiplyByVector(normal, normal);

		for (int i = 0; i < lightCount(); i++) {
			Light light = GetLight(i);
			if (light == null)
				continue;

			if (light.type == LightType.Ambient) {
				finalCol.addTo(light.getColor());
				continue;
			}

			float attenuation = 1f;

			Vector3.subtract2Vecs(cameraTransform.position, in.worldPos, cameraDir);
			cameraDir.normalized(cameraDir);

			if (light.type == LightType.Directional) {
				light.transform.forward.normalized(-1f, lightDir);
			} else {
				Vector3.subtract2Vecs(light.transform.position, in.worldPos, lightDir);
				float lightDist = lightDir.magnitude();
				lightDir.normalized();
				attenuation = 1f - Mathf.clamp01(lightDist / light.radius);
			}

			Vector3.add2Vecs(lightDir, cameraDir, halfVector);
			halfVector.normalized(halfVector);
			float specular = Vector3.Dot(normal, halfVector);
			specular = Mathf.pow(specular, shininess);
			specular = Mathf.clamp01(specular);
			specular *= light.intensity * attenuation;
			finalSpecular.addTo(specularColor, light.getColor(), specular);

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Mathf.clamp01(diffuse);
			diffuse *= light.intensity * attenuation;
			finalCol.addTo(light.getColor(), diffuse);
		}

		if (baseColor == null) {
			finalCol.addTo(finalSpecular);
		} else {
			Vector3 surfaceColor = baseColor.Fragment(in, new Vector3());
			finalCol = Vector3.mul2Vecs(finalCol, surfaceColor);
			finalCol.addTo(finalSpecular);
		}

		Vector3.POOL.sub(5);

		return finalCol;
	}
}
