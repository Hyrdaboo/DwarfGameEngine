import java.awt.Color;
import java.io.File;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import DwarfEngine.SimpleGraphics2D.Sprite;

class Demo extends Application {

	Sprite s = new Sprite("/Textures/bg.jpg");
	public void OnStart() {
		AppName = "Sprite testing";
		s.scale = new Vector2((float)getWindowSize().x/s.getWidth(), (float)getWindowSize().y/s.getHeight());
		//s.scale = new Vector2(.2f, .2f);
	}

	Vector2 pos = new Vector2(0, 0);
	float angle = 0;
	public void OnUpdate() {
		clear(Color.darkGray);
		
		
		pos.x = getWindowSize().x/2;
		pos.y = getWindowSize().y/2;
		
		//pos = Input.getMousePosition();
		if (Input.OnKeyHeld(Keycode.Space)) angle += deltaTime * 100;
		
		Draw2D.DrawSpriteTransformed(s, pos, angle);
		Draw2D.LineTo(new Vector2(pos.x, 0), new Vector2(pos.x, pos.y*2), Color.red);
		Draw2D.LineTo(new Vector2(0, pos.y), new Vector2(pos.x*2, pos.y), Color.red);
		Draw2D.FillCircle(pos, 2, Color.black);
		
	}
	
}

public class spriteTest {
	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.SetResizable(true);
		demo.Construct(720, 480, 1);
	}
}
