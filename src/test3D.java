import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;

import javax.swing.plaf.basic.BasicPanelUI;

import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.Sprite.SamplingMode;
import DwarfEngine.Sprite.WrapMode;

import static DwarfEngine.Core.DisplayRenderer.*;
import Renderer3D.Camera;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Pipeline;
import Renderer3D.RenderObject;
import Renderer3D.Shader;
import Renderer3D.Transform;
import Renderer3D.Vertex;

class saul implements Shader {
	static Sprite spr = new Sprite();
	
	public saul() {
		spr.LoadFromFile("C:\\Users\\USER\\Downloads\\source\\1911tex_2.png");
	}
	
	public Color Fragment(Vertex in) {
		Vector2 coord = in.texcoord;
		Color col = spr.SampleColor(coord.x, coord.y);
		
		return col;
	}
}

class depth implements Shader {

	@Override
	public Color Fragment(Vertex in) {
		float w = 1.0f / in.position.w;
		float x = w / 10.0f;
		Color c = Mathf.LerpColor(Color.white, Color.black, x);
		return c;
	}
	
}

class frag implements Shader {
	@Override
	public Color Fragment(Vertex in) {
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

		Mesh cubeMesh = Mesh.MakeCube();
		cube = new RenderObject(cubeMesh);
		//cube.transform.rotation.y = 45;
		//cube.transform.scale = new Vector3(10.15f, 3.15f, 3.15f);
		cube.shader = new saul();
		
		Mesh cube2Mesh = monke();
		cube2 = new RenderObject(cube2Mesh);
		//cube2.transform.position.z = 5;
		cube2.shader = new saul();
		
		cam.transform.position.z = -3.5f;
		cam.SetFar(10);
		cam.SetNear(1);
		//cam.transform.position.y = 1;
		pipeline = new Pipeline(this, cam);
		//pipeline.drawFlag = DrawFlag.wireframe;
	}
	
	Mesh monke() {
		Mesh mesh = null;
		try {
			//mesh = ObjLoader.Load("C:\\Users\\USER\\Downloads\\source\\1911.obj");
			mesh = ObjLoader.Load("C:\\Users\\USER\\OneDrive\\Desktop\\testcube.obj");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mesh;
	}
	
	@Override
	public void OnUpdate() {
		//clear(Color.red);
		
		pipeline.clear();
		GetInput();
		//pipeline.DrawMesh(cube);
		pipeline.DrawMesh(cube2);
	}
	
	boolean confined = false;
	void GetInput() {
		float deltaTime = (float) getDeltaTime();
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.Shift)) {
			mul = 5;
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
			camTransform.rotation.y += Input.GetMouseDelta().x*50;
			camTransform.rotation.x += Input.GetMouseDelta().y*50;
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
	
}

public class test3D {
	public static void main(String[] args) {
		demo3D d = new demo3D();
		//d.Initialize(144, 81, 6);
		d.Initialize(1280, 720, 1);
		//d.Initialize(720, 405, 1);
	}
}