package DwarfEngine;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JFrame;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class Input implements KeyListener, MouseListener, MouseMotionListener {
	private static List<Integer> heldKeys = new ArrayList<Integer>();
	private static Input instance = null;
	
	private Input() {
		
	}
	public static Input GetInstance() {
		if (instance == null) {
			instance = new Input();
		}
		return instance;
	}
	////// KEYBOARD EVENTS /////////
	static int pressedKey = -1;
	static int releasedKey = -1;
	public static boolean OnKeyPressed(Keycode e) {
		if (e.GetKeyCode() == pressedKey) {
			pressedKey = -1;
			return true;
		}
		return false;
	}
	public static boolean OnKeyReleased(Keycode e) {
		if (e.GetKeyCode() == releasedKey) {
			releasedKey = -1;
			return true;
		}
		return false;
	}
	//TODO heldKeys.get(i) returns null sometimes. Deal with it
	public static boolean OnKeyHeld(Keycode e) {
		for (int i = 0; i < heldKeys.size(); i++) {
			if (heldKeys.get(i) == e.GetKeyCode()) {
				return true;
			}
		}
		return false;
	}
	
	
	public void keyTyped(KeyEvent e) {}
	
    // press and hold
	public void keyPressed(KeyEvent e) {
		if (!heldKeys.contains(e.getKeyCode())) {
			heldKeys.add(e.getKeyCode());
			pressedKey = e.getKeyCode();
		}
	}
	public void keyReleased(KeyEvent e) {
		if (heldKeys.contains(e.getKeyCode())) {
			Integer code = e.getKeyCode();
			heldKeys.remove(code);
			releasedKey = e.getKeyCode();
		}
	}

	
	//////// MOUSE EVENTS /////////
	private static Vector2 mousePosition = Vector2.zero();
	private static Vector2 trueMousePosition = Vector2.zero();
	
	/**
	 * The location of mouse relative to game window
	 **/
	public static Vector2 getMousePosition() {
		return mousePosition;
	}
	
	/**
	 * The location of mouse in window relative to 
	 * device screen
	 **/
	public static Vector2 getTrueMousePosition() {
		return trueMousePosition;
	}
	
	private static int heldButton = -1;
	private static int clickedButton = -1;
	private static int releasedButton = -1;
	
	public static boolean MouseButtonHeld(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		if (heldButton == button) {
			return true;
		}
		return false;
	}
	public static boolean MouseButtonClicked(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		if (clickedButton == button) {
			clickedButton = -1;
			return true;
		}
		return false;
	}
	public static boolean MouseButtonReleased(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		if (releasedButton == button) {
			releasedButton = -1;
			return true;
		}
		return false;
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		heldButton = e.getButton();
		clickedButton = e.getButton();
	}
	public void mouseReleased(MouseEvent e) {
		heldButton = -1;
		releasedButton = e.getButton();
	}
	public void mouseEntered(MouseEvent e) {
		
	}
	public void mouseExited(MouseEvent e) {
		mouseDelta = Vector2.zero();
	}
	public void mouseDragged(MouseEvent e) {
		
	}	
	
	protected static JFrame applicationWindow;
	protected static Point canvasSize = new Point();
	private static Vector2 lastMousePos = Vector2.zero();
	private static Vector2 mouseDelta = Vector2.zero();

	private static Vector2 deltaFromLastFrame = Vector2.zero();
	private static int timesCheck = 0;
	private static int checkAccuracy = 3;
	/**
	 * <strong>Experimental!</strong><br>
	 * This function is experimental and is not guaranteed to work right.
	 * I couldn't find a decent solution that works 100% and the current implementation
	 * has some bugs and is a bit clunky
	 * @return A Vector2 that represents how much the mouse has moved since the last frame
	 **/
	public static Vector2 getMouseDelta() {
		if ((mouseDelta.x == deltaFromLastFrame.x) && (mouseDelta.y == deltaFromLastFrame.y)) {
			timesCheck++;
		}
		if (timesCheck >= checkAccuracy) {
			return Vector2.zero();
		}
		deltaFromLastFrame.x = mouseDelta.x;
		deltaFromLastFrame.y = mouseDelta.y;
		
		return mouseDelta;
	}
	
	private static boolean mouseLocked = false;
	/**
	 * Locks the mouse to the center of the screen and makes it invisible.<br>
	 * Note that mouse has to be locked in order to use {@code getMouseDelta()}
	 **/
	public static void lockMouse(boolean lock) {
		mouseLocked = lock;
		
		Cursor cursor = null;
		if (mouseLocked) {
			try {
				if (bot == null) {
					bot = new Robot();
				}
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
			lastMousePos = new Vector2(canvasSize.x/2, canvasSize.y/2);
			moveMouseToCenter();
		}
		applicationWindow.setCursor(cursor);
	}
	
	/**
	 * When mouse is stopped moving delta doesn't stop getting calculated. 
	 * To confirm that mouse has indeed stopped moving we check it some amount of times
	 * and this function sets that value. <br><br>
	 * 
	 * Higher values mean more time to stop.
	 **/
	public static void setMouseCheckAccuracy(int accuracy) {
		accuracy = (int)Mathf.Clamp(accuracy, 1, 1000);
		checkAccuracy = accuracy;
	}
	private static void moveMouseToCenter() {
		Point windowLocation = applicationWindow.getLocationOnScreen();
		Vector2 windowSize = new Vector2(applicationWindow.getWidth(), applicationWindow.getHeight());
		Vector2 middle = new Vector2(windowLocation.x+windowSize.x/2.0f, windowLocation.y+windowSize.y/2.0f);
		bot.mouseMove((int)middle.x, (int)middle.y);
	}
	
	private static Robot bot;
	public void mouseMoved(MouseEvent e) {
		mousePosition.x = e.getX();
	    mousePosition.y = e.getY();
	    trueMousePosition.x = e.getXOnScreen();
	    trueMousePosition.y = e.getYOnScreen();
	    
	    if (!mouseLocked) return;
	    timesCheck = 0;
	    
		mouseDelta = new Vector2(mousePosition.x-lastMousePos.x, mousePosition.y - lastMousePos.y);
		mouseDelta = new Vector2(mouseDelta.x*-0.002f, mouseDelta.y * -0.002f);
		moveMouseToCenter();
		lastMousePos = new Vector2(mousePosition.x, mousePosition.y);
	}
}
