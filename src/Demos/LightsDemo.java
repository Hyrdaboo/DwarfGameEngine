package Demos;

import java.io.File;

import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Camera;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Prop;
import Renderer3D.Scene;
import Renderer3D.Transform;
import Renderer3D.BuiltInShaders.Diffuse;
import Renderer3D.BuiltInShaders.Phong;
import Renderer3D.BuiltInShaders.Unlit;

public class LightsDemo extends Scene {

	Application app;

	public LightsDemo(Application application) {
		super(application);
		app = application;
		app.title = "Lights";
	}

	@Override
	protected void OnSceneLoad() {
		Vector3[] verts = new Vector3[] { new Vector3(-0.5f, 0, -0.5f), new Vector3(0.5f, 0, -0.5f),
				new Vector3(-0.5f, 0, 0.5f), new Vector3(0.5f, 0, 0.5f), };
		Vector2[] uvs = new Vector2[] { new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 1), new Vector2(1, 1), };
		int[] triangles = new int[] { 0, 2, 3, 3, 1, 0 };
		Mesh plane = new Mesh();
		plane.setVertices(verts);
		plane.setTriangles(triangles);
		plane.setUV(uvs);

		Texture uvgrid = new Texture();
		uvgrid.Load("DemoResources/uvgrid.png");
		Unlit unlit = new Unlit();
		unlit.setTexture(uvgrid);
		Prop planeProp = new Prop(plane);
		planeProp.transform.scale = Vector3.mulVecFloat(Vector3.one(), 30);
		planeProp.setShader(new Diffuse(unlit));
		objects.add(planeProp);

		Phong phong = new Phong(unlit);
		phong.shininess = 75;
		monkey = new Prop(ObjLoader.Load(new File("DemoResources/suzanne.obj")));
		monkey.transform.rotation.y = 180;
		monkey.transform.position.y = 1;
		monkey.setShader(phong);
		objects.add(monkey);

		redLamp = new Light();
		redLamp.type = LightType.Point;
		redLamp.setColor(Vector3.right());
		redLamp.transform.position.z = -1;
		redLamp.transform.position.x = 5;
		redLamp.transform.position.y = 0.5f;
		redLamp.radius = 5;
		lights.add(redLamp);

		greenLamp = new Light();
		greenLamp.type = LightType.Point;
		greenLamp.setColor(Vector3.up());
		greenLamp.transform.position.x = -1;
		greenLamp.transform.position.z = 1;
		greenLamp.transform.position.y = 0.5f;
		greenLamp.radius = 5;
		lights.add(greenLamp);

		blueLamp = new Light();
		blueLamp.type = LightType.Point;
		blueLamp.setColor(Vector3.forward());
		blueLamp.transform.position.x = 1;
		blueLamp.transform.position.z = 1;
		blueLamp.transform.position.y = 0.5f;
		blueLamp.radius = 5;
		lights.add(blueLamp);

		camera = new Camera();
		camera.transform.position.z = -3;
		camera.transform.position.y = 1;
		setCamera(camera);
	}

	Prop monkey;
	Camera camera;
	Light redLamp, greenLamp, blueLamp;

	@Override
	protected void OnSceneUpdate() {
		FpsControls.GetInput((float) app.getDeltaTime(), camera.transform);
		bob(1, 2, 1, monkey.transform, 1);
		bob(-5, 5, 0.5f, redLamp.transform, 0);
		bob(0.1f, 5, 1, greenLamp.transform, 1);
		bob(-5, 5, 1.5f, blueLamp.transform, 2);
		monkey.transform.rotation.y += app.getDeltaTime() * 10;
	}

	void bob(float start, float end, float speed, Transform transform, int axis) {
		float t = ((float) Math.sin(app.getTime() * speed) + 1) / 2;
		if (axis == 0)
			transform.position.x = Mathf.lerp(start, end, t);
		else if (axis == 1)
			transform.position.y = Mathf.lerp(start, end, t);
		else
			transform.position.z = Mathf.lerp(start, end, t);
	}
}
