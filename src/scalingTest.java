import java.awt.Color;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.SimpleGraphics2D.Draw2D;

@SuppressWarnings("serial")
class scalingDemo extends Application {
	
	Vector2 originalSize;
	public void OnStart() {
		originalSize = getWindowSize();
	}
	
	public Vector2 center;
	public Vector2 size = new Vector2(100, 100);
	public Vector2 pos;
	Vector2 halfPos = new Vector2(0, 0);
	public void OnUpdate() {
		clear(getBackground());
		center = new Vector2(getWindowSize().x/2, getWindowSize().y/2);
		
		halfPos.x = getWindowSize().x/2;
		halfPos.y = getWindowSize().y/2;
		
		float newX = size.x*(originalSize.x/getWidth());
		float newY = size.y*(originalSize.y/getHeight());
		pos = new Vector2(center.x-newX/2, center.y-newY/2);
		
		Debug.println(newX + ", " + newY);
		Draw2D.FillRect(pos, new Vector2(newX, newY), Color.red);
		
		Draw2D.LineTo(new Vector2(halfPos.x, 0), new Vector2(halfPos.x, halfPos.y*2), Color.white);
		Draw2D.LineTo(new Vector2(0, halfPos.y), new Vector2(halfPos.x*2, halfPos.y), Color.white);
	}
}

public class scalingTest {

	public static void main(String[] args) {
		scalingDemo demo = new scalingDemo();
		demo.SetResizable(true);
		demo.Construct(720, 480, 1);
	}

}
