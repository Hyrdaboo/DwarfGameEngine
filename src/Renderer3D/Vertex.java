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
		v.color = Mathf.Lerp(a.color, b.color, t);
		return v;
	}
	
	static Vertex delta(Vertex a, Vertex b, float mag) {
		Vertex v = new Vertex();
		
		subVecs(b.position, a.position, v.position);
		v.position.divideBy(mag);
		v.position.w /= mag;
		subVecs(b.texcoord, a.texcoord, v.texcoord);
		v.texcoord.divideBy(mag);	
		return v;
	}
	static void add(Vertex a, Vertex b, Vertex v) {
		addVecs(a.position, b.position, v.position);
		addVecs(a.texcoord, b.texcoord, v.texcoord);
	}
	
	private static void subVecs(Vector3 a, Vector3 b, Vector3 p) {
		p.x = a.x-b.x;
		p.y = a.y-b.y;
		p.z = a.z-b.z;
		p.w = a.w-b.w;
	}
	private static void subVecs(Vector2 a, Vector2 b, Vector2 p) {
		p.x = a.x-b.x;
		p.y = a.y-b.y;
	}
	
	private static void addVecs(Vector3 a, Vector3 b, Vector3 p) {
		p.x = a.x+b.x;
		p.y = a.y+b.y;
		p.z = a.z+b.z;
		p.w = a.w+b.w;
	}
	private static void addVecs(Vector2 a, Vector2 b, Vector2 p) {
		p.x = a.x+b.x;
		p.y = a.y+b.y;
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