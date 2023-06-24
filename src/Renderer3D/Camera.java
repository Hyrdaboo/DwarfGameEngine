package Renderer3D;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

/**
 * The <code>Camera</code> class enables the engine to "see" objects in the 3D world. It provides functionality for positioning
 * and orienting the camera, allowing the rendering of scenes from different perspectives.
 */
public class Camera {
	public final Transform transform = new Transform();
	float fov = 60;
	float near = .1f;
	float far = 1000;

	/**
	 * Sets the field of view (FOV) of the camera.
	 *
	 * @param fov The desired field of view in degrees.
	 */
	public void SetFov(float fov) {
		fov = Mathf.Clamp(fov, 0.01f, 179);
		this.fov = fov;
	}

	public float GetFov() {
		return fov;
	}

	/**
	 * Sets the distance to the near clipping plane of the camera.
	 *
	 * @param near The distance to the near clipping plane.
	 */
	public void SetNear(float near) {
		near = Mathf.Clamp(near, 0.01f, far);
		this.near = near;
	}

	public float GetNear() {
		return near;
	}

	/**
	 * Sets the distance to the far clipping plane of the camera.
	 *
	 * @param far The distance to the far clipping plane.
	 */
	public void SetFar(float far) {
		far = far < near ? near + 0.01f : far;
		this.far = far;
	}

	public float GetFar() {
		return far;
	}

	/**
	 * Retrieves the view matrix of the camera.
	 *
	 * @return The view matrix representing the camera's orientation and position in the scene.
	 */
	public Matrix4x4 getViewMatrix() {
		transform.getMatrixTRS();

		Vector3 targetForward = Vector3.add2Vecs(transform.position, transform.forward);
		Matrix4x4 cameraMatrix = Matrix4x4.MatrixPointAt(transform.position, targetForward, transform.up);
		Matrix4x4 viewMatrix = Matrix4x4.inverseMatrix(cameraMatrix);

		return viewMatrix;
	}
	
	Matrix4x4 projection;
	Matrix4x4 view;
	/**
	 * Transforms a point from world space to screen space coordinates.
	 *
	 * @param point The point in world space to transform.
	 * @return The point's coordinates in screen space.
	 */
	public Vector3 worldToScreenPoint(Vector3 point) {
		return viewportToScreenPoint(worldToViewportPoint(point));
	}
	
	/**
	 * Transforms a point from world coordinates to viewport coordinates.
	 *
	 * @param point The point in world coordinates to transform.
	 * @return The point's coordinates in viewport space.
	 */
	public Vector3 worldToViewportPoint(Vector3 point) {
		if (view == null || projection == null) return Vector3.zero();
		Vector3 newPoint = view.MultiplyByVector(point);
	    newPoint = projection.MultiplyByVector(newPoint);
	    newPoint.divideBy(newPoint.w);
		return newPoint;
	}
	
	Vector2 frameSize;
	/**
	 * Transforms a point from viewport coordinates to screen coordinates.
	 *
	 * @param point The point in viewport coordinates to transform.
	 * @return The point's coordinates in screen space.
	 */
	public Vector3 viewportToScreenPoint(Vector3 point) {
		if (frameSize == null) return Vector3.zero();
		float x = (point.x + 1) / 2.0f * frameSize.x;
		float y = (-point.y + 1) / 2.0f * frameSize.y;

		Vector3 v = new Vector3(x, y, point.z);
		v.w = point.w;
		return v;
	}
}
