package Renderer3D;

import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

public class Camera {
	public final Transform transform = new Transform();
	public float fov = 60;
	public float near = .1f;
	public float far = 1000;
	
	public Matrix4x4 getViewMatrix() {
		transform.getTransformMatrix();
		
		Vector3 targetForward = Vector3.add2Vecs(transform.position, transform.forward);
		Matrix4x4 cameraMatrix = Matrix4x4.MatrixPointAt(transform.position, targetForward, transform.up);
		Matrix4x4 viewMatrix = Matrix4x4.inverseMatrix(cameraMatrix);
		
		return viewMatrix;
	}
}
