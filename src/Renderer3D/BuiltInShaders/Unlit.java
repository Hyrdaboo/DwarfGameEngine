package Renderer3D.BuiltInShaders;

import java.awt.Color;

import DwarfEngine.Texture;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Shader;
import Renderer3D.Vertex;

/**
 * A simple shader that outputs a texture with a tint
 */
public class Unlit extends Shader {

	Texture texture = Texture.solidTexture(Color.white);
	private Vector3 tint = Vector3.one();
	public boolean fastSample = true;

	public Unlit() {
	}

	public void setTint(Vector3 tint) {
		if (tint == null)
			return;
		this.tint = new Vector3(tint);
	}

	public Vector3 getTint() {
		return tint;
	}

	public void setTexture(Texture texture) {
		if (texture == null)
			return;
		this.texture = texture;
	}

	@Override
	public Vector3 Fragment(Vertex in, Vector3 dst) {
		Vector3 col;
		if (fastSample) {
			col = texture.SampleFast(in.texcoord.x, in.texcoord.y, dst);
		} else {
			col = texture.Sample(in.texcoord.x, in.texcoord.y, dst);
		}
		Vector3.mul2Vecs(col, tint, col);
		return col;
	}

}
