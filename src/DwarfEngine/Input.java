package DwarfEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

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
	
	
	public void keyTyped(KeyEvent e) {
		
	}
    // press and hold
	public void keyPressed(KeyEvent e) {
		if (!heldKeys.contains(e.getKeyCode())) {
			heldKeys.add(e.getKeyCode());
			pressedKey = e.getKeyCode();
		}
	}
	public void keyReleased(KeyEvent e) {
		if (heldKeys.contains(e.getKeyCode())) {
			heldKeys.remove(new Integer(e.getKeyCode()));
			releasedKey = e.getKeyCode();
		}
	}

	
	//////// MOUSE EVENTS /////////
	private static Vector2 mousePosition = new Vector2(-1, -1);
	private Vector2 windowSize = Vector2.one;
	private float windowWidth = -1;
	private float windowHeight = -1;
	public void SetDimensions(Vector2 size, float w, float h) {
		windowSize = size;
		windowWidth = w;
		windowHeight = h;
	}
	public static Vector2 getMousePosition() {
		return mousePosition;
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
		
	}
	public void mouseDragged(MouseEvent e) {
		
	}	
	public void mouseMoved(MouseEvent e) {
		mousePosition.x = e.getX()/(windowSize.x/windowWidth);
	    mousePosition.y = e.getY()/(windowSize.y/windowHeight);
	}
}
