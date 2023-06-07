package Renderer3D;

import java.util.ArrayList;
import java.util.List;

import DwarfEngine.Core.Application;
import Renderer3D.Light.LightType;
import Renderer3D.Pipeline.RenderFlag;

public abstract class Scene {
	private Pipeline pipeline;
	private Camera camera;
	public final List<Prop> objects;
	public final List<Light> lights;

	public Scene(Application application) {
		objects = new ArrayList<>();
		lights = new ArrayList<>();
		pipeline = new Pipeline(application);
		OnSceneLoad();
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
		pipeline.setCamera(camera);
	}

	public void render() {
		if (camera == null) return;
		pipeline.clear();

		for (Light l : lights) {
			if (l == null) {
				lights.remove(l);
				continue;
			}
			if (l.type == LightType.Ambient) continue;
			l.transform.getMatrixTRS();
		}

		for (Prop obj : objects) {
			Shader shader = obj.getShader();
			shader.lights = lights;
			pipeline.DrawMesh(obj);
		}

		OnSceneUpdate();
	}

	public void SetRenderFlag(RenderFlag flag) {
		pipeline.renderFlag = flag;
	}

	protected abstract void OnSceneLoad();
	protected abstract void OnSceneUpdate();
	protected void OnSceneGUI() {

	}
}
