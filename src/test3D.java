import java.awt.Color;

import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.DisplayRenderer;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Camera;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Pipeline.RenderFlag;
import Renderer3D.Prop;
import Renderer3D.Scene;
import Renderer3D.SceneManager;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;
import Renderer3D.BuiltInShaders.Diffuse;
import Renderer3D.BuiltInShaders.Phong;
import Renderer3D.BuiltInShaders.Unlit;

class Tex extends Shader {
	static Texture spr = new Texture();

	public Tex(String path) {
		spr.LoadFromFile(path);
	}

	@Override
	public Vector3 Fragment(Vertex in) {
		Vector2 coord = in.texcoord;
		Vector3 col = spr.SampleFast(coord.x, coord.y);

		return col;
	}
}

class frag extends Shader {

	Vector3 white = Vector3.one();

	@Override
	public Vector3 Fragment(Vertex in) {
		return white;
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
	}

	Prop monke;
	Light sun;
	Camera cam;
	@Override
	public void OnSceneLoad() {
		cam = new Camera();
		cam.transform.position.z = -3;
		setCamera(cam);
		
		monke = new Prop(ObjLoader.Load("res/3D-Objects/monke.obj"));
		//monke = new Prop(ObjLoader.Load("res/3D-Objects/teapot.obj"));
		//monke = new Prop(ObjLoader.Load("C:\\Users\\USER\\Downloads\\cube.obj"));
	
		Phong shader = new Phong("res/Textures/uvtest.png");
		//Phong shader = new Phong();
		shader.shininess = 45;
		
		monke.setShader(shader);
		objects.add(monke);
		
		sun = new Light();
		//sun.type = LightType.Point;
		sun.transform.position = new Vector3(0, 0, -2f);
		sun.setColor(new Vector3(1, 1, 0.3f));
		sun.radius = 2;
		lights.add(sun);
		
		Light ambient = new Light();
		ambient.type = LightType.Ambient;
		float strength = 0.05f;
		ambient.setColor(new Vector3(strength, strength, strength));
		lights.add(ambient);
		
		//Prop sky = new Prop(ObjLoader.Load("C:\\Users\\USER\\Downloads\\sky\\skybox.obj"));
		//sky.setShader(new Tex("C:\\Users\\USER\\Downloads\\sky\\Space.png"));
		//sky.setShader(new Tex("res/Textures/uvtest.png"));
		//addObject(sky);
	}

	boolean confined = false;
	void GetInput() {
		float deltaTime = (float) app.getDeltaTime();
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.Shift)) {
			mul = 2;
		}
		float speed = 15 * mul;
		float lookSpeed = 100;
		Transform camTransform = cam.transform;
		
		if (Input.OnKeyHeld(Keycode.LeftArrow)) {
			monke.transform.rotation.y += lookSpeed * app.getDeltaTime();
		}
		if (Input.OnKeyHeld(Keycode.RightArrow)) {
			monke.transform.rotation.y -= lookSpeed * app.getDeltaTime();
		}
		
		if (Input.OnKeyHeld(Keycode.UpArrow)) {
			monke.transform.rotation.x -= lookSpeed * app.getDeltaTime();
		}
		if (Input.OnKeyHeld(Keycode.DownArrow)) {
			monke.transform.rotation.x += lookSpeed * app.getDeltaTime();
		}

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

		if (Input.OnKeyPressed(Keycode.Escape)) {
			confined = !confined;
			Input.setMouseConfined(confined);
		}
		if (Input.isMouseConfined()) {
			camTransform.rotation.y += Input.GetMouseDelta().x * 100;
			camTransform.rotation.x += Input.GetMouseDelta().y * 100;
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

class cubeScene extends Scene {
	Application app;
	public cubeScene(Application application) {
		super(application);
		app = application;
	}

	@Override
	public void OnSceneUpdate() {
		cube.transform.rotation.y += app.getDeltaTime() * 50;
	}

	Prop cube;
	Camera cam;
	@Override
	public void OnSceneLoad() {
		cam = new Camera();
		cam.transform.position.z = -2;
		setCamera(cam);
		cube = new Prop(Mesh.MakeCube());
		cube.setShader(new Tex("res/Textures/uvtest.png"));
		objects.add(cube);
	}
	
	@Override
	protected void OnSceneGUI() {
		DisplayRenderer.FillCircle(new Vector2(300, 100), 20, Color.yellow);
	}
}

@SuppressWarnings("serial")
class demo3D extends Application {

	@Override
	public void OnStart() {
		SceneManager.AddScene(suzanne.class, "Monke");
		SceneManager.AddScene(cubeScene.class, "Cube");
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