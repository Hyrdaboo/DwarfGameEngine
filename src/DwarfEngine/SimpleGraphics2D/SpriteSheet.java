package DwarfEngine.SimpleGraphics2D;

import DwarfEngine.MathTypes.Vector2;

public final class SpriteSheet {
	protected Sprite spritesheet;
	public int spriteWidth;
	public int spriteHeight;
	public Vector2 scale = Vector2.one();
	private int[] pixels;
	
	public SpriteSheet(Sprite spritesheet, int spriteWidth, int spriteHeight) {
		this.spritesheet = spritesheet;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		pixels = new int[spriteWidth*spriteHeight];
	}
	protected int[] GetSprite(int x, int y) {
		for (int j = 0; j < spriteHeight; j++) {
			for (int i = 0; i < spriteWidth; i++) {
				pixels[i+j*spriteWidth] = spritesheet.GetPixel((i+spriteWidth*x)&spritesheet.width-1, (j+spriteHeight*y)&spritesheet.height-1);
			}
		}
		return pixels;
	}
}
