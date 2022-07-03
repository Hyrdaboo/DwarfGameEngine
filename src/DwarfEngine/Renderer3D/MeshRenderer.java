package DwarfEngine.Renderer3D;

import java.awt.Color;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DwarfEngine.Engine;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import DwarfEngine.SimpleGraphics2D.Sprite;

public class MeshRenderer {
	public enum renderMode {shaded, wireframe, shadedWireframe}
	class Triangle {
		public Vector3[] point;
		public Color color;
		
		public Triangle(Vector3 a, Vector3 b, Vector3 c) {
			point = new Vector3[3];
			point[0] = a;
			point[1] = b;
			point[2] = c;		
		}
		
		public Triangle() {
			point = new Vector3[3];
			point[0] = new Vector3(0, 0, 0);
			point[1] = new Vector3(0, 0, 0);
			point[2] = new Vector3(0, 0, 0);	
		}
		
		public void print() {
			Engine.PrintLn((point[0].z + point[1].z + point[2].z) / 3);
		}
	}
	
	public Mesh mesh = null;
	public Vector3 position = new Vector3(0, 0, 0);
	public Vector3 rotation = new Vector3(0, 0, 0);
	public Camera camera = null;
	public static renderMode RENDER_MODE = renderMode.shaded;
	
	private Matrix4x4 projectionMatrix;
	private Matrix4x4 translation;
	private Matrix4x4 rotX;
	private Matrix4x4 rotY;
	private Matrix4x4 rotZ;
	private Matrix4x4 worldMatrix;
	
	float w = 0;
	float h = 0;
	
	private Engine engineInstance;
	public MeshRenderer(Engine engine, Camera camera) {
		engineInstance = engine;
		this.camera = camera;
		w = engine.getWindowSize().x;
		h = engine.getWindowSize().y;
		
		Engine.PrintLn(w);
		float fAspectRatio = (float)h / (float)w;
		float fFovRad =  1.0f / (float)Math.tan(camera.fov * 0.5f * Mathf.Deg2Rad);
        
		// projection matrix
		projectionMatrix = Matrix4x4.ProjectionMatrix(fFovRad, fAspectRatio, camera.near, camera.far);
		
		rotX = new Matrix4x4();
		rotY = new Matrix4x4();
		rotZ = new Matrix4x4();
		translation = new Matrix4x4();
	}
	
