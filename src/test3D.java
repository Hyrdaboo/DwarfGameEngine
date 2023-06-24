import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DebugGraphics;

import DwarfEngine.Text;
import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Camera;
import Renderer3D.Light;
import Renderer3D.Mesh;
import Renderer3D.Light.LightType;
import Renderer3D.Pipeline.RenderFlag;
import Renderer3D.ObjLoader;
import Renderer3D.Prop;
import Renderer3D.Scene;
import Renderer3D.SceneManager;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;
import Renderer3D.BuiltInShaders.Diffuse;
import Renderer3D.BuiltInShaders.Phong;
import Renderer3D.BuiltInShaders.Unlit;
import Renderer3D.BuiltInShaders.VertexColor;

class normal extends Shader {

	@Override
	public Vector3 Fragment(Vertex in) {
		return in.normal;
	}
	
}

class suzanne extends Scene {
	Application app;
	public suzanne(Application application) {
		super(application);
		app = application;
		app.title = "Hehe Monke";
	}

	@Override
	public void OnSceneUpdate() {
		GetInput();
		//sun.transform.rotation.x += app.getDeltaTime() * 20;
	}

	Camera cam;
	Light sun;
	@Override
	public void OnSceneLoad() {
		cam = new Camera();
		cam.transform.position.z = -3;
		setCamera(cam);
		
		Texture tex = new Texture();
		tex.LoadFromFile("res/Textures/Heightmap.png");
		Texture ramp = new Texture();
		ramp.LoadFromFile("res/Textures/ramp.png");
		Mesh m = GeneratePlane(100, tex, 30, ramp);
		Prop prop = new Prop(m);
		Diffuse phong = new Diffuse(new VertexColor());
		prop.setShader(phong);
		objects.add(prop);
		
		sun = new Light();
		sun.transform.rotation.x = 70;
		sun.setColor(new Vector3(0.75f, 0.75f, 0.8f));
		lights.add(sun);
		
		Light ambient = new Light();
		ambient.type = LightType.Ambient;
		ambient.setColor(new Vector3(0.3f, 0.3f, 0.3f));
		lights.add(ambient);
		//SetRenderFlag(RenderFlag.Wireframe);
	}
	
	public static Mesh GeneratePlane(int res, Texture heightmap, float maxHeight, Texture ramp) {
		res = Math.max(1, res);
		
		int[] indices = new int[res*res*6];
		res += 1;
		Vector3[] vertices = new Vector3[res*res];
		Vector3[] colors = new Vector3[res*res];
		
		for (int y = 0; y < res; y++) {
			for (int x = 0; x < res; x++) {
				Vector3 col = heightmap.SampleFast(x / (float)res, y / (float)res);
				float h = (col.x+col.y+col.z) / 3.0f;
				Vector3 vtcol = ramp.SampleFast(0, h);
				colors[x + y*res] = vtcol;
				h *= maxHeight;
				vertices[x + y*res] = new Vector3(x, h, y);
			}
		}
		
		int faceCount = indices.length / 6;
		for (int i = 0, k = 0; i < faceCount; i++, k++) {
			if (k % res == res-1) k++;
			
			indices[0+i*6] = k;
			indices[1+i*6] = k+res;
			indices[2+i*6] = k+res+1;
			indices[3+i*6] = k+res+1;
			indices[4+i*6] = k+1;
			indices[5+i*6] = k;
		}
		
		Mesh mesh = new Mesh();
		mesh.setVertices(vertices);
		mesh.setTriangles(indices);
		mesh.setColors(colors);
		mesh.recalculateNormals();
		return mesh;
	}

	void GetInput() {
		float deltaTime = (float) app.getDeltaTime();
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.Shift)) {
			mul = 2;
		}
		float speed = 15 * mul;
		float lookSpeed = 100;
		Transform camTransform = cam.transform;

		if (Input.OnKeyHeld(Keycode.W)) {
			Vector3 forward = Vector3.mulVecFloat(camTransform.forward, speed * deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			Vector3 forward = Vector3.mulVecFloat(camTransform.forward, -speed * deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.A)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, -speed * deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, right);
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, speed * deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, right);
		}

		if (Input.OnKeyHeld(Keycode.Q)) {
			camTransform.position.y -= deltaTime * speed;
		}
		if (Input.OnKeyHeld(Keycode.E)) {
			camTransform.position.y += deltaTime * speed;
		}

		if (Input.MouseButtonClicked(3)) {
			Input.setMouseConfined(true);
		}
		if (Input.MouseButtonReleased(3)) {
			Input.setMouseConfined(false);
		}
		if (Input.isMouseConfined()) {
			camTransform.rotation.y += Input.GetMouseDelta().x * 200;
			camTransform.rotation.x += Input.GetMouseDelta().y * 200;
		}

		// rotate camera
		if (Input.OnKeyHeld(Keycode.I)) {
			camTransform.rotation.x -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.J)) {
			camTransform.rotation.y -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.K)) {
			camTransform.rotation.x += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.L)) {
			camTransform.rotation.y += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.U)) {
			camTransform.rotation.z += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.O)) {
			camTransform.rotation.z -= deltaTime * lookSpeed;
		}
	}
}

@SuppressWarnings("serial")
class demo3D extends Application {

	@Override
	public void OnStart() {
		SceneManager.AddScene(suzanne.class, "Monke");
		SceneManager.LoadScene("Monke");
	}

	@Override
	public void OnUpdate() {
		SceneManager.renderActiveScene();
	}
}

public class test3D {

	public static void main(String[] args) {
		demo3D d = new demo3D();
		// d.Initialize(144, 81, 6);
		d.Initialize(1280/2, 720/2, 1);
		// d.Initialize(32, 32, 1);
		// d.Initialize(720, 405, 1);
	}
}