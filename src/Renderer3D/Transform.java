package Renderer3D;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

public class Transform {
	public Vector3 position = Vector3.zero();
	public Vector3 rotation = Vector3.zero();
	public Vector3 scale = Vector3.one();
		
	private Matrix4x4 transformMatrix = Matrix4x4.identityMatrix();
	private Matrix4x4 translation = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationX = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationY = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationZ = Matrix4x4.identityMatrix();
	private Matrix4x4 scaleMatrix = Matrix4x4.identityMatrix();
	
	public Matrix4x4 getTransformMatrix() {
		translation.makeTranslation(position);
		rotationX.xRotation(rotation.x * Mathf.Deg2Rad);
		rotationY.yRotation(rotation.y * Mathf.Deg2Rad);
		rotationZ.zRotation(rotation.z * Mathf.Deg2Rad);
		scaleMatrix.scaleMatrix(scale);
		
		transformMatrix = Matrix4x4.identityMatrix();
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, scaleMatrix);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, rotationX);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, rotationY);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, rotationZ);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, translation);
		
		return transformMatrix;
	}
}
