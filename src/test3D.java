import java.awt.Color;
import java.awt.image.BufferedImage;

import DwarfEngine.Core.Application;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Vector3;
import static DwarfEngine.Core.DisplayRenderer.*;
import Renderer3D.Camera;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Pipeline;
import Renderer3D.RenderObject;
import Renderer3D.Transform;


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
		
		Mesh cube2Mesh = monke();
		cube2 = new RenderObject(cube2Mesh);
		//cube2.transform.position.z = 5;
		
		cam.transform.position.z = -3.0f;
		//cam.transform.position.y = 1;
		pipeline = new Pipeline(this, cam);
		//pipeline.drawFlag = DrawFlag.wireframe;	
	}
	
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
	
	@Override
	public void OnUpdate() {
		clear(Color.black);
		
		pipeline.clearDepth();
		GetInput();
		//pipeline.DrawMesh(cube2);
		pipeline.DrawMesh(cube);
	}
	
	boolean confined = false;
	void switchCursor() {
		if (confined) {
			SetCursorImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
		}
		else {
			SetCursorImage(null);
		}
	}
	void GetInput() {
		float deltaTime = (float) getDeltaTime();
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
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(camTransform.right, speed*(float)deltaTime);
			camTransform.position = Vector3.add2Vecs(camTransform.position, right);
		}
		
		if (Input.OnKeyHeld(Keycode.Q)) {
			camTransform.position.y += deltaTime * speed;
		}
		if (Input.OnKeyHeld(Keycode.E)) {
			camTransform.position.y -= deltaTime * speed;
		}
		
		if (Input.OnKeyPressed(Keycode.Escape)) {
			confined = !confined;
			Input.setMouseConfined(confined);
			switchCursor();
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
	}
	
}

public class test3D {
	public static void main(String[] args) {
		demo3D d = new demo3D();
		//d.Construct(144, 81, 6);
		d.Initialize(1280, 720, 1);
	}
}