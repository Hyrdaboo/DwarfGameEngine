package DwarfEngine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.text.BreakIterator;
import java.util.Arrays;

import javax.imageio.ImageIO;

import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class Sprite {
	private int[] pixels;
	int width, height;
	
	public enum WrapMode {Repeat, Clamp, RepeatMirrored}
	public WrapMode wrapMode = WrapMode.Repeat;
	public enum SamplingMode {Point, Bilinear}
	public SamplingMode samplingMode = SamplingMode.Point;
	public Vector2 tiling = Vector2.one();
	public Vector2 offset = Vector2.zero();
	
	public Sprite() {}
	public Sprite(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width*height];
	}
	
	public void LoadFromFile(String path) {
		try {
			BufferedImage image = ImageIO.read(new File(path));
			int w = image.getWidth();
			int h = image.getHeight();
			width = w;
			height = h;
			pixels = new int[width*height];
			pixels = image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException | IllegalArgumentException e) {
			if (e instanceof IOException) {
				throw new RuntimeException("Invalid image path");
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	
	public Color GetPixel(int x, int y) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new IndexOutOfBoundsException("Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}
		
		int rgb = pixels[x+y*width];
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb >> 0) & 0xFF;
		int a = (rgb >> 24) & 0xFF;
		return new Color(r, g, b, a);
	}
	
	public void SetPixel(int x, int y, Color c) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new IndexOutOfBoundsException("Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}
		pixels[x + y*width] = c.getRGB();
	}
	
	public Color SampleColor(float u, float v) {
		
		u *= tiling.x + Mathf.epsilon;
		v *= tiling.y + Mathf.epsilon;
		
		u += offset.x;
		v += offset.y;
		
		switch (wrapMode) {
		case Repeat:
			u = Mathf.abs(Mathf.frac(u));
			v = Mathf.abs(Mathf.frac(v));
			break;
		case Clamp:
			u = Mathf.Clamp01(u);
			v = Mathf.Clamp01(v);
			break;
		case RepeatMirrored:
			u = (int)u % 2 == 0 ? Mathf.abs(Mathf.frac(u)) :  1-Mathf.abs(Mathf.frac(u));
			v = (int)v % 2 == 0 ? Mathf.abs(Mathf.frac(v)) :  1-Mathf.abs(Mathf.frac(v));
			break;
		default:
			break;
		}
		
		if (samplingMode == SamplingMode.Point) {
			int x = (int)(((float)width-1)*u + 0.5f);
			int y = (int)(((float)height-1)*v + 0.5f);
			
			return GetPixel(x, y);
		}
		else {
			float x = ((float)width-1) * u;
			float y = ((float)height-1) * v;
			
			Vector2 tl = new Vector2((int)x, (int)y);
			Vector2 tr = new Vector2((int)(Mathf.ceil(x)), (int)y);
			Vector2 bl = new Vector2((int)x, (int)(Mathf.ceil(y)));
			Vector2 br = new Vector2((int)(Mathf.ceil(x)), (int)(Mathf.ceil(y)));
			
			float tx = x-tl.x;
			float ty = y-tl.y;
			
			Color top = Mathf.LerpColor(GetPixel((int)tl.x, (int)tl.y), GetPixel((int)tr.x, (int)tr.y), tx);
			Color bottom = Mathf.LerpColor(GetPixel((int)bl.x, (int)bl.y), GetPixel((int)br.x, (int)br.y), tx);
			
			return Mathf.LerpColor(top, bottom, ty);
		}
	}
}
