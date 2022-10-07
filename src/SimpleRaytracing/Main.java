package SimpleRaytracing;

import java.awt.Color;

import DwarfEngine.Application;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;

@SuppressWarnings("serial")
class Raytracer extends Application {
	
	private Matrix4x4 camRotation;
	
	private Vector2 screenSize;
	private R_Camera camera;
	public void OnStart() {
		AppName = "Raytracing Demo";
		camera = new R_Camera();
		camera.fov = 60;
		screenSize = new Vector2(getWidth()/scaleX, getHeight()/scaleY);
		
		Matrix4x4 rot = Matrix4x4.GetRotation(new Vector3(-15,45,0));
		lightDir = rot.MultiplyByVector(Vector3.forward());
	}
	
	float speed = 20;
	public void OnUpdate() {
		float lookSpeed = 50;
		
		camera.transform.getTransformMatrix();
		if (Input.OnKeyHeld(Keycode.W)) {
			Vector3 forward = Vector3.mulVecFloat(camera.transform.forward, speed*(float)deltaTime);
			camera.transform.position = Vector3.add2Vecs(camera.transform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			Vector3 forward = Vector3.mulVecFloat(camera.transform.forward, -speed*(float)deltaTime);
			camera.transform.position = Vector3.add2Vecs(camera.transform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.A)) {
			Vector3 right = Vector3.mulVecFloat(camera.transform.right, -speed*(float)deltaTime);
			camera.transform.position = Vector3.add2Vecs(camera.transform.position, right);
			
			//camPos.x -= speed * deltaTime; 
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(camera.transform.right, speed*(float)deltaTime);
			camera.transform.position = Vector3.add2Vecs(camera.transform.position, right);
			
			//camPos.x += speed * deltaTime;
		}
		
		if (Input.OnKeyHeld(Keycode.Q)) {
			camera.transform.position.y += deltaTime * speed;
		}
		if (Input.OnKeyHeld(Keycode.E)) {
			camera.transform.position.y -= deltaTime * speed;
		}
		// rotate camera
		if (Input.OnKeyHeld(Keycode.I)) {
			camera.transform.rotation.x += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.J)) {
			camera.transform.rotation.y -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.K)) {
			camera.transform.rotation.x -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.L)) {
			camera.transform.rotation.y += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.U)) {
			camera.transform.rotation.z += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.O)) {
			camera.transform.rotation.z -= deltaTime * lookSpeed;
		}
		camRotation = Matrix4x4.GetRotation(camera.transform.rotation);
		
		//Debug.println(camera.fov);
		drawScene();
		
		if (Input.OnKeyPressed(Keycode.F1)) {
			saveImage("C:\\Users\\USER\\OneDrive\\Desktop", "Sphere");
		}
	}
	
	Vector3 lightDir = new Vector3(0, 0, 1);
	Vector3 lightRotation = Vector3.zero();
	Matrix4x4 lightRot = Matrix4x4.identityMatrix();
	Color ambient = new Color(.12f, .12f, .12f);
	Color objectColor = Color.magenta;
	private void drawScene() {
		if (camera == null) return;
		lightRot = Matrix4x4.GetRotation(lightRotation);
		lightDir = lightRot.MultiplyByVector(Vector3.forward());
		
		if (Input.OnKeyHeld(Keycode.LeftArrow)) {
			lightRotation.y += deltaTime * 80;
		}
		if (Input.OnKeyHeld(Keycode.RightArrow)) {
			lightRotation.y -= deltaTime * 80;
		}
		if (Input.OnKeyHeld(Keycode.DownArrow)) {
			lightRotation.x += deltaTime * 80;
		}
		if (Input.OnKeyHeld(Keycode.UpArrow)) {
			lightRotation.x -= deltaTime * 80;
		}
		
		for (int y = 0; y < screenSize.y; y++) {
			for (int x = 0; x < screenSize.x; x++) {
				float sharedAngleXZ = camera.fov / (screenSize.x - 1);
                float startAngleXZ = (screenSize.x / 2) * sharedAngleXZ * -1;
                float sharedAngleYZ = camera.fov / (screenSize.y - 1);
                float startAngleYZ = (screenSize.y / 2) * sharedAngleYZ * -1;
                
                Vector2 dirXZ = dirFromAngle(startAngleXZ + x * sharedAngleXZ);
                Vector2 dirYZ = dirFromAngle(startAngleYZ + y * sharedAngleYZ);
                Vector3 dir = new Vector3(dirXZ.y, dirYZ.y, (dirXZ.x+dirYZ.x)/2);
                dir = camRotation.MultiplyByVector(dir);
                
                RaycastHit hit = new RaycastHit();
                float spehereZ = 30;
                float radius = 5;
                if (Raycaster.Raycast(camera.transform.position, dir, hit, new Vector3(0, 0, spehereZ), radius)) {
                	float intensity = Vector3.Dot(hit.normal, Vector3.mulVecFloat(lightDir, -1));
                	intensity = Mathf.Clamp(intensity, 0, 1);
        			
        			float r = intensity + ambient.getRed()/255.0f;
        			float g = intensity + ambient.getBlue()/255.0f;
        			float b = intensity + ambient.getGreen()/255.0f;
        			
        			r = (r * objectColor.getRed()) / 255.0f;
        			g = (g * objectColor.getGreen()) / 255.0f;
        			b = (b * objectColor.getBlue()) / 255.0f;
        			
        			r = Mathf.Clamp(r, 0, 1); g = Mathf.Clamp(g, 0, 1); b = Mathf.Clamp(b, 0, 1);
                	
                	Color c = new Color(r, g, b);
                	
                	Draw2D.SetPixel(x, y, c);
                }
                //else Draw2D.SetPixel(x, y, new Color(x/screenSize.x, y/screenSize.y, 1));
                else Draw2D.SetPixel(x, y, Color.black);
			}
		}
	}
	private Vector2 dirFromAngle(float angleDegrees) {
		Vector2 dir = new Vector2(Mathf.cos(angleDegrees*Mathf.Deg2Rad), Mathf.sin(angleDegrees*Mathf.Deg2Rad));
        return dir;
	}
}

public class Main {

	public static void main(String[] args) {
		Raytracer raytracer = new Raytracer();
		raytracer.SetResizable(true);
		raytracer.Construct(500, 500, 1);
	}

}
