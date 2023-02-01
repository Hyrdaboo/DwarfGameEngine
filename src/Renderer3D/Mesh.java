package Renderer3D;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public final class Mesh {
	public Vector3[] vertices;
	public Vector2[] uvs;
	public int[] triangles;
	
	
	public static Mesh MakeCube() {
		Mesh cube = new Mesh();
		cube.vertices = new Vector3[] {
				// front
	        	new Vector3(-.5f, -.5f, -.5f),
	        	new Vector3(-.5f, .5f, -.5f),
	        	new Vector3(.5f, .5f, -.5f),
	        	new Vector3(.5f, .5f, -.5f),
	        	new Vector3(.5f, -.5f, -.5f),
	        	new Vector3(-.5f, -.5f, -.5f),
	        	
	        	//// back
	        	new Vector3(-.5f, -.5f, .5f),
	        	new Vector3(-.5f, .5f, .5f),
	        	new Vector3(.5f, .5f, .5f),
	        	new Vector3(.5f, .5f, .5f),
	        	new Vector3(.5f, -.5f, .5f),
	        	new Vector3(-.5f, -.5f, .5f),
	        	
	        	//right
	        	new Vector3(.5f, -.5f, -.5f),
	        	new Vector3(.5f, .5f, -.5f),
	        	new Vector3(.5f, .5f, .5f),
	        	new Vector3(.5f, .5f, .5f),
	        	new Vector3(.5f, -.5f, .5f),
	        	new Vector3(.5f, -.5f, -.5f),
	        	
	        	//left
	        	new Vector3(-.5f, -.5f, -.5f),
	        	new Vector3(-.5f, .5f, -.5f),
	        	new Vector3(-.5f, .5f, .5f),
	        	new Vector3(-.5f, .5f, .5f),
	        	new Vector3(-.5f, -.5f, .5f),
	        	new Vector3(-.5f, -.5f, -.5f),
	        	
				// bottom
				new Vector3(-.5f, -.5f, -.5f),
				new Vector3(-.5f, -.5f, .5f),
				new Vector3(.5f, -.5f, .5f),
				new Vector3(.5f, -.5f, .5f),
				new Vector3(.5f, -.5f, -.5f),
				new Vector3(-.5f, -.5f, -.5f),
				
				//top
				new Vector3(-.5f, .5f, -.5f),
				new Vector3(-.5f, .5f, .5f),
				new Vector3(.5f, .5f, .5f),
				new Vector3(.5f, .5f, .5f),
				new Vector3(.5f, .5f, -.5f),
				new Vector3(-.5f, .5f, -.5f),
		};
		cube.triangles = new int[] {
	        	0, 1, 2, 
	        	3, 4, 5, 
	        	6, 10, 8,
	        	9, 7, 11, 
	        	12, 13, 14, 
	        	15, 16, 17, 
	        	18, 22, 20, 
	        	21, 19, 23, 
	        	24, 28, 26,
	        	27, 25, 29, 
	        	30, 31, 32, 
	        	33, 34, 35
		};
		return cube;
	}
	
	public static Mesh MakeQuad() {
		Mesh quad = new Mesh();
		
		quad.vertices = new Vector3[] {
				new Vector3(-0.5f, -0.5f, 0),
				new Vector3(0.5f, -0.5f, 0),
				new Vector3(-0.5f, 0.5f, 0),
				new Vector3(0.5f, 0.5f, 0),				
		};
		quad.triangles = new int[] {
				0, 2, 3,
				3, 1, 0
		};
		
		return quad;
	}
	
}
