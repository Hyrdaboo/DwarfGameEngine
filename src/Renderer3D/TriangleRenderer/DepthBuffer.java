package Renderer3D.TriangleRenderer;

import java.util.Arrays;

public final class DepthBuffer implements Buffer<Float> {

	private final float[] buffer;
	private final int width;
	private final int height;
	
	public DepthBuffer(float[] buffer, int width, int height) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void clear() {
		Arrays.fill(buffer, Float.MAX_VALUE);
	}

	@Override
	public void write(int x, int y, Float value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid write index. Cannot read at " + x + ", " + y);
		}
		buffer[x + y*width] = value;
	}

	@Override
	public Float read(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid read index. Cannot read at " + x + ", " + y);
		}
		return buffer[x + y*width];
	}

}
