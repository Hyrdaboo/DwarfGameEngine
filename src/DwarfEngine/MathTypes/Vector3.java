package DwarfEngine.MathTypes;

import DwarfEngine.Engine;

public final class Vector3 {
	public float x = 0;
	public float y = 0;
	public float z = 0;
	
	public static final Vector3 zero = new Vector3(0, 0, 0);
	public static final Vector3 one = new Vector3(1, 1, 1);
	
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public void PrintVector() {
		Engine.PrintLn(x + ", " + y + ", " + z);
	}
	public void addTo(Vector3 vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}
	public void addTo(float a) {
		x += a;
		y += a;
		z += a;
	}
	public void subtractFrom(Vector3 vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}
	public void subtractFrom(float a) {
		x -= a;
		y -= a;
		z -= a;
	}
	public void multiplyBy(float a) {
		x *= a;
		y *= a;
		z *= a;
	}
	public static Vector3 mulVecFloat(Vector3 vec, float num) {
		return new Vector3(vec.x*num, vec.y*num, vec.z*num);
	}
	public static Vector3 mul2Vecs(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x*vec2.x, vec1.y*vec2.y, vec1.z*vec2.z);
	}
	public static Vector3 div2Vecs(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x/vec2.x, vec1.y/vec2.y, vec1.z/vec2.z);
	}
	public static Vector3 subtract2Vecs(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x-vec2.x, vec1.y-vec2.y, vec1.z-vec2.z);
	}
	public static Vector3 add2Vecs(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x+vec2.x, vec1.y+vec2.y, vec1.z+vec2.z);
	}
	
	public float magnitude() {
		return (float) Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
	}
	
	public static Vector3 Cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
	}
	public static float Dot(Vector3 a, Vector3 b) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	public void Normalize() {
		x /= magnitude();
		y /= magnitude();
		z /= magnitude();
	}
	public Vector3 normalized() {
		return new Vector3(x/magnitude(), y/magnitude(), z/magnitude());
	}
}
