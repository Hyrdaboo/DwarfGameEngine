package Renderer3D;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

final class Triangle {
	public final Vector3[] points;
	public final Vector2[] texcoord;
	
	public Triangle(Vector3 a, Vector3 b, Vector3 c) {
		points = new Vector3[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;
		
		texcoord = new Vector2[3];
		texcoord[0] = Vector2.zero();
		texcoord[1] = Vector2.zero();
		texcoord[2] = Vector2.zero();
	}
	
	public Triangle() {
		points = new Vector3[3];
		points[0] = Vector3.zero();
		points[1] = Vector3.zero();
		points[2] = Vector3.zero();	
		
		texcoord = new Vector2[3];
		texcoord[0] = Vector2.zero();
		texcoord[1] = Vector2.zero();
		texcoord[2] = Vector2.zero();
	}
	
	public static Triangle[] CreateIndexedTriangleStream(Mesh mesh) {
		Triangle[] triangles = new Triangle[mesh.triangleCount()/3];
		Vector3[] vertices = mesh.getVetices();
		Vector2[] uv = mesh.getUV();
		int[] tris = mesh.getTriangles();
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle t = new Triangle();
			t.points[0] = vertices[tris[0+i*3]];
			t.points[1] = vertices[tris[1+i*3]];
			t.points[2] = vertices[tris[2+i*3]];
			
			if (uv != null) {
				t.texcoord[0] = uv[tris[0+i*3]];
				t.texcoord[1] = uv[tris[1+i*3]];
				t.texcoord[2] = uv[tris[2+i*3]];
			}
			
			triangles[i] = t;
		}
		
		return triangles;
	}
	
	public void print() {
		Debug.log("("+points[0].x+", "+points[0].y+", "+points[0].z+") "+ 
					  "("+points[1].x+", "+points[1].y+", "+points[1].z+") "+
					  "("+points[2].x+", "+points[2].y+", "+points[2].z+") ");
	}
}
