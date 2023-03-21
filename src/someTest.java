import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import static DwarfEngine.Core.DisplayRenderer.*;

import java.awt.Color;

class app extends Application {

	@Override
	public void OnStart() {
		s = new Sprite();
		s.LoadFromFile("/Textures/bg.jpg");
	}
	Sprite s;

	@Override
	public void OnUpdate() {
		clear(Color.black);
		
		if (Input.OnKeyPressed(Keycode.F)) {
			switchFullscreen();
		}
		
		
		DrawSprite(s, Vector2.zero());
	}
	
}

public class someTest {

	public static void main(String[] args) {
		app a = new app();
		a.Initialize(1280, 720, 1);
	}

}
