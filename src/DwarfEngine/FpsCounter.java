package DwarfEngine;

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
		averageFps = Math.round(averageFps * 10.0) / 10.0;
	}
}
