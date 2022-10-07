package DwarfEngine.MathTypes;

public final class Vector2 {
	public float x = 0;
	public float y = 0;
	
	public static final Vector2 zero = new Vector2(0, 0); 
	public static final Vector2 one = new Vector2(1, 1); 
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2 rotatePoint(Vector2 point, Vector2 pivot, float angle) {
		angle *= Mathf.Deg2Rad;
		float xTrans = point.x - pivot.x;
		float yTrans = point.y - pivot.y;
		float x = xTrans*Mathf.cos(angle) - yTrans*Mathf.sin(angle);
		float y = xTrans*Mathf.sin(angle) + yTrans*Mathf.cos(angle);
		x += pivot.x;
		y += pivot.y;
		return new Vector2(x, y);
	}
	
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x+b.x, a.y+b.y);
	}
	public static Vector2 subtract(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	public static Vector2 multiply(Vector2 v, float a) {
		return new Vector2(v.x*a, v.y*a);
	}
	public static Vector2 divide(Vector2 v, float a) {
		return new Vector2(v.x/a, v.y/a);
	}
	public float magnitude() {
		return (float)Math.sqrt((x*x+y*y));
	}
	
	public void Normalize() {
		x /= magnitude();
		y /= magnitude();
	}
	
	public Vector2 normalized() {
		return new Vector2(x/magnitude(), y/magnitude());
	}
}
