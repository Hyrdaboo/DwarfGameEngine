package Renderer3D;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

public class Transform {
	public Vector3 position = Vector3.zero();
	public Vector3 rotation = Vector3.zero();
	public Vector3 scale = Vector3.one();
	
	public Vector3 forward = Vector3.forward();
	public Vector3 up = Vector3.up();
	public Vector3 right = Vector3.right();
		
	private Matrix4x4 transformMatrix = Matrix4x4.identityMatrix();
	private Matrix4x4 translation = Matrix4x4.identityMatrix();
	private Matrix4x4 rotationMatrix = Matrix4x4.identityMatrix();
	private Matrix4x4 scaleMatrix = Matrix4x4.identityMatrix();
	
	public Matrix4x4 getTransformMatrix() {
		translation.makeTranslation(position);
		rotationMatrix = Matrix4x4.GetRotation(rotation);
		scaleMatrix.scaleMatrix(scale);
		
		transformMatrix = Matrix4x4.identityMatrix();
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, scaleMatrix);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, rotationMatrix);
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, translation);
		
		forward = rotationMatrix.MultiplyByVector(Vector3.forward());
		up = rotationMatrix.MultiplyByVector(Vector3.up());
		right = rotationMatrix.MultiplyByVector(Vector3.right());
		
		return transformMatrix;
	}
	
	
}
