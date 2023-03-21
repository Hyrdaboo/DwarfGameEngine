package Renderer3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Vector3;

public final class ObjLoader {
	
	private List<Vector3> vertices = new ArrayList<Vector3>();
	private List<Integer> indices = new ArrayList<Integer>();
	
	private Mesh loadedMesh;
	private File objFile;
	
	public ObjLoader(String path) throws Exception {
		Pattern pattern = Pattern.compile(".obj");
		Matcher matcher = pattern.matcher(path+"$");
		
		if (!matcher.find()) {
			throw new Exception("The specified file format is not \".obj\"");
		}
		objFile = new File(path);
		if (!objFile.exists()) throw new Exception("Specified file does not exist!");
	}
	
	public Mesh Load() {
		if (loadedMesh != null) {
			Debug.log("Loaded already initialized mesh!");
			return loadedMesh;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(objFile));
			
			char firstChar, secondChar;
			
			String line;
			while ((line = reader.readLine()) != null) {
				firstChar = line.charAt(0); secondChar = line.charAt(1);
				
				if (firstChar == 'v') {
					if (secondChar == ' ') {
						String[] vp = line.split(" ");
						vertices.add(new Vector3(Float.parseFloat(vp[1]), Float.parseFloat(vp[2]), Float.parseFloat(vp[3])));
					}
				}
				
				if (firstChar == 'f') {
					String[] face = line.split(" ");
					
					for (String s : face) {
						if (s.equals("f")) continue;
						String[] triIndex = s.split("/");
						
						indices.add(Integer.parseInt(triIndex[0])-1);
					} 
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		loadedMesh = new Mesh();
		loadedMesh.setVertices((Vector3[]) vertices.toArray(new Vector3[vertices.size()]));
		loadedMesh.setTriangles(indices.stream().mapToInt(Integer::intValue).toArray());
		Debug.log("Finished loading mesh! Loaded "+loadedMesh.vertexCount()+" vertices, "+loadedMesh.triangleCount()+" triangles");
		return loadedMesh;
	}	
	
	
}
