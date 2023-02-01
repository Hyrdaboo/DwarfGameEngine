import java.awt.Color;

import DwarfEngine.Application;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Vector3;
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
	RenderObject monke;
	@Override
	public void OnStart() {
		cam = new Camera();
		
		Mesh cubeMesh = Mesh.MakeCube();
		cube = new RenderObject(cubeMesh);
		
		try {
			Mesh monkeMesh = new ObjLoader("./res/3D-Objects/teapot.obj").Load();
			monke = new RenderObject(monkeMesh);
			monke.transform.position.z = 3;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cam.transform.position.z = -1;
		cam.transform.rotation.y = 45;
		cam.transform.position.x = -1;
		//cam.transform.position.y = 1;
		pipeline = new Pipeline(this, cam);
		//pipeline.drawFlag = DrawFlag.wireframe;
		
	}

	@Override
	public void OnUpdate() {
		clear(Color.black);
		GetInput();
		
		pipeline.DrawMesh(monke);
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
		d.Construct(1280, 720, 1);
	}
}
