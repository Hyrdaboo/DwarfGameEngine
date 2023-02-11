package DwarfEngine.MathTypes;

import java.util.Objects;

public final class Vector2 {
	public float x = 0;
	public float y = 0;
	
	public static Vector2 zero() {
		return new Vector2(0, 0);
	}
	public static Vector2 one() {
		return new Vector2(1, 1);
	}
	public static Vector2 up() {
		return new Vector2(0, 1);
	}
	public static Vector2 down() {
		return new Vector2(0, -1);
	}
	public static Vector2 right() {
		return new Vector2(1, 0);
	}
	public static Vector2 left() {
		return new Vector2(-1, 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		
		final Vector2 other = (Vector2) obj;
		if (this.x == other.x && this.y == other.y) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return x + ", " + y;
	}
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void addTo(float a) {
		x += a;
		y += a;
	}
	public void addTo(Vector2 v) {
		x += v.x;
		y += v.y;
	}
	public void subtractFrom(float a) {
		x -= a;
		y -= a;
	}
	public void subtractFrom(Vector2 v) {
		x -= v.x;
		y -= v.y;
	}
	public void multiplyBy(float a) {
		x *= a;
		y *= a;
	}
	public void divideBy(float a) {
		x /= a;
		y /= a;
	}
	
	public static Vector2 mulVecFloat(Vector3 vec, float num) {
		return new Vector2(vec.x*num, vec.y*num);
	}
	public static Vector2 divide2Vecs(Vector2 a, Vector2 b) {
		return new Vector2(a.x/b.x, a.y/b.y);
	}
	public static Vector2 subtract2Vecs(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static Vector2 add2Vecs(Vector2 a, Vector2 b) {
		return new Vector2(a.x+b.x, a.y+b.y);
	}
	
	public float magnitude() {
		return (float) Math.sqrt((x*x+y*y));
	}
	
	public static float Dot(Vector2 a, Vector2 b) {
		return a.x*b.x + a.y*b.y;
	}
	
	public void Normalize() {
		x /= magnitude();
		y /= magnitude();
	}
	
	public Vector2 normalized() {
		return new Vector2(x/magnitude(), y/magnitude());
	}
	
	public static float distance(Vector2 from, Vector2 to) {
		Vector2 v = Vector2.subtract2Vecs(to, from);
		return v.magnitude();
	}
	
	public static float InverseLerp(Vector2 a, Vector2 b, Vector2 value)
    {
        Vector2 AB = Vector2.subtract2Vecs(b, a);
        Vector2 AV = Vector2.subtract2Vecs(value, a);
        return Vector2.Dot(AV, AB) / Vector2.Dot(AB, AB);
    }
	public static Vector2 Lerp(Vector2 a, Vector2 b, float t) {
		Vector2 v = Vector2.zero(); 
		v.x = Mathf.Lerp(a.x, b.x, t);
		v.y = Mathf.Lerp(a.y, b.y, t);
		return v;
	}
}
