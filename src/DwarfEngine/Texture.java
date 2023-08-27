package DwarfEngine;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The Texture class represents an image texture used for loading and
 * manipulating textures. It provides functionalities to load image files, set
 * and get individual pixel colors, and supports various wrap modes and sampling
 * modes for texture sampling.
 */
public final class Texture {
	private int[] pixels;
	int width, height;

	/**
	 * The WrapMode enum represents different wrap modes for texture sampling. It
	 * defines the following wrap modes:
	 * <ul>
	 * <li><b>Repeat:</b> The texture wraps around and repeats in both
	 * directions.</li>
	 * <li><b>Clamp:</b> The texture's edge pixels are repeated beyond the texture
	 * boundaries.</li>
	 * <li><b>RepeatMirrored:</b> The texture wraps around and repeats in both
	 * directions, with mirrored repetitions.</li>
	 * </ul>
	 *
	 * <strong>Note!</strong> This doesn't work with
	 * {@link Texture#SampleFast(float, float, Vector3)} as it ignores wrap modes and by
	 * default only supports clamping
	 */
	public enum WrapMode {
		Repeat, Clamp, RepeatMirrored
	}

	/**
	 * The wrapMode field represents the current wrap mode for texture sampling. It
	 * is initialized with the default value of {@link WrapMode#Clamp}.
	 */
	public WrapMode wrapMode = WrapMode.Clamp;

	/**
	 * The SamplingMode enum represents different sampling modes for texture
	 * sampling. It defines the following sampling modes:
	 * <ul>
	 * <li><b>Point:</b> The texture is sampled using nearest-neighbor
	 * interpolation.</li>
	 * <li><b>Bilinear:</b> The texture is sampled using bilinear
	 * interpolation.</li>
	 * </ul>
	 */
	public enum SamplingMode {
		Point, Bilinear
	}

	/**
	 * The samplingMode field represents the current sampling mode for texture
	 * sampling. It is initialized with the default value of
	 * {@link SamplingMode#Point}.
	 */
	public SamplingMode samplingMode = SamplingMode.Point;
	public Vector2 tiling = Vector2.one();
	public Vector2 offset = Vector2.zero();

	public Texture() {
	}

