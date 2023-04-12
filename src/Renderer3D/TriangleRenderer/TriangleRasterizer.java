package Renderer3D.TriangleRenderer;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;

public final class TriangleRasterizer {
	private ColorBuffer colorBuffer;
	
	public <T> void bindBuffer(Buffer<T> buffer) {
		if (buffer instanceof ColorBuffer) {
			colorBuffer = (ColorBuffer) buffer;
		}
	}
	
	private void SetPixel(int x, int y, int color) {
		if (x < 0 || x >= colorBuffer.getWidth() || y < 0 || y >= colorBuffer.getHeight()) {
			return;
		}
		colorBuffer.write(x, y, color);
	}
	
	public void DrawTriangle(Vector3[] vertices, int rgb) {
		Arrays.sort(vertices, Comparator.comparingDouble((v) -> v.y));
		Vector3 v1 = vertices[0], v2 = vertices[1], v3 = vertices[2];	
		
		if (v2.y == v3.y) {
			if (v2.x > v3.x) {
				flatBottom(v1, v3, v2, rgb);
			}
			flatBottom(v1, v2, v3, rgb);
		}
		else if (v1.y == v2.y) {
			if (v1.x < v2.x) {
				flatTop(v2, v1, v3, rgb);
			}
			flatTop(v1, v2, v3, rgb);
		}
		else {
			float t = Mathf.InverseLerp(v1.y, v3.y, v2.y);
			Vector3 v4 = Vector3.Lerp(v1, v3, t);
			
			if (v4.x > v2.x) {
				flatBottom(v1, v2, v4, rgb);
				flatTop(v2, v4, v3, rgb);
			}
			flatBottom(v1, v4, v2, rgb);
			flatTop(v4, v2, v3, rgb);
		}
	}
	
	private void flatBottom(Vector3 v1, Vector3 v2, Vector3 v3, int rgb) {
		float leftSlope = calculateSlope(v1, v2);
		float rightSlope = calculateSlope(v1, v3);
		flatTriangle(v1, v3, v1, leftSlope, rightSlope, rgb);
	}
	
	private void flatTop(Vector3 v1, Vector3 v2, Vector3 v3, int rgb) {
		float leftSlope = calculateSlope(v1, v3);
		float rightSlope = calculateSlope(v2, v3);
		flatTriangle(v1, v3, v2, leftSlope, rightSlope, rgb);
	}
	
	private void flatTriangle(Vector3 v1, Vector3 v3, Vector3 sv, float leftSlope, float rightSlope, int rgb) {
		int yStart = (int) Mathf.ceil(v1.y-0.5f);
		int yEnd = (int) Mathf.ceil(v3.y-0.5f);
		
		for (int y = yStart; y < yEnd; y++) {
			float px1 = (leftSlope*(y+0.5f-v1.y)) + v1.x;
			float px2 = (rightSlope*(y+0.5f-v1.y)) + sv.x;
			
			int xStart = (int) Mathf.ceil(px1-0.5f);
			int xEnd = (int) Mathf.ceil(px2-0.5f);
			
			for (int x = xStart; x < xEnd; x++) {
				SetPixel(x, y, rgb);
			}
		}
	}
	
	private float calculateSlope(Vector3 v1, Vector3 v2) {
		return (v2.x - v1.x) / (v2.y - v1.y);
	}
	
}
