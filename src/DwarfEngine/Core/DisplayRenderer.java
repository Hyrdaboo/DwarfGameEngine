package DwarfEngine.Core;

import java.awt.Color;
import java.util.Arrays;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

public final class DisplayRenderer {
	private static int[] pixelBuffer;
	private static int bufferWidth;
	private static int bufferHeight;
	
	private DisplayRenderer() {};
	static void Initialize(int width, int height, int[] buffer) {
		bufferWidth = width;
		bufferHeight = height;
		pixelBuffer = buffer;
	}
	
	public static void clear(Color color) {
		Arrays.fill(pixelBuffer, color.getRGB());
	}
	
	public static void SetPixel(int x, int y, Color color) {
		if (y >= bufferHeight || y < 0) return;
		if (x >= bufferWidth || x < 0) return;
		pixelBuffer[x+y*bufferWidth] = color.getRGB();
	}
	
	/**
	 * Use this method to assign a custom pixel buffer
	 */
	public static int[] GetPixels() {
		return pixelBuffer;
	}
	
	public static void FillRect(Vector2 pos, Vector2 size, Color color) {
    	for (int y = 0; y < size.y; y++) {
    		for (int x = 0; x < size.x; x++) {
    			
    			SetPixel((int)pos.x+x, (int)pos.y+y, color);
    		}
    	}
     }
	
	public static void DrawRect(Vector2 pos, Vector2 size, Color color) {
    	DrawLine(pos, new Vector2(pos.x+size.x, pos.y), color);
    	DrawLine(new Vector2(pos.x+size.x, pos.y), new Vector2(pos.x+size.x, pos.y+size.y), color);
    	DrawLine(new Vector2(pos.x+size.x, pos.y+size.y), new Vector2(pos.x, pos.y+size.y), color);
    	DrawLine(new Vector2(pos.x, pos.y+size.y), pos, color);
    }
	
	public static void FillCircle(Vector2 center, float radius, Color color) {
    	for (int y = (int)-radius; y < radius; y++) {
    		for (int x = (int)-radius; x < radius; x++) {
    			if (x*x + y*y < radius*radius) {
    				SetPixel((int)(center.x+x), (int)(center.y+y), color);
    			}
    		}
    	}
    }
	public static void DrawCircle(Vector2 center, float radius, float strokeWidth, Color color) {
		strokeWidth = Mathf.Clamp(strokeWidth, 1, radius-1);
    	for (int y = (int)-radius; y < radius; y++) {
    		for (int x = (int)-radius; x < radius; x++) {
    			float dst = Vector2.distance(Vector2.zero(), new Vector2(x, y));
    			if (dst < radius && dst > radius-strokeWidth) {
    				SetPixel((int)(center.x+x), (int)(center.y+y), color);
    			}
    		}
    	}
    }
    
    private static void DrawLineLow(Vector2 from, Vector2 to, Color color) {
    	float dx = to.x-from.x;
    	float dy = to.y-from.y;
    	int yi = 1;
    	if (dy < 0)  {
    		yi = -1;
    		dy = -dy;
    	}
    	float D = (2*dy)-dx;
    	float y= from.y;
    	
    	for (float x = from.x; x < to.x; x++) {
    		SetPixel((int)x, (int)y, color);
    		if (D > 0) {
    			y += yi;
    			D += 2*(dy-dx);
    		}
    		else {
				D += 2*dy;
			}
    	}

    }
    
    private static void DrawLineHigh(Vector2 from, Vector2 to, Color color) {
    	float dx = to.x-from.x;
    	float dy = to.y-from.y;
    	int xi = 1;
    	if (dx < 0)  {
    		xi = -1;
    		dx = -dx;
    	}
    	float D = (2*dx)-dy;
    	float x= from.x;
    	
    	for (float y = from.y; y < to.y; y++) {
    		SetPixel((int)x, (int)y, color);
    		if (D > 0) {
    			x += xi;
    			D += 2*(dx-dy);
    		}
    		else {
				D += 2*dx;
			}
    	}
    }
    
    public static void DrawLine(Vector2 from, Vector2 to, Color color) {
    	if (Math.abs(to.y-from.y) < Math.abs(to.x - from.x)) {
    		if (from.x > to.x) {
    			DrawLineLow(to, from, color);
    		}
    		else {
    			DrawLineLow(from, to, color);
    		}
    	}
    	else {
			if (from.y > to.y) {
				DrawLineHigh(to, from, color);
			}
			else {
				DrawLineHigh(from, to, color);
			}
		}
    }
    
    public static void DrawTriangle(Vector2 p1, Vector2 p2, Vector2 p3, Color color) {
    	DrawLine(p1, p2, color);
    	DrawLine(p2, p3, color);
    	DrawLine(p3, p1, color);
    }
}
