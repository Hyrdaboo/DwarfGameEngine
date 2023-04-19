package DwarfEngine.Core;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	class KeyEvt {
		Integer keycode;
		
	}
	
	private static List<Integer> heldKeys = new ArrayList<Integer>();
	private static Input instance = null;
	
	private Input() {}
	static Input GetInstance() {
		if (instance == null) {
			instance = new Input();
		}
		return instance;
	}
	
	////// KEYBOARD EVENTS /////////
	static int pressedKey = -1;
	static int releasedKey = -1;
	public static boolean OnKeyPressed(Keycode e) {
		if ((e.GetKeyCode() == pressedKey) || (pressedKey != -1 && e == Keycode.AnyKey)) {
			pressedKey = -1;
			return true;
		}
		return false;
	}
	
	public static boolean OnKeyReleased(Keycode e) {
		if ((e.GetKeyCode() == releasedKey) || (releasedKey != -1 && e == Keycode.AnyKey)) {
			releasedKey = -1;
			return true;
		}
		return false;
	}
	
	public static boolean OnKeyHeld(Keycode e) {
		for (int i = 0; i < heldKeys.size(); i++) {
			try {
				if (heldKeys.get(i) == e.GetKeyCode()) {
					return true;
				}
			} catch (Exception e2) {
				return false;
			}
		}
		
		if (heldKeys.size() > 0 && e == Keycode.AnyKey) {
			return true;
		}
		
		return false;
	}
	
	static void resetKeyStates() {
		heldKeys.clear();
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
			pressedKey = -1;
		}
	}

	
	//////// MOUSE EVENTS /////////
	private static Vector2 mousePosition = Vector2.zero();
	public static Vector2 getMousePosition() {
		return mousePosition;
	}
	
	private static int heldButton = -1;
	private static int clickedButton = -1;
	private static int releasedButton = -1;
	static float frameWidth, frameHeight;
	static float windowWidth, windowHeight;
	static float windowX, windowY;
	
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
		mousePosition.x = (e.getX() / windowWidth) * frameWidth;
		mousePosition.y = (e.getY() / windowHeight) * frameHeight;
	}
	
	private static Vector2 delta = Vector2.zero();
	private static float lastX = -1, lastY = -1;
	static void calculateDelta() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// prevent the big delta at the start
		lastX = lastX < 0 ? pos.x : lastX;
		lastY = lastY < 0 ? pos.y : lastY;
		
		delta.x = (pos.x - lastX) / (float)screenSize.width;
		delta.y = (pos.y - lastY) / (float)screenSize.height;
		lastX = pos.x;
		lastY = pos.y;
		
		if (!mouseConfined) return;
		float rightEdge = Mathf.Clamp(windowX + windowWidth, 0, screenSize.width-1);
		float leftEdge = Mathf.Clamp(windowX, 0, screenSize.width-1);
		float bottomEdge = Mathf.Clamp(windowY + windowHeight, 0, screenSize.height-1);
		float topEdge = Mathf.Clamp(windowY, 0, screenSize.height-1);
		
		if (pos.x >= rightEdge) {
			bot.mouseMove((int)leftEdge+1, pos.y);
			lastX = leftEdge+1;
		}
		if (pos.x <= leftEdge) {
			bot.mouseMove((int)rightEdge-1, pos.y);
			lastX = rightEdge-1;
		}
		if (pos.y >= bottomEdge) {
			bot.mouseMove(pos.x, (int)topEdge+1);
			lastY = topEdge+1;
		}
		if (pos.y <= topEdge) {
			bot.mouseMove(pos.x, (int)bottomEdge-1);
			lastY = bottomEdge-1;
		}
	}
	public static Vector2 GetMouseDelta() {
		return delta;
	}
	
	private static Robot bot;
	private static boolean mouseConfined = false;
	public static void setMouseConfined(boolean confined) {
		mouseConfined = confined;
		
		try {
			if (bot == null) bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
	}
	public static boolean isMouseConfined() {
		return mouseConfined;
	}
	
	private static int scrollDir = 0;
	static void resetScrollDir() {
		scrollDir = 0;
	}
	public static int getMouseWheel() {
		return scrollDir;
	}
	
	static float timeSinceLastScroll = 0;
	public void mouseWheelMoved(MouseWheelEvent e) {
		timeSinceLastScroll = 0;
		scrollDir = e.getWheelRotation();
	}	
}
