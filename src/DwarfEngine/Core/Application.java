package DwarfEngine.Core;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import DwarfEngine.FpsCounter;
import DwarfEngine.MathTypes.Vector2;

public abstract class Application extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public String title = "Untitled";
	public boolean showFpsOnTitle = true;
	
	private double deltaTime;
	private double time;
	private double startTime;
	private FpsCounter fpsCounter = new FpsCounter();
	
	private boolean isRunning = true;
	private Thread mainThread;
	
	private JFrame applicationWindow;
	private BufferedImage frame;
	private Vector2 frameSize;
	private int pixelScale = 1;
	
	public void Initialize(int resX, int resY, int pixelScale) {
		this.pixelScale = pixelScale;
		frameSize = new Vector2(resX, resY);
		
		frame = new BufferedImage(resX, resY, BufferedImage.TYPE_INT_RGB);
		DisplayRenderer.Initialize(resX, resY, ((DataBufferInt)frame.getRaster().getDataBuffer()).getData());
		Dimension size = new Dimension(resX * pixelScale, resY * pixelScale);
		setPreferredSize(size);
		
		applicationWindow = new JFrame();
		addKeyListener(Input.GetInstance());
		addMouseListener(Input.GetInstance());
		addMouseMotionListener(Input.GetInstance());
		addMouseWheelListener(Input.GetInstance());
		
		Start();
	}
	
	private synchronized void Start() {
		applicationWindow.add(this);
		applicationWindow.pack();
		applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		applicationWindow.setLocationRelativeTo(null);
		applicationWindow.setVisible(true);
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
		applicationWindow.getContentPane().setBackground(Color.black);
		
		mainThread = new Thread(this, "Dwarf Main Thread");
		OnStart();
		mainThread.start();
		
		startTime = System.currentTimeMillis();
	}
	
	private synchronized void Update() {
		updateFps();
		
		String modifiedTitle = "Dwarf Engine - " + title;
		if (showFpsOnTitle) modifiedTitle += " | " + fpsCounter.GetFps();
		applicationWindow.setTitle(modifiedTitle);
		
		if (!hasFocus()) requestFocus();
		
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
	
	double lastUpdate = .15;
	double fpsRefreshRate = .15f;
	private void updateFps() {
		if (time > lastUpdate) {
			lastUpdate = time + fpsRefreshRate;
			fpsCounter.updateFps(deltaTime);
		}
	}
	
	public synchronized void Exit() {
		isRunning = false;
		
		try {
			applicationWindow.dispatchEvent(new WindowEvent(applicationWindow, WindowEvent.WINDOW_CLOSING));
			mainThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void OnStart(); 
    public abstract void OnUpdate();
	
	public void run() {
		long timeLastFrame = System.nanoTime();
		while (isRunning) {
			long currentTime = System.nanoTime();
			deltaTime = (currentTime - timeLastFrame) / 1000000000.0;
			timeLastFrame = currentTime;
			time = (System.currentTimeMillis()-startTime) / 1000.0f;
			
			render();
			Update();
		}
	}
	
	private void render() {
		BufferStrategy bufferStrategy = getBufferStrategy();
		
		if (bufferStrategy == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics graphics = bufferStrategy.getDrawGraphics();
		graphics.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
		graphics.dispose();
		bufferStrategy.show();
	}
	
	// Some useful functions //
	
	public double getDeltaTime() {
		return deltaTime;
	}
	public Vector2 getFrameSize() {
		return new Vector2(frameSize.x, frameSize.y);
	}
	public int getPixelScale() {
		return pixelScale;
	}
	
	boolean fullscreen = false;
	public void switchFullscreen() {
		applicationWindow.dispose();
		Input.resetKeyStates();
		
    	if (!fullscreen) {
    		applicationWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
	
	public final void setIcon(String imagepath) {
    	try {
    		applicationWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagepath)));
    	}
    	catch (Exception e) {
    		Debug.log("failed to load icon");
			return;
		}
    }
	
    public void saveImage(String directory, String imageName) {
    	File outputFile = new File(directory + "/" + imageName + ".png");
    	int i = 1;
    	while (outputFile.exists()) {
    		outputFile = new File(directory + "/" + imageName + i + ".png");
    		i++;
    	}
    	try {
			ImageIO.write(frame, "png", outputFile);
			Debug.log("Image " + imageName + ".png" + " saved to " + directory);
		} catch (IOException e) {
			Debug.log("AN ERROR OCCURED WHILE TRYING TO SAVE THE IMAGE. Is the given directory correct?");
			e.printStackTrace();
		}
    }
    
    public void log(Object o) {
    	Debug.log(o);
    }
}
