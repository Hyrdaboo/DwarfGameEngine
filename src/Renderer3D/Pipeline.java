package Renderer3D;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.DisplayRenderer;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.TriangleRenderer.ColorBuffer;
import Renderer3D.TriangleRenderer.DepthBuffer;
import Renderer3D.TriangleRenderer.Shader;
import Renderer3D.TriangleRenderer.TriangleRasterizer;

import static DwarfEngine.Core.DisplayRenderer.*;


public final class Pipeline {

	public enum DrawFlag { shaded, wireframe };
	public DrawFlag drawFlag = DrawFlag.shaded;
	
	private Application application;
	private Camera camera;
	private Matrix4x4 projectionMatrix;
	
	private float[] depthBufferArr;
	private Vector2 frameSize;
	
	private TriangleRasterizer tr;
	private ColorBuffer colorBuffer;
	private DepthBuffer depthBuffer;
	
	public Pipeline(Application application, Camera camera) {
		this.application = application;
		this.camera = camera;
		
		projectionMatrix = new Matrix4x4();
		
		frameSize = application.getFrameSize();
		depthBufferArr = new float[(int)(frameSize.x*frameSize.y)];
		
		tr = new TriangleRasterizer();
		colorBuffer = new ColorBuffer(DisplayRenderer.GetPixels(), (int)frameSize.x, (int)frameSize.y);
		depthBuffer = new DepthBuffer(depthBufferArr, (int)frameSize.x, (int)frameSize.y);
		tr.bindBuffer(colorBuffer);
		tr.bindBuffer(depthBuffer);
	}
	
	public void DrawMesh(RenderObject renderObject) {
		if (renderObject == null) {
			Debug.log("WARNING: RenderObject is null!!!");
			return;
		}
		
		Vector2 windowSize = new Vector2(application.getWidth()/application.getPixelScale(), application.getHeight()/application.getPixelScale());
		float aspectRatio = windowSize.y / windowSize.x;
		
		Matrix4x4.PerspectiveProjection(camera.fov, aspectRatio, camera.near, camera.far, projectionMatrix);
		Matrix4x4 transformMatrix = renderObject.transform.getTransformMatrix();
		Matrix4x4 viewMatrix = camera.getViewMatrix();
		Matrix4x4 cameraObjectCombined = Matrix4x4.matrixMultiplyMatrix(transformMatrix, viewMatrix);
		
		for (Triangle t : renderObject.triangles) {
			Triangle fullyTransformed = new Triangle();
			Triangle transformed = new Triangle();
			
			for (int i = 0; i < 3; i++) {
				transformed.verts[i].position = transformMatrix.MultiplyByVector(t.verts[i].position);
				fullyTransformed.verts[i].position = cameraObjectCombined.MultiplyByVector(t.verts[i].position);
				fullyTransformed.verts[i].texcoord = t.verts[i].texcoord;
			}
			
			Vector3 faceNormal = surfaceNormalFromIndices(transformed.verts[0].position, transformed.verts[1].position, transformed.verts[2].position);
			Vector3 dirToCamera = Vector3.subtract2Vecs(camera.transform.position, transformed.verts[0].position).normalized();
			
			if (Vector3.Dot(faceNormal, dirToCamera) < 0.0f) continue;
			
			Triangle[] clippedTris = triangleClipAgainstPlane(new Vector3(0, 0, camera.near), Vector3.forward(), fullyTransformed);
			for (Triangle clipped : clippedTris) {
				if (clipped == null) continue;
				
				for (int i = 0; i < 3; i++) {
					clipped.verts[i].position = projectionMatrix.MultiplyByVector(clipped.verts[i].position);
					clipped.verts[i].position.w = 1.0f / clipped.verts[i].position.w;
					clipped.verts[i].position.multiplyBy(clipped.verts[i].position.w);	
					clipped.verts[i].texcoord = Vector2.mulVecFloat(clipped.verts[i].texcoord, clipped.verts[i].position.w);
					
					clipped.verts[i].position = viewportPointToScreenPoint(clipped.verts[i].position);
				}

				DrawProjectedTriangle(clipped, renderObject.shader);
			}
		}
	}
	
	private void DrawProjectedTriangle(Triangle projected, Shader shader) {
		if (drawFlag != DrawFlag.wireframe) {
			tr.DrawTriangle(projected.verts, shader);
			return;
		}
		DrawTriangle(new Vector2(projected.verts[0].position.x, projected.verts[0].position.y),
				new Vector2(projected.verts[1].position.x, projected.verts[1].position.y),
				new Vector2(projected.verts[2].position.x, projected.verts[2].position.y), Color.gray);
	}
	
	//REGION Utility Functions
	
	public void clear() {
		depthBuffer.clear();
		colorBuffer.clear();
	}
	
	public Vector3 viewportPointToScreenPoint(Vector3 point) {
		float x = Mathf.InverseLerp(-1, 1, point.x);
		float y = Mathf.InverseLerp(1, -1, point.y);
		
		point.x = x*frameSize.x; point.y = y*frameSize.y;
		return point;
	}
	
