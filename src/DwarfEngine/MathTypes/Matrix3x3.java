package DwarfEngine.MathTypes;

import Renderer3D.TriangleRasterizer;

/**
 * A 3x3 matrix implementation. This class was originally implemented for 2D
 * image manipulation purposes. But later I decided to scrap that but I still
 * kept this class. If you want to implement your own 2D image manipulation you
 * can do so using {@link TriangleRasterizer} class and manipulate vertices with
 * 3x3 matrices. Though the performance of the rasterizer at the time of writing
 * this is not that great and I am not sure if I'll improve it in the future so
 * I don't know it's here do whatever you please with this.
 */
public final class Matrix3x3 {
	private final float[] matrix = new float[9];

	public Matrix3x3() {}

	private static int i2D(int x, int y) {
		return x + y * 3;
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

	/**
	 * Sets the value of the matrix element at the specified row and column.
	 *
	 * @param x     The row index of the element.
	 * @param y     The column index of the element.
	 * @param value The value to set.
	 * @throws IndexOutOfBoundsException If the provided row or column index is out
	 *                                   of bounds.
	 */
	public void setElement(int x, int y, float value) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("index " + x + ", " + y + " is invalid");
		}
		matrix[i2D(x, y)] = value;
	}

	/**
	 * Retrieves the value of the matrix element at the specified row and column.
	 *
	 * @param x The row index of the element.
	 * @param y The column index of the element.
	 * @return The value of the matrix element at the given indices.
	 * @throws IndexOutOfBoundsException If the provided row or column index is out
	 *                                   of bounds.
	 */
	public float getElement(int x, int y) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("index " + x + ", " + y + " is invalid");
		}
		return matrix[i2D(x, y)];
	}

	/**
	 * Creates and returns an identity matrix.
	 *
	 * @return An identity matrix where all diagonal elements are set to 1.
	 */
	public static Matrix3x3 identityMatrix() {
		Matrix3x3 m = new Matrix3x3();
		m.matrix[i2D(0, 0)] = 1;
		m.matrix[i2D(1, 1)] = 1;
		m.matrix[i2D(2, 2)] = 1;
		return m;
	}

	/**
	 * Creates a translation matrix based on the given translation vector.
	 *
	 * @param translation The translation vector representing the displacement in x
	 *                    and y coordinates.
	 * @return The translation matrix.
	 */
	public static Matrix3x3 translationMatrix(Vector2 translation) {
		Matrix3x3 m = identityMatrix();
		m.matrix[i2D(2, 0)] = translation.x;
		m.matrix[i2D(2, 1)] = translation.y;
		return m;
	}

	/**
	 * Creates a scale matrix based on the given scale vector.
	 *
	 * @param scale The scale vector representing the scaling factors in x and y
	 *              coordinates.
	 * @return The scale matrix.
	 */
	public static Matrix3x3 scaleMatrix(Vector2 scale) {
		Matrix3x3 m = new Matrix3x3();
		m.matrix[i2D(0, 0)] = scale.x;
		m.matrix[i2D(1, 1)] = scale.y;
		m.matrix[i2D(2, 2)] = 1;
		return m;
	}

	/**
	 * Creates a rotation matrix based on the given angle in degrees.
	 *
	 * @param angleDeg The angle in degrees to rotate by.
	 * @return The rotation matrix.
	 */
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

	/**
	 * Creates a shear matrix based on the given shear vector.
	 *
	 * @param shear The shear vector representing the shearing factors in x and y
	 *              coordinates.
	 * @return The shear matrix.
	 */
	public static Matrix3x3 shearMatrix(Vector2 shear) {
		Matrix3x3 m = identityMatrix();
		m.matrix[i2D(1, 0)] = shear.x;
		m.matrix[i2D(0, 1)] = shear.y;
		return m;
	}

	/**
	 * Calculates the inverse of a 3x3 matrix.
	 *
	 * @param m The input 3x3 matrix to invert.
	 * @return The inverted matrix as a new Matrix3x3 object.
	 */
	public static Matrix3x3 invertMatrix(Matrix3x3 m) {
		Matrix3x3 out = new Matrix3x3();
		float detM = m.matrix[i2D(0, 0)]
				* ((m.matrix[i2D(1, 1)] * m.matrix[i2D(2, 2)]) - (m.matrix[i2D(2, 1)] * m.matrix[i2D(1, 2)]))
				- m.matrix[i2D(1, 0)]
						* ((m.matrix[i2D(0, 1)] * m.matrix[i2D(2, 2)]) - (m.matrix[i2D(2, 1)] * m.matrix[i2D(0, 2)]))
				+ m.matrix[i2D(2, 0)]
						* ((m.matrix[i2D(0, 1)] * m.matrix[i2D(1, 2)]) - (m.matrix[i2D(1, 1)] * m.matrix[i2D(0, 2)]));

		float iDetM = 1 / detM;
		out.matrix[i2D(0, 0)] = (m.matrix[i2D(1, 1)] * m.matrix[i2D(2, 2)] - m.matrix[i2D(1, 2)] * m.matrix[i2D(2, 1)])
				* iDetM;
		out.matrix[i2D(1, 0)] = (m.matrix[i2D(2, 0)] * m.matrix[i2D(1, 2)] - m.matrix[i2D(1, 0)] * m.matrix[i2D(2, 2)])
				* iDetM;
		out.matrix[i2D(2, 0)] = (m.matrix[i2D(1, 0)] * m.matrix[i2D(2, 1)] - m.matrix[i2D(2, 0)] * m.matrix[i2D(1, 1)])
				* iDetM;
		out.matrix[i2D(0, 1)] = (m.matrix[i2D(2, 1)] * m.matrix[i2D(0, 2)] - m.matrix[i2D(0, 1)] * m.matrix[i2D(2, 2)])
				* iDetM;
		out.matrix[i2D(1, 1)] = (m.matrix[i2D(0, 0)] * m.matrix[i2D(2, 2)] - m.matrix[i2D(2, 0)] * m.matrix[i2D(0, 2)])
				* iDetM;
		out.matrix[i2D(2, 1)] = (m.matrix[i2D(0, 1)] * m.matrix[i2D(2, 0)] - m.matrix[i2D(0, 0)] * m.matrix[i2D(2, 1)])
				* iDetM;
		out.matrix[i2D(0, 2)] = (m.matrix[i2D(0, 1)] * m.matrix[i2D(1, 2)] - m.matrix[i2D(0, 2)] * m.matrix[i2D(1, 1)])
				* iDetM;
		out.matrix[i2D(1, 2)] = (m.matrix[i2D(0, 2)] * m.matrix[i2D(1, 0)] - m.matrix[i2D(0, 0)] * m.matrix[i2D(1, 2)])
				* iDetM;
		out.matrix[i2D(2, 2)] = (m.matrix[i2D(0, 0)] * m.matrix[i2D(1, 1)] - m.matrix[i2D(0, 1)] * m.matrix[i2D(1, 0)])
				* iDetM;
		return out;
	}

	/**
	 * Multiplies two matrices and returns the resulting matrix.
	 *
	 * @param m1 The first matrix to multiply.
	 * @param m2 The second matrix to multiply.
	 * @return The matrix resulting from the multiplication of m1 and m2.
	 */
	public static Matrix3x3 matrixMultiplyMatrix(Matrix3x3 m1, Matrix3x3 m2) {
		Matrix3x3 out = new Matrix3x3();
		for (int c = 0; c < 3; c++) {
			for (int r = 0; r < 3; r++) {
				out.matrix[i2D(r, c)] = m1.matrix[i2D(0, c)] * m2.matrix[i2D(r, 0)]
						+ m1.matrix[i2D(1, c)] * m2.matrix[i2D(r, 1)] + m1.matrix[i2D(2, c)] * m2.matrix[i2D(r, 2)];
			}
		}
		return out;
	}

	/**
	 * Performs matrix multiplication between a 3x3 matrix and a 2D vector.
	 *
	 * @param m The 3x3 matrix
	 * @param v A {@link Vector2} that gets multiplied
	 * @return The result of matrix multiplication as a new Vector2 object.
	 */
	public static Vector2 matrixMultiplyVector(Matrix3x3 m, Vector2 v) {
		Vector2 out = Vector2.zero();
		out.x = m.matrix[i2D(0, 0)] * v.x + m.matrix[i2D(1, 0)] * v.y + m.matrix[i2D(2, 0)] * v.z;
		out.y = m.matrix[i2D(0, 1)] * v.x + m.matrix[i2D(1, 1)] * v.y + m.matrix[i2D(2, 1)] * v.z;
		out.z = m.matrix[i2D(0, 2)] * v.x + m.matrix[i2D(1, 2)] * v.y + m.matrix[i2D(2, 2)] * v.z;
		return out;
	}
}
