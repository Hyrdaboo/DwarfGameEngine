package Renderer3D;

import java.awt.Color;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public final class Vertex implements Cloneable {
	public Vector3 position = Vector3.zero();
	public Vector2 texcoord = Vector2.zero();
	public Color color = Color.white;
	
	static Vertex Lerp(Vertex a, Vertex b, float t) {
		Vertex v = new Vertex();
		
		v.position = Vector3.Lerp(a.position, b.position, t);
		v.texcoord = Vector2.Lerp(a.texcoord, b.texcoord, t);
		v.color = Mathf.LerpColor(a.color, b.color, t);
		return v;
	}
	
	
	@Override
	public String toString() {
		return position.toString();
	}
	
	@Override
	public Vertex clone() {
		try {
			return (Vertex) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
