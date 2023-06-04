package Renderer3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public final class ObjLoader {

	public static Mesh Load(String path) {
		File objFile = null;

		try {
			Pattern pattern = Pattern.compile(".obj");
			Matcher matcher = pattern.matcher(path + "$");
			if (!matcher.find()) {
				throw new Exception("The specified file format is not \".obj\"");
			}

			objFile = new File(path);
			if (!objFile.exists())
				throw new Exception("Specified file does not exist!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<Vector3> tempVertices = new ArrayList<>();
		List<Vector3> tempColors = new ArrayList<>();
		List<Vector2> tempUvs = new ArrayList<>();
		List<Vector3> tempNormals = new ArrayList<>();

		List<Integer> vertexIndices = new ArrayList<>();
		List<Integer> uvIndices = new ArrayList<>();
		List<Integer> normalIndices = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(objFile));

			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.isBlank() || line.charAt(0) == '#')
					continue;

				String[] components = line.split(" ");

				switch (components[0]) {
				case "v":
					float x = Float.parseFloat(components[1]);
					float y = Float.parseFloat(components[2]);
					float z = Float.parseFloat(components[3]);
					tempVertices.add(new Vector3(x, y, z));

					if (components.length > 4) {
						float r = Float.parseFloat(components[4]);
						float g = Float.parseFloat(components[5]);
						float b = Float.parseFloat(components[6]);
						tempColors.add(new Vector3(r, g, b));
					}
					break;
				case "vt":
					float u = Float.parseFloat(components[1]);
					float v = Float.parseFloat(components[2]);
					tempUvs.add(new Vector2(u, v));
					break;
				case "vn":
					float nx = Float.parseFloat(components[1]);
					float ny = Float.parseFloat(components[2]);
					float nz = Float.parseFloat(components[3]);
					tempNormals.add(new Vector3(nx, ny, nz));
					break;
				case "f":
					for (int i = 1; i < 4; i++) {
						String[] vertAttributes = components[i].split(" ");

						for (String attrib : vertAttributes) {
							String[] attributes = attrib.split("/");

							vertexIndices.add(Integer.parseInt(attributes[0]) - 1);
							if (attributes.length == 1)
								break;

							if (!attributes[1].isBlank()) {
								uvIndices.add(Integer.parseInt(attributes[1]) - 1);
							}
							if (attributes.length < 3)
								break;
							normalIndices.add(Integer.parseInt(attributes[2]) - 1);
						}
					}
					break;
				default:
					break;
				}
			}
			reader.close();

		} catch (Exception e) {
			Debug.log("An unknown error occured while trying to load your obj file");
		}

		List<Vector3> vertices = new ArrayList<>();
		for (int i : vertexIndices) {
			vertices.add(tempVertices.get(i));
		}

		List<Vector3> colors = null;
		if (tempColors.size() > 0) {
			colors = new ArrayList<>();
			for (int i : vertexIndices) {
				colors.add(tempColors.get(i));
			}
		}

		List<Vector2> uvs = null;
		if (tempUvs.size() > 0) {
			uvs = new ArrayList<>();
			for (int i : uvIndices) {
				uvs.add(tempUvs.get(i));
			}
		}

		List<Vector3> normals = null;
		if (tempNormals.size() > 0) {
			normals = new ArrayList<>();
			for (int i : normalIndices) {
				normals.add(tempNormals.get(i));
			}
		}

		int indexCount = vertexIndices.size();
		vertexIndices.clear();
		for (int i = 0; i < indexCount; i++) {
			vertexIndices.add(i);
		}

		Mesh mesh = new Mesh();
		mesh.setVertices(vertices.toArray(new Vector3[vertices.size()]));
		if (colors != null) {
			mesh.setColors(colors.toArray(new Vector3[colors.size()]));
		}
		if (uvs != null) {
			mesh.setUV(uvs.toArray(new Vector2[uvs.size()]));
		}
		if (normals != null) {
			mesh.setNormals(normals.toArray(new Vector3[normals.size()]));
		}
		mesh.setTriangles(vertexIndices.stream().mapToInt(Integer::intValue).toArray());
		Debug.log("Loaded mesh with " + indexCount / 3 + " triangles");
		return mesh;
	}
}