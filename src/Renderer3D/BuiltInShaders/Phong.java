package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Shader;
import Renderer3D.Vertex;

public class Phong extends Shader {
	public float shininess = 1;
	public Vector3 specularColor = Vector3.one();
	private Shader baseColor;

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
				attenuation = Mathf.Clamp01((light.radius / lightDist) - 1);
			}

			Vector3 halfVector = Vector3.add2Vecs(lightDir, cameraDir).normalized();
			float specular = Vector3.Dot(normal, halfVector);
			specular = Mathf.pow(specular, shininess);
			specular = Mathf.Clamp01(specular);
			finalSpecular.addTo(Vector3.mulVecFloat(specularColor, specular));
			finalSpecular = Vector3.mul2Vecs(finalSpecular, light.getColor());
			finalSpecular.multiplyBy(light.intensity);
			finalSpecular.multiplyBy(attenuation);

			float diffuse = Vector3.Dot(normal, lightDir);
			diffuse = Mathf.Clamp01(diffuse);
			finalCol.addTo(diffuse);
			finalCol = Vector3.mul2Vecs(finalCol, light.getColor());
			finalCol.multiplyBy(light.intensity);
			finalCol.multiplyBy(attenuation);
		}
		Vector3 surfaceColor = baseColor == null ? white : baseColor.Fragment(in);
		finalCol = Vector3.mul2Vecs(finalCol, surfaceColor);
		finalCol.addTo(finalSpecular);

		return finalCol;
	}
}