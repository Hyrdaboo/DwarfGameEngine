package Demos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import DwarfEngine.Text;
import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Camera;
import Renderer3D.Light;
import Renderer3D.Light.LightType;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Prop;
import Renderer3D.Scene;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;
import Renderer3D.BuiltInShaders.Diffuse;
import Renderer3D.BuiltInShaders.Phong;
import Renderer3D.BuiltInShaders.Unlit;
import Renderer3D.BuiltInShaders.VertexColor;

class SolidColor extends Shader {
	Vector3 col = new Vector3(0, 0, 0);

	public SolidColor(Color color) {
		col.x = color.getRed() / 255.0f;
		col.y = color.getGreen() / 255.0f;
		col.z = color.getBlue() / 255.0f;
	}

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		return col;
	}
}

class Text3D {
	Camera cam;
	Text text;
	Vector3 worldPos;

	public Text3D(String text, Vector3 worldPos, Camera cam) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource("/font-atlas.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Texture atlas = new Texture();
		atlas.Load(image);
		this.text = new Text(atlas, 32);
		this.text.spacing = -20;
		this.text.SetText(text);

		this.cam = cam;
		this.worldPos = worldPos;
	}

	Vector2 scale = Vector2.one();

	public void render() {
		Vector3 dir = Vector3.subtract2Vecs(worldPos, cam.transform.position).normalized();
		float dot = Vector3.Dot(dir, cam.transform.forward);
		if (dot < 0)
			return;

		Vector3 pos = cam.worldToScreenPoint(worldPos);
		float offset = text.getText().length() * (32 + text.spacing) / 2;
		text.draw(new Vector2(pos.x - offset, pos.y), scale);
	}
}

public class ShaderShowcaseScene extends Scene {

	Application app;

	public ShaderShowcaseScene(Application application) {
		super(application);
		app = application;
		app.title = "Shader showcase.";
	}

	@Override
	protected void OnSceneLoad() {

		Texture skyTexture = new Texture();
		skyTexture.Load("DemoResources/skyImage.png");

		Unlit skyShader = new Unlit();
		skyShader.setTexture(skyTexture);
		File skyFile = new File("DemoResources/sky.obj");
		Prop sky = new Prop(ObjLoader.Load(skyFile));
		sky.setShader(skyShader);
		sky.transform.scale = Vector3.mulVecFloat(Vector3.one(), 10000);
		objects.add(sky);

		camera = new Camera();
		camera.SetFar(15000);
		camera.transform.position.z = -40;
		camera.transform.position.y = 15;
		setCamera(camera);

		Light sun = new Light();
		sun.transform.rotation = new Vector3(30, 25, 0);
		sun.setColor(new Vector3(1, 1, 0.6f));
		lights.add(sun);

		Light ambient = new Light();
		ambient.setColor(new Vector3(0.3f, 0.37f, 0.43f));
		ambient.type = LightType.Ambient;
		lights.add(ambient);

		Vector3[] verts = new Vector3[] { new Vector3(-0.5f, 0, -0.5f), new Vector3(0.5f, 0, -0.5f),
				new Vector3(-0.5f, 0, 0.5f), new Vector3(0.5f, 0, 0.5f), };
		int[] triangles = new int[] { 0, 2, 3, 3, 1, 0 };
		Mesh plane = new Mesh();
		plane.setVertices(verts);
		plane.setTriangles(triangles);

		Prop planeProp = new Prop(plane);
		planeProp.transform.scale = Vector3.mulVecFloat(Vector3.one(), 30);
		planeProp.setShader(new Diffuse(new Unlit()));
		planeProp.transform.position.x = 10;
		planeProp.transform.position.z = 10;
		objects.add(planeProp);

		Texture uvchecker = new Texture();
		uvchecker.Load("DemoResources/uvgrid.png");
		Unlit unlit = new Unlit();
		unlit.setTexture(uvchecker);

		Diffuse pillarShader = new Diffuse(new SolidColor(Color.gray));
		File pillar1File = new File("DemoResources/Pillar.obj");
		Mesh pillarMesh = ObjLoader.Load(pillar1File);

		Prop pillar1 = new Prop(pillarMesh);
		pillar1.setShader(pillarShader);
		objects.add(pillar1);
		texts.add(new Text3D("Phong", pillar1.transform.position, camera));

		Prop pillar2 = new Prop(pillarMesh);
		pillar2.setShader(pillarShader);
		pillar2.transform.position.z = 20;
		objects.add(pillar2);
		texts.add(new Text3D("Diffuse", pillar2.transform.position, camera));

		Prop pillar3 = new Prop(pillarMesh);
		pillar3.setShader(pillarShader);
		pillar3.transform.position.x = 20;
		objects.add(pillar3);
		texts.add(new Text3D("Vertex Color", pillar3.transform.position, camera));

		Prop pillar4 = new Prop(pillarMesh);
		pillar4.setShader(pillarShader);
		pillar4.transform.position.x = 20;
		pillar4.transform.position.z = 20;
		objects.add(pillar4);
		texts.add(new Text3D("Unlit Texture", pillar4.transform.position, camera));

		Phong phong = new Phong(unlit);
		phong.shininess = 80;
		File suzanneFile = new File("DemoResources/suzanne.obj");
		suzanne = new Prop(ObjLoader.Load(suzanneFile));
		suzanne.setShader(phong);
		suzanne.transform.position = Vector3.add2Vecs(pillar1.transform.position, new Vector3(0, 15, 0));
		objects.add(suzanne);

		File bunnyFile = new File("DemoResources/bunny.obj");
		bunny = new Prop(ObjLoader.Load(bunnyFile));
		bunny.setShader(new Diffuse(new SolidColor(new Color(0xfffbd9))));
		bunny.transform.position = Vector3.add2Vecs(pillar2.transform.position, new Vector3(0, 15, 0));
		objects.add(bunny);

		File bookFile = new File("DemoResources/book.obj");
		book = new Prop(ObjLoader.Load(bookFile));
		book.setShader(new Diffuse(new VertexColor()));
		book.transform.position = Vector3.add2Vecs(pillar3.transform.position, new Vector3(0, 15, 0));
		book.transform.scale = new Vector3(5, 5, 5);
		objects.add(book);

		Texture albedo = new Texture();
		albedo.Load("DemoResources/rat/albedo.png");
		Unlit ratShader = new Unlit();
		ratShader.setTexture(albedo);

		File ratFile = new File("DemoResources/rat/rat.obj");
		rat = new Prop(ObjLoader.Load(ratFile));
		rat.setShader(ratShader);
		rat.transform.position = Vector3.add2Vecs(pillar4.transform.position, new Vector3(0, 15, 0));
		rat.transform.scale = new Vector3(2, 2, 2);
		objects.add(rat);
	}

	Camera camera;
	List<Text3D> texts = new ArrayList<>();
	Prop suzanne;
	Prop bunny;
	Prop book;
	Prop rat;

	@Override
	protected void OnSceneUpdate() {
		FpsControls.GetInput((float) app.getDeltaTime(), camera.transform);
		spin(suzanne.transform);
		spin(bunny.transform);
		spin(book.transform);
		rat.transform.rotation.x += app.getDeltaTime() * 100;
		rat.transform.rotation.y += app.getDeltaTime() * 100;
	}

	void spin(Transform transform) {
		transform.rotation.y += app.getDeltaTime() * 10;
	}

	@Override
	protected void OnSceneAfterUpdate() {
		for (Text3D t : texts) {
			t.render();
		}
	}
}