package Renderer3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import static DwarfEngine.Core.DisplayRenderer.*;

public final class TriangleRasterizer {
	
	private static float[] depthBuffer;
	private int width, height;
	public TriangleRasterizer() {
		width = getBufferWidth();
		height = getBufferHeight();
	}
	
	public void bindDepth(float[] buffer) {
		if (buffer.length != width*height) {
			throw new IllegalArgumentException("Depth buffer should be same size as screen");
		}
		depthBuffer = buffer;
	}
	
	public void clearAll() {
		clear(Color.black);
		Arrays.fill(depthBuffer, Float.MAX_VALUE);
	}

	public void writeDepth(int x, int y, Float value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid write index. Cannot write at " + x + ", " + y);
		}
		depthBuffer[x + y*width] = value;
	}
	
	public Float readDepth(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid read index. Cannot read at " + x + ", " + y);
		}
		return depthBuffer[x + y*width];
	}
	
	public void DrawTriangle(Vertex[] vertices, Shader shader) {
		
		Plane[] clippingPlanes = new Plane[] {
				new Plane(Vector3.zero(), Vector3.up()), //top
				new Plane(Vector3.zero(), Vector3.right()), //left
				new Plane(new Vector3(width, 0, 0), Vector3.left()), //right
				new Plane(new Vector3(0, height, 0), Vector3.down()), //bottom
		};
		
		Triangle in = new Triangle(vertices[0], vertices[1], vertices[2]);
		List<Triangle> finalResult = new ArrayList<Triangle>();
		finalResult.add(in);
		
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
			DrawTriangleClipped(clipped.verts, shader);
		}
	}
	
	private void DrawTriangleClipped(Vertex[] vertices, Shader shader) {
		Arrays.sort(vertices, Comparator.comparingDouble((v) -> v.position.y));
		Vertex v1 = vertices[0], v2 = vertices[1], v3 = vertices[2];	
		
		if (v2.position.y == v3.position.y) {
			if (v2.position.x > v3.position.x) {
				flatBottom(v1, v3, v2, shader);
				return;
			}
			flatBottom(v1, v2, v3, shader);
		}
		else if (v1.position.y == v2.position.y) {
			if (v1.position.x > v2.position.x) {
				flatTop(v2, v1, v3, shader);
				return;
			}
			flatTop(v1, v2, v3, shader);
		}
		else {
			float t = Mathf.InverseLerp(v1.position.y, v3.position.y, v2.position.y);
			Vertex v4 = Vertex.Lerp(v1, v3, t);
			
			if (v4.position.x > v2.position.x) {
				flatBottom(v1, v2, v4, shader);
				flatTop(v2, v4, v3, shader);
				return;
			}
			flatBottom(v1, v4, v2, shader);
			flatTop(v4, v2, v3, shader);
		}
	}
	
	private void flatBottom(Vertex v1, Vertex v2, Vertex v3, Shader shader) {
		flatTriangle(v1, v2, v1, v3, shader);
	}
	
	private void flatTop(Vertex v1, Vertex v2, Vertex v3, Shader shader) {
		flatTriangle(v1, v3, v2, v3, shader);
	}
	
	private void flatTriangle(Vertex leftSlope1, Vertex leftSlope2, Vertex rightSlope1, Vertex rightSlope2, Shader shader) {
		int yStart = (int) Mathf.ceil(leftSlope1.position.y-0.5f);
		int yEnd = (int) Mathf.ceil(rightSlope2.position.y-0.5f);
		
		float leftSlope = calculateSlope(leftSlope1.position, leftSlope2.position);
		float rightSlope = calculateSlope(rightSlope1.position, rightSlope2.position);
		
		for (int y = yStart; y < yEnd; y++) {
			float px1 = (leftSlope*(y+0.5f-leftSlope1.position.y)) + leftSlope1.position.x;
			float px2 = (rightSlope*(y+0.5f-rightSlope1.position.y)) + rightSlope1.position.x;
			
			int xStart = (int) Mathf.ceil(px1-0.5f);
			int xEnd = (int) Mathf.ceil(px2-0.5f);
			
			float si = InverseLerp(leftSlope1.position, leftSlope2.position, new Vector3(xStart, y, 0));
			float ei = InverseLerp(rightSlope1.position, rightSlope2.position, new Vector3(xEnd, y, 0));
			Vertex startVertex = Vertex.Lerp(leftSlope1, leftSlope2, si);
			Vertex endVertex = Vertex.Lerp(rightSlope1, rightSlope2, ei);
			
			float mag = xEnd - xStart;
			Vertex delta = Vertex.delta(startVertex, endVertex, mag);

			Vertex in = new Vertex();
			for (int x = xStart; x < xEnd; x++) {
				in.position.w = startVertex.position.w;
				in.texcoord.x = startVertex.texcoord.x;
				in.texcoord.y = startVertex.texcoord.y;
				in.color.x = startVertex.color.x;
				in.color.y = startVertex.color.y;
				in.color.z = startVertex.color.z;
				in.normal.x = startVertex.normal.x;
				in.normal.y = startVertex.normal.y;
				in.normal.z = startVertex.normal.z;
				in.worldPos.x = startVertex.worldPos.x;
				in.worldPos.y = startVertex.worldPos.y;
				in.worldPos.z = startVertex.worldPos.z;
				
				float w = in.position.w;
				w = 1.0f / w;
				
				boolean depthTestPassed = true;
				if (depthBuffer != null) {
					depthTestPassed = w < readDepth(x, y);
				}
				
				if (depthTestPassed) {					
					in.texcoord.multiplyBy(w);
					in.worldPos.multiplyBy(w);
					int finalCol = toColor(shader.Fragment(in));
					SetPixel(x, y, finalCol);
					
					if (depthBuffer != null) {
						writeDepth(x, y, w);
					}
				}
				
				Vertex.add(startVertex, delta, startVertex);
			}
		}
	}
	
	private int toColor(Vector3 v) {
		v.x = Mathf.Clamp01(v.x);
		v.y = Mathf.Clamp01(v.y);
		v.z = Mathf.Clamp01(v.z);
		
		int r = (int) (v.x * 255);
		int g = (int) (v.y * 255);
		int b = (int) (v.z * 255);
		int rgb = (r << 16) | (g << 8) | (b << 0);
		return rgb;
	}
	
	public static float InverseLerp(Vector3 a, Vector3 b, Vector3 value)
    {
        Vector3 AB = Vector3.subtract2Vecs(b, a);
        Vector3 AV = Vector3.subtract2Vecs(value, a);
        return Vector3.Dot(AV, AB) / Vector3.Dot(AB, AB);
    }
	
	private float calculateSlope(Vector3 v1, Vector3 v2) {
		return (v2.x - v1.x) / (v2.y - v1.y);
	}
	
}
