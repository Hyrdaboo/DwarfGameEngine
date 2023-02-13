import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import DwarfEngine.SimpleGraphics2D.Sprite;
import Renderer3D.Camera;
import Renderer3D.DwarfShader;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Pipeline;
import Renderer3D.RenderObject;
import Renderer3D.Transform;

class myShader implements DwarfShader {
	public Color Fragment() {
		return Color.gray;
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
		cube.shader = new myShader();
		//cube.transform.rotation.y = 45;
		//cube.transform.scale = new Vector3(10.15f, 3.15f, 3.15f);
		
		Mesh cube2Mesh = monke();
		cube2 = new RenderObject(cube2Mesh);
		//cube2.transform.position.z = 5;
		
		cam.transform.position.z = -3.0f;
		//cam.transform.position.y = 1;
		pipeline = new Pipeline(this, cam);
		//pipeline.drawFlag = DrawFlag.wireframe;
		
		//spr = new Sprite("/Textures/uvtest.png");
	}
	Sprite spr;
	
	Mesh monke() {
		Mesh mesh = null;
		try {
			mesh = new ObjLoader("./res/3D-Objects/monke.obj").Load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mesh;
	}
	
	Vector3 A = new Vector3(200, 0, 0);
	Vector3 B = new Vector3(200,720,0);
	Vector3 C = new Vector3(1080,720,0);
	
	/*
	Vector3 A = new Vector3(144/2, 0, 0);
	Vector3 B = new Vector3(20,81,0);
	Vector3 C = new Vector3(124,81,0);
	*/
	
	Vector2 a = new Vector2(0.7f, 0.1f);
	Vector2 b = new Vector2(0.1f, 0.1f);
	Vector2 c = new Vector2(0.7f, 0.5f);
	
	@Override
	public void OnUpdate() {
		clear(Color.black);
		
		pipeline.clearDepth();
		GetInput();
		//pipeline.DrawMesh(cube2);
		pipeline.DrawMesh(cube);
		//pipeline.drawDepth();
		//cube.transform.rotation.x += deltaTime * 30;
		//cube.transform.rotation.y += deltaTime * 30;
		//cube.transform.rotation.z += deltaTime * 30;
		
		if (Input.OnKeyHeld(Keycode.A)) {
			A.x -= deltaTime * 200;
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			A.x += deltaTime * 200;
		}
		if (Input.OnKeyHeld(Keycode.W)) {
			A.y -= deltaTime * 200;
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			A.y += deltaTime * 200;
		}
		
		//DrawFlatBottomTriangle(A, B, C, a, b, c);
	}
	
	private void DrawFlatBottomTriangle(Vector3 v1, Vector3 v2, Vector3 v3,
										Vector2 t1, Vector2 t2, Vector2 t3) 
	{
		float slope1 = (v2.x - v1.x) / (v2.y - v1.y);
		float slope2 = (v3.x - v1.x) / (v3.y - v1.y);
		
		int startY = (int) Mathf.ceil(v1.y - 0.5f);
		int endY = (int) Mathf.ceil(v3.y - 0.5f);
		
		for (int y = startY; y < endY; y++) {
			
			float px1 = slope1 * ((float)y + 0.5f - v1.y) + v1.x;
			float px2 = slope2 * ((float)y + 0.5f - v1.y) + v1.x;
	
			float yi = Mathf.Clamp01(Mathf.InverseLerp(startY, endY, y));
			
			int startX = (int) (px1 - 0.5f);
			int endX = (int) Mathf.ceil(px2 - 0.5f);
			
			for (int x = startX; x < endX; x++) {
				float xi = Mathf.Clamp01(Mathf.InverseLerp(startX, endX, x));
				
				Vector2 line1 = Vector2.Lerp(t1, t2, yi);
				Vector2 line2 = Vector2.Lerp(t1, t3, yi);
				Vector2 scanline = Vector2.Lerp(line1, line2, xi);
				//tc = Vector2.Lerp(t1, tc, yi);
				Color c = spr.SampleColor(scanline.x, scanline.y);
				
				Draw2D.SetPixel(x, y, c);
			}
		}
	}
	
	void GetInput() {
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.LeftShift)) {
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
			
			//camPos.x -= speed * deltaTime; 
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, speed*(float)deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, right);
			
			//camPos.x += speed * deltaTime;
		}
		
		if (Input.OnKeyHeld(Keycode.Q)) {
			camTransform.position.y += deltaTime * speed;
		}
		if (Input.OnKeyHeld(Keycode.E)) {
			camTransform.position.y -= deltaTime * speed;
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

public class test3D {
	public static void main(String[] args) {
		demo3D d = new demo3D();
		d.SetResizable(true);
		//d.Construct(144, 81, 6);
		d.Construct(1280, 720, 1);
	}
}
