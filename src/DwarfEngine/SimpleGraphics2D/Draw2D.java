package DwarfEngine.SimpleGraphics2D;

import java.awt.Color;
import DwarfEngine.Application;
import DwarfEngine.Screen;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Vector2;

public final class Draw2D {
	public static Screen screen = null;
	
	private Draw2D() {
		
	}
	
	public static void SetPixel(int x, int y, Color color) {
		screen.SetPixel(x, y, color);
	}
	
	public static void FillRect(Vector2 pos, Vector2 size, Color color) {
    	for (int y = 0; y < size.y; y++) {
    		for (int x = 0; x < size.x; x++) {
    			
    			SetPixel((int)pos.x+x, (int)pos.y+y, color);
    		}
    	}
     }
    
    public static void DrawCircle(Vector2 pos, float radius, Color color) {
    	int x, y, r2;
    	int xCent = (int)pos.x;
    	int yCent = (int)pos.y;
    	int radiusInt = (int)radius;
    	
    	r2 = (int)(radius*radius);
    	SetPixel(xCent, yCent + radiusInt, color);
        SetPixel(xCent, yCent - radiusInt, color);
        SetPixel(xCent + radiusInt, yCent, color);
        SetPixel(xCent - radiusInt, yCent, color);
    	
    	y= (int)radius;
    	x = 1;
    	y = (int)(Math.sqrt(r2-1)+0.5f);
    	while (x < y) {
            SetPixel(xCent + x, yCent + y, color);
            SetPixel(xCent + x, yCent - y, color);
            SetPixel(xCent - x, yCent + y, color);
            SetPixel(xCent - x, yCent - y, color);
            SetPixel(xCent + y, yCent + x, color);
            SetPixel(xCent + y, yCent - x, color);
            SetPixel(xCent - y, yCent + x, color);
            SetPixel(xCent - y, yCent - x, color);
            x += 1;                           
            y = (int) (Math.sqrt(r2 - x*x) + 0.5);
        }
    	 if (x == y) {
            SetPixel(xCent + x, yCent + y, color);
            SetPixel(xCent + x, yCent - y, color);
            SetPixel(xCent - x, yCent + y, color);
            SetPixel(xCent - x, yCent - y, color);
         }
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
    
    public static void DrawRect(Vector2 pos, Vector2 size, Color color) {
    	LineTo(pos, new Vector2(pos.x+size.x, pos.y), color);
    	LineTo(new Vector2(pos.x+size.x, pos.y), new Vector2(pos.x+size.x, pos.y+size.y), color);
    	LineTo(new Vector2(pos.x+size.x, pos.y+size.y), new Vector2(pos.x, pos.y+size.y), color);
    	LineTo(new Vector2(pos.x, pos.y+size.y), pos, color);
    }
    
    private static void LineToLow(Vector2 from, Vector2 to, Color color) {
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
    
    private static void LineToHigh(Vector2 from, Vector2 to, Color color) {
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
    
    public static void LineTo(Vector2 from, Vector2 to, Color color) {
    	if (Math.abs(to.y-from.y) < Math.abs(to.x - from.x)) {
    		if (from.x > to.x) {
    			LineToLow(to, from, color);
    		}
    		else {
    			LineToLow(from, to, color);
    		}
    	}
    	else {
			if (from.y > to.y) {
				LineToHigh(to, from, color);
			}
			else {
				LineToHigh(from, to, color);
			}
		}
    }
    
    public static void DrawTriangle(Vector2 p1, Vector2 p2, Vector2 p3, Color color) {
    	LineTo(p1, p2, color);
    	LineTo(p2, p3, color);
    	LineTo(p3, p1, color);
    }
    
    private static float minFrom3(float a, float b, float c) {
    	float lowest = Float.MAX_VALUE;
    	if (a < lowest) {
    		lowest = a;
    	}
    	if (b < lowest) {
			lowest = b;
		}
    	if (c < lowest) {
    		lowest = c;
    	}
    	return lowest;
    }
    private static float maxFrom3(float a, float b, float c) {
    	float highest = Float.MIN_VALUE;
    	if (a > highest) {
    		highest = a;
    	}
    	if (b > highest) {
			highest = b;
		}
    	if (c > highest) {
    		highest = c;
    	}
    	return highest;
    }
    
    private static boolean pointInTriangle(Vector2 A, Vector2 B, Vector2 C, int x, int y) {
    	float w1 = (A.x*(C.y-A.y) + (y - A.y)*(C.x-A.x) - x*(C.y-A.y)) / ((B.y - A.y)*(C.x - A.x) - (B.x-A.x)*(C.y-A.y)); 
		float w2 = (y - A.y - w1*(B.y - A.y)) / (C.y-A.y);
		
		if (w1 >= 0 && w2 >= 0 && (w1+w2) <= 1) return true;
		else return false;
    }
    
    public static void FillTriangle(Vector2 A, Vector2 B, Vector2 C, Color color) {
    	float startX = minFrom3(A.x, B.x, C.x);
    	float startY = minFrom3(A.y, B.y, C.y);
    	float endX = maxFrom3(A.x, B.x, C.x);
    	float endY = maxFrom3(A.y, B.y, C.y);
    	
    	startX = Math.max(startX, 0);
    	startY = Math.max(startY, 0);
    	endX = Math.min(endX, Application.windowSize.x-1);
    	endY = Math.min(endY, Application.windowSize.y-1);
    	
    	for (int y = (int) startY; y < endY; y++) {
    		for (int x = (int) startX; x < endX; x++) {
    			if (pointInTriangle(A, B, C, x, y)) SetPixel(x, y, color);
    		}
    	} 
    }  
    
    /// Sprite drawing methods ///
    public static enum SpriteDrawMode {transparent, solid}
    private static SpriteDrawMode drawMode = SpriteDrawMode.solid;
    public static void SetSpriteDrawMode(SpriteDrawMode mode) {
    	drawMode = mode;
    }
    
    private static int getRed(int rgb) {
    	return (rgb >> 16) & 0xFF;
    }
    private static int getGreen(int rgb) {
    	return (rgb >> 8) & 0xFF;
    }
    private static int getBlue(int rgb) {
    	return (rgb >> 0) & 0xFF;
    }
    private static int getAlpha(int rgb) {
    	return (rgb >> 24) & 0xFF;
    }
    
    public static void DrawSpriteTransformed(Sprite s, Vector2 pos, float angle) {
    	Vector2 center = new Vector2((s.width*s.scale.x)/2, (s.height*s.scale.y)/2);
    	
    	Matrix3x3 matCenterTrans;
		Matrix3x3 matTrans;
		Matrix3x3 matScale;
		Matrix3x3 matRot;
		Matrix3x3 matFinal = Matrix3x3.Identity();
		Matrix3x3 matFinalInv;
		
		matTrans = Matrix3x3.Translate(new Vector2(-center.x, -center.y));
		matRot = Matrix3x3.Rotate(angle);
		matScale = Matrix3x3.Scale(s.scale);
		
		matCenterTrans = Matrix3x3.MatrixMultiply(matRot, matTrans);
		matTrans = Matrix3x3.Translate(pos);
		matFinal = Matrix3x3.MatrixMultiply(matTrans, matCenterTrans);
		matFinal = Matrix3x3.MatrixMultiply(matFinal, matScale);
		
		matFinalInv = Matrix3x3.Invert(matFinal);
		
		
		float sx, sy;
		float ex, ey;
		Vector2 p;
		
		p = Matrix3x3.Forward(matFinal, 0.0f, 0.0f);
		sx = p.x; sy = p.y;
		ex = p.x; ey = p.y;
		
		p = Matrix3x3.Forward(matFinal, (float)s.getWidth(), (float)s.getHeight());
		sx = Math.min(sx, p.x); sy = Math.min(sy, p.y);
		ex = Math.max(ex, p.x); ey = Math.max(ey, p.y);
		
		p = Matrix3x3.Forward(matFinal, 0.0f, (float)s.getHeight());
		sx = Math.min(sx, p.x); sy = Math.min(sy, p.y);
		ex = Math.max(ex, p.x); ey = Math.max(ey, p.y);
		
		p = Matrix3x3.Forward(matFinal, (float)s.getWidth(), 0.0f);
		sx = Math.min(sx, p.x); sy = Math.min(sy, p.y);
		ex = Math.max(ex, p.x); ey = Math.max(ey, p.y);
		
		//Draw2D.DrawRect(new Vector2(sx-1, sy-1), new Vector2(ex-sx, ey-sy), Color.green);
		
		for (int x = (int) sx; x < ex; x++) {
			for (int y = (int) sy; y < ey; y++) {
				Vector2 newPos = Matrix3x3.Forward(matFinalInv, (float)x, (float)y);
				
				int px = (int)(newPos.x+0.5f);
				int py = (int)(newPos.y+0.5f);
				if (px < 0 || px >= s.getWidth() || py < 0 || py >= s.getHeight()) continue;
				int rgb = s.GetPixel(px, py);
				Draw2D.SetPixel(x, y, new Color(rgb));
			}
		}
    }
    
    public static void DrawSprite(Sprite s, Vector2 pos) {
    	if (s == null) return;
    	int w = (int) (s.width*s.scale.x);
    	int h = (int) (s.height*s.scale.y);
    	
    	for (int y = 0; y < h; y++) {
    		for (int x = 0; x < w; x++) {
    			int px = (int)(x+pos.x);
    			int py = (int)(y+pos.y);
    			
    			if (px > Application.windowSize.x || px < 0 || py > Application.windowSize.y || py < 0) continue;
    			
    			float xLerp = Mathf.Lerp(0, s.width, (float)x/w);
    			float yLerp = Mathf.Lerp(0, s.height, (float)y/h);
    			
    			
    			int rgb = s.GetPixel((int)xLerp,(int)yLerp);
    			
    			Color c = new Color((getRed(rgb)*s.tint.getRed())/255, 
    								(getGreen(rgb)*s.tint.getGreen())/255, 
    								(getBlue(rgb)*s.tint.getBlue())/255, getAlpha(rgb)); 
    			
    			if (drawMode.equals(SpriteDrawMode.transparent) && c.getAlpha() == 0) continue;
    			SetPixel(px, py, c);
    		}
    	} 	
    }
    
    public static void DrawSprite(SpriteSheet sheet, Vector2 pos, int x, int y) {
    	if (sheet == null) return;
    	int[] sprite = sheet.GetSprite(x, y);
    	int w = (int) (sheet.spriteWidth * sheet.scale.x);
    	int h = (int) (sheet.spriteHeight * sheet.scale.y);
    	
    	for (int j = 0; j < h; j++) {
    		for (int i = 0; i < w; i++) {
    			int px = i+(int)pos.x;
    			int py = j+(int)pos.y;
    			if (px > Application.windowSize.x || px < 0 || py > Application.windowSize.y || py < 0) continue;
    			
    			float xLerp = Mathf.Lerp(0, sheet.spriteWidth, (float)i/w);
    			float yLerp = Mathf.Lerp(0, sheet.spriteHeight, (float)j/h);
    			
    			int rgb = sprite[(int)xLerp + (int)yLerp*sheet.spriteWidth];
    			Color c = new Color((getRed(rgb)*sheet.spritesheet.tint.getRed())/255, 
									(getGreen(rgb)*sheet.spritesheet.tint.getGreen())/255, 
									(getBlue(rgb) *sheet.spritesheet.tint.getBlue())/255, (rgb >> 24)&0xFF);
    			
    			if (drawMode.equals(SpriteDrawMode.transparent) && c.getAlpha() == 0) continue;
    			SetPixel(px, py, c);
    		}
    	}
    }
}
