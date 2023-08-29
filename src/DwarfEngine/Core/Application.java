package DwarfEngine.Core;

import DwarfEngine.FpsCounter;
import DwarfEngine.MathTypes.Vector2;
import Renderer3D.SceneManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

/**
 * The <code>Application</code> class serves as the central component of the
 * program, responsible for rendering pixels and creating the application
 * window. It is an abstract class that extends the <code>Canvas</code> class,
 * providing a drawing surface on which pixels can be rendered. Subclasses of
 * <code>Application</code> must implement the necessary methods for rendering
 * and handling user interactions.
 */
public abstract class Application extends Canvas {
	private static final long serialVersionUID = 1L;

	public String title = "Untitled";
	public boolean onTitleFrameStats = true;

	private double deltaTime;
	private double time;
	private double startTime;
	private final FpsCounter fpsCounter = new FpsCounter();

	private boolean isRunning = true;

	private JFrame applicationWindow;
	private BufferedImage frame;
	private Vector2 frameSize;
	private int pixelScale = 1;

	/**
	 * Initializes the Application with the specified resolution and pixel scale.
	 *
	 * @param resX       The horizontal resolution of the display.
	 * @param resY       The vertical resolution of the display.
	 * @param pixelScale The scaling factor for the display pixels.
	 */
	public void Initialize(int resX, int resY, int pixelScale) {
		pixelScale = Math.max(pixelScale, 1);
		this.pixelScale = pixelScale;
		frameSize = new Vector2(resX, resY);

		frame = new BufferedImage(resX, resY, BufferedImage.TYPE_INT_RGB);
		DisplayRenderer.Initialize(resX, resY, ((DataBufferInt) frame.getRaster().getDataBuffer()).getData());
		Dimension size = new Dimension(resX * pixelScale, resY * pixelScale);
		setPreferredSize(size);

		applicationWindow = new JFrame();
		addKeyListener(Input.GetInstance());
		addMouseListener(Input.GetInstance());
		addMouseMotionListener(Input.GetInstance());
		addMouseWheelListener(Input.GetInstance());
		setFocusTraversalKeysEnabled(false);

		SceneManager.setTargetApplication(this);

		// initialize application window and start the application
		applicationWindow.add(this);
		applicationWindow.pack();
		applicationWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		applicationWindow.setLocationRelativeTo(null);
		applicationWindow.setVisible(true);
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
		applicationWindow.getContentPane().setBackground(Color.black);

		try {
			setIcon(ImageIO.read(Application.class.getResource("/Dwarf-Logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		OnStart();
		startTime = System.currentTimeMillis();
		Run();
	}

	private void Update() {

		String modifiedTitle = "Dwarf Engine - " + title;
		if (onTitleFrameStats) {
			double ms = 1.0 / fpsCounter.GetFps() * 1000;
			modifiedTitle += String.format(" | %.1f FPS", fpsCounter.GetFps()) + String.format(" %.1f ms", ms);
		}
		applicationWindow.setTitle(modifiedTitle);

		if (!hasFocus())
			requestFocus();

		Input.frameWidth = frameSize.x;
		Input.frameHeight = frameSize.y;
		Input.windowWidth = getWidth();
		Input.windowHeight = getHeight();
		Input.windowX = getLocationOnScreen().x;
		Input.windowY = getLocationOnScreen().y;

		if (Input.timeSinceLastScroll > deltaTime) {
			Input.resetScrollDir();
		}
		Input.timeSinceLastScroll += deltaTime;
		Input.calculateDelta();

		OnUpdate();
	}

	/**
	 * Gracefully exits the application by stopping the main loop and triggering the
	 * window closing event. This method should be called to terminate the
	 * application and clean up any resources.
	 */
	public synchronized void Exit() {
		isRunning = false;
		applicationWindow.dispatchEvent(new WindowEvent(applicationWindow, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * This method is called when the application starts. Implement this method in
	 * your subclass to define the actions to be performed on application start.
	 */
	public abstract void OnStart();

	/**
	 * This method is called on every update frame of the application. Implement
	 * this method in your subclass to define the actions to be performed on each
	 * update.
	 */
	public abstract void OnUpdate();

	private void Run() {
		long timeLastFrame = System.nanoTime();
		long frameIndex = 0;
		long benchmarkWarmup = 20;
		long benchmarkFrames = -1;
		long benchmarkTime = 0;
		deltaTime = 1e-9;
		time = (System.currentTimeMillis() - startTime) / 1e9;
		while (isRunning) {

			boolean isBenchmarking = frameIndex >= benchmarkWarmup && frameIndex - benchmarkWarmup < benchmarkFrames;
			if (isBenchmarking) { // disable input, fix time
				time = 10;
				deltaTime = 0;
			}

			Update();
			Render();

			long currentTime = System.nanoTime();
			long deltaTimeNanos = currentTime - timeLastFrame;
			deltaTime = deltaTimeNanos / 1e9;
			timeLastFrame = currentTime;
			time = (System.currentTimeMillis() - startTime) / 1e9;
			fpsCounter.updateFps(deltaTime);
			if (isBenchmarking) {
				benchmarkTime += deltaTimeNanos;
				if (frameIndex == benchmarkWarmup + benchmarkFrames - 1) {
					System.out.println("Benchmark Result: " + (benchmarkTime * 1e-6f / benchmarkFrames) + " ms/frame");
					// frameIndex = benchmarkWarmup - 1;// restarting benchmark ^^
					benchmarkTime = 0;
				}
			}
			frameIndex++;
		}
	}

	private void Render() {
		BufferStrategy bufferStrategy = getBufferStrategy();

		if (bufferStrategy == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics graphics = bufferStrategy.getDrawGraphics();
		graphics.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
		graphics.dispose();
		bufferStrategy.show();
	}

	// Some useful functions //

	/**
	 * Returns the time elapsed between the current and previous frame in seconds.
	 *
	 * @return The time difference between frames as a double value.
	 */
	public double getDeltaTime() {
		return deltaTime;
	}

	/**
	 * Returns the time passed since the application started in seconds.
	 *
	 * @return The time since start as a double value.
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Returns the size of the application frame.
	 *
	 * @return The size of the application frame as a Vector2 object.
	 */
	public Vector2 getFrameSize() {
		return new Vector2(frameSize.x, frameSize.y);
	}

	public int getPixelScale() {
		return pixelScale;
	}

	boolean fullscreen = false;

	/**
	 * Switches the application between fullscreen and windowed mode. This method
	 * disposes the application window and resets the key states. If the application
	 * is currently in windowed mode, it will switch to fullscreen mode. If the
	 * application is already in fullscreen mode, it will switch back to windowed
	 * mode.
	 */
	public void switchFullscreen() {
		applicationWindow.dispose();
		Input.resetKeyStates();

		if (!fullscreen) {
			applicationWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
			applicationWindow.setUndecorated(true);
			applicationWindow.setVisible(true);

			fullscreen = true;
			return;
		}

		applicationWindow.setUndecorated(false);
		applicationWindow.pack();
		applicationWindow.setLocationRelativeTo(null);
		applicationWindow.setVisible(true);
		fullscreen = false;
	}

	/**
	 * Sets the icon of the application window
	 *
	 * @param icon The image to be set as an icon
	 */
	public final void setIcon(Image icon) {
		applicationWindow.setIconImage(icon);
	}

	/**
	 * Saves the current frame as an image file in the specified directory with the
	 * given image name.
	 *
	 * @param directory The directory path where the image will be saved.
	 * @param imageName The name of the image file (without the file extension).
	 */
	public void saveImage(String directory, String imageName) {
		File outputFile = new File(directory, imageName + ".png");
		if (!outputFile.getParentFile().exists()) {
			System.err.println("Specified directory is invalid!");
			return;
		}
		try {
			ImageIO.write(frame, "png", outputFile);
			System.out.println("Image " + outputFile.getName() + " saved to " + directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
