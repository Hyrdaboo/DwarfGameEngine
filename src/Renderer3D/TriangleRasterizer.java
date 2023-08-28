package Renderer3D;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static DwarfEngine.Core.DisplayRenderer.*;

/**
 * The <code>TriangleRasterizer</code> class provides functionality for
 * rasterizing triangles and interpolating attributes across the triangle
 * surface. It is used for rendering triangles on a 2D screen or surface.
 */
public final class TriangleRasterizer {

	private static float[] depthBuffer;
	private final int width, height;

	Plane[] clippingPlanes;

	public TriangleRasterizer() {
		width = getBufferWidth();
		height = getBufferHeight();
		clippingPlanes = new Plane[]{
				new Plane(Vector3.zero(), Vector3.up()), // top
				new Plane(Vector3.zero(), Vector3.right()), // left
				new Plane(new Vector3(width, 0, 0), Vector3.left()), // right
				new Plane(new Vector3(0, height, 0), Vector3.down()), // bottom
		};
	}

	/**
	 * Binds a depth buffer to the renderer, specifying the buffer to use for
	 * storing depth information. The depth buffer should have the same size as the
	 * screen.
	 *
	 * @param buffer The depth buffer to bind.
	 * @throws IllegalArgumentException if the provided depth buffer size does not
	 *                                  match the screen size.
	 */
	public void bindDepth(float[] buffer) {
		if (buffer.length != width * height) {
			throw new IllegalArgumentException("Depth buffer should be same size as screen");
		}
		depthBuffer = buffer;
	}

	/**
	 * Clears all the buffers
	 */
	public void clearAll() {
		clear(Color.black);
		Arrays.fill(depthBuffer, Float.MAX_VALUE);
	}

	private void writeDepth(int x, int y, Float value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid write index. Cannot write at " + x + ", " + y);
		}
		depthBuffer[x + y * width] = value;
	}

	private float readDepth(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid read index. Cannot read at " + x + ", " + y);
		}
		return depthBuffer[x + y * width];
	}

	/**
	 * Draws a triangle using the specified vertices and shader.
	 *
	 * @param vertices The array of vertices that make up the triangle.
	 * @param shader   The shader to use for rendering the triangle.
	 */
	public void DrawTriangle(Vertex[] vertices, Shader shader) {

		Triangle in = new Triangle(vertices[0], vertices[1], vertices[2]);
		List<Triangle> finalResult = new ArrayList<>();
		finalResult.add(in);

		for (Plane p : clippingPlanes) {
			int initialSize = finalResult.size();
			for (int i = 0; i < initialSize; i++) {
				Triangle[] clippedTris = Plane.triangleClipAgainstPlane(p.point, p.normal, finalResult.get(0));
				finalResult.remove(0);

				for (Triangle clipped : clippedTris) {
					if (clipped != null) finalResult.add(clipped);
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
			} else {
				flatBottom(v1, v2, v3, shader);
			}
		} else if (v1.position.y == v2.position.y) {
			if (v1.position.x > v2.position.x) {
				flatTop(v2, v1, v3, shader);
			} else {
				flatTop(v1, v2, v3, shader);
			}
		} else {
			float t = Mathf.inverseLerp(v1.position.y, v3.position.y, v2.position.y);
			Vertex v4 = Vertex.Lerp(v1, v3, t);
			if (v4.position.x > v2.position.x) {
				flatBottom(v1, v2, v4, shader);
				flatTop(v2, v4, v3, shader);
			} else {
				flatBottom(v1, v4, v2, shader);
				flatTop(v4, v2, v3, shader);
			}
		}
	}

	private void flatBottom(Vertex v1, Vertex v2, Vertex v3, Shader shader) {
		flatTriangle(v1, v2, v1, v3, shader);
	}

	private void flatTop(Vertex v1, Vertex v2, Vertex v3, Shader shader) {
		flatTriangle(v1, v3, v2, v3, shader);
	}

	private final static ThreadLocal<RasterizerData> tld = ThreadLocal.withInitial(RasterizerData::new);

	private static class RasterizerData {
		Vertex startVertex = new Vertex(), endVertex = new Vertex();
		Vertex in = new Vertex();
		Vector3 col = new Vector3();
		Vertex delta = new Vertex();

		{
			// a dirty hack, saves unnecessary copies
			in.position = startVertex.position;
			in.normal = startVertex.normal;
			in.color = startVertex.color;
		}
	}

	private void flatTriangle(Vertex leftSlope1, Vertex leftSlope2, Vertex rightSlope1, Vertex rightSlope2,
							  Shader shader) {

		int yStart = (int) Mathf.round(leftSlope1.position.y);
		int yEnd = (int) Mathf.round(rightSlope2.position.y);

		float leftSlope = calculateSlope(leftSlope1.position, leftSlope2.position);
		float rightSlope = calculateSlope(rightSlope1.position, rightSlope2.position);

		RasterizerData ftd = TriangleRasterizer.tld.get();

		Vertex startVertex = ftd.startVertex, endVertex = ftd.endVertex;
		Vertex in = ftd.in;
		Vector3 col = ftd.col;
		Vertex delta = ftd.delta;

		for (int y = yStart; y < yEnd; y++) {
			float yf = y + 0.5f;
			float px1 = (leftSlope * (yf - leftSlope1.position.y)) + leftSlope1.position.x;
			float px2 = (rightSlope * (yf - rightSlope1.position.y)) + rightSlope1.position.x;

			int xStart = (int) Mathf.round(px1);
			int xEnd = (int) Mathf.round(px2);
			if (xEnd <= xStart) continue;

			float si = InverseLerp(leftSlope1.position, leftSlope2.position, xStart, y);
			float ei = InverseLerp(rightSlope1.position, rightSlope2.position, xEnd, y);
			Vertex.Lerp(leftSlope1, leftSlope2, si, startVertex);
			Vertex.Lerp(rightSlope1, rightSlope2, ei, endVertex);

			Vertex.delta(startVertex, endVertex, 1f / (xEnd - xStart), delta);

			int missing = 0;
			float wi = startVertex.position.w, dwi = delta.position.w;
			for (int x = xStart; x < xEnd; x++) {

				boolean depthTestPassed = true;
				if (depthBuffer != null) {
					depthTestPassed = 1f < readDepth(x, y) * wi;
				}

				if (depthTestPassed) {

					if (missing > 0) {
						Vertex.add(startVertex, delta, missing);
						missing = 0;
					}

					float w = 1f / wi;
					Vector2.mulVecFloat(startVertex.texcoord, w, in.texcoord);
					Vector3.mulVecFloat(startVertex.worldPos, w, in.worldPos);

					int finalCol = toColor(shader.Fragment(in, col));
					SetPixel(x, y, finalCol);

					if (depthBuffer != null) {
						writeDepth(x, y, w);
					}
				}

				missing++;
				wi += dwi;
			}
		}
	}

	private int toColor(Vector3 v) {
		int r = Math.min((int) (v.x * 255), 255);
		int g = Math.min((int) (v.y * 255), 255);
		int b = Math.min((int) (v.z * 255), 255);
		return (r << 16) | (g << 8) | (b);
	}

	private static float InverseLerp(Vector3 a, Vector3 b, float vx, float vy) {
		float abx = b.x - a.x, aby = b.y - a.y, abz = b.z - a.z;
		float avx = vx - a.x, avy = vy - a.y, avz = -a.z;
		return (avx * abx + avy * aby + avz * abz) / (abx * abx + aby * aby + abz * abz);
	}

	private float calculateSlope(Vector3 v1, Vector3 v2) {
		return (v2.x - v1.x) / (v2.y - v1.y);
	}

}
