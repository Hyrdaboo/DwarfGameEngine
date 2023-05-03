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
import Renderer3D.TriangleRenderer.Vertex;

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
				fullyTransformed.verts[i].color = t.verts[i].color;
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
	
	private float lineIntersectPlane(Vector3 planePoint, Vector3 planeNormal, Vector3 lineStart, Vector3 lineEnd) {
		planeNormal.Normalize();
		float planeD = -Vector3.Dot(planeNormal, planePoint);
		float ad = Vector3.Dot(lineStart, planeNormal);
		float bd = Vector3.Dot(lineEnd, planeNormal);
		float t = (-planeD-ad) / (bd-ad);
		return t;
	}
	
	private Triangle[] triangleClipAgainstPlane(Vector3 planePoint, Vector3 planeNormal, Triangle inTri) {
		Triangle[] outTris = new Triangle[2];
		planeNormal.Normalize();
		
		Function<Vector3, Float> dist = (p) -> {
			return (planeNormal.x*p.x + planeNormal.y*p.y + planeNormal.z*p.z - Vector3.Dot(planeNormal, planePoint));
		};
		
		Vertex[] insidePoints = new Vertex[3]; int insidePointCount = 0;
		Vertex[] outsidePoints = new Vertex[3]; int outsidePointCount = 0;
		
		float d0 = dist.apply(inTri.verts[0].position);
		float d1 = dist.apply(inTri.verts[1].position);
		float d2 = dist.apply(inTri.verts[2].position);
		
		if (d0 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[0];
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[0];
			outsidePointCount++;
		}
		if (d1 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[1];
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[1];
			outsidePointCount++;
		}
		if (d2 >= 0) {
			insidePoints[insidePointCount] = inTri.verts[2];
			insidePointCount++;
		}
		else {
			outsidePoints[outsidePointCount] = inTri.verts[2];
			outsidePointCount++;
		}
		
		if (insidePointCount == 3) outTris[0] = inTri;
		if (insidePointCount == 1 && outsidePointCount == 2) {
			outTris[0] = new Triangle();
			
			outTris[0].verts[0] = insidePoints[0];
			
			float intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position, outsidePoints[0].position);
			float intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position, outsidePoints[1].position);
			
			outTris[0].verts[1] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);
			outTris[0].verts[2] = Vertex.Lerp(insidePoints[0], outsidePoints[1], intersection2);
		}
		if (insidePointCount == 2 && outsidePointCount == 1) {
			outTris[0] = new Triangle();
			outTris[1] = new Triangle();
			
			float intersection1 = lineIntersectPlane(planePoint, planeNormal, insidePoints[0].position, outsidePoints[0].position);
			float intersection2 = lineIntersectPlane(planePoint, planeNormal, insidePoints[1].position, outsidePoints[0].position);
			
			outTris[0].verts[0] = insidePoints[0];
			outTris[0].verts[1] = insidePoints[1];
			outTris[0].verts[2] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);
			
			outTris[1].verts[0] = insidePoints[1].clone();
			outTris[1].verts[1] = Vertex.Lerp(insidePoints[0], outsidePoints[0], intersection1);
			outTris[1].verts[2] = Vertex.Lerp(insidePoints[1], outsidePoints[0], intersection2);			
		}
		
		return outTris;
	}
	//ENDREGION
}