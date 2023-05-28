package Renderer3D;

import java.awt.Color;

import DwarfEngine.MathTypes.Vector3;

public abstract class Shader {
	public Transform objectTransform = null;
	public boolean cull = true;
	public abstract Vector3 Fragment(Vertex in);
}
