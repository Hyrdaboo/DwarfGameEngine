package Demos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import DwarfEngine.Text;
import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Vector2;
import Renderer3D.Scene;

public class WarningScene extends Scene {

	Application app;

	public WarningScene(Application application) {
		super(application);
		// TODO Auto-generated constructor stub
		app = application;
		app.title = "Sorry for terrible performance :')";
	}

	Text text;

	@Override
	protected void OnSceneLoad() {
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
		text.SetText("WARNING!!!\n\nThis next scene has poor performance.\n\nBe careful to not set your PC on fire");

		pos.y = app.getFrameSize().y / 2 - 50;
	}

	@Override
	protected void OnSceneUpdate() {
		// TODO Auto-generated method stub

	}

	Vector2 pos = new Vector2(0, 0);
	Vector2 scale = Vector2.one();

	@Override
	protected void OnSceneAfterUpdate() {
		text.draw(pos, scale);
	}
}
