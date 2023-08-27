package Renderer3D;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

/**
 * The Vertex class represents a vertex of a model with all of its various
 * attributes such as position, texture coordinates, color, normal, and world
 * position.
 */
public final class Vertex implements Cloneable {
	public Vector3 position = Vector3.zero();
	public Vector2 texcoord = Vector2.zero();
	public Vector3 color;
	public Vector3 normal = Vector3.up(); // not normalized
	public Vector3 worldPos = Vector3.one();


	static Vertex Lerp(Vertex a, Vertex b, float t) {
		return Lerp(a, b, t, new Vertex());
	}

	static Vertex Lerp(Vertex a, Vertex b, float t, Vertex v) {
		Vector3.Lerp(a.position, b.position, t, v.position);
		Vector2.Lerp(a.texcoord, b.texcoord, t, v.texcoord);
		if (a.color != null && v.color != null) Vector3.Lerp(a.color, b.color, t, v.color);
		Vector3.Lerp(a.normal, b.normal, t, v.normal);
		v.normal.normalized(v.normal);
		Vector3.Lerp(a.worldPos, b.worldPos, t, v.worldPos);
		return v;
	}

	void cloneVertex(Vertex startVertex) {
		this.position.w = startVertex.position.w;
		this.texcoord.x = startVertex.texcoord.x;
		this.texcoord.y = startVertex.texcoord.y;
		this.color.x = startVertex.color.x;
		this.color.y = startVertex.color.y;
		this.color.z = startVertex.color.z;
		this.normal.x = startVertex.normal.x;
		this.normal.y = startVertex.normal.y;
		this.normal.z = startVertex.normal.z;
		this.worldPos.x = startVertex.worldPos.x;
		this.worldPos.y = startVertex.worldPos.y;
		this.worldPos.z = startVertex.worldPos.z;
	}

	static void delta(Vertex a, Vertex b, float mag, Vertex v) {
		v.position.w = (b.position.w - a.position.w) * mag;
		v.texcoord.x = (b.texcoord.x - a.texcoord.x) * mag;
		v.texcoord.y = (b.texcoord.y - a.texcoord.y) * mag;
		v.normal.x = (b.normal.x - a.normal.x) * mag;
		v.normal.y = (b.normal.y - a.normal.y) * mag;
		v.normal.z = (b.normal.z - a.normal.z) * mag;
		v.worldPos.x = (b.worldPos.x - a.worldPos.x) * mag;
		v.worldPos.y = (b.worldPos.y - a.worldPos.y) * mag;
		v.worldPos.z = (b.worldPos.z - a.worldPos.z) * mag;
		if (a.color != null) {
			v.color.x = (b.color.x - a.color.x) * mag;
			v.color.y = (b.color.y - a.color.y) * mag;
			v.color.z = (b.color.z - a.color.z) * mag;
		}
	}

	static void add(Vertex a, Vertex b, float f) {
		a.texcoord.x += b.texcoord.x * f;
		a.texcoord.y += b.texcoord.y * f;
		a.texcoord.z += b.texcoord.z * f;
		a.normal.x += b.normal.x * f;
		a.normal.y += b.normal.y * f;
		a.normal.z += b.normal.z * f;
		a.worldPos.x += b.worldPos.x * f;
		a.worldPos.y += b.worldPos.y * f;
		a.worldPos.z += b.worldPos.z * f;
		a.position.w += b.position.w * f;
		if (a.color != null) {
			a.color.x += b.color.x * f;
			a.color.y += b.color.y * f;
			a.color.z += b.color.z * f;
			a.color.w += b.color.w * f;
		}
	}

	private static void subVecs(Vector3 a, Vector3 b, Vector3 dst) {
		dst.x = a.x - b.x;
		dst.y = a.y - b.y;
		dst.z = a.z - b.z;
	}

	private static void subVecs(Vector2 a, Vector2 b, Vector2 dst) {
		dst.x = a.x - b.x;
		dst.y = a.y - b.y;
	}

	private static void addVecs(Vector3 a, Vector3 b, Vector3 dst) {
		dst.x = a.x + b.x;
		dst.y = a.y + b.y;
		dst.z = a.z + b.z;
	}

	private static void addVecs(Vector2 a, Vector2 b, Vector2 dst) {
		dst.x = a.x + b.x;
		dst.y = a.y + b.y;
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
