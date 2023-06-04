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
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.RenderObject;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;
import Renderer3D.SceneManagment.Scene;
import Renderer3D.SceneManagment.SceneManager;

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

class normal extends Shader {

	Vector3 lightDir = Vector3.back();

	static Texture spr = new Texture();

	public normal() {
		// spr.LoadFromFile("./res/3D-Objects/rat/albedo.png");
		spr.LoadFromFile("./res/Textures/uvtest.png");
	}

	@Override
	public Vector3 Fragment(Vertex in) {
		Vector2 coord = in.texcoord;
		Vector3 col = spr.SampleFast(coord.x, coord.y);
		Vector3 normal = objectTransform.getRotationMatrix().MultiplyByVector(in.normal);

		float nl = Vector3.Dot(lightDir, normal);
		nl = Mathf.Clamp01(nl - 0.3f);
		nl += 0.3f;
		// nl = (nl < 0.6) ? 0.2f : 1;
		col.multiplyBy(nl);
		return col;
	}

}

class point extends Shader {

	public Vector3 ldir = new Vector3(0.75f, 0.4f, 1);
	public Vector3 point = Vector3.back();
	float radius = 4.5f;
	public Vector3 plightColor = new Vector3(0, 0, 0.8f);
	private Vector3 dlCol = new Vector3(0.96f, 0.72f, 0.18f);

	@Override
	public Vector3 Fragment(Vertex in) {
		Vector3 normal = objectTransform.getRotationMatrix().MultiplyByVector(in.normal);
		Vector3 sub = Vector3.subtract2Vecs(point, in.worldPos);
		float nl = Vector3.Dot(sub.normalized(), normal);
		nl *= Mathf.Clamp01((radius / sub.magnitude()) - 1);
		float nldir = Vector3.Dot(ldir, normal);

		Vector3 plout = Vector3.mulVecFloat(plightColor, nl);
		Vector3 dlout = Vector3.mulVecFloat(dlCol, nldir);
		Vector3.Clamp01(plout);
		Vector3.Clamp01(dlout);
		Vector3 fCol = Vector3.add2Vecs(plout, dlout);

		return fCol;
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

	Camera cam;
	@Override
	public void OnSceneLoad() {
		cam = new Camera();
		cam.transform.position.z = -3;
		setCamera(cam);

		RenderObject monke = new RenderObject(ObjLoader.Load("res/3D-Objects/monke.obj"));
		monke.setShader(new point());
		addObject(monke);

		RenderObject sky = new RenderObject(ObjLoader.Load("C:\\Users\\USER\\Downloads\\sky\\skybox.obj"));
		sky.setShader(new Tex("C:\\Users\\USER\\Downloads\\sky\\Space.png"));
		//sky.setShader(new Tex("res/Textures/uvtest.png"));
		addObject(sky);
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

	RenderObject cube;
	Camera cam;
	@Override
	public void OnSceneLoad() {
		cam = new Camera();
		cam.transform.position.z = -2;
		setCamera(cam);
		cube = new RenderObject(Mesh.MakeCube());
		cube.setShader(new Tex("res/Textures/uvtest.png"));
		addObject(cube);
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
		SceneManager.LoadScene("Monke");
		SceneManager.AddScene(cubeScene.class, "Cube");
	}

	@Override
	public void OnUpdate() {
		SceneManager.renderActiveScene();
		if (Input.OnKeyPressed(Keycode.RightArrow)) {
			SceneManager.LoadScene("Cube");
		}
		if (Input.OnKeyPressed(Keycode.LeftArrow)) {
			SceneManager.LoadScene("Monke");
		}
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