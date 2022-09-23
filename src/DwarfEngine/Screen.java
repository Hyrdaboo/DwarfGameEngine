package DwarfEngine;

import java.awt.Color;
import java.util.Arrays;

public class Screen {
    private int width;
    private int height;
    public int[] pixels;

    private static Screen screenInstance = null;
    private Screen(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }

    public static Screen getInstance(int width, int height) {
    	if (screenInstance == null) {
    		screenInstance = new Screen(width, height);
    	}
    	return screenInstance;
    }
    
    public void clear(Color color) {
        Arrays.fill(pixels, color.getRGB());
    }

    public void SetPixel(int x, int y, Color color) {
        if (y >= height || y < 0) return;
        if (x >= width || x < 0) return;
        pixels[x + y * width] = color.getRGB();
    }
}
