package Renderer3D.TriangleRenderer;

import java.util.Arrays;

public final class ColorBuffer implements Buffer<Integer> {

	public final int[] buffer;
	private final int width;
	private final int height;
	
	public ColorBuffer(int[] buffer, int width, int height) {
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
		Arrays.fill(buffer, 0);
	}
	
	@Override
	public void write(int x, int y, Integer value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid write index. Cannot read at " + x + ", " + y);
		}
		buffer[x + y*width] = value;
	}

	@Override
	public Integer read(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("Invalid read index. Cannot read at " + x + ", " + y);
		}
		return buffer[x + y*width];
	}
}
