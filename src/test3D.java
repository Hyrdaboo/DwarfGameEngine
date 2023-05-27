import java.awt.Color;
import java.awt.Cursor;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.image.BufferedImage;

import javax.swing.plaf.basic.BasicPanelUI;

import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.Texture.SamplingMode;
import DwarfEngine.Texture.WrapMode;

import static DwarfEngine.Core.DisplayRenderer.*;
import Renderer3D.Camera;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Pipeline;
import Renderer3D.RenderObject;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;
import Renderer3D.Pipeline.DrawFlag;

class Tex extends Shader {
	static Texture spr = new Texture();
	
	public Tex() {
		//spr.LoadFromFile("./res/3D-Objects/rat/albedo.png");
		
		//spr.samplingMode = SamplingMode.Bilinear;
		//spr.LoadFromFile("./res/Textures/uvtest.png");
		spr.LoadFromFile("C:\\Users\\USER\\Downloads\\level\\High.png");
		//spr.LoadFromFile("C:\\Users\\USER\\Downloads\\sky\\Sky.png");
	}
	
	public Vector3 Fragment(Vertex in) {
		Vector2 coord = in.texcoord;
		Vector3 col = spr.SampleFast(coord.x, coord.y);
		
		return col;
	}
}

class depth extends Shader {

	@Override
	public Vector3 Fragment(Vertex in) {
		float w = 1.0f / in.position.w;
		float x = w / 5.0f;
		Vector3 c = Vector3.Lerp(Vector3.one(), Vector3.zero(), x);
		return c;
	}
	
}

class frag extends Shader {
	@Override
	public Vector3 Fragment(Vertex in) {
		return in.color;
	}
}

@SuppressWarnings("serial")
class demo3D extends Application {

	Pipeline pipeline;
	Camera cam;
	
	RenderObject cube;
	RenderObject cube2;
	@Override
	public void OnStart() {
		cam = new Camera();

		Mesh cubeMesh = Mesh.MakeQuad();
		cube = new RenderObject(cubeMesh);
		//cube.transform.rotation.y = 45;
		//cube.transform.scale = new Vector3(10.15f, 3.15f, 3.15f);
		cube.shader = new Tex();
		
		//Mesh cube2Mesh = ObjLoader.Load("./res/3D-Objects/book.obj");
		Mesh cube2Mesh = ObjLoader.Load("C:\\Users\\USER\\Downloads\\level\\level.obj");
		//Mesh cube2Mesh = ObjLoader.Load("C:\\Users\\USER\\Downloads\\sky\\skybox.obj");
		cube2 = new RenderObject(cube2Mesh);
		//cube2.transform.position.z = 5;
		cube2.shader = new Tex();
		//cube2.transform.scale = new Vector3(3, 3, 3);
		
		cam.transform.position.z = -3.5f;
		pipeline = new Pipeline(this, cam);
	}
	
	@Override
	public void OnUpdate() {
		//clear(Color.red);
		
		pipeline.clear();
		GetInput();
		//pipeline.DrawMesh(cube);
		pipeline.DrawMesh(cube2);
		//cube2.transform.rotation.y += getDeltaTime() * 80;
	}
	
	boolean confined = false;
	void GetInput() {
		float deltaTime = (float) getDeltaTime();
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.Shift)) {
			mul = 2;
		}
		float speed = 15 * mul;
		float lookSpeed = 100;
		Transform camTransform = cam.transform;
		
		if (Input.OnKeyHeld(Keycode.W)) {
			Vector3 forward = Vector3.mulVecFloat(camTransform.forward, speed*(float)deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			Vector3 forward = Vector3.mulVecFloat(camTransform.forward, -speed*(float)deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.A)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, -speed*(float)deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, right);
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, speed*(float)deltaTime);
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
			camTransform.rotation.y += Input.GetMouseDelta().x*100;
			camTransform.rotation.x += Input.GetMouseDelta().y*100;
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
		
		if (Input.OnKeyPressed(Keycode.F)) {
			switchFullscreen();
		}
	}
	
	public static void printbuffer(float[] buffer, int w, int h, String separator) {
		String s = "";
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				s += buffer[x + y*w] + separator;
			}
			s += "\n";
		}
		Debug.log(s);
	}
}

public class test3D {
	
	public static void main(String[] args) {
		demo3D d = new demo3D();
		//d.Initialize(144, 81, 6);
		d.Initialize(1280, 720, 1);
		//d.Initialize(32, 32, 1);
		//d.Initialize(720, 405, 1);
	}
}