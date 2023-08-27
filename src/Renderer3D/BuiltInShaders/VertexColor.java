package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Shader;
import Renderer3D.Vertex;

/**
 * The simplest shader that just outputs the vertex colors <code>in.color</code>
 */
public class VertexColor extends Shader {

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		return in.color;
	}

}
