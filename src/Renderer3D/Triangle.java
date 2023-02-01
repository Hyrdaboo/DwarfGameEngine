package Renderer3D;

import java.awt.Color;

import DwarfEngine.Debug;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector3;

final class Triangle {
	public final Vector3[] points;
	public Color color = Color.white;
	
	public Triangle(Vector3 a, Vector3 b, Vector3 c) {
		points = new Vector3[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;		
	}
	
	public Triangle() {
		points = new Vector3[3];
		points[0] = Vector3.zero();
		points[1] = Vector3.zero();
		points[2] = Vector3.zero();	
	}
	
	public static Triangle[] CreateIndexedTriangleStream(Mesh mesh) {
		Triangle[] triangles = new Triangle[mesh.triangles.length/3];

		for (int i = 0; i < triangles.length; i++) {
			Triangle t = new Triangle();
			t.points[0] = mesh.vertices[mesh.triangles[0+i*3]];
			t.points[1] = mesh.vertices[mesh.triangles[1+i*3]];
			t.points[2] = mesh.vertices[mesh.triangles[2+i*3]];
			
			triangles[i] = t;
		}
		
		return triangles;
	}
	
	public float calculateArea() {
		Vector3 v1 = points[0], v2 = points[1], v3 = points[2];
		
		float a = Vector3.subtract2Vecs(v3, v1).magnitude();
		float b = Vector3.subtract2Vecs(v3, v2).magnitude();
		float c = Vector3.subtract2Vecs(v2, v1).magnitude();
		
		float p = (a+b+c)/2;
		
		return Mathf.sqrt(p*(p-a)*(p-b)*(p-c));
	}
	
	public void print() {
		Debug.println("("+points[0].x+", "+points[0].y+", "+points[0].z+") "+ 
					  "("+points[1].x+", "+points[1].y+", "+points[1].z+") "+
					  "("+points[2].x+", "+points[2].y+", "+points[2].z+") ");
	}
}