	private Vector3 surfaceNormalFromIndices(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 sideAB = Vector3.subtract2Vecs(b, a);
		Vector3 sideAC = Vector3.subtract2Vecs(c, a);
		
		return Vector3.Cross(sideAB, sideAC).normalized();
	}
	
	class Intersection {
		public final Vector3 point;
		public final float t; // a value between 0-1 indicating where the intersection happened
		
		public Intersection(Vector3 hitPoint, float lineRatio) {
			this.point = hitPoint;
			this.t = lineRatio;
		}
	}
	
	private Intersection lineIntersectPlane(Vector3 planePoint, Vector3 planeNormal, Vector3 lineStart, Vector3 lineEnd) {
		planeNormal.Normalize();
		float planeD = -Vector3.Dot(planeNormal, planePoint);
		float ad = Vector3.Dot(lineStart, planeNormal);
		float bd = Vector3.Dot(lineEnd, planeNormal);
		float t = (-planeD-ad) / (bd-ad);
		Vector3 lineStartToEnd = Vector3.subtract2Vecs(lineEnd, lineStart);
		Vector3 lineToIntersect = Vector3.mulVecFloat(lineStartToEnd, t);
		Intersection hit = new Intersection(Vector3.add2Vecs(lineStart, lineToIntersect), t);
		return hit;
	}
	
	private Triangle[] triangleClipAgainstPlane(Vector3 planePoint, Vector3 planeNormal, Triangle inTri) {
		Triangle[] outTris = new Triangle[2];
		planeNormal.Normalize();
		
		Function<Vector3, Float> dist = (p) -> {
			return (planeNormal.x*p.x + planeNormal.y*p.y + planeNormal.z*p.z - Vector3.Dot(planeNormal, planePoint));
		};
		
		Vector3[] insidePoints = new Vector3[3]; int insidePointCount = 0;
		Vector3[] outsidePoints = new Vector3[3]; int outsidePointCount = 0;
		Vector2[] insideUv = new Vector2[3]; Vector2[] outsideUv = new Vector2[3];
		
		float d0 = dist.apply(inTri.verts[0].position);
		float d1 = dist.apply(inTri.verts[1].position);
		float d2 = dist.apply(inTri.verts[2].position);
		
		if (d0 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[0].position;
			insideUv[insidePointCount] = inTri.verts[0].texcoord;
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[0].position;
			outsideUv[outsidePointCount] = inTri.verts[0].texcoord;
			outsidePointCount++;
		}
		if (d1 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[1].position;
			insideUv[insidePointCount] = inTri.verts[1].texcoord;
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[1].position;
			outsideUv[outsidePointCount] = inTri.verts[1].texcoord;
			outsidePointCount++;
		}
		if (d2 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[2].position;
			insideUv[insidePointCount] = inTri.verts[2].texcoord;
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[2].position;
			outsideUv[outsidePointCount] = inTri.verts[2].texcoord;
			outsidePointCount++;
		}
		
		if (insidePointCount == 3) outTris[0] = inTri;
		if (insidePointCount == 1 && outsidePointCount == 2) {
			outTris[0] = new Triangle();
			
			outTris[0].verts[0].position = insidePoints[0];
			outTris[0].verts[0].texcoord = insideUv[0];
			
			Intersection intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[0]);
			Intersection intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[1]);
			
			outTris[0].verts[1].position = intersection1.point;
			outTris[0].verts[1].texcoord = Vector2.Lerp(insideUv[0], outsideUv[0], intersection1.t);
			outTris[0].verts[2].position = intersection2.point;
			outTris[0].verts[2].texcoord = Vector2.Lerp(insideUv[0], outsideUv[1], intersection2.t);
		}
		if (insidePointCount == 2 && outsidePointCount == 1) {
			outTris[0] = new Triangle();
			outTris[1] = new Triangle();
			
			Intersection intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0], outsidePoints[0]);
			Intersection intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[1], outsidePoints[0]);
			
			outTris[0].verts[0].position = insidePoints[0];
			outTris[0].verts[1].position = insidePoints[1];
			outTris[0].verts[2].position = intersection1.point;
			
			outTris[0].verts[0].texcoord = insideUv[0];
			outTris[0].verts[1].texcoord = insideUv[1];
			outTris[0].verts[2].texcoord = Vector2.Lerp(insideUv[0], outsideUv[0], intersection1.t);
			
			outTris[1].verts[0].position = insidePoints[1];
			outTris[1].verts[1].position = outTris[0].verts[2].position;
			outTris[1].verts[2].position = intersection2.point;
			
			outTris[1].verts[0].texcoord = insideUv[1];
			outTris[1].verts[1].texcoord = outTris[0].verts[2].texcoord;
			outTris[1].verts[2].texcoord = Vector2.Lerp(insideUv[1], outsideUv[0], intersection2.t);
		}
		
		return outTris;
	}
	//ENDREGION
}