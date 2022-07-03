package DwarfEngine.Renderer3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import DwarfEngine.Engine;
import DwarfEngine.MathTypes.Vector3;

public final class ObjLoader {
	
	private String pathname;
	private List<Vector3> verts = new ArrayList<Vector3>();
	private List<Integer> tris = new ArrayList<Integer>();
	private Mesh loadedMesh = new Mesh();
	
	public ObjLoader(String pathname) {
		String extension = "";
		String pathReversed = new StringBuffer(pathname).reverse().toString();
		
		for (char i : pathReversed.toCharArray()) {
			extension += i;
			if (i == '.') break;
		}
		if (!extension.equals("jbo.")) {
			Engine.PrintLn(extension);
			throw new IllegalArgumentException("The file specified is not type of obj");
		}
		this.pathname = pathname;
	}
	
	private Vector3[] listToArr(List<Vector3> list) {
		Vector3[] arr = new Vector3[list.size()];
		
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		
		return arr;
	}
	
	public Mesh loadMesh() {
		File objFile = new File(pathname);
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(objFile)));
			
			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				boolean vertexLine = nextLine.charAt(1) == ' ' && nextLine.charAt(0) == 'v';
				boolean triLine = nextLine.charAt(0) == 'f';
				
				if (vertexLine) {
					String[] faceVerts = nextLine.split(" ");
					verts.add(new Vector3(Float.parseFloat(faceVerts[1]), Float.parseFloat(faceVerts[2]), Float.parseFloat(faceVerts[3])));
				}
				
				if (triLine) {	
					String[] face = nextLine.split(" ");
					
					for (String s : face) {
						if (s.equals("f")) continue;
						String[] triIndex = s.split("/");
						
						tris.add(Integer.parseInt(triIndex[0])-1);
					} 
				}
			}
			
			loadedMesh.vertices = listToArr(verts);
			loadedMesh.triangles = tris.stream().mapToInt(Integer::intValue).toArray();
			
			Engine.PrintLn("LOADING OBJ FINISHED!");
			Engine.PrintLn("Loaded " + verts.size() + " vertices " + tris.size() + " triangles");
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadedMesh;
	}
}
