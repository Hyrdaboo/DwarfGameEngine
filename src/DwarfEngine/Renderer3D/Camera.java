package DwarfEngine.Renderer3D;

import DwarfEngine.MathTypes.Vector3;

public class Camera {
	public float near = 0.1f;
	public float far = 1000;
	public float fov = 90;
	public Vector3 position = new Vector3(0, 0, 0);
	public Vector3 rotation = new Vector3(0, 0, 0);
	public Vector3 lookDir = Vector3.zero;
	
}