	/**
	 * Constructs a Texture object with the specified width and height.
	 *
	 * @param width  The width of the texture in pixels.
	 * @param height The height of the texture in pixels.
	 */
	public Texture(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	/**
	 * Creates a small 2x2 texture filled with the specified color.
	 *
	 * @param color The color to use for the solid texture.
	 * @return A Texture object representing the solid texture.
	 */
	public static Texture solidTexture(Color color) {
		Texture t = new Texture(2, 2);
		t.SetPixel(0, 0, color);
		t.SetPixel(0, 1, color);
		t.SetPixel(1, 0, color);
		t.SetPixel(1, 1, color);
		return t;
	}

	/**
	 * Loads an image from the specified file path and initializes the texture with
	 * its pixel data.
	 *
	 * @param path The path of the image file to load.
	 * @throws RuntimeException If the image path is invalid or the image cannot be
	 *                          loaded.
	 */
	public void Load(String path) {
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

	/**
	 * Initializes the texture with pixel data of a {@link BufferedImage}
	 *
	 * @param image <code>BufferedImage</code> to be used
	 */
	public void Load(BufferedImage image) {
		if (image == null)
			return;
		int w = image.getWidth();
		int h = image.getHeight();
		width = w;
		height = h;
		pixels = new int[width * height];
		pixels = image.getRGB(0, 0, width, height, pixels, 0, width);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Retrieves the color of the pixel at the specified coordinates.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return The Color object representing the pixel color.
	 * @throws IndexOutOfBoundsException If the specified coordinates are outside
	 *                                   the image bounds.
	 */
	public Color GetPixel(int x, int y) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new IndexOutOfBoundsException(
					"Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}

		int rgb = pixels[x + y * width];
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb) & 0xFF;
		int a = (rgb >>> 24);
		return new Color(r, g, b, a);
	}

	public int GetPixelRaw(int x, int y) {
		return pixels[x + y * width];
	}

	/**
	 * Retrieves an array of pixels from the specified region of the texture.
	 *
	 * @param xStart The starting x-coordinate of the region.
	 * @param yStart The starting y-coordinate of the region.
	 * @param w      The width of the region.
	 * @param h      The height of the region.
	 * @return An array of pixels representing the specified region. <br>
	 * <code>null</code> if something goes wrong
	 */
	public int[] GetPixels(int xStart, int yStart, int w, int h) {
		try {
			int[] newImg = new int[w * h];
			for (int y = yStart; y < yStart + h; y++) {
				for (int x = xStart; x < xStart + w; x++) {
					int xCoord = x - xStart;
					int yCoord = y - yStart;
					newImg[xCoord + yCoord * w] = pixels[x + y * width];
				}
			}
			return newImg;
		} catch (Exception e) {
			System.err.println("Image out of bounds!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the color of the pixel at the specified coordinates.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @param c The Color object representing the new color of the pixel.
	 * @throws IndexOutOfBoundsException If the specified coordinates are outside
	 *                                   the image bounds.
	 */
	public void SetPixel(int x, int y, Color c) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new IndexOutOfBoundsException(
					"Index (" + x + ", " + y + ") is out of bounds for image size (" + width + ", " + height + ")");
		}
		pixels[x + y * width] = c.getRGB();
	}

	/**
	 * Sets the pixels of the texture starting from the specified coordinates.
	 *
	 * @param pixels The array of pixels to set.
	 * @param w      The width of the pixel data.
	 * @param h      The height of the pixel data.
	 * @param xStart The starting x-coordinate of the destination region.
	 * @param yStart The starting y-coordinate of the destination region.
	 */
	public void SetPixels(int[] pixels, int w, int h, int xStart, int yStart) {
		if (pixels == null)
			return;

		try {
			for (int y = yStart; y < yStart + h; y++) {
				for (int x = xStart; x < xStart + w; x++) {
					int xCoord = x - xStart;
					int yCoord = y - yStart;
					this.pixels[x + y * width] = pixels[xCoord + yCoord * w];
				}
			}
		} catch (Exception e) {
			System.err.println("Image out of bounds!");
			e.printStackTrace();
		}
	}

	private Vector3 GetPixelUv(int x, int y, Vector3 dst) {
		y = height - 1 - y;
		int rgb = pixels[x + y * width];
		dst.x = ((rgb >> 16) & 0xFF) / 255.0f;
		dst.y = ((rgb >> 8) & 0xFF) / 255.0f;
		dst.z = ((rgb) & 0xFF) / 255.0f;
		return dst;
	}

	/**
	 * Samples the texture at the specified UV coordinates and returns the sampled
	 * color as a Vector3. This method implements all the sampling and wrap modes,
	 * but it exhibits slower performance due to the implementation of multiple
	 * sampling and wrap modes. If performance is a concern and wrap modes aren't a
	 * requirement, then using {@link Texture#SampleFast(float, float, Vector3)} is advised.
	 *
	 * @param u The U-coordinate of the texture.
	 * @param v The V-coordinate of the texture.
	 * @return The sampled color as a Vector3.
	 */
	public Vector3 Sample(float u, float v, Vector3 dst) {
		u *= tiling.x;
		v *= tiling.y;
		u += offset.x;
		v += offset.y;

		int precision = 1000;
		int x = (int) (u * width * precision);
		int y = (int) (v * height * precision);

		switch (wrapMode) {
			case Clamp: {
				x = (int) Mathf.clamp(x, 0, ((width - 1) * precision));
				y = (int) Mathf.clamp(y, 0, ((height - 1) * precision));
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
			return GetPixelUv(x, y, dst);
		} else {
			float xf = x / (float) precision;
			float yf = y / (float) precision;
			xf = Mathf.clamp(xf, 0, width - 1);
			yf = Mathf.clamp(yf, 0, height - 1);
			Vector2 tl = new Vector2((int) xf, (int) yf);
			Vector2 tr = new Vector2(Mathf.ceil(xf), (int) yf);
			Vector2 bl = new Vector2((int) xf, Mathf.ceil(yf));
			Vector2 br = new Vector2(Mathf.ceil(xf), Mathf.ceil(yf));

			float tx = xf - tl.x;
			float ty = yf - tl.y;

			Vector3 top = Vector3.Lerp(
					GetPixelUv((int) tl.x, (int) tl.y, new Vector3()),
					GetPixelUv((int) tr.x, (int) tr.y, new Vector3()), tx);
			Vector3 bottom = Vector3.Lerp(
					GetPixelUv((int) bl.x, (int) bl.y, new Vector3()),
					GetPixelUv((int) br.x, (int) br.y, new Vector3()), tx);

			return Vector3.Lerp(top, bottom, ty, dst);
		}
	}

	/**
	 * Fast sampling method that performs clamping and interpolation to sample the
	 * texture at the specified UV coordinates. This method is faster than the
	 * regular {@link Texture#Sample(float, float, Vector3)} method, making it advantageous
	 * when performance is a concern.
	 *
	 * @param u The U-coordinate of the texture.
	 * @param v The V-coordinate of the texture.
	 * @return The sampled color as a Vector3.
	 */
	public Vector3 SampleFast(float u, float v, Vector3 dst) {
		float x = Mathf.clamp(u * width, 0, width - 1);
		float y = Mathf.clamp(v * height, 0, height - 1);

		if (samplingMode == SamplingMode.Point) {
			return GetPixelUv((int) x, (int) y, dst);
		} else {
			Vector2 tl = new Vector2((int) x, (int) y);
			Vector2 tr = new Vector2(Mathf.ceil(x), (int) y);
			Vector2 bl = new Vector2((int) x, Mathf.ceil(y));
			Vector2 br = new Vector2(Mathf.ceil(x), Mathf.ceil(y));

			float tx = x - tl.x;
			float ty = y - tl.y;

			Vector3 top = Vector3.Lerp(
					GetPixelUv((int) tl.x, (int) tl.y, new Vector3()),
					GetPixelUv((int) tr.x, (int) tr.y, new Vector3()), tx);
			Vector3 bottom = Vector3.Lerp(
					GetPixelUv((int) bl.x, (int) bl.y, new Vector3()),
					GetPixelUv((int) br.x, (int) br.y, new Vector3()), tx);

			return Vector3.Lerp(top, bottom, ty, dst);
		}
	}
}