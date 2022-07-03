import DwarfEngine.*;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.Renderer3D.Camera;
import DwarfEngine.Renderer3D.Mesh;
import DwarfEngine.Renderer3D.MeshRenderer;
import DwarfEngine.Renderer3D.ObjLoader;
import DwarfEngine.Renderer3D.MeshRenderer.renderMode;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import DwarfEngine.SimpleGraphics2D.Sprite;
import DwarfEngine.SimpleGraphics2D.SpriteSheet;

import java.awt.Color;

class Game extends Engine {
	private static final long serialVersionUID = 1L;
	
	private Mesh mesh;
	private MeshRenderer mr;
	Camera camera;
	SpriteSheet s;
	public void OnStart() {
		
        System.out.println("Game started");
        setIcon("/Textures/grass-side.png");
        AppName = "My stupid engine which refuses to work";
        mesh = new ObjLoader("./res/icosphere.obj").loadMesh();
        camera = new Camera();
        mr = new MeshRenderer(this, camera);
        mr.mesh = mesh;
        mr.position = new Vector3(0, 0, 10);
    }                     
	
	public void OnUpdate() {
    	clear(Color.black);
    	InWasd(-20f);
		mr.Render();
	}
	
	void InWasd(float speed) {
		if (Input.OnKeyHeld(Keycode.Space))  {
			mr.rotation.z += deltaTime;
			mr.rotation.x += deltaTime;
		}
		Vector3 forw = Vector3.mulVecFloat(camera.lookDir, (float) (speed * deltaTime));
		if (Input.OnKeyHeld(Keycode.W)) {
			camera.position.subtractFrom(forw);
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			camera.position.addTo(forw);
		}
		if (Input.OnKeyHeld(Keycode.A)) {
			camera.rotation.y += speed/40 * deltaTime;
			//mr.cam.x += speed * deltaTime;
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			camera.rotation.y -= speed/40 * deltaTime;
			//mr.cam.x -= speed * deltaTime;
		}
		
		if (Input.OnKeyHeld(Keycode.UpArrow)) {
			camera.position.y -= speed * deltaTime;
		}
		if (Input.OnKeyHeld(Keycode.DownArrow)) {
			camera.position.y += speed * deltaTime;
		}
		if (Input.OnKeyHeld(Keycode.LeftArrow)) {
			camera.position.x -= speed * deltaTime;
		}
		if (Input.OnKeyHeld(Keycode.RightArrow)) {
			camera.position.x += speed * deltaTime;
		}
		if (Input.OnKeyPressed(Keycode.F3)) {
			if (MeshRenderer.RENDER_MODE == renderMode.shaded) {
				MeshRenderer.RENDER_MODE = renderMode.shadedWireframe;
			}
			else MeshRenderer.RENDER_MODE = renderMode.shaded;
		}
	}
	
	// call this to make a cube
}

public class Main {
    public static void main(String[] args) {
    	int resolution = 360;
    	int pix = 1;
        Game game = new Game();
        game.SetResizable(true);
        //game.SetFullscreen();
        game.Construct(resolution, resolution/16*9, pix);
	}
}