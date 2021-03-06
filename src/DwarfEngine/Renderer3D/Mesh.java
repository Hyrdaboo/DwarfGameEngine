package DwarfEngine.Renderer3D;

import DwarfEngine.MathTypes.Vector3;

public final class Mesh {
	public Vector3[] vertices;
	public int[] triangles;
	
	public static Mesh makeCube() {
		Mesh mesh = new Mesh();
	    mesh.vertices = new Vector3[] {
	    	// front
	    	new Vector3(0, 0, 0),
	    	new Vector3(0, 1, 0),
	    	new Vector3(1, 1, 0),
	    	new Vector3(1, 1, 0),
	    	new Vector3(1, 0, 0),
	    	new Vector3(0, 0, 0),
	    	
	    	//// back
	    	new Vector3(0, 0, 1),
	    	new Vector3(0, 1, 1),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 0, 1),
	    	new Vector3(0, 0, 1),
	    	
	    	//right
	    	new Vector3(1, 0, 0),
	    	new Vector3(1, 1, 0),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 0, 1),
	    	new Vector3(1, 0, 0),
	    	
	    	//left
	    	new Vector3(0, 0, 0),
	    	new Vector3(0, 1, 0),
	    	new Vector3(0, 1, 1),
	    	new Vector3(0, 1, 1),
	    	new Vector3(0, 0, 1),
	    	new Vector3(0, 0, 0),
	    	
	    	// bottom
	    	new Vector3(0, 0, 0),
	    	new Vector3(0, 0, 1),
	    	new Vector3(1, 0, 1),
	    	new Vector3(1, 0, 1),
	    	new Vector3(1, 0, 0),
	    	new Vector3(0, 0, 0),
	    	
	    	//top
	    	new Vector3(0, 1, 0),
	    	new Vector3(0, 1, 1),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 1, 1),
	    	new Vector3(1, 1, 0),
	    	new Vector3(0, 1, 0),
	    };
	    
	    mesh.triangles = new int[] {
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
	    return mesh;
	}
}


