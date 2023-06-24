package DwarfEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DwarfEngine.Core.DisplayRenderer;
import DwarfEngine.MathTypes.Vector2;

/**
 * Basic font rendering. Does not support font files or any advanced features. Uses image font atlas that consists of
 * printable ASCII characters: <br>
 *  !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~ <br><br>
 *  If you want to change the font you will need to create a Texture atlas using the charset mentioned above.
 *  You can use <a href="https://lucide.github.io/Font-Atlas-Generator/">Font Atlas Generator</a> to create your own font atlas
 */
public class Text {
	private Texture source;
	private int charSize;
	private List<Texture> charImages = new ArrayList<>();
	/**
	 * Spacing of characters when being drawn.<br> Can be negative. Default value is 0
	 */
	public float spacing = 0;
	
	private static final HashMap<Character, int[]> charset = new HashMap<>() {
		{
			put(' ', new int[] {0, 0});
			put('!', new int[] {1, 0});
			put('"', new int[] {2, 0});
			put('#', new int[] {3, 0});
			put('$', new int[] {4, 0});
			put('%', new int[] {5, 0});
			put('&', new int[] {6, 0});
			put('\'', new int[] {7,0});
			put('(', new int[] {8, 0});
			put(')', new int[] {9, 0});
			put('*', new int[] {10,0});
			put('+', new int[] {11,0});
			put(',', new int[] {12,0});
			put('-', new int[] {13,0});
			put('.', new int[] {14,0});
			put('/', new int[] {15,0});
			
			put('0', new int[] {0, 1});
			put('1', new int[] {1, 1});
			put('2', new int[] {2, 1});
			put('3', new int[] {3, 1});
			put('4', new int[] {4, 1});
			put('5', new int[] {5, 1});
			put('6', new int[] {6, 1});
			put('7', new int[] {7, 1});
			put('8', new int[] {8, 1});
			put('9', new int[] {9, 1});
			put(':', new int[] {10,1});
			put(';', new int[] {11,1});
			put('<', new int[] {12,1});
			put('=', new int[] {13,1});
			put('>', new int[] {14,1});
			put('?', new int[] {15,1});
			
			put('@', new int[] {0, 2});
			put('A', new int[] {1, 2});
			put('B', new int[] {2, 2});
			put('C', new int[] {3, 2});
			put('D', new int[] {4, 2});
			put('E', new int[] {5, 2});
			put('F', new int[] {6, 2});
			put('G', new int[] {7, 2});
			put('H', new int[] {8, 2});
			put('I', new int[] {9, 2});
			put('J', new int[] {10,2});
			put('K', new int[] {11,2});
			put('L', new int[] {12,2});
			put('M', new int[] {13,2});
			put('N', new int[] {14,2});
			put('O', new int[] {15,2});
			
			put('P', new int[] {0, 3});
			put('Q', new int[] {1, 3});
			put('R', new int[] {2, 3});
			put('S', new int[] {3, 3});
			put('T', new int[] {4, 3});
			put('U', new int[] {5, 3});
			put('V', new int[] {6, 3});
			put('W', new int[] {7, 3});
			put('X', new int[] {8, 3});
			put('Y', new int[] {9, 3});
			put('Z', new int[] {10,3});
			put('[', new int[] {11,3});
			put('\\', new int[] {12,3});
			put(']', new int[] {13,3});
			put('^', new int[] {14,3});
			put('_', new int[] {15,3});
			
			put('`', new int[] {0, 4});
			put('a', new int[] {1, 4});
			put('b', new int[] {2, 4});
			put('c', new int[] {3, 4});
			put('d', new int[] {4, 4});
			put('e', new int[] {5, 4});
			put('f', new int[] {6, 4});
			put('g', new int[] {7, 4});
			put('h', new int[] {8, 4});
			put('i', new int[] {9, 4});
			put('j', new int[] {10,4});
			put('k', new int[] {11,4});
			put('l', new int[] {12,4});
			put('m', new int[] {13,4});
			put('n', new int[] {14,4});
			put('o', new int[] {15,4});
			
			put('p', new int[] {0, 5});
			put('q', new int[] {1, 5});
			put('r', new int[] {2, 5});
			put('s', new int[] {3, 5});
			put('t', new int[] {4, 5});
			put('u', new int[] {5, 5});
			put('v', new int[] {6, 5});
			put('w', new int[] {7, 5});
			put('x', new int[] {8, 5});
			put('y', new int[] {9, 5});
			put('z', new int[] {10,5});
			put('{', new int[] {11,5});
			put('|', new int[] {12,5});
			put('}', new int[] {13,5});
			put('~', new int[] {14,5});
		}
	};
	
	/**
	 * Creates text object defined by a font atlas and size of each character
	 * @param fontAtlas A texture that contains all the characters from ASCII printable charset
	 * @param characterSize cell size of each character in the font atlas
	 */
	public Text(Texture fontAtlas, int characterSize) {
		source = fontAtlas;
		charSize = characterSize;
	}
	
	/**
	 * Assings text to be drawn
	 * @param text
	 */
	public void SetText(String text) {
		charImages.clear();
		for (char c : text.toCharArray()) {
			int x = 14, y = 4;
			if (charset.containsKey(c)) {
				x = charset.get(c)[0];
				y = charset.get(c)[1];
			}
			if (c == '\n') {
				charImages.add(null);
				continue;
			}
			charImages.add(getTexture(x, y));
		}
	}
	
	private Texture getTexture(int x, int y) {
		Texture tex = new Texture(charSize, charSize);
		int[] pixels = source.GetPixels(x*charSize, y*charSize, charSize, charSize);
		tex.SetPixels(pixels, charSize, charSize, 0, 0);
		return tex;
	}
	
	/**
	 * Draws the text assigned to this object
	 * @param position Screen space position of the text (top-left)
	 * @param scale Scale of the whole text
	 */
	public void drawText(Vector2 position, Vector2 scale) {
		Vector2 pos = new Vector2(position);
		Vector2 size = Vector2.mulVecFloat(scale, charSize);
		for (int i = 0; i < charImages.size(); i++) {
			Texture t = charImages.get(i);
			if (t == null) {
				pos.y += charSize * scale.y + spacing;
				pos.x = position.x;
				continue;
			}
			DisplayRenderer.DrawImage(pos, size, charImages.get(i));
			pos.x += charSize * scale.x + spacing;
		}
	}
}
