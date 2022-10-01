import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import Renderer3D.Camera;
import Renderer3D.Mesh;
import Renderer3D.ObjLoader;


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
	private Matrix4x4 tranformMatrix = Matrix4x4.identityMatrix();
	private Matrix4x4 projectionMatrix = new Matrix4x4();
	private Matrix4x4 translation = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationX = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationY = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationZ = Matrix4x4.identityMatrix(); 
	private Matrix4x4 scaleMatrix = Matrix4x4.identityMatrix();
	
	Mesh mesh;
	Camera camera;
	public void OnStart() {
		AppName = "new 3d";
		
		try {
			ObjLoader loader = new ObjLoader("./res/3D-Objects/monke.obj");
			mesh = loader.Load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//mesh = Mesh.MakeCube();
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
	
	Vector3 camPos = new Vector3(0, 0, 0);
	Vector3 lightDir = new Vector3(0, 0, 1);
	Vector3 position = new Vector3(0, 0, 2.5f);
	Vector3 eulerAngles = new Vector3(1, 215*0, 0);
	Vector3 scale = new Vector3(1, 1, 1);
	
	Color objectColor = new Color(102, 51, 47);
	Color ambientLight = new Color(.25f*1, .25f*1, .25f*1);
	public void OnUpdate() {
		clear(Color.black);
		/*for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				Draw2D.SetPixel(x, y, new Color((float)x/getWidth(), (float)y/getHeight(), 1));
			}
		}*/
		//eulerAngles.x += deltaTime*30;
		if (Input.OnKeyHeld(Keycode.Space)) eulerAngles.y += deltaTime*45;
		//eulerAngles.z += deltaTime*35;
		
		Vector2 windowSize = new Vector2(getWidth()/scaleX, getHeight()/scaleY);
		float aspectRatio = windowSize.y/windowSize.x;
		
		Matrix4x4.ProjectionMatrix(camera.fov, aspectRatio, camera.near, camera.far, projectionMatrix);
		translation.makeTranslation(position);
		rotationX.xRotation(eulerAngles.x * Mathf.Deg2Rad);
		rotationY.yRotation(eulerAngles.y * Mathf.Deg2Rad);
		rotationZ.zRotation(eulerAngles.z * Mathf.Deg2Rad);
		scaleMatrix.scaleMatrix(scale);
		
		worldMatrix = Matrix4x4.identityMatrix();
		tranformMatrix = Matrix4x4.identityMatrix();
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, scaleMatrix);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationX);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationY);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotationZ);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, translation);
		tranformMatrix = Matrix4x4.matrixMultiplyMatrix(tranformMatrix, worldMatrix);
		worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, projectionMatrix);
		
		List<Triangle> trianglesToRaster = new ArrayList<Triangle>();
		for (Triangle t : triangles) {
			Triangle projected = new Triangle();
			Triangle transformed = new Triangle();
			
			for (int i = 0; i < 3; i++) {
				projected.points[i] = worldMatrix.MultiplyByVector(t.points[i]);
				transformed.points[i] = tranformMatrix.MultiplyByVector(t.points[i]);
			}
			
			Vector3 normal = surfaceNormalFromIndices(transformed.points[0], transformed.points[1], transformed.points[2]);
			Vector3 dirToCamera = Vector3.subtract2Vecs(camPos, transformed.points[0]).normalized();
			
			// back culling
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
			
			// convert to screen point
			for (int i = 0; i < 3; i++) {
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
			Draw2D.FillTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
					new Vector2(projected.points[1].x, projected.points[1].y),
					new Vector2(projected.points[2].x, projected.points[2].y), projected.color);
			/*Draw2D.DrawTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
					new Vector2(projected.points[1].x, projected.points[1].y),
					new Vector2(projected.points[2].x, projected.points[2].y), Color.gray);*/
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
