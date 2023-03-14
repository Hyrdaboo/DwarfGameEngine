package Renderer3D;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector3;

public class Camera {
	public final Transform transform = new Transform();
	float fov = 60;
	float near = .1f;
	float far = 1000;
	
	public void SetFov(float fov) {
		fov = Mathf.Clamp(fov, 0.01f, 179);
		this.fov = fov;
	}
	public float GetFov () {
		return fov;
	}
	public void SetNear(float near) {
		near = Mathf.Clamp(near, 0.01f, far);
		this.near = near;
	}
	public float GetNear() {
		return near;
	}
	public void SetFar(float far) {
		far = far < near ? near + 0.01f : far;
		this.far = far;
	}
	public float GetFar() {
		return far;
	}
	
	public Matrix4x4 getViewMatrix() {
		transform.getTransformMatrix();
		
		Vector3 targetForward = Vector3.add2Vecs(transform.position, transform.forward);
		Matrix4x4 cameraMatrix = Matrix4x4.MatrixPointAt(transform.position, targetForward, transform.up);
		Matrix4x4 viewMatrix = Matrix4x4.inverseMatrix(cameraMatrix);
		
		return viewMatrix;
	}
}
