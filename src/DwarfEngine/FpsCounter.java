package DwarfEngine;

/**
 * A precise fps counter based on tutorials from catlike coding. <br>
 * Uses an fps buffer that accumulates fps values over time and calculates the average fps
 */
public final class FpsCounter {
	public int bufferLength = 25;

	private double averageFps = 0;
	private double[] fpsBuffer;
	private int fpsBufferIndex = 0;

	public void updateFps(double deltaTime) {
		if (fpsBuffer == null || fpsBuffer.length != bufferLength) {
			initializeBuffer();
		}

		updateBuffer(deltaTime);
		calculateFps();
	}

	/**
	 * @return the calculated average fps
	 */
	public double GetFps() {
		return averageFps;
	}

	private void initializeBuffer() {
		fpsBuffer = new double[bufferLength];
		fpsBufferIndex = 0;
	}

	private void updateBuffer(double deltaTime) {
		fpsBuffer[fpsBufferIndex++] = 1.0 / deltaTime;
		fpsBufferIndex %= bufferLength;
	}

	private void calculateFps() {
		float sum = 0;

		for (double f : fpsBuffer) {
			sum += f;
		}

		averageFps = sum / bufferLength;
	}
}
