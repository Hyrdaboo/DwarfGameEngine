package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Shader;
import Renderer3D.Vertex;

public class VertexColor extends Shader {

	@Override
	public Vector3 Fragment(Vertex in) {
		return in.color;
	}

}
