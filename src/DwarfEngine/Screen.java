package DwarfEngine;

import java.awt.Color;
import java.util.Arrays;

public class Screen {
    private static int width;
    private static int height;
    static int[] pixels;

    private Screen() {}
    static void Initialize(int Width, int Height) {
        width = Width;
        height = Height;
        pixels = new int[width * height];
    }
    
    static void clear(Color color) {
        Arrays.fill(pixels, color.getRGB());
    }

    public static void SetPixel(int x, int y, Color color) {
        if (y >= height || y < 0) return;
        if (x >= width || x < 0) return;
        pixels[x + y * width] = color.getRGB();
    }
}
