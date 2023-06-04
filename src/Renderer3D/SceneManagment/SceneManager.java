package Renderer3D.SceneManagment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;


public class SceneManager {
	private static final HashMap<String, Class<? extends Scene>> scenes = new HashMap<>();
	private static Scene activeScene;
	private static Application app;

	/**
	 * Called by {@link Application} to set the currently active application for which scenes will be initialized.
	 * @param application Target application
	 */
	public static void setTargetApplication(Application application) {
		if (application == null) return;
		app = application;
	}

	public static void AddScene(Class<? extends Scene> scene, String name) {
		if (scene == null || name == null) return;
		scenes.putIfAbsent(name, scene);
	}

	public static void LoadScene(String name) {
		if (app == null) {
			return;
		}

		Class<? extends Scene> sceneClass = scenes.getOrDefault(name, null);
		if (sceneClass == null) {
			Debug.log("Scene " + name + " doesn't exist");
			return;
		}

		try {
			Constructor<?> constructor = sceneClass.getConstructor(Application.class);
			constructor.setAccessible(true);
			activeScene = (Scene) constructor.newInstance(app);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Debug.log("An unkown error occured while trying to load your scene");
		}
	}

	public static void renderActiveScene() {
		if (activeScene == null) return;
		activeScene.render();
		activeScene.OnSceneGUI();
	}
}
