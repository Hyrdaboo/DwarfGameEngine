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

	public Unlit() {}
	/**
	 * Initializes the shader texture directly from a file path.<br>
	 * Not that you can also use the {@link #setTexture(Texture)} method.
	 *
	 * @param texturePath The path to the texture file.
	 */
	public Unlit(String texturePath) {
		try {
			texture.LoadFromFile(texturePath);
		} catch (Exception e) {
			System.out.println("Couldn't load texture");
		}
	}

	public void setTint(Vector3 tint) {
		if (tint == null) return;
		this.tint = new Vector3(tint);
	}

	public Vector3 getTint() {
		return tint;
	}

	public void setTexture(Texture texture) {
		if (texture == null) return;
		this.texture = texture;
	}

	Vector3 magenta = new Vector3(1, 0, 1);

	@Override
	public Vector3 Fragment(Vertex in) {
		Vector3 col = null;
		if (fastSample) {
			col = texture.SampleFast(in.texcoord.x, in.texcoord.y);
		}
		else {
			col = texture.Sample(in.texcoord.x, in.texcoord.y);
		}
		col = Vector3.mul2Vecs(col, tint);
		return col;
	}

}
