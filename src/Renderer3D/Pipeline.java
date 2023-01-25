package Renderer3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


import DwarfEngine.Application;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.SimpleGraphics2D.Draw2D;


public final class Pipeline {

	public enum DrawFlag { shaded, wireframe };
	public DrawFlag drawFlag = DrawFlag.shaded;
	
	private Application application;
	private Camera camera;
	private Mesh mesh;
	
	private Matrix4x4 projectionMatrix;
	private Transform objectTransform;
	
	private Triangle[] triangles;
	
	public Pipeline(Application application, Camera camera, Mesh mesh, Transform objectTransform) {
		this.application = application;
		this.camera = camera;
		this.mesh = mesh;
		this.objectTransform = objectTransform;
		
		projectionMatrix = new Matrix4x4();
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
	}
	
	public void ProcessMesh() {
		Vector2 windowSize = new Vector2(application.getWidth()/application.scaleX, application.getHeight()/application.scaleY);
		float aspectRatio = windowSize.y / windowSize.x;
		
		Matrix4x4.PerspectiveProjection(camera.fov, aspectRatio, camera.near, camera.far, projectionMatrix);
		Matrix4x4 transformMatrix = objectTransform.getTransformMatrix();
		Matrix4x4 viewMatrix = camera.getViewMatrix();
		Matrix4x4 cameraObjectCombined = Matrix4x4.matrixMultiplyMatrix(transformMatrix, viewMatrix);
		
		List<Triangle> trianglesToRaster = new ArrayList<Triangle>();
		for (Triangle t : triangles) {
			Triangle fullyTransformed = new Triangle();
			Triangle transformed = new Triangle();
			
			for (int i = 0; i < 3; i++) {
				transformed.points[i] = transformMatrix.MultiplyByVector(t.points[i]);
				fullyTransformed.points[i] = cameraObjectCombined.MultiplyByVector(t.points[i]);
			}
			
			Vector3 faceNormal = surfaceNormalFromIndices(transformed.points[0], transformed.points[1], transformed.points[2]);
			Vector3 dirToCamera = Vector3.subtract2Vecs(camera.transform.position, transformed.points[0]).normalized();
			
			if (Vector3.Dot(faceNormal, dirToCamera) < 0.0f) continue;
			
			Triangle[] clippedTris = triangleClipAgainstPlane(new Vector3(0, 0, camera.near), Vector3.forward(), fullyTransformed);
			for (Triangle clipped : clippedTris) {
				if (clipped == null) continue;
				Triangle fullyProcessed = clipped;
				
				// convert to screen coordinates
				for (int i = 0; i < 3; i++) {
					fullyProcessed.points[i] = projectionMatrix.MultiplyByVector(clipped.points[i]);
					clipped.points[i].divideBy(clipped.points[i].w);
					
					clipped.points[i] = viewportPointToScreenPoint(clipped.points[i]);
				}
				
				trianglesToRaster.add(clipped);
			}	
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
			if (drawFlag != DrawFlag.wireframe) {
				Draw2D.FillTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
						new Vector2(projected.points[1].x, projected.points[1].y),
						new Vector2(projected.points[2].x, projected.points[2].y), Color.white); 
				continue;
			}
			Draw2D.DrawTriangle(new Vector2(projected.points[0].x, projected.points[0].y),
					new Vector2(projected.points[1].x, projected.points[1].y),
					new Vector2(projected.points[2].x, projected.points[2].y), Color.gray);
		}
	}
	
	
	//REGION Utility Functions
	
	public Vector3 viewportPointToScreenPoint(Vector3 point) {
		float x = Mathf.InverseLerp(-1, 1, point.x);
		float y = Mathf.InverseLerp(1, -1, point.y);
		
		Vector2 windowSize = application.getWindowSize();
		point.x = x*windowSize.x; point.y = y*windowSize.y;
		return point;
	}
	
	private Vector3 surfaceNormalFromIndices(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 sideAB = Vector3.subtract2Vecs(b, a);
		Vector3 sideAC = Vector3.subtract2Vecs(c, a);
		
		return Vector3.Cross(sideAB, sideAC).normalized();
	}
	
	private Vector3 lineIntersectPlane(Vector3 planePoint, Vector3 planeNormal, Vector3 lineStart, Vector3 lineEnd) {
		planeNormal.Normalize();
		float planeD = -Vector3.Dot(planeNormal, planePoint);
		float ad = Vector3.Dot(lineStart, planeNormal);
		float bd = Vector3.Dot(lineEnd, planeNormal);
		float t = (-planeD-ad) / (bd-ad);
		Vector3 lineStartToEnd = Vector3.subtract2Vecs(lineEnd, lineStart);
		Vector3 lineToIntersect = Vector3.mulVecFloat(lineStartToEnd, t);
		return Vector3.add2Vecs(lineStart, lineToIntersect);
	}
	
	private Triangle[] triangleClipAgainstPlane(Vector3 planePoint, Vector3 planeNormal, Triangle inTri) {
		Triangle[] outTris = new Triangle[2];
		planeNormal.Normalize();
		
		Function<Vector3, Float> dist = (p) -> {
			return (planeNormal.x*p.x + planeNormal.y*p.y + planeNormal.z*p.z - Vector3.Dot(planeNormal, planePoint));
		};
		
		Vector3[] insidePoints = new Vector3[3]; int insidePointCount = 0;
		Vector3[] outsidePoints = new Vector3[3]; int outsidePointCount = 0;
		
		float d0 = dist.apply(inTri.points[0]);
		float d1 = dist.apply(inTri.points[1]);
		float d2 = dist.apply(inTri.points[2]);
		
		if (d0 >= 0) insidePoints[insidePointCount++] = inTri.points[0];
		else outsidePoints[outsidePointCount++] = inTri.points[0];
		if (d1 >= 0) insidePoints[insidePointCount++] = inTri.points[1];
		else outsidePoints[outsidePointCount++] = inTri.points[1];
		if (d2 >= 0) insidePoints[insidePointCount++] = inTri.points[2];
		else outsidePoints[outsidePointCount++] = inTri.points[2];
		
		if (insidePointCount == 3) outTris[0] = inTri;
		if (insidePointCount == 1 && outsidePointCount == 2) {
			outTris[0] = new Triangle();
			
			outTris[0].points[0] = insidePoints[0];
			
			outTris[0].points[1] = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[0]);
			outTris[0].points[2] = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[1]);
		}
		if (insidePointCount == 2 && outsidePointCount == 1) {
			outTris[0] = new Triangle();
			outTris[1] = new Triangle();
			
			outTris[0].points[0] = insidePoints[0];
			outTris[0].points[1] = insidePoints[1];
			outTris[0].points[2] = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[0]);
			
			outTris[1].points[0] = insidePoints[1];
			outTris[1].points[1] = outTris[0].points[2];
			outTris[1].points[2] = lineIntersectPlane(planePoint, planeNormal, insidePoints[1], outsidePoints[0]);
		}
		
		return outTris;
	}
	//ENDREGION
}
