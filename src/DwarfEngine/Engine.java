package DwarfEngine;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


import javax.swing.JFrame;

import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.SimpleGraphics2D.Draw2D;

public abstract class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private static int FPS = 0;
    public static double deltaTime;
    public static double time;
    private static double startTime;
    private int WindowWidth = 0; 
    private int WindowHeight = 0;
    public int scaleX = 0;
    public int scaleY = 0;
    public static String AppName = "Untitled"; 
    public static Vector2 windowSize = Vector2.zero;
    
    private String title = "Dwarf Engine - initializing...";
    private Thread GameThread;
    private JFrame GameWindow;
    private boolean Running = false;

    private Screen screen;
    private Input input;
    private BufferedImage image;
    private int[] pixels;

    public static void PrintLn(Object msg) {
		System.out.println(msg);
	}
    

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
    public void Construct(int width, int height) {
    	WindowWidth = width;
    	WindowHeight = height;
    	if (fullscreen) {
    		this.scaleX = getWidth() / width;
    		this.scaleY = getHeight() / height;
    		PrintLn(scaleX);
    	}
    	else {
			this.scaleX = 1;
			this.scaleY = 1;
		}
    	
    	image = new BufferedImage(WindowWidth, WindowHeight, BufferedImage.TYPE_INT_RGB);
    	pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    	Dimension size = new Dimension(WindowWidth * scaleX, WindowHeight * scaleY);
        setPreferredSize(size);
        
        screen = Screen.getInstance(WindowWidth, WindowHeight); 
        Draw2D.screen = screen;
        GameWindow = new JFrame();
        
        input = Input.GetInstance();
        addKeyListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
        
        Start();
    }
    public void Construct(int width, int height, int scale) {
    	WindowWidth = width;
    	WindowHeight = height;
    	this.scaleX = scale;
    	this.scaleY = scale;
    	
    	image = new BufferedImage(WindowWidth, WindowHeight, BufferedImage.TYPE_INT_RGB);
    	pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    	Dimension size = new Dimension(WindowWidth * scaleX, WindowHeight * scaleY);
        setPreferredSize(size);
        
        screen = Screen.getInstance(WindowWidth, WindowHeight); 
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
    		PrintLn("failed to load icon");
			return;
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
        GameWindow.setBackground(Color.black);
        
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
        double OneSecond = 0;
        while (Running) {
            long now = System.nanoTime();
            deltaTime = (now - lastTime) / 1000000000.0;
            lastTime = now;
            OneSecond += deltaTime;
            if  (OneSecond >= 1) { 
                FPS = (int) (1 / deltaTime);
                // set the title to the current FPS
                title = "Dwarf Engine - " + AppName + " | FPS: " + FPS;
                GameWindow.setTitle(title);
                OneSecond = 0;
            }
            time = (System.currentTimeMillis()-startTime)/1000;
            render();
            utility();
        }
    }
    
    public Vector2 getWindowSize() {
    	
    	return new Vector2(WindowWidth, WindowHeight);
    }
    
    private void utility() {
    	windowSize.x = WindowWidth;
    	windowSize.y = WindowHeight;
    	OnUpdate();
        input.SetDimensions(new Vector2(getWidth(), getHeight()), WindowWidth, WindowHeight);
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
        graphics.setColor(Color.black);
        graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bufferStrategy.show();
    }
}
