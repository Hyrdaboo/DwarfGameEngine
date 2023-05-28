package Renderer3D;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public final class Vertex implements Cloneable {
	public Vector3 position = Vector3.zero();
	public Vector2 texcoord = Vector2.zero();
	public Vector3 color = Vector3.zero();
	public Vector3 normal = Vector3.one();
	
	static Vertex Lerp(Vertex a, Vertex b, float t) {
		Vertex v = new Vertex();
		
		v.position = Vector3.Lerp(a.position, b.position, t);
		v.texcoord = Vector2.Lerp(a.texcoord, b.texcoord, t);
		v.color = Vector3.Lerp(a.color, b.color, t);
		v.normal = Vector3.Lerp(a.normal, b.normal, t);
		return v;
	}
	
	static Vertex delta(Vertex a, Vertex b, float mag) {
		Vertex v = new Vertex();
		
		v.position.w = (b.position.w - a.position.w) / mag;
		subVecs(b.texcoord, a.texcoord, v.texcoord);
		v.texcoord.divideBy(mag);	
		subVecs(b.color, a.color, v.color);
		v.color.divideBy(mag);
		subVecs(b.normal, a.normal, v.normal);
		v.normal.divideBy(mag);
		return v;
	}
	static void add(Vertex a, Vertex b, Vertex v) {
		v.position.w = a.position.w+b.position.w;
		addVecs(a.texcoord, b.texcoord, v.texcoord);
		addVecs(a.color, b.color, v.color);
		addVecs(a.normal, b.normal, v.normal);
	}
	
	private static void subVecs(Vector3 a, Vector3 b, Vector3 p) {
		p.x = a.x-b.x;
		p.y = a.y-b.y;
		p.z = a.z-b.z;
	}
	private static void subVecs(Vector2 a, Vector2 b, Vector2 p) {
		p.x = a.x-b.x;
		p.y = a.y-b.y;
	}
	
	private static void addVecs(Vector3 a, Vector3 b, Vector3 p) {
		p.x = a.x+b.x;
		p.y = a.y+b.y;
		p.z = a.z+b.z;
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
