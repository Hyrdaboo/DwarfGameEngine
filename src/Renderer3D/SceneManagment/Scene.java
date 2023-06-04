package Renderer3D.SceneManagment;

import java.util.ArrayList;
import java.util.List;

import DwarfEngine.Core.Application;
import Renderer3D.Camera;
import Renderer3D.Pipeline;
import Renderer3D.Prop;

public abstract class Scene {
	private Pipeline pipeline;
	private Camera camera;
	private final List<Prop> objects;

	public Scene(Application application) {
		objects = new ArrayList<>();
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
		for (Prop obj : objects) {
			pipeline.DrawMesh(obj);
		}

		OnSceneUpdate();
	}

	public void addObject(Prop object) {
		if (object == null) return;
		objects.add(object);
	}
	
	public void removeObject(Prop object) {
		if (object == null) return;
		objects.remove(object);
	}
	public void removeObject(int index) {
		if (index < 0 || index >= objects.size()) return;
		objects.remove(index);
	}
	
	public void clearObjects() {
		objects.clear();
	}

	protected abstract void OnSceneLoad();
	protected abstract void OnSceneUpdate();
	protected void OnSceneGUI() {
		
	};
}
