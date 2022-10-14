import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import Renderer3D.Camera;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;
import Renderer3D.Transform;


class Triangle {
	public Vector3[] points;
	public Color color;
	
	public Triangle(Vector3 a, Vector3 b, Vector3 c) {
		points = new Vector3[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;		
	}
	
	public Triangle() {
		points = new Vector3[3];
		points[0] = new Vector3(0, 0, 0);
		points[1] = new Vector3(0, 0, 0);
		points[2] = new Vector3(0, 0, 0);	
	}
	
	public void print() {
		Debug.println("("+points[0].x+", "+points[0].y+", "+points[0].z+") "+ 
					  "("+points[1].x+", "+points[1].y+", "+points[1].z+") "+
					  "("+points[2].x+", "+points[2].y+", "+points[2].z+") ");
	}
}


@SuppressWarnings("serial")
class demo3d extends Application {

	private Matrix4x4 projectionMatrix = new Matrix4x4();
	private Transform objectTransform = new Transform();
	
	Mesh mesh;
	Camera camera;
	public void OnStart() {
		AppName = "new 3d";
		Input.setMouseCheckAccuracy(5);
		
		try {
			ObjLoader loader = new ObjLoader("./res/3D-Objects/monke.obj");
			mesh = loader.Load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//mesh = Mesh.MakeCube();
		InitMesh(mesh);
		
		camera = new Camera();
		camera.transform.position.z = -3;
	}
	
	private Triangle[] triangles;
	public void InitMesh(Mesh mesh) {
		if (mesh == null) {
			throw new NullPointerException("Provided mesh is null");
		}
		triangles = new Triangle[mesh.triangles.length/3];
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = new Triangle();
			t.points[0] = mesh.vertices[mesh.triangles[0+i*3]];
			t.points[1] = mesh.vertices[mesh.triangles[1+i*3]];
			t.points[2] = mesh.vertices[mesh.triangles[2+i*3]];
			
			triangles[i] = t;
		}
	}
	
	Vector3 lightDir = new Vector3(0, 0, 1);
	
	Color objectColor = new Color(102, 51, 47);
	Color ambientLight = new Color(.35f*1, .35f*1, .35f*1);
	boolean wireframe = false;
	boolean locked = false;
	public void OnUpdate() {
		clear(Color.black);
		
		float speed = 10;
		float lookSpeed = 100;
		Transform camTransform = camera.transform;
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
		
		if (Input.OnKeyPressed(Keycode.Escape)) {
			locked = !locked;
			Input.lockMouse(locked);
			Debug.println(locked);
		}
		
		// camera look with mouse
		Vector2 mouseDelta = Input.getMouseDelta();
		camTransform.rotation.y += deltaTime * lookSpeed * 60 * mouseDelta.x;
		camTransform.rotation.x += deltaTime * lookSpeed * 60 * mouseDelta.y;
		
		
		objectTransform.position.z = 2;
		objectTransform.rotation.y = 181;
		//objectTransform.rotation.x -= deltaTime * 50;
		//objectTransform.scale.x = 2;
		//objectTransform.rotation.y -= deltaTime * 50;
		//objectTransform.rotation.z -= deltaTime * 50;
		
		Vector2 windowSize = new Vector2(getWidth()/scaleX, getHeight()/scaleY);
		float aspectRatio = windowSize.y/windowSize.x;
		
		Matrix4x4.PerspectiveProjection(camera.fov, aspectRatio, camera.near, camera.far, projectionMatrix);
		Matrix4x4 tranformMatrix = objectTransform.getTransformMatrix();
		Matrix4x4 viewMatrix = camera.getViewMatrix();
		Matrix4x4 cameraObjectCombined = Matrix4x4.matrixMultiplyMatrix(tranformMatrix, viewMatrix);
		Matrix4x4 worldMatrix = Matrix4x4.matrixMultiplyMatrix(cameraObjectCombined, projectionMatrix);
		
		List<Triangle> trianglesToRaster = new ArrayList<Triangle>();
		for (Triangle t : triangles) {
			Triangle projected = new Triangle();
			Triangle transformed = new Triangle();
			
			for (int i = 0; i < 3; i++) {
				projected.points[i] = worldMatrix.MultiplyByVector(t.points[i]);
				transformed.points[i] = tranformMatrix.MultiplyByVector(t.points[i]);
			}
			
			Vector3 normal = surfaceNormalFromIndices(transformed.points[0], transformed.points[1], transformed.points[2]);
			Vector3 dirToCamera = Vector3.subtract2Vecs(camTransform.position, transformed.points[0]).normalized();
						
			// backface culling
			if (Vector3.Dot(normal, dirToCamera) < 0.0f) continue;
			
			float intensity = Vector3.Dot(normal, Vector3.mulVecFloat(lightDir.normalized(), -1));
			intensity = Mathf.Clamp(intensity, 0, 1);
			
			float r = intensity + ambientLight.getRed()/255.0f;
			float g = intensity + ambientLight.getBlue()/255.0f;
			float b = intensity + ambientLight.getGreen()/255.0f;
			
			r = (r * objectColor.getRed()) / 255.0f;
			g = (g * objectColor.getGreen()) / 255.0f;
			b = (b * objectColor.getBlue()) / 255.0f;
			
			r = Mathf.Clamp(r, 0, 1); g = Mathf.Clamp(g, 0, 1); b = Mathf.Clamp(b, 0, 1);
			
			Color faceColor = new Color(r, g, b);
			projected.color = faceColor;
			
			// convert to screen coordinates
			for (int i = 0; i < 3; i++) {
				projected.points[i].divideBy(projected.points[i].w);
				projected.points[i] = ViewportPointToScreenPoint(projected.points[i]);
			}
			
			trianglesToRaster.add(projected);
		}
		
		trianglesToRaster.sort(new Comparator<Triangle>() {
			@Override
			public int compare(Triangle t1, Triangle t2) {
				float z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0f;
				float z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0f;
				if (z1 == z2) return 0;
				else if (z1 > z2) return -1;
				else return 1;
			}
			
		});
		
		for (Triangle projected : trianglesToRaster) {
			if (!wireframe) {
				Draw2D.FillTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
						new Vector2(projected.points[1].x, projected.points[1].y),
						new Vector2(projected.points[2].x, projected.points[2].y), projected.color); 
				continue;
			}
			Draw2D.DrawTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
					new Vector2(projected.points[1].x, projected.points[1].y),
					new Vector2(projected.points[2].x, projected.points[2].y), Color.gray);
		}
	}
	
	public Vector3 ViewportPointToScreenPoint(Vector3 point) {
		float x = Mathf.InverseLerp(-1, 1, point.x);
		float y = Mathf.InverseLerp(1, -1, point.y);
		
		Vector2 windowSize = getWindowSize();
		point.x = x*windowSize.x; point.y = y*windowSize.y;
		return point;
	}
	
	public Vector3 surfaceNormalFromIndices(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 sideAB = Vector3.subtract2Vecs(b, a);
		Vector3 sideAC = Vector3.subtract2Vecs(c, a);
		
		return Vector3.Cross(sideAB, sideAC).normalized();
	}
}

public class test3d {

	public static void main(String[] args) {
		demo3d demo = new demo3d();
		demo.SetResizable(true);
		demo.Construct(720, 405, 1);
	}

}
