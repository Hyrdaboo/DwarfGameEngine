package Demos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import DwarfEngine.Text;
import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.DisplayRenderer;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Vector2;
import Renderer3D.SceneManager;

/**
 * To load demos you'll need to copy the folder DemoResources into the main directory of 
 * your project otherwise none of the demos will work
 */
@SuppressWarnings("serial")
public class DemoSceneLoader extends Application {

	Text text;

	@Override
	public void OnStart() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource("/font-atlas.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Texture atlas = new Texture();
		atlas.Load(image);
		text = new Text(atlas, 32);
		text.spacing = -20;
		text.SetText("Press TAB to switch between scenes");

		SceneManager.AddScene(SuzanneDemo.class, "Suzanne");
		SceneManager.AddScene(WarningScene.class, "Warning");
		SceneManager.AddScene(ShaderShowcaseScene.class, "Showcase");
		SceneManager.AddScene(WarningScene.class, "Warning");
		SceneManager.AddScene(TerrainDemo.class, "Terrain");
		SceneManager.LoadScene("Suzanne");
	}

	Vector2 textPos = Vector2.zero();
	Vector2 textScale = new Vector2(0.6f, 0.6f);
	String[] sceneNames = new String[] { "Suzanne", "Warning", "Showcase", "Warning", "Terrain" };
	int sceneIndex = 1;

	@Override
	public void OnUpdate() {
		DisplayRenderer.clear(Color.black);
		SceneManager.renderActiveScene();
		text.draw(textPos, textScale);

		if (Input.OnKeyPressed(Keycode.Tab)) {
			SceneManager.LoadScene(sceneNames[sceneIndex++]);
			sceneIndex %= sceneNames.length;
		}
	}
}