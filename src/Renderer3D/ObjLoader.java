package Renderer3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

/**
 * Utility class for loading 3D models from OBJ files. <br>
 * <br>
 * <strong>IMPORTANT!</strong> You have to make sure your mesh is properly
 * triangulated. This OBJ loader is a basic implementation and does not perform
 * automatic triangulation.
 */
public final class ObjLoader {

	/**
	 *
	 * Loads a 3D Mesh object from an OBJ file.
	 *
	 * @param objFile ObjFile
	 * @return The loaded Mesh as a {@link Mesh} object
	 */
	public static Mesh Load(File objFile) {
		String[] name = objFile.getName().split("\\.");
		String extension = name[name.length - 1];
		if (!extension.equals("obj")) {
			System.err.println("Provided file is not obj");
			return null;
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
			System.err.println("An unknown error occured while trying to load your obj file: \n" + e.getMessage());
		}

		Map<Vertex, Integer> vertexMap = new HashMap<>();
		List<Vector3> vertices = new ArrayList<>();
		List<Vector2> uvs = new ArrayList<>();
		List<Vector3> normals = new ArrayList<>();
		List<Vector3> colors = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		for (int k = 0; k < vertexIndices.size(); k++) {
			int vertexIndex = vertexIndices.get(k);
			Vector3 vertex = tempVertices.get(vertexIndex);
			Vector3 color = tempColors.size() > 0 ? tempColors.get(vertexIndex) : Vector3.zero();
			Vector2 uv = tempUvs.size() > 0 ? uv = tempUvs.get(uvIndices.get(k)) : Vector2.zero();
			Vector3 normal = tempNormals.size() > 0 ? tempNormals.get(normalIndices.get(k)) : Vector3.zero();
			Vertex v = new Vertex();
			v.position = vertex;
			v.color = color;
			v.texcoord = uv;
			v.normal = normal;

			if (vertexMap.containsKey(v)) {
				indices.add(vertexMap.get(v));
			}
			else {
				int newIndex = vertices.size();
				vertices.add(vertex);
				colors.add(color);
				uvs.add(uv);
				normals.add(normal);

				vertexMap.put(v, newIndex);
				indices.add(newIndex);
			}
		}

		Mesh mesh = new Mesh();
		mesh.setVertices(vertices.toArray(new Vector3[vertices.size()]));
		mesh.setTriangles(indices.stream().mapToInt(Integer::intValue).toArray());
		if (colors != null) {
			mesh.setColors(colors.toArray(new Vector3[colors.size()]));
		}
		if (uvs != null && uvs.size() > 0) {
			mesh.setUV(uvs.toArray(new Vector2[uvs.size()]));
		}
		if (normals != null && normals.size() > 0) {
			mesh.setNormals(normals.toArray(new Vector3[normals.size()]));
		}
		System.out.printf("Loaded mesh with %d triangles and %d vertices\n", indices.size() / 3, vertices.size());
		return mesh;
	}
}