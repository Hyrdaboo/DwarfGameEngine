package SimpleRaytracing;

import DwarfEngine.MathTypes.Vector3;

public class RaycastHit {
	protected Vector3 point = Vector3.zero();
	protected Vector3 normal = Vector3.up();
	
	public Vector3 getPoint() {
		return point;
	}
}
