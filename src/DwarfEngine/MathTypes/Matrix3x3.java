package DwarfEngine.MathTypes;

public class Matrix3x3 {
	private float[][] matrix;
	
	public Matrix3x3() {
		matrix = new float[3][3];
	}
	
	public void setElement(int x, int y, float elem) {
		matrix[x][y] = elem;
	}
	
	public void PrintMatrix() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				System.out.print(matrix[x][y] + ", ");
			}
			System.out.println("\n");
		}
	}
	
	public static Matrix3x3 Identity()
	{
		Matrix3x3 mat = new Matrix3x3();
		mat.matrix[0][0] = 1.0f; mat.matrix[1][0] = 0.0f; mat.matrix[2][0] = 0.0f;
		mat.matrix[0][1] = 0.0f; mat.matrix[1][1] = 1.0f; mat.matrix[2][1] = 0.0f;
		mat.matrix[0][2] = 0.0f; mat.matrix[1][2] = 0.0f; mat.matrix[2][2] = 1.0f;
		return mat;
	}
	public static Matrix3x3 Translate(Vector2 o)
	{
		Matrix3x3 mat = new Matrix3x3();
		mat.matrix[0][0] = 1.0f; mat.matrix[1][0] = 0.0f; mat.matrix[2][0] = o.x;
		mat.matrix[0][1] = 0.0f; mat.matrix[1][1] = 1.0f; mat.matrix[2][1] = o.y;
		mat.matrix[0][2] = 0.0f;	mat.matrix[1][2] = 0.0f;	mat.matrix[2][2] = 1.0f;
		return mat;
	}

	public static Matrix3x3 Rotate(float angleDeg)
	{
		Matrix3x3 mat = new Matrix3x3();
		angleDeg *= Mathf.Deg2Rad;
		mat.matrix[0][0] = Mathf.cos(angleDeg);  mat.matrix[1][0] = Mathf.sin(angleDeg); mat.matrix[2][0] = 0.0f;
		mat.matrix[0][1] = -Mathf.sin(angleDeg); mat.matrix[1][1] = Mathf.cos(angleDeg); mat.matrix[2][1] = 0.0f;
		mat.matrix[0][2] = 0.0f;			 mat.matrix[1][2] = 0.0f;		 mat.matrix[2][2] = 1.0f;
		return mat;
	}

	public static Matrix3x3 Scale(Vector2 s)
	{
		Matrix3x3 mat = new Matrix3x3();
		mat.matrix[0][0] = s.x;   mat.matrix[1][0] = 0.0f; mat.matrix[2][0] = 0.0f;
		mat.matrix[0][1] = 0.0f; mat.matrix[1][1] = s.y;   mat.matrix[2][1] = 0.0f;
		mat.matrix[0][2] = 0.0f;	mat.matrix[1][2] = 0.0f;	mat.matrix[2][2] = 1.0f;
		return mat;
	}
	public static Matrix3x3 Shear(Vector2 s)
	{	
		Matrix3x3 mat = new Matrix3x3();
		mat.matrix[0][0] = 1.0f; mat.matrix[1][0] = s.x;   mat.matrix[2][0] = 0.0f;
		mat.matrix[0][1] = s.y;   mat.matrix[1][1] = 1.0f; mat.matrix[2][1] = 0.0f;
		mat.matrix[0][2] = 0.0f;	mat.matrix[1][2] = 0.0f;	mat.matrix[2][2] = 1.0f;
		return mat;
	}

	public static Matrix3x3 MatrixMultiply(Matrix3x3 matA, Matrix3x3 matB)
	{
		Matrix3x3 matResult = new Matrix3x3();
		for (int c = 0; c < 3; c++)
		{
			for (int r = 0; r < 3; r++)
			{
				matResult.matrix[c][r] = matA.matrix[0][r] * matB.matrix[c][0] +
					                matA.matrix[1][r] * matB.matrix[c][1] +
					                matA.matrix[2][r] * matB.matrix[c][2];
			}
		}
		return matResult;
	}

	public static Vector2 Forward(Matrix3x3 mat, float in_x, float in_y)
	{
		Vector2 out = new Vector2(0, 0);
		out.x = in_x * mat.matrix[0][0] + in_y * mat.matrix[1][0] + mat.matrix[2][0];
		out.y = in_x * mat.matrix[0][1] + in_y * mat.matrix[1][1] + mat.matrix[2][1];
		
		return out;
	}
	public static Matrix3x3 Invert(Matrix3x3 matIn)
	{
		Matrix3x3 matOut = new Matrix3x3();
		float det = matIn.matrix[0][0] * (matIn.matrix[1][1] * matIn.matrix[2][2] - matIn.matrix[1][2] * matIn.matrix[2][1]) -
			matIn.matrix[1][0] * (matIn.matrix[0][1] * matIn.matrix[2][2] - matIn.matrix[2][1] * matIn.matrix[0][2]) +
			matIn.matrix[2][0] * (matIn.matrix[0][1] * matIn.matrix[1][2] - matIn.matrix[1][1] * matIn.matrix[0][2]);

		float idet = 1.0f / det;
		matOut.matrix[0][0] = (matIn.matrix[1][1] * matIn.matrix[2][2] - matIn.matrix[1][2] * matIn.matrix[2][1]) * idet;
		matOut.matrix[1][0] = (matIn.matrix[2][0] * matIn.matrix[1][2] - matIn.matrix[1][0] * matIn.matrix[2][2]) * idet;
		matOut.matrix[2][0] = (matIn.matrix[1][0] * matIn.matrix[2][1] - matIn.matrix[2][0] * matIn.matrix[1][1]) * idet;
		matOut.matrix[0][1] = (matIn.matrix[2][1] * matIn.matrix[0][2] - matIn.matrix[0][1] * matIn.matrix[2][2]) * idet;
		matOut.matrix[1][1] = (matIn.matrix[0][0] * matIn.matrix[2][2] - matIn.matrix[2][0] * matIn.matrix[0][2]) * idet;
		matOut.matrix[2][1] = (matIn.matrix[0][1] * matIn.matrix[2][0] - matIn.matrix[0][0] * matIn.matrix[2][1]) * idet;
		matOut.matrix[0][2] = (matIn.matrix[0][1] * matIn.matrix[1][2] - matIn.matrix[0][2] * matIn.matrix[1][1]) * idet;
		matOut.matrix[1][2] = (matIn.matrix[0][2] * matIn.matrix[1][0] - matIn.matrix[0][0] * matIn.matrix[1][2]) * idet;
		matOut.matrix[2][2] = (matIn.matrix[0][0] * matIn.matrix[1][1] - matIn.matrix[0][1] * matIn.matrix[1][0]) * idet;
		return matOut;
	}
}
