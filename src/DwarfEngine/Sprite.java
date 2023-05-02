package DwarfEngine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import DwarfEngine.Core.Debug;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class Sprite {
	private int[] pixels;
	int width, height;
	
	public Sprite() {}
	public Sprite(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width*height];
	}
	
	public void LoadFromFile(String path) {
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
	
	
	public Color GetPixel(int x, int y) {
		int rgb = pixels[x+y*width];
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb >> 0) & 0xFF;
		int a = (rgb >> 24) & 0xFF;
		return new Color(r, g, b, a);
	}
	
	public void SetPixel(int x, int y, Color c) {
		pixels[x + y*width] = c.getRGB();
	}
	
	public Color SampleColor(float u, float v) {
		u = Mathf.abs(Mathf.frac(u));
		v = Mathf.abs(Mathf.frac(v));
		
		int x = (int)Mathf.Lerp(0, width, u);
		int y = (int)Mathf.Lerp(0, height, v);
		
		return GetPixel(x, y);
	}
	
	public Color SampleColorPerspective(float u, float v, float w) {
		w = 1.0f / w;
		u *= w;
		v *= w;
		return SampleColor(u, v);
	}
}
