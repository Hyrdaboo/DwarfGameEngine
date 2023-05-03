package Renderer3D.TriangleRenderer;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;

public final class TriangleRasterizer {
	private ColorBuffer colorBuffer;
	private DepthBuffer depthBuffer;
	
	public <T> void bindBuffer(Buffer<T> buffer) {
		if (buffer instanceof ColorBuffer) {
			colorBuffer = (ColorBuffer) buffer;
		}
		else if (buffer instanceof DepthBuffer) {
			depthBuffer = (DepthBuffer) buffer;
		}
	}
	
	private void SetPixel(int x, int y, int color) {
		if (x < 0 || x >= colorBuffer.getWidth() || y < 0 || y >= colorBuffer.getHeight()) {
			return;
		}
		
		colorBuffer.write(x, y, color);
	}
	
	public void DrawTriangle(Vertex[] vertices, Shader shader) {
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
			for (int x = xStart; x < xEnd; x++) {
				float xi = Mathf.InverseLerp(xStart, xEnd, x);	
				
				
				Color finalCol = shader.Fragment(startVertex, endVertex, xi);
				SetPixel(x, y, finalCol.getRGB());
				/*
				if (depthBuffer != null) {
					float w = Mathf.Lerp(startVertex.position.w, endVertex.position.w, xi);
					w = 1.0f / w;
					
					if (w < depthBuffer.read(x, y)) {
						Color finalCol = shader.Fragment(startVertex, endVertex, xi);
						SetPixel(x, y, finalCol.getRGB());
						depthBuffer.write(x, y, w);
					}
				}
				else {
					Color finalCol = shader.Fragment(startVertex, endVertex, xi);
					SetPixel(x, y, finalCol.getRGB());
				}
				*/
			}
		}
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
