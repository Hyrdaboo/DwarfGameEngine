package DwarfEngine.MathTypes;

import DwarfEngine.Core.Debug;

public final class Matrix3x3 {
	private float[] matrix;
	
	public Matrix3x3() {
		matrix = new float[9];
	}
	private static int i2D(int x, int y) {
		return x + y*3;
	}
	private boolean inBounds(int x, int y) {
		return x < 3 && x >= 0 && y < 3 && y >= 0;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += matrix[i2D(0, 0)] + ", " + matrix[i2D(1, 0)] + ", " + matrix[i2D(2, 0)] + "\n";
		s += matrix[i2D(0, 1)] + ", " + matrix[i2D(1, 1)] + ", " + matrix[i2D(2, 1)] + "\n";
		s += matrix[i2D(0, 2)] + ", " + matrix[i2D(1, 2)] + ", " + matrix[i2D(2, 2)];
		return s;
	}
	
	public void setElement(int x, int y, float value) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("index "+x+", "+y+" is invalid");
		}
		matrix[i2D(x, y)] = value;
	}
	public float getElement(int x, int y) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("index "+x+", "+y+" is invalid");
		}
		return matrix[i2D(x, y)];
	}
	
	public static Matrix3x3 identityMatrix() {
		Matrix3x3 m = new Matrix3x3();
		m.matrix[i2D(0, 0)] = 1;
		m.matrix[i2D(1, 1)] = 1;
		m.matrix[i2D(2, 2)] = 1;
		return m;
	}
	public static Matrix3x3 translationMatrix(Vector2 translation) {
		Matrix3x3 m = identityMatrix();
		m.matrix[i2D(2, 0)] = translation.x;
		m.matrix[i2D(2, 1)] = translation.y;
		return m;
	}
	public static Matrix3x3 scaleMatrix(Vector2 scale) {
		Matrix3x3 m = new Matrix3x3();
		m.matrix[i2D(0, 0)] = scale.x;
		m.matrix[i2D(1, 1)] = scale.y;
		m.matrix[i2D(2, 2)] = 1;
		return m;
	}
	public static Matrix3x3 rotationMatrix(float angleDeg) {
		angleDeg *= Mathf.Deg2Rad;
		Matrix3x3 m = new Matrix3x3();
		m.matrix[i2D(0, 0)] = Mathf.cos(angleDeg);
		m.matrix[i2D(1, 0)] = Mathf.sin(angleDeg);
		m.matrix[i2D(0, 1)] = -Mathf.sin(angleDeg);
		m.matrix[i2D(1, 1)] = Mathf.cos(angleDeg);
		m.matrix[i2D(2, 2)] = 1;
		return m;
	}
	public static Matrix3x3 shearMatrix(Vector2 shear) {
		Matrix3x3 m = identityMatrix();
		m.matrix[i2D(1, 0)] = shear.x;
		m.matrix[i2D(0, 1)] = shear.y;
		return m;
	}
	
	public static Matrix3x3 invertMatrix(Matrix3x3 m) {
		Matrix3x3 out = new Matrix3x3();
		float detM = m.matrix[i2D(0,0)]*((m.matrix[i2D(1,1)]*m.matrix[i2D(2,2)])-(m.matrix[i2D(2,1)]*m.matrix[i2D(1,2)])) -
					 m.matrix[i2D(1,0)]*((m.matrix[i2D(0,1)]*m.matrix[i2D(2,2)])-(m.matrix[i2D(2,1)]*m.matrix[i2D(0,2)])) +
					 m.matrix[i2D(2,0)]*((m.matrix[i2D(0,1)]*m.matrix[i2D(1,2)])-(m.matrix[i2D(1,1)]*m.matrix[i2D(0,2)]));
		
		float iDetM = 1/detM;
		out.matrix[i2D(0, 0)] = (m.matrix[i2D(1, 1)]*m.matrix[i2D(2, 2)] - m.matrix[i2D(1, 2)]*m.matrix[i2D(2, 1)])*iDetM;
		out.matrix[i2D(1, 0)] = (m.matrix[i2D(2, 0)]*m.matrix[i2D(1, 2)] - m.matrix[i2D(1, 0)]*m.matrix[i2D(2, 2)])*iDetM;
		out.matrix[i2D(2, 0)] = (m.matrix[i2D(1, 0)]*m.matrix[i2D(2, 1)] - m.matrix[i2D(2, 0)]*m.matrix[i2D(1, 1)])*iDetM;
		out.matrix[i2D(0, 1)] = (m.matrix[i2D(2, 1)]*m.matrix[i2D(0, 2)] - m.matrix[i2D(0, 1)]*m.matrix[i2D(2, 2)])*iDetM;
		out.matrix[i2D(1, 1)] = (m.matrix[i2D(0, 0)]*m.matrix[i2D(2, 2)] - m.matrix[i2D(2, 0)]*m.matrix[i2D(0, 2)])*iDetM;
		out.matrix[i2D(2, 1)] = (m.matrix[i2D(0, 1)]*m.matrix[i2D(2, 0)] - m.matrix[i2D(0, 0)]*m.matrix[i2D(2, 1)])*iDetM;
		out.matrix[i2D(0, 2)] = (m.matrix[i2D(0, 1)]*m.matrix[i2D(1, 2)] - m.matrix[i2D(0, 2)]*m.matrix[i2D(1, 1)])*iDetM;
		out.matrix[i2D(1, 2)] = (m.matrix[i2D(0, 2)]*m.matrix[i2D(1, 0)] - m.matrix[i2D(0, 0)]*m.matrix[i2D(1, 2)])*iDetM;
		out.matrix[i2D(2, 2)] = (m.matrix[i2D(0, 0)]*m.matrix[i2D(1, 1)] - m.matrix[i2D(0, 1)]*m.matrix[i2D(1, 0)])*iDetM;
		return out;
	}
	public static Matrix3x3 matrixMultiplyMatrix(Matrix3x3 m1, Matrix3x3 m2) {
		Matrix3x3 out = new Matrix3x3();
		for (int c = 0; c < 3; c++) {
			for (int r = 0; r < 3; r++) {
				out.matrix[i2D(r, c)] = m1.matrix[i2D(0, c)]*m2.matrix[i2D(r, 0)] +
											m1.matrix[i2D(1, c)]*m2.matrix[i2D(r, 1)] +
											m1.matrix[i2D(2, c)]*m2.matrix[i2D(r, 2)];
			}
		}
		return out;
	}
	public static Vector2 matrixMultiplyVector(Matrix3x3 m, Vector2 v) {
		Vector2 out = Vector2.zero();
		out.x = m.matrix[i2D(0, 0)]*v.x + m.matrix[i2D(1, 0)]*v.y + m.matrix[i2D(2, 0)]*v.z;
		out.y = m.matrix[i2D(0, 1)]*v.x + m.matrix[i2D(1, 1)]*v.y + m.matrix[i2D(2, 1)]*v.z;
		out.z = m.matrix[i2D(0, 2)]*v.x + m.matrix[i2D(1, 2)]*v.y + m.matrix[i2D(2, 2)]*v.z;
		return out;
	}
}
