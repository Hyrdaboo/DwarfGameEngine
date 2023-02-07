package DwarfEngine.SimpleGraphics2D;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class Sprite {
	private String path;
	private int[] pixels;
	protected int width;
	protected int height;
	public Vector2 scale = Vector2.one();
	public Color tint = Color.white;
	
	public Sprite(String path) {
		this.path = path;
		load();
	}
	
	private void load() {
		try {
			BufferedImage image = ImageIO.read(Sprite.class.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			width = w;
			height = h;
			pixels = new int[width*height];
			pixels = image.getRGB(0, 0, width, height, pixels, 0, width);
			
		} catch (IOException | IllegalArgumentException e) {
			if (e instanceof IllegalArgumentException) {
				throw new IllegalArgumentException("Invalid image path");
			}
			e.printStackTrace();
		}
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	
	public int GetPixel(int x, int y) {
		return pixels[x+y*width];
	}
	public Color SampleColor(float u, float v) {
		u = Mathf.abs(Mathf.frac(u));
		v = Mathf.abs(Mathf.frac(v));
		
		int x = (int)Mathf.Lerp(0, width, u);
		int y = (int)Mathf.Lerp(0, height, v);
		
		return new Color(GetPixel(x, y));
	}
}