	public void Render() {
		w = engineInstance.getWindowSize().x;
		h = engineInstance.getWindowSize().y;
		//Engine.PrintLn(w + ", " + h);
		
		rotX.rotateAroundX(rotation.x);
    	rotY.rotateAroundY(rotation.y);
    	rotZ.rotateAroundZ(rotation.z);
		translation.makeTranslation(position);   
		
		worldMatrix = Matrix4x4.identityMatrix();
    	worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotX);
    	worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotY);
    	worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, rotZ);
    	worldMatrix = Matrix4x4.matrixMultiplyMatrix(worldMatrix, translation);
    	
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 target = new Vector3(0, 0, 1);
        Matrix4x4 matCamRot = new Matrix4x4();
        matCamRot.rotateAroundX(camera.rotation.x);
        matCamRot.rotateAroundY(camera.rotation.y);
        matCamRot.rotateAroundZ(camera.rotation.z);
        camera.lookDir = matCamRot.MultiplyByVector(target);
        target = Vector3.add2Vecs(camera.position, camera.lookDir);
        
        Matrix4x4 cameraMat = Matrix4x4.MatrixPointAt(camera.position, target, up);
        Matrix4x4 matView = Matrix4x4.inverseMatrix(cameraMat);
       
    	List<Triangle> trianglesToRaster = new ArrayList<Triangle>();
    	for (int i = 0; i < mesh.triangles.length; i += 3) {  		
    		Triangle transformed = new Triangle(worldMatrix.MultiplyByVector(mesh.vertices[mesh.triangles[i]]), 
    											worldMatrix.MultiplyByVector(mesh.vertices[mesh.triangles[i+1]]), 
    											worldMatrix.MultiplyByVector(mesh.vertices[mesh.triangles[i+2]]));
    		
    		
    		Vector3 normal = SurfaceNormalFromIndices(transformed.point[0], 
    												  transformed.point[1], 
    												  transformed.point[2]);
    		    		
    		
    		if(Vector3.Dot(normal, Vector3.subtract2Vecs(transformed.point[0], camera.position)) < 0.0f)
    		{ 
    			Vector3 light = new Vector3(0, 0, -1);
        		light.Normalize();
        		float intensity = Vector3.Dot(normal, light);
        		intensity = Mathf.Clamp(intensity, 0, 1);
        		Color c = new Color(intensity, intensity, intensity);
        		Triangle viewed = new Triangle(matView.MultiplyByVector(transformed.point[0]), 
        									   matView.MultiplyByVector(transformed.point[1]),
        									   matView.MultiplyByVector(transformed.point[2]));
        		
        		Triangle projected = new Triangle(projectionMatrix.MultiplyByVector(viewed.point[0]), 
        										  projectionMatrix.MultiplyByVector(viewed.point[1]), 
        										  projectionMatrix.MultiplyByVector(viewed.point[2])); 
    	
        		projected.color = c;
        		Vector3 offsetView = new Vector3(1, 1, 0);
        		projected.point[0].addTo(offsetView);
        		projected.point[1].addTo(offsetView);
        		projected.point[2].addTo(offsetView);
    		
        		projected.point[0].x *= 0.5f * w;
        		projected.point[0].y *= 0.5f * h;
    			projected.point[1].x *= 0.5f * w;
    			projected.point[1].y *= 0.5f * h;
    			projected.point[2].x *= 0.5f * w;
    			projected.point[2].y *= 0.5f * h;
    		
    			
    		    trianglesToRaster.add(projected);
    		}
    	}
    	
    	// sort triangles that are behind
    	trianglesToRaster.sort(new Comparator<Triangle>() {

			public int compare(Triangle t1, Triangle t2) {
				float z1 = (t1.point[0].z + t1.point[1].z + t1.point[2].z) / 3.0f;
				float z2 = (t2.point[0].z + t2.point[1].z + t2.point[2].z) / 3.0f;
				if (z1 == z2) return 0;
				else if (z1 > z2) return -1;
				else return 1;
			}
		});
    	
    	for (Triangle projected : trianglesToRaster) {
    		switch (RENDER_MODE) {
			case shaded:
				Draw2D.FillTriangle(new Vector2(projected.point[0].x, projected.point[0].y), 
									new Vector2(projected.point[1].x, projected.point[1].y), 
									new Vector2(projected.point[2].x, projected.point[2].y), projected.color);
				break;
			case wireframe:
				Draw2D.DrawTriangle(new Vector2(projected.point[0].x, projected.point[0].y), 
									new Vector2(projected.point[1].x, projected.point[1].y), 
									new Vector2(projected.point[2].x, projected.point[2].y), Color.lightGray);
				break;
				case shadedWireframe:
				Draw2D.FillTriangle(new Vector2(projected.point[0].x, projected.point[0].y), 
							new Vector2(projected.point[1].x, projected.point[1].y), 
							new Vector2(projected.point[2].x, projected.point[2].y), projected.color);
				Draw2D.DrawTriangle(new Vector2(projected.point[0].x, projected.point[0].y), 
						new Vector2(projected.point[1].x, projected.point[1].y), 
						new Vector2(projected.point[2].x, projected.point[2].y), Color.lightGray);
				break;
			default:
				break;
			}
    	}
	}
	
	private Vector3 SurfaceNormalFromIndices(Vector3 pointA, Vector3 pointB, Vector3 pointC) {
		
		Vector3 sideAB = Vector3.subtract2Vecs(pointB, pointA);
		Vector3 sideAC = Vector3.subtract2Vecs(pointC, pointA);
		
		return Vector3.Cross(sideAB, sideAC).normalized();
	}
}
