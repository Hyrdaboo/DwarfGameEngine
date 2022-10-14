package DwarfEngine;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.SimpleGraphics2D.Draw2D;

public abstract class Application extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private static int FPS = 0;
    public static double deltaTime;
    public static double time;
    private static double startTime;
    private int FrameWidth = 0; 
    private int FrameHeight = 0;
    public int scaleX = 0;
    public int scaleY = 0;
    public static String AppName = "Untitled"; 
    public static Vector2 windowSize = Vector2.zero();
    
    private String title = "Dwarf Engine - initializing...";
    private Thread GameThread;
    private JFrame GameWindow;
    private boolean Running = false;

    private Screen screen;
    private Input input;
    private BufferedImage image;
    private int[] pixels;

    private boolean resizable = false;
    private boolean fullscreen = false;
    
    public void SetFullscreen() {
    	GameWindow.dispose();
    	if (!fullscreen) {
    		GameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
            GameWindow.setUndecorated(true);
            GameWindow.setVisible(true);
            fullscreen = true;
    		return;
    	}
    	
        GameWindow.setUndecorated(false);
        GameWindow.pack();
        GameWindow.setLocationRelativeTo(null);
        GameWindow.setVisible(true);
        fullscreen = false;
    }
    public void SetResizable(boolean resizable) {
    	this.resizable = resizable;
    }
    public void Construct(int width, int height, int scale) {
    	FrameWidth = width;
    	FrameHeight = height;
    	this.scaleX = scale;
    	this.scaleY = scale;
    	
    	image = new BufferedImage(FrameWidth, FrameHeight, BufferedImage.TYPE_INT_RGB);
    	pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    	Dimension size = new Dimension(FrameWidth * scaleX, FrameHeight * scaleY);
        setPreferredSize(size);
        
        screen = Screen.getInstance(FrameWidth, FrameHeight); 
        Draw2D.screen = screen;
        GameWindow = new JFrame();
        
        input = Input.GetInstance();
        addKeyListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
        
        Start();
    }
    public void setIcon(String imagepath) {
    	try {
    		GameWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagepath)));
    	}
    	catch (Exception e) {
    		Debug.println("failed to load icon");
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
			ImageIO.write(image, "png", outputFile);
			Debug.println("Image " + imageName + ".png" + " saved to " + directory);
		} catch (IOException e) {
			Debug.println("AN ERROR OCCURED WHILE TRYING TO SAVE THE IMAGE. Is the given directory correct?");
			e.printStackTrace();
		}
    }
    
    
    public abstract void OnStart(); 
    public abstract void OnUpdate();

    private synchronized void Start() {
        GameWindow.setTitle(title);
        GameWindow.add(this);
        GameWindow.pack();
        GameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameWindow.setLocationRelativeTo(null);
        GameWindow.setVisible(true);
        GameWindow.setResizable(resizable);
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        GameWindow.getContentPane().setBackground(Color.black);
        
        Running = true;
        GameThread = new Thread(this, "Dwarf Engine Main Thread");
        OnStart();
        startTime = System.currentTimeMillis();
        GameThread.start();
    }
    
    public synchronized void Exit () {
        
        Running = false;
        try {
        	GameWindow.dispatchEvent(new WindowEvent(GameWindow, WindowEvent.WINDOW_CLOSING));;
            GameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run () {
        long lastTime = System.nanoTime();
        double fpsUpdateRate = 0;
        while (Running) {
            long now = System.nanoTime();
            deltaTime = (now - lastTime) / 1000000000.0;
            lastTime = now;
            fpsUpdateRate += deltaTime;
            if  (fpsUpdateRate >= .5f) { 
                FPS = (int) (1 / deltaTime);
                // set the title to the current FPS
                title = "Dwarf Engine - " + AppName + " | FPS: " + FPS;
                GameWindow.setTitle(title);
                fpsUpdateRate = 0;
            }
            time = (System.currentTimeMillis()-startTime)/1000;
            render();
            utility();
        }
    }
    
    public Vector2 getWindowSize() {
    	return new Vector2(FrameWidth, FrameHeight);
    }
    
    private void utility() {
    	windowSize.x = FrameWidth;
    	windowSize.y = FrameHeight;
    	OnUpdate();
    	
    	Input.applicationWindow = GameWindow;
    	Input.canvasSize = new Point(getWidth(), getHeight());
    }
    
    public void clear(Color color) {
    	screen.clear(color);
    }
    
    public void render () {
        BufferStrategy bufferStrategy = getBufferStrategy();
        
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            return;
        }
        
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bufferStrategy.show();
    }
}
