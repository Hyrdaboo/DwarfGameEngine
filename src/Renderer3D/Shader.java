package Renderer3D;

import java.util.List;

import DwarfEngine.MathTypes.Vector3;

public abstract class Shader {
	List<Light> lights = null;
	protected Transform objectTransform = null;
	protected Transform cameraTransform = null;
	public boolean cull = true;

	protected Light GetLight(int index) {
		if (index < 0 || index >= lights.size()) return null;
		return lights.get(index);
	}

	protected int lightCount() {
		return lights.size();
	}

	public abstract Vector3 Fragment(Vertex in);
}
