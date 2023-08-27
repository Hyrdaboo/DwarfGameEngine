package DwarfEngine.MathTypes;

import DwarfEngine.Pool;

import java.util.Objects;

/**
 * Represents a three-dimensional vector.
 */
public final class Vector3 {

	public static final Pool<Vector3> POOL = new Pool<>(Vector3::new);

	public Vector3() {
	}

	public float x = 0;
	public float y = 0;
	public float z = 0;
	/**
	 * Need a fourth term for matrix operations Do not directly edit this value!
	 **/
	public float w = 1;

	public static Vector3 zero() {
		return new Vector3(0, 0, 0);
	}

	public static Vector3 one() {
		return new Vector3(1, 1, 1);
	}

	public static Vector3 up() {
		return new Vector3(0, 1, 0);
	}

	public static Vector3 down() {
		return new Vector3(0, -1, 0);
	}

	public static Vector3 forward() {
		return new Vector3(0, 0, 1);
	}

	public static Vector3 back() {
		return new Vector3(0, 0, -1);
	}

	public static Vector3 right() {
		return new Vector3(1, 0, 0);
	}

	public static Vector3 left() {
		return new Vector3(-1, 0, 0);
	}

	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		final Vector3 other = (Vector3) obj;
		if (this.x == other.x && this.y == other.y && this.z == other.z) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}

	public Vector3(Vector2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	public void addTo(Vector3 vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	public void addTo(Vector3 vec, float s) {
		x += vec.x * s;
		y += vec.y * s;
		z += vec.z * s;
	}

	public void addTo(Vector3 vec, Vector3 s, float t) {
		x += vec.x * s.x * t;
		y += vec.y * s.y * t;
		z += vec.z * s.z * t;
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

	public void divideBy(float a) {
		x /= a;
		y /= a;
		z /= a;
	}

	public static Vector3 mulVecFloat(Vector3 vec, float num) {
		return mulVecFloat(vec, num, new Vector3());
	}

	public static Vector3 mulVecFloat(Vector3 vec, float num, Vector3 dst) {
		dst.x = vec.x * num;
		dst.y = vec.y * num;
		dst.z = vec.z * num;
		return dst;
	}

	public static Vector3 divide2Vecs(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x / vec2.x, vec1.y / vec2.y, vec1.z / vec2.z);
	}

	public static Vector3 subtract2Vecs(Vector3 vec1, Vector3 vec2) {
		return subtract2Vecs(vec1, vec2, new Vector3());
	}

	public static Vector3 subtract2Vecs(Vector3 vec1, Vector3 vec2, Vector3 dst) {
		dst.x = vec1.x - vec2.x;
		dst.y = vec1.y - vec2.y;
		dst.z = vec1.z - vec2.z;
		return dst;
	}

	public static Vector3 add2Vecs(Vector3 vec1, Vector3 vec2) {
		return add2Vecs(vec1, vec2, new Vector3());
	}

	public static Vector3 add2Vecs(Vector3 vec1, Vector3 vec2, Vector3 dst) {
		dst.x = vec1.x + vec2.x;
		dst.y = vec1.y + vec2.y;
		dst.z = vec1.z + vec2.z;
		return dst;
	}

	public static Vector3 mul2Vecs(Vector3 vec1, Vector3 vec2) {
		Vector3 dst = new Vector3();
		mul2Vecs(vec1, vec2, dst);
		return dst;
	}

	public static void mul2Vecs(Vector3 vec1, Vector3 vec2, Vector3 dst) {
		dst.x = vec1.x * vec2.x;
		dst.y = vec1.y * vec2.y;
		dst.z = vec1.z * vec2.z;
	}

	/**
	 * Calculates the magnitude (length) of the vector.
	 *
	 * @return The magnitude of the vector.
	 */
	public float magnitude() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	/**
	 * Calculates the cross product of two vectors.
	 *
	 * @param a The first vector.
	 * @param b The second vector.
	 * @return The cross product of the two vectors.
	 */
	public static Vector3 Cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	/**
	 * Calculates the dot product of two vectors.
	 *
	 * @param a The first vector.
	 * @param b The second vector.
	 * @return The dot product of the two vectors.
	 */
	public static float Dot(Vector3 a, Vector3 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	/**
	 * Normalizes this vector making it a unit vector.
	 */
	public void Normalize() {
		x /= magnitude();
		y /= magnitude();
		z /= magnitude();
	}

	/**
	 * Returns a normalized version of the vector. If the vector's magnitude is
	 * zero, returns a zero vector.
	 *
	 * @return The normalized vector.
	 */
	public Vector3 normalized() {
		return normalized(1f, new Vector3());
	}

	public Vector3 normalized(Vector3 dst) {
		return normalized(1f, dst);
	}

	public Vector3 normalized(float magnitude, Vector3 dst) {
		float scale = magnitude / Math.max(magnitude(), 1e-38f);
		return mulVecFloat(this, scale, dst);
	}

	/**
	 * Calculates the distance between two vectors.
	 *
	 * @param from The starting vector.
	 * @param to   The target vector.
	 * @return The distance between the two vectors.
	 */
	public static float distance(Vector3 from, Vector3 to) {
		Vector3 v = Vector3.subtract2Vecs(to, from);
		return v.magnitude();
	}

	/**
	 * Performs a linear interpolation between two vectors.
	 *
	 * @param a The starting vector.
	 * @param b The target vector.
	 * @param t The interpolation parameter. Should be between 0 and 1.
	 * @return The interpolated vector.
	 */
	public static Vector3 Lerp(Vector3 a, Vector3 b, float t) {
		return Lerp(a, b, t, new Vector3());
	}

	public static Vector3 Lerp(Vector3 a, Vector3 b, float t, Vector3 dst) {
		dst.x = a.x + (b.x - a.x) * t;
		dst.y = a.y + (b.y - a.y) * t;
		dst.z = a.z + (b.z - a.z) * t;
		dst.w = a.w + (b.w - a.w) * t;
		return dst;
	}

	/**
	 * Clamps each component of the vector to the range [0, 1].
	 *
	 * @param v The vector to clamp.
	 */
	public static void Clamp01(Vector3 v) {
		v.x = Mathf.clamp01(v.x);
		v.y = Mathf.clamp01(v.y);
		v.z = Mathf.clamp01(v.z);
	}
}
