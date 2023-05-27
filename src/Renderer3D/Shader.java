package Renderer3D;

import java.awt.Color;

public abstract class Shader {
	public boolean cull = true;
	public abstract Color Fragment(Vertex in);
}
