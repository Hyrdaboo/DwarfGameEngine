package Renderer3D;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import DwarfEngine.Core.Application;

/**
 * The <code>SceneManager</code> class is responsible for managing the scenes in
 * the application. It provides a simple implementation that allows loading and
 * rendering scenes based on their names. To use the <code>SceneManager</code>,
 * call the <code>LoadScene</code> method with the desired scene name, and call
 * the <code>renderActiveScene</code> method in the application's update loop to
 * render the currently active scene.
 *
 * <br>
 * <br>
 * Example usage:
 *
 * <pre>
 * class YourApplication extends Application {
 *
 * 	&#64;Override
 * 	public void OnStart() {
 * 		// MyScene is a class that extends Scene
 * 		SceneManager.AddScene(MyScene.class, "Sample");
 * 		// Load the scene
 * 		SceneManager.LoadScene("Sample");
 * 	}
 *
 * 	&#64;Override
 * 	public void OnUpdate() {
 * 		// Render the currently active scene
 * 		SceneManager.renderActiveScene();
 * 	}
 * }
 * </pre>
 */
public class SceneManager {
	private static final HashMap<String, Class<? extends Scene>> scenes = new HashMap<>();
	private static Scene activeScene;
	private static Application app;

	/**
	 * Called by {@link Application} to set the currently active application for
	 * which scenes will be initialized.
	 *
	 * @param application Target application
	 */
	public static void setTargetApplication(Application application) {
		if (application == null)
			return;
		app = application;
	}

	/**
	 * Adds a scene class to the scene manager with the specified name.
	 *
	 * @param scene The scene class to add.
	 * @param name  The name of the scene.
	 */
	public static void AddScene(Class<? extends Scene> scene, String name) {
		if (scene == null || name == null)
			return;
		scenes.putIfAbsent(name, scene);
	}

	/**
	 * Loads a scene with the specified name and sets it as the active scene.
	 *
	 * @param name The name of the scene to load.
	 */
	public static void LoadScene(String name) {
		if (app == null) {
			return;
		}

		Class<? extends Scene> sceneClass = scenes.getOrDefault(name, null);
		if (sceneClass == null) {
			System.err.println("Scene " + name + " doesn't exist");
			return;
		}

		try {
			Constructor<?> constructor = sceneClass.getConstructor(Application.class);
			constructor.setAccessible(true);
			activeScene = (Scene) constructor.newInstance(app);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println(e.getCause());
			e.printStackTrace();
		}
	}

	/**
	 * Renders the active scene by calling its render method and executing any
	 * post-update logic.
	 */
	public static void renderActiveScene() {
		if (activeScene == null)
			return;
		activeScene.render();
		activeScene.OnSceneAfterUpdate();
	}
}
