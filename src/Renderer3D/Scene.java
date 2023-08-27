package Renderer3D;

import java.util.ArrayList;
import java.util.List;

import DwarfEngine.Core.Application;
import Renderer3D.Light.LightType;
import Renderer3D.Pipeline.RenderFlag;

/**
 * The base class for defining a scene in the 3D rendering engine. Subclasses of
 * this class can be created to represent different scenes in the application.
 */
public abstract class Scene {
	private Pipeline pipeline;
	private Camera camera;
	/**
	 * The list of props (objects) present in the scene.
	 */
	public final List<Prop> objects;
	/**
	 * The list of lights present in the scene.
	 */
	public final List<Light> lights;

	public Scene(Application application) {
		objects = new ArrayList<>();
		lights = new ArrayList<>();
		pipeline = new Pipeline(application);
	}

	/**
	 * Sets the camera for the scene.
	 *
	 * @param camera The camera to be set.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
		pipeline.setCamera(camera);
	}

	public void render() {
		if (camera == null)
			return;
		pipeline.clear();

		for (Light l : lights) {
			if (l == null) {
				lights.remove(null);
				continue;
			}
			if (l.type == LightType.Ambient)
				continue;
			l.transform.getMatrixTRS();
		}

		for (Prop obj : objects) {
			Shader shader = obj.getShader();
			shader.lights = lights;
			pipeline.DrawMesh(obj);
		}

		OnSceneUpdate();
	}

	/**
	 * Sets the render flag for the rendering pipeline. The render flag determines
	 * the specific rendering operation or mode to be used by the pipeline.
	 *
	 * @param flag The render flag to be set.
	 * @see Pipeline#renderFlag
	 */
	public void SetRenderFlag(RenderFlag flag) {
		pipeline.renderFlag = flag;
	}

	/**
	 * Called when the scene is loaded. Subclasses can override this method to
	 * perform scene-specific initialization logic.
	 */
	protected abstract void OnSceneLoad();

	/**
	 * Called on each frame update of the scene. Subclasses can override this method
	 * to update the scene's state and perform rendering operations.
	 */
	protected abstract void OnSceneUpdate();

	/**
	 * Called after the {@link Scene#OnSceneUpdate()} function Subclasses can
	 * override this method to perform additional operations after the scene update
	 * such as drawing shapes over the 3D scene.<br>
	 * By default, this method does nothing.
	 */
	protected void OnSceneAfterUpdate() {

	}
}
