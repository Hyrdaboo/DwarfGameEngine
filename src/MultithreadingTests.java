import java.awt.Color;
import java.util.stream.IntStream;

import DwarfEngine.Texture;
import DwarfEngine.Core.Application;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import static DwarfEngine.Core.DisplayRenderer.*;

class demo extends Application {

	Texture tex = new Texture();
	Vector2 frameSize;
	@Override
	public void OnStart() {
		frameSize = getFrameSize();
		tex.LoadFromFile("res/Textures/uvtest.png");
	}

	@Override
	public void OnUpdate() {
		/*
		for (int y = 0; y < frameSize.y; y++) {
			for (int x = 0; x < frameSize.x; x++) {
				float u = x / frameSize.x;
				float v = y / frameSize.y;
				v = 1 - v;
				
				Vector3 sr = tex.SampleFast(u, v);
				int c = toColor(sr);
				SetPixel(x, y, c);
			}
		}
		*/
		
		Vector2 uv = Vector2.zero();
		IntStream.range(0, (int)frameSize.y).parallel().forEach(y -> {
			IntStream.range(0, (int)frameSize.x).parallel().forEach(x -> {
				uv.x = x / frameSize.x;
				uv.y = 1 - y / frameSize.y;
				
				Vector3 sr = tex.SampleFast(uv.x, uv.y);
				int c = toColor(sr);
				SetPixel(x, y, c);
			});
		});
	}
	
	private int toColor(Vector3 v) {
		v.x = Mathf.Clamp01(v.x);
		v.y = Mathf.Clamp01(v.y);
		v.z = Mathf.Clamp01(v.z);

		int r = (int) (v.x * 255);
		int g = (int) (v.y * 255);
		int b = (int) (v.z * 255);
		int rgb = (r << 16) | (g << 8) | (b << 0);
		return rgb;
	}
}

public class MultithreadingTests {

	public static void main(String[] args) {
		demo d = new demo();
		d.Initialize(1280, 720, 1);
	}

}
