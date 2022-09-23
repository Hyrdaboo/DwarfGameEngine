import java.awt.Color;

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
	
	private Matrix4x4 worldMatrix = Matrix4x4.identityMatrix();
	private Matrix4x4 projectionMatrix = new Matrix4x4();
	private Matrix4x4 translation = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationX = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationY = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationZ = Matrix4x4.identityMatrix(); 
	
	Mesh mesh;
	Camera camera;
	public void OnStart() {
		AppName = "new 3d";
		
		mesh = Mesh.MakeCube();
		InitMesh(mesh);
		
		camera = new Camera();
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
	
	Vector3 eulerAngles = new Vector3(0, 0, 0);
	public void OnUpdate() {
		clear(Color.black);
		eulerAngles.x += deltaTime*30;
		eulerAngles.y += deltaTime*35;
		eulerAngles.z += deltaTime*40;
		
		Vector2 windowSize = getWindowSize();
		float aspectRatio = windowSize.y/windowSize.x;
		
		Matrix4x4.ProjectionMatrix(camera.fov, aspectRatio, camera.near, camera.far, projectionMatrix);
		translation.makeTranslation(new Vector3(0, 0, 3));
		rotationX.rotateAroundX(eulerAngles.x * Mathf.Deg2Rad);
		rotationY.rotateAroundY(eulerAngles.y * Mathf.Deg2Rad);
		rotationZ.rotateAroundZ(eulerAngles.z * Mathf.Deg2Rad);
		
		worldMatrix = Matrix4x4.identityMatrix();
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationX);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationY);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationZ);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, translation);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, projectionMatrix);
		
		for (Triangle t : triangles) {
			Triangle projected = new Triangle();
			
			// calculate normal here
			
			for (int i = 0; i < 3; i++) {
				projected.points[i] = worldMatrix.MultiplyByVector(t.points[i]);
				projected.points[i] = ViewportPointToScreenPoint(projected.points[i]);
				
			}
			
			Draw2D.DrawTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
								new Vector2(projected.points[1].x, projected.points[1].y),
								new Vector2(projected.points[2].x, projected.points[2].y), Color.white);
		}
		
		
		if (Input.OnKeyHeld(Keycode.W)) camera.fov += .1f;
		if (Input.OnKeyHeld(Keycode.S)) camera.fov -= .1f;
	}
	
	public Vector3 ViewportPointToScreenPoint(Vector3 point) {
		float x = Mathf.InverseLerp(-1, 1, point.x);
		float y = Mathf.InverseLerp(1, -1, point.y);
		
		Vector2 windowSize = getWindowSize();
		point.x = x*windowSize.x; point.y = y*windowSize.y;
		return point;
	}
}

public class test3d {

	public static void main(String[] args) {
		demo3d demo = new demo3d();
		demo.SetResizable(true);
		demo.Construct(720, 405, 1);
	}

}
