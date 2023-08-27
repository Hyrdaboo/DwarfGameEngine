package Demos;

import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.Texture;
import Renderer3D.BuiltInShaders.Phong;
import Renderer3D.BuiltInShaders.Unlit;
import Renderer3D.*;
import Renderer3D.Light.LightType;

import java.io.File;

// Loading a simple 3D scene with a model
public class SuzanneDemo extends Scene {

	Application app;

	public SuzanneDemo(Application application) {
		super(application);
		// set a reference to application to access its functions
		app = application;
		app.title = "Simple Scene with 1 object";
	}

	@Override
	protected void OnSceneLoad() {
		Texture uvchecker = new Texture();
		uvchecker.Load("DemoResources/uvgrid.png");

		// create a phong shader from unlit texture shader
		Unlit unlit = new Unlit();
		unlit.setTexture(uvchecker);
		Phong phong = new Phong(unlit);
		phong.shininess = 50;

		File suzanneFile = new File("DemoResources/suzanne.obj");
		suzanne = new Prop(ObjLoader.Load(suzanneFile));
		suzanne.setShader(phong);
		objects.add(suzanne);

		Texture skyTexture = new Texture();
		skyTexture.Load("DemoResources/skyImage.png");

		Unlit skyShader = new Unlit();
		skyShader.setTexture(skyTexture);
		File skyFile = new File("DemoResources/sky.obj");
		Prop sky = new Prop(ObjLoader.Load(skyFile));
		sky.setShader(skyShader);
		sky.transform.scale = Vector3.mulVecFloat(Vector3.one(), 1000);
		objects.add(sky);

		// add a sun and rotate it
		Light sun = new Light();
		sun.transform.rotation = new Vector3(40, 25, 0);
		sun.setColor(new Vector3(1, 1, 0.6f));
		lights.add(sun);

		// add ambient light so that it isn't so dark where light doesn't shine directly
		Light ambient = new Light();
		ambient.setColor(new Vector3(0.3f, 0.37f, 0.43f));
		ambient.type = LightType.Ambient;
		lights.add(ambient);

		// add camera so that we can see and move it back 3 units
		camera = new Camera();
		camera.transform.position.z = -3;
		setCamera(camera);
	}

	Prop suzanne;
	Camera camera;

	@Override
	protected void OnSceneUpdate() {
		// simple fps control
		FpsControls.GetInput((float) app.getDeltaTime(), camera.transform);
		suzanne.transform.rotation.y += app.getDeltaTime() * 10;
	}
}
