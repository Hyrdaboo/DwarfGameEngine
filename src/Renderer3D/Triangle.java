package Renderer3D;

import java.awt.Color;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.TriangleRenderer.Vertex;

final class Triangle {
	public final Vertex[] verts;
	
	public Triangle(Vertex a, Vertex b, Vertex c) {
		verts = new Vertex[3];
		verts[0] = a;
		verts[1] = b;
		verts[2] = c;
	}
	
	public Triangle() {
		verts = new Vertex[3];
		verts[0] = new Vertex();
		verts[1] = new Vertex();
		verts[2] = new Vertex();
	}
	
	public static Triangle[] CreateIndexedTriangleStream(Mesh mesh) {
		Triangle[] triangles = new Triangle[mesh.triangleCount()/3];
		Vector3[] vertices = mesh.getVetices();
		Vector2[] uv = mesh.getUV();
		Color[] colors = mesh.getColors();
		int[] tris = mesh.getTriangles();
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = new Triangle();
			t.verts[0].position = vertices[tris[0+i*3]];
			t.verts[1].position = vertices[tris[1+i*3]];
			t.verts[2].position = vertices[tris[2+i*3]];
			
			if (uv != null) {
				t.verts[0].texcoord = uv[tris[0+i*3]];
				t.verts[1].texcoord = uv[tris[1+i*3]];
				t.verts[2].texcoord = uv[tris[2+i*3]];
			}
			if (colors != null) {
				t.verts[0].color = colors[tris[0+i*3]];
				t.verts[1].color = colors[tris[1+i*3]];
				t.verts[2].color = colors[tris[2+i*3]];
			}
			
			triangles[i] = t;
		}
		
		return triangles;
	}
}
