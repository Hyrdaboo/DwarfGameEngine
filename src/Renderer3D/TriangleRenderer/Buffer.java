package Renderer3D.TriangleRenderer;

public interface Buffer<T> {
	int getWidth();
	int getHeight();
	void clear();
	void write(int x, int y, T value);
	T read(int x, int y);
}
