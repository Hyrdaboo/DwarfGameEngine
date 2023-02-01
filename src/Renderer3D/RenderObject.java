package Renderer3D;

import java.awt.Color;
import java.util.Random;

public final class RenderObject {
	public final Transform transform;
	Triangle[] triangles;
	
	public RenderObject(Mesh mesh) {
		transform = new Transform();
		
		triangles = Triangle.CreateIndexedTriangleStream(mesh);
		
		Random rand = new Random();
		for (Triangle t : triangles) {
			t.color = new Color(rand.nextInt(0, 0xffffff));
		}
	}
}
