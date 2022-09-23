package SimpleRaytracing;

import java.awt.Color;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;

@SuppressWarnings("serial")
class Raytracer extends Application {
	
	private Vector2 screenSize;
	private R_Camera camera;
	public void OnStart() {
		AppName = "Raytracing Demo";
		screenSize = getWindowSize();
		camera = new R_Camera();
		camera.fov = 60;
	}
	
	float speed = 20;
	public void OnUpdate() {
		if (Input.OnKeyHeld(Keycode.W)) camera.position.z += speed * deltaTime;
		if (Input.OnKeyHeld(Keycode.S)) camera.position.z -= speed * deltaTime;
		if (Input.OnKeyHeld(Keycode.A)) camera.position.x += speed * deltaTime;
		if (Input.OnKeyHeld(Keycode.D)) camera.position.x -= speed * deltaTime;
		//Debug.println(camera.fov);
		drawScene();
		
		if (Input.OnKeyPressed(Keycode.F1)) {
			saveImage("C:\\Users\\USER\\OneDrive\\Desktop");
		}
	}
	
	
	private void drawScene() {
		if (camera == null) return;
		for (int y = 0; y < screenSize.y; y++) {
			for (int x = 0; x < screenSize.x; x++) {
				float sharedAngleXZ = camera.fov / (screenSize.x - 1);
                float startAngleXZ = (screenSize.x / 2) * sharedAngleXZ * -1;
                float sharedAngleYZ = camera.fov / (screenSize.y - 1);
                float startAngleYZ = (screenSize.y / 2) * sharedAngleYZ * -1;
                
                Vector2 dirXZ = dirFromAngle(startAngleXZ + x * sharedAngleXZ);
                Vector2 dirYZ = dirFromAngle(startAngleYZ + y * sharedAngleYZ);
                Vector3 dir = new Vector3(dirXZ.y, dirYZ.y, (dirXZ.x+dirYZ.x)/2);
                
                RaycastHit hit = new RaycastHit();
                float spehereZ = 30;
                float radius = 5;
                if (Raycaster.Raycast(camera.position, dir, hit, new Vector3(0, 0, spehereZ), radius)) {
                	
                	Color c = Color.magenta;
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
		raytracer.Construct(480, 480, 1);
	}

}
