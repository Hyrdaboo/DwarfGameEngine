package Renderer3D;

import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

/**
 * The <code>Transform</code> class represents the transformation of an object in 3D space.
 * It encapsulates the position, rotation, and scale of the object, as well as provides
 * utility methods for generating transformation matrices.
 */
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

	public Matrix4x4 getRotationMatrix() {
		rotationMatrix = Matrix4x4.GetRotation(rotation);
		return rotationMatrix;
	}

	public Matrix4x4 getTranslationMatrix() {
		translation.makeTranslation(position);
		return translation;
	}

	public Matrix4x4 getScaleMatrix() {
		scaleMatrix.scaleMatrix(scale);
		return scaleMatrix;
	}

	/**
     * Retrieves the transformation matrix of the object.
     *
     * @return The transformation matrix.
     */
	public Matrix4x4 getMatrixTRS() {

		transformMatrix = Matrix4x4.identityMatrix();
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, getScaleMatrix());
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, getRotationMatrix());
		transformMatrix = Matrix4x4.matrixMultiplyMatrix(transformMatrix, getTranslationMatrix());

		forward = rotationMatrix.MultiplyByVector(Vector3.forward());
		up = rotationMatrix.MultiplyByVector(Vector3.up());
		right = rotationMatrix.MultiplyByVector(Vector3.right());

		return transformMatrix;
	}

}
