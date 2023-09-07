package Renderer3D.BuiltInShaders;

import DwarfEngine.MathTypes.Vector3;
import DwarfEngine.Texture;
import Renderer3D.Shader;
import Renderer3D.Vertex;

import java.awt.*;

/**
 * A simple shader that outputs a texture with a tint
 */
public class Unlit extends Shader {

	Texture texture = Texture.solidTexture(Color.white);
	private Vector3 tint = null;
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
		if (fastSample) {
			texture.SampleFast(in.texcoord.x, in.texcoord.y, dst);
		} else {
			texture.Sample(in.texcoord.x, in.texcoord.y, dst);
		}
		if (tint != null) Vector3.mul2Vecs(dst, tint, dst);
		return dst;
	}

}
