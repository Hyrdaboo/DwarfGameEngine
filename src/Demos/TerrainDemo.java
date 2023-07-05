package Demos;

import java.awt.Color;
import java.io.File;

import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Camera;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Prop;
import Renderer3D.Scene;
import Renderer3D.Shader;
import Renderer3D.Vertex;
import Renderer3D.BuiltInShaders.Diffuse;
import Renderer3D.BuiltInShaders.Unlit;

class Triplanar extends Shader {
	float scale = 1;
	float maxHeight = 10;
	float blendHeight = 0.5f;
	float blend = 0.1f;

	Texture grass;
	Texture sand;

	public Triplanar() {
		grass = new Texture();
		grass.Load("DemoResources/Grass.png");
		sand = new Texture();
		sand.Load("DemoResources/Sand.png");
	}

	@Override
	public Vector3 Fragment(Vertex in) {
		Vector3 worldPos = in.worldPos;
		float mask = worldPos.y / Math.max(1, maxHeight);
		mask = Mathf.smoothstep(blendHeight - blend, blendHeight + blend, mask);

		float u = Mathf.abs(worldPos.x) / scale;
		float v = Mathf.abs(worldPos.z) / scale;
		Vector3 grassCol = grass.SampleFast(u, v);
		grassCol.multiplyBy(mask);
		Vector3 sandCol = sand.Sample(u, v);
		sandCol.multiplyBy(1 - mask);
		return Vector3.add2Vecs(grassCol, sandCol);
	}

}

class Fog extends Shader {
	private Shader base;
	public float density = 0.02f;
	private Vector3 fogColor = Vector3.one();

	public Fog(Shader other) {
		base = other;
	}

	void setFogColor(Color color) {
		fogColor.x = color.getRed() / 255.0f;
		fogColor.y = color.getGreen() / 255.0f;
		fogColor.z = color.getBlue() / 255.0f;
	}

	@Override
	public Vector3 Fragment(Vertex in) {
		float d = 1.0f / in.position.w;
		float depth = Mathf.pow(2.71828f, -(d * d * density * density));
		depth = 1 - depth;
		Vector3 fogCol = new Vector3(fogColor);
		fogCol.multiplyBy(depth);
		passObjectData(base);
		Vector3 baseCol = base.Fragment(in);
		return Vector3.Lerp(baseCol, fogCol, depth);
	}
}

public class TerrainDemo extends Scene {

	Application app;

	public TerrainDemo(Application application) {
		super(application);
		app = application;
		app.title = "Simple Terrain";
	}

	@Override
	protected void OnSceneLoad() {
		camera = new Camera();
		camera.transform.position.z = -45;
		camera.transform.position.y = 15;
		camera.transform.position.x = 45;
		camera.transform.rotation.y = -45;
		camera.SetFar(10000);
		setCamera(camera);

		Light sun = new Light();
		sun.transform.rotation = new Vector3(30, 45, 0);
		sun.setColor(new Vector3(1, 1, 0.6f));
		lights.add(sun);

		Light ambient = new Light();
		ambient.setColor(new Vector3(0.3f, 0.37f, 0.43f));
		ambient.type = LightType.Ambient;
		lights.add(ambient);

		Texture heightmap = new Texture();
		heightmap.Load("DemoResources/Heightmap.png");
		Texture skyTexture = new Texture();
		skyTexture.Load("DemoResources/skyImage.png");

		Unlit skyShader = new Unlit();
		skyShader.setTexture(skyTexture);
		File skyFile = new File("DemoResources/sky.obj");
		Prop sky = new Prop(ObjLoader.Load(skyFile));
		sky.setShader(skyShader);
		sky.transform.scale = Vector3.mulVecFloat(Vector3.one(), 10000);
		objects.add(sky);

		int res = 100;
		Mesh terrainMesh = GenerateTerrain(res, heightmap, 30);
		Prop terrain = new Prop(terrainMesh);
		Triplanar triplanar = new Triplanar();
		triplanar.scale = 100 / 2;
		triplanar.maxHeight = 30;
		triplanar.blendHeight = 0.1f;
		Fog terrainShader = new Fog(new Diffuse(triplanar));
		terrainShader.setFogColor(new Color(0x8192ae));
		terrain.setShader(terrainShader);
		terrain.transform.position.x = -res / 2;
		terrain.transform.position.z = -res / 2;
		objects.add(terrain);
	}

	Camera camera;

	public static Mesh GenerateTerrain(int res, Texture heightmap, float maxHeight) {
		res = Math.max(1, res);

		int[] indices = new int[res * res * 6];
		res += 1;
		Vector3[] vertices = new Vector3[res * res];

		for (int y = 0; y < res; y++) {
			for (int x = 0; x < res; x++) {
				Vector3 col = heightmap.SampleFast(x / (float) res, y / (float) res);
				float h = (col.x + col.y + col.z) / 3.0f;
				h *= maxHeight;
				vertices[x + y * res] = new Vector3(x, h, y);
			}
		}

		int faceCount = indices.length / 6;
		for (int i = 0, k = 0; i < faceCount; i++, k++) {
			if (k % res == res - 1)
				k++;

			indices[0 + i * 6] = k;
			indices[1 + i * 6] = k + res;
			indices[2 + i * 6] = k + res + 1;
			indices[3 + i * 6] = k + res + 1;
			indices[4 + i * 6] = k + 1;
			indices[5 + i * 6] = k;
		}

		Mesh mesh = new Mesh();
		mesh.setVertices(vertices);
		mesh.setTriangles(indices);
		mesh.recalculateNormals();
		return mesh;
	}

	@Override
	protected void OnSceneUpdate() {
		FpsControls.GetInput((float) app.getDeltaTime(), camera.transform);
	}
}
