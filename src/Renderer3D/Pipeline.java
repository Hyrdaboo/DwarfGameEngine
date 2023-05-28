package Renderer3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import static DwarfEngine.Core.DisplayRenderer.*;


public final class Pipeline {

	public enum RenderFlag { Shaded, Wireframe, ShadedWireframe };
	public RenderFlag renderFlag = RenderFlag.Shaded;
	
	private Application application;
	private Camera camera;
	private Matrix4x4 projectionMatrix;
	
	private float[] depthBuffer;
	private Vector2 frameSize;
	
	private TriangleRasterizer tr;
	
	public Pipeline(Application application, Camera camera) {
		this.application = application;
		this.camera = camera;
		
		projectionMatrix = new Matrix4x4();
		
		frameSize = application.getFrameSize();
		depthBuffer = new float[(int)(frameSize.x*frameSize.y)];
		
		tr = new TriangleRasterizer();
		tr.bindDepth(depthBuffer);
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
		
		renderObject.shader.objectTransform = renderObject.transform;
		for (Triangle t : renderObject.triangles) {
			Triangle fullyTransformed = new Triangle();
			Triangle transformed = new Triangle();
			
			for (int i = 0; i < 3; i++) {
				transformed.verts[i].position = transformMatrix.MultiplyByVector(t.verts[i].position);
				fullyTransformed.verts[i] = t.verts[i].clone();
				fullyTransformed.verts[i].position = cameraObjectCombined.MultiplyByVector(t.verts[i].position);
			}
			
			Vector3 faceNormal = surfaceNormalFromIndices(transformed.verts[0].position, transformed.verts[1].position, transformed.verts[2].position);
			Vector3 dirToCamera = Vector3.subtract2Vecs(camera.transform.position, transformed.verts[0].position).normalized();
			
			if (Vector3.Dot(faceNormal, dirToCamera) < 0.0f && renderObject.shader.cull) continue;
			
			
			Plane[] clippingPlanes = new Plane[] {
				new Plane(new Vector3(0, 0, camera.near), Vector3.forward()),
				new Plane(new Vector3(0, 0, camera.far), Vector3.back()),
			};
			
			List<Triangle> finalResult = new ArrayList<Triangle>();
			finalResult.add(fullyTransformed);
			
			for (Plane p : clippingPlanes) {
				int initialSize = finalResult.size();
				for (int i = 0; i < initialSize; i++) {
					Triangle[] clippedTris = Plane.triangleClipAgainstPlane(p.point, p.normal, finalResult.get(0));
					finalResult.remove(0);
					
					for (Triangle clipped : clippedTris) {
						
						if (clipped == null) continue;
						finalResult.add(clipped);
					}
				}
			}
			
			for (Triangle clipped : finalResult) {
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
		if (renderFlag != RenderFlag.Wireframe) {
			tr.DrawTriangle(projected.verts, shader);
		}
		if (renderFlag != RenderFlag.Shaded) {
			DrawTriangle(new Vector2(projected.verts[0].position),new Vector2(projected.verts[1].position),new Vector2(projected.verts[2].position), Color.gray);
		}
	}
	
	public void clear() {
		tr.clearAll();
	}
	
	private Vector3 viewportPointToScreenPoint(Vector3 point) {
		float x = (point.x+1)/2.0f * frameSize.x;
		float y = (-point.y+1)/2.0f * frameSize.y;
		
		Vector3 v = new Vector3(x, y, point.z);
		v.w = point.w;
		return v;
	}
	
	private Vector3 surfaceNormalFromIndices(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 sideAB = Vector3.subtract2Vecs(b, a);
		Vector3 sideAC = Vector3.subtract2Vecs(c, a);
		
		return Vector3.Cross(sideAB, sideAC).normalized();
	}
}