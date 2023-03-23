import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import static DwarfEngine.Core.DisplayRenderer.*;

import java.awt.Color;
import java.awt.Taskbar.State;
import java.util.Arrays;
import java.util.Comparator;

class app extends Application {

	@Override
	public void OnStart() {
		s = new Sprite();
		
		s.LoadFromFile("/Textures/grass-side.png");
	}
	Sprite s;

	@Override
	public void OnUpdate() {
		clear(Color.black);
		
		
	}
}

public class someTest {
	
	public static void main(String[] args) {
		app a = new app();
		a.Initialize(1280, 720, 1);
		//a.Initialize(144, 81, 7);
	}
	
}
