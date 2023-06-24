package Renderer3D;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

/**
 * Represents a mesh, which is a collection of vertices and polygons, used for rendering 3D objects.
 */
public final class Mesh {
	private Vector3[] vertices;
	private Vector2[] uv;
	private Vector3[] colors;
	private Vector3[] normals;
	private int[] triangles;

	/**
     * Sets the vertices of the mesh.
     *
     * @param vertices The array of vertices to set.
     */
	public void setVertices(Vector3[] vertices) {
		this.vertices = vertices;
	}

	/**
     * Returns the vertices of the mesh.
     *
     * @return The array of vertices.
     */
	public Vector3[] getVertices() {
		return vertices;
	}

	/**
     * Returns the number of vertices in the mesh.
     *
     * @return The vertex count.
     */
	public int vertexCount() {
		return vertices.length;
	}

	/**
     * Sets the triangles of the mesh.
     *
     * @param triangles The array of triangle indices to set.
     * @throws IllegalArgumentException if the size of the triangles array is not divisible by 3.
     */
	public void setTriangles(int[] triangles) {
		if (triangles.length % 3 != 0) {
			throw new IllegalArgumentException("Triangles size must be divisible by 3!");
		}
		this.triangles = triangles;
	}

	/**
     * Returns the triangles of the mesh.
     *
     * @return The array of triangle indices.
     */
	public int[] getTriangles() {
		return triangles;
	}

	/**
     * Returns the number of triangles in the mesh.
     *
     * @return The triangle count.
     */
	public int triangleCount() {
		return triangles.length;
	}

	/**
     * Sets the UV coordinates of the mesh.
     *
     * @param uvs The array of UV coordinates to set.
     * @throws IllegalArgumentException if the size of the uvs array is not equal to the vertex count.
     */
	public void setUV(Vector2[] uvs) {
		if (uvs.length != vertices.length) {
			throw new IllegalArgumentException("uvs must be the same size as vertices");
		}
		this.uv = uvs;
	}

	/**
     * Returns the UV coordinates of the mesh.
     *
     * @return The array of UV coordinates.
     */
	public Vector2[] getUV() {
		return uv;
	}

	/**
     * Sets the colors of the mesh.
     *
     * @param colors The array of vertex colors to set.
     * @throws IllegalArgumentException if the size of the colors array is not equal to the vertex count.
     */
	public void setColors(Vector3[] colors) {
		if (colors.length != vertices.length) {
			throw new IllegalArgumentException("Colors must be the same size as vertices");
		}
		this.colors = colors;
	}

	/**
     * Returns the colors of the mesh.
     *
     * @return The array of vertex colors.
     */
	public Vector3[] getColors() {
		return colors;
	}

	/**
     * Sets the normals of the mesh.
     *
     * @param normals The array of vertex normals to set.
     * @throws IllegalArgumentException if the size of the normals array is not equal to the vertex count.
     */
	public void setNormals(Vector3[] normals) {
		if (normals.length != vertices.length) {
			throw new IllegalArgumentException("Normals must be the same size as vertices");
		}

		this.normals = normals;
	}

	/**
     * Returns the normals of the mesh.
     *
     * @return The array of vertex normals.
     */
	public Vector3[] getNormals() {
		return normals;
	}

}
