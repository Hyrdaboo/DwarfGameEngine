package DwarfEngine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public final class Texture {
	private int[] pixels;
	int width, height;

	public enum WrapMode {
		Repeat, Clamp, RepeatMirrored
	}

	public WrapMode wrapMode = WrapMode.Clamp;

	public enum SamplingMode {
		Point, Bilinear
	}

	public SamplingMode samplingMode = SamplingMode.Point;
	public Vector2 tiling = Vector2.one();
	public Vector2 offset = Vector2.zero();

	public Texture() {
	}

	public Texture(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public void LoadFromFile(String path) {
		try {
			BufferedImage image = ImageIO.read(new File(path));
			int w = image.getWidth();
			int h = image.getHeight();
			width = w;
			height = h;
			pixels = new int[width * height];
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
			throw new IndexOutOfBoundsException(
					"Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}

		int rgb = pixels[x + y * width];
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb >> 0) & 0xFF;
		int a = (rgb >> 24) & 0xFF;
		return new Color(r, g, b, a);
	}

	public void SetPixel(int x, int y, Color c) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new IndexOutOfBoundsException(
					"Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}
		pixels[x + y * width] = c.getRGB();
	}

	private Vector3 GetPixelUv(int x, int y) {
		y = height - 1 - y;
		int rgb = pixels[x + y * width];
		float r = ((rgb >> 16) & 0xFF) / 255.0f;
		float g = ((rgb >> 8) & 0xFF) / 255.0f;
		float b = ((rgb >> 0) & 0xFF) / 255.0f;
		return new Vector3(r, g, b);
	}

	public Vector3 Sample(float u, float v) {
		u *= tiling.x;
		v *= tiling.y;
		u += offset.x;
		v += offset.y;

		int precision = 1000;
		int x = (int) (u * width * precision);
		int y = (int) (v * height * precision);

		switch (wrapMode) {
		case Clamp: {
			x = (int) Mathf.Clamp(x, 0, ((width - 1) * precision));
			y = (int) Mathf.Clamp(y, 0, ((height - 1) * precision));
			break;
		}
		case Repeat:
			int w = width * precision;
			int h = height * precision;
			x = ((x % w) + w) % w;
			y = ((y % h) + h) % h;
			break;
		case RepeatMirrored:
			w = width * precision;
			h = height * precision;

			boolean xEven = (x / w) % 2 == 0;
			boolean yEven = (y / h) % 2 == 0;

			x = ((x % w) + w) % w;
			y = ((y % h) + h) % h;

			if (!xEven) {
				x = w - 1 - x;
			}
			if (!yEven) {
				y = h - 1 - y;
			}
			break;
		default:
			break;
		}

		if (samplingMode == SamplingMode.Point) {
			x /= precision;
			y /= precision;
			return GetPixelUv(x, y);
		} else {
			float xf = x / (float) precision;
			float yf = y / (float) precision;
			xf = Mathf.Clamp(xf, 0, width - 1);
			yf = Mathf.Clamp(yf, 0, height - 1);
			Vector2 tl = new Vector2((int) xf, (int) yf);
			Vector2 tr = new Vector2(Mathf.ceil(xf), (int) yf);
			Vector2 bl = new Vector2((int) xf, Mathf.ceil(yf));
			Vector2 br = new Vector2(Mathf.ceil(xf), Mathf.ceil(yf));

			float tx = xf - tl.x;
			float ty = yf - tl.y;

			Vector3 top = Vector3.Lerp(GetPixelUv((int) tl.x, (int) tl.y), GetPixelUv((int) tr.x, (int) tr.y), tx);
			Vector3 bottom = Vector3.Lerp(GetPixelUv((int) bl.x, (int) bl.y), GetPixelUv((int) br.x, (int) br.y), tx);

			return Vector3.Lerp(top, bottom, ty);
		}
	}

	public Vector3 SampleFast(float u, float v) {
		float x = Mathf.Clamp(u * width, 0, width - 1);
		float y = Mathf.Clamp(v * height, 0, height - 1);

		if (samplingMode == SamplingMode.Point) {
			return GetPixelUv((int) x, (int) y);
		} else {
			Vector2 tl = new Vector2((int) x, (int) y);
			Vector2 tr = new Vector2(Mathf.ceil(x), (int) y);
			Vector2 bl = new Vector2((int) x, Mathf.ceil(y));
			Vector2 br = new Vector2(Mathf.ceil(x), Mathf.ceil(y));

			float tx = x - tl.x;
			float ty = y - tl.y;

			Vector3 top = Vector3.Lerp(GetPixelUv((int) tl.x, (int) tl.y), GetPixelUv((int) tr.x, (int) tr.y), tx);
			Vector3 bottom = Vector3.Lerp(GetPixelUv((int) bl.x, (int) bl.y), GetPixelUv((int) br.x, (int) br.y), tx);

			return Vector3.Lerp(top, bottom, ty);
		}
	}
}