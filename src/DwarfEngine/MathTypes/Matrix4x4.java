package DwarfEngine.MathTypes;

public final class Matrix4x4 {
	private float matrix[][];

	public Matrix4x4() {
		matrix = new float[4][4];
	}

	@Override
	public String toString() {
		String s = "";
		s += matrix[0][0] + ", " + matrix[1][0] + ", " + matrix[2][0] + "\n";
		s += matrix[0][1] + ", " + matrix[1][1] + ", " + matrix[2][1] + "\n";
		s += matrix[0][2] + ", " + matrix[1][2] + ", " + matrix[2][2] + "\n";
		return s;
	}

	public void setElement(int x, int y, float element) {
		matrix[x][y] = element;
	}

	public float getElement(int x, int y) {
		return matrix[x][y];
	}

	public static Matrix4x4 identityMatrix() {
		Matrix4x4 mat = new Matrix4x4();
		mat.matrix[0][0] = 1;
		mat.matrix[1][1] = 1;
		mat.matrix[2][2] = 1;
		mat.matrix[3][3] = 1;
		return mat;
	}

	public static Matrix4x4 matrixMultiplyMatrix(Matrix4x4 mat1, Matrix4x4 mat2) {
		Matrix4x4 mat = new Matrix4x4();
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				mat.matrix[r][c] = mat1.matrix[r][0] * mat2.matrix[0][c] + mat1.matrix[r][1] * mat2.matrix[1][c]
						+ mat1.matrix[r][2] * mat2.matrix[2][c] + mat1.matrix[r][3] * mat2.matrix[3][c];
			}
		}
		return mat;
	}

	public Vector3 MultiplyByVector(Vector3 vec) {
		Vector3 out = new Vector3(0, 0, 0);
		out.x = vec.x * matrix[0][0] + vec.y * matrix[1][0] + vec.z * matrix[2][0] + matrix[3][0];
		out.y = vec.x * matrix[0][1] + vec.y * matrix[1][1] + vec.z * matrix[2][1] + matrix[3][1];
		out.z = vec.x * matrix[0][2] + vec.y * matrix[1][2] + vec.z * matrix[2][2] + matrix[3][2];
		out.w = vec.x * matrix[0][3] + vec.y * matrix[1][3] + vec.z * matrix[2][3] + matrix[3][3];
		return out;
	}

	public static void PerspectiveProjection(float fov, float aspectRatio, float near, float far,
			Matrix4x4 projectionMatrix) {
		fov = Mathf.Clamp(fov, 1, 179);

		float fovRad = 1.0f / Mathf.tan(fov * 0.5f * Mathf.Deg2Rad);

		projectionMatrix.matrix[0][0] = aspectRatio * fovRad;
		projectionMatrix.matrix[1][1] = fovRad;
		projectionMatrix.matrix[2][2] = far / (far - near);
		projectionMatrix.matrix[2][3] = 1.0f;
		projectionMatrix.matrix[3][2] = (-far * near) / (far - near);
		projectionMatrix.matrix[3][3] = 0.0f;
	}

	public void makeTranslation(Vector3 pos) {
		matrix[0][0] = 1;
		matrix[1][1] = 1;
		matrix[2][2] = 1;
		matrix[3][3] = 1;
		matrix[3][0] = pos.x;
		matrix[3][1] = pos.y;
		matrix[3][2] = pos.z;
	}

	public static Matrix4x4 xRotation(float angleRad) {
		Matrix4x4 m = new Matrix4x4();
		m.matrix[0][0] = 1;
		m.matrix[1][1] = Mathf.cos(angleRad);
		m.matrix[1][2] = Mathf.sin(angleRad);
		m.matrix[2][1] = -Mathf.sin(angleRad);
		m.matrix[2][2] = Mathf.cos(angleRad);
		m.matrix[3][3] = 1;
		return m;
	}

	public static Matrix4x4 yRotation(float angleRad) {
		Matrix4x4 m = new Matrix4x4();
		m.matrix[0][0] = Mathf.cos(angleRad);
		m.matrix[0][2] = -Mathf.sin(angleRad);
		m.matrix[1][1] = 1;
		m.matrix[2][0] = Mathf.sin(angleRad);
		m.matrix[2][2] = Mathf.cos(angleRad);
		m.matrix[3][3] = 1;
		return m;
	}

	public static Matrix4x4 zRotation(float angleRad) {
		Matrix4x4 m = new Matrix4x4();
		m.matrix[0][0] = Mathf.cos(angleRad);
		m.matrix[0][1] = Mathf.sin(angleRad);
		m.matrix[1][0] = -Mathf.sin(angleRad);
		m.matrix[1][1] = Mathf.cos(angleRad);
		m.matrix[2][2] = 1;
		m.matrix[3][3] = 1;
		return m;
	}

	public static Matrix4x4 GetRotation(Vector3 angles) {
		Matrix4x4 x = xRotation(angles.x * Mathf.Deg2Rad);
		Matrix4x4 y = yRotation(angles.y * Mathf.Deg2Rad);
		Matrix4x4 z = zRotation(angles.z * Mathf.Deg2Rad);

		// yxz
		Matrix4x4 combined = matrixMultiplyMatrix(z, x);
		combined = matrixMultiplyMatrix(combined, y);

		return combined;
	}

	public void scaleMatrix(Vector3 scale) {
		matrix[0][0] = scale.x;
		matrix[1][1] = scale.y;
		matrix[2][2] = scale.z;
		matrix[3][3] = 1;
	}

	public static Matrix4x4 MatrixPointAt(Vector3 pos, Vector3 target, Vector3 up) {
		Vector3 newForward = Vector3.subtract2Vecs(target, pos);
		newForward.Normalize();

		Vector3 a = Vector3.mulVecFloat(newForward, Vector3.Dot(up, newForward));
		Vector3 newUp = Vector3.subtract2Vecs(up, a);
		newUp.Normalize();

		Vector3 newRight = Vector3.Cross(newUp, newForward);

		Matrix4x4 mat = new Matrix4x4();
		mat.matrix[0][0] = newRight.x;
		mat.matrix[0][1] = newRight.y;
		mat.matrix[0][2] = newRight.z;
		mat.matrix[0][3] = 0.0f;
		mat.matrix[1][0] = newUp.x;
		mat.matrix[1][1] = newUp.y;
		mat.matrix[1][2] = newUp.z;
		mat.matrix[1][3] = 0.0f;
		mat.matrix[2][0] = newForward.x;
		mat.matrix[2][1] = newForward.y;
		mat.matrix[2][2] = newForward.z;
		mat.matrix[2][3] = 0.0f;
		mat.matrix[3][0] = pos.x;
		mat.matrix[3][1] = pos.y;
		mat.matrix[3][2] = pos.z;
		mat.matrix[3][3] = 1.0f;
		return mat;
	}

	/**
	 * Only for Rotation/Translation Matrices. Copied from Javidx9 like most of the
	 * other functions :)
	 */
	public static Matrix4x4 inverseMatrix(Matrix4x4 mat) {
		Matrix4x4 inversed = new Matrix4x4();
		inversed.matrix[0][0] = mat.matrix[0][0];
		inversed.matrix[0][1] = mat.matrix[1][0];
		inversed.matrix[0][2] = mat.matrix[2][0];
		inversed.matrix[0][3] = 0.0f;
		inversed.matrix[1][0] = mat.matrix[0][1];
		inversed.matrix[1][1] = mat.matrix[1][1];
		inversed.matrix[1][2] = mat.matrix[2][1];
		inversed.matrix[1][3] = 0.0f;
		inversed.matrix[2][0] = mat.matrix[0][2];
		inversed.matrix[2][1] = mat.matrix[1][2];
		inversed.matrix[2][2] = mat.matrix[2][2];
		inversed.matrix[2][3] = 0.0f;
		inversed.matrix[3][0] = -(mat.matrix[3][0] * inversed.matrix[0][0] + mat.matrix[3][1] * inversed.matrix[1][0]
				+ mat.matrix[3][2] * inversed.matrix[2][0]);
		inversed.matrix[3][1] = -(mat.matrix[3][0] * inversed.matrix[0][1] + mat.matrix[3][1] * inversed.matrix[1][1]
				+ mat.matrix[3][2] * inversed.matrix[2][1]);
		inversed.matrix[3][2] = -(mat.matrix[3][0] * inversed.matrix[0][2] + mat.matrix[3][1] * inversed.matrix[1][2]
				+ mat.matrix[3][2] * inversed.matrix[2][2]);
		inversed.matrix[3][3] = 1.0f;
		return inversed;
	}
}
