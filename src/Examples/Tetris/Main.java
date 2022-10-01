package Examples.Tetris;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import DwarfEngine.Application;
import DwarfEngine.Debug;
import DwarfEngine.Input;
import DwarfEngine.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.SimpleGraphics2D.Draw2D;
import DwarfEngine.SimpleGraphics2D.Sprite;

class Tile {
	public int x = 0;
	public int y = 0;
	public Sprite sprite;
	
	public boolean lit = false;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void DrawTile () {
		if (sprite == null) {
			Debug.println("Sprite is null");
			return;
		}
		Draw2D.DrawSprite(sprite, new Vector2(x*sprite.getWidth(), y*sprite.getHeight()));
	}
}

class Tetris extends Application {
	private static final long serialVersionUID = 1L;
	
	private Tile[] tiles;
	private Sprite tileSprite = new Sprite("/Textures/tile.png");
	private int w, h, xTiles, yTiles;
	
	private int ty = 0;
	private int tx = 5;
	private int theta = 0;
	private int minY = 15;
	private boolean gameIsOver = false;
	
	private Vector2[] T = new Vector2[] {
		new Vector2(0, 0),
		new Vector2(-1, 0),
		new Vector2(1, 0),
		new Vector2(0, -1)
	};
	private Vector2[] square = new Vector2[] {
		new Vector2(0, 0),
		new Vector2(1, 0),
		new Vector2(0, 1),
		new Vector2(1, 1)
	};
	private Vector2[] straight = new Vector2[] {
		new Vector2(0, 0),	
		new Vector2(-1, 0),	
		new Vector2(1, 0),	
		new Vector2(2, 0),	
	};
	private Vector2[] z = new Vector2[] {
		new Vector2(0, 0),
		new Vector2(1, 0),
		new Vector2(0, -1),
		new Vector2(-1, -1),
	};
	private Vector2[] reverseZ = new Vector2[] {
			new Vector2(0, 0),
			new Vector2(-1, 0),
			new Vector2(0, -1),
			new Vector2(1, -1),
		};
	private Vector2[] L = new Vector2[] {
		new Vector2(0, 0),	
		new Vector2(1, 0),	
		new Vector2(0, -1),	
		new Vector2(0, -2),	
	};
	private Vector2[] reverseL = new Vector2[] {
			new Vector2(0, 0),	
			new Vector2(-1, 0),	
			new Vector2(0, -1),	
			new Vector2(0, -2),	
	};
	
	private Vector2[] selectedTetromino = straight;
	private Color selectedColor = getRandomColor();
	private List<Vector2> fallenBlocks = new ArrayList<Vector2>();
	private HashMap<Vector2, Color> fallenBlockColors = new HashMap<Vector2, Color>();
	private Vector2[][] allTetrominos = new Vector2[][]{T, square, straight, z, reverseZ, L, reverseL};
	private int[] randRotation = {0, 90, 180, 270};
	
	public int score = 0;
	public void OnStart() {
	    w = (int) getWindowSize().x;
		h = (int) getWindowSize().y;
		xTiles = (w/tileSprite.getWidth());
		yTiles = (h/tileSprite.getHeight());
		
		AppName = "Tetris";
		tiles = new Tile[xTiles*yTiles];
		
		for (int y = 0; y < yTiles; y++) {
			for (int x = 0; x < xTiles; x++) {
				tiles[x + y*xTiles] = new Tile(x, y);
				tiles[x + y*xTiles].sprite = tileSprite;
			}
		}
		selectedTetromino = allTetrominos[new Random().nextInt(0, allTetrominos.length)];
		
		setIcon("/Textures/logo.jpg");
	}
	
	public void OnUpdate() {
		clear(Color.black);
		if (Input.OnKeyPressed(Keycode.R) && gameIsOver) {
			gameOver(false);
		}
		if (!gameIsOver) GameLoop();
	}
	
	private void GameLoop() {
		for (Tile t : tiles) {
			t.lit = false;
			t.sprite.tint = Color.white;
		}
		for (Vector2 v : fallenBlocks) {
			int x = (int)v.x;
			int y = (int)v.y;
			SetTile(x, y, fallenBlockColors.get(v));
		}
		GetInput();
		DrawTetorid(selectedTetromino, tx, ty, theta);
	}
	
	private void DrawTetorid(Vector2[] tetromino, int x, int y, int angle) {
		Vector2[] pos = calculateTetrominoPos(tetromino, x, y, angle);
		for (Vector2 v : pos) {
			SetTile((int)v.x, (int)v.y, selectedColor);
		}
	}
	
	private void changeTetromino(Vector2[] tetromino) {
		minY = ty;
		if (minY == 0) {
			gameOver(true);
		}
		ty = 0;
		for (Vector2 v : tetromino) {
			fallenBlocks.add(v);
			fallenBlockColors.putIfAbsent(v, selectedColor);
		}
		Random rand = new Random();
		theta = randRotation[rand.nextInt(0, randRotation.length)];
		selectedTetromino = allTetrominos[rand.nextInt(0, allTetrominos.length)];
		selectedColor = getRandomColor();
	}
	
	private void clearRow() {
		boolean destroy = false;
		for (int y = 0; y < yTiles; y++) {
			for (int x = 0; x < xTiles; x++) {
				if (!tiles[x+y*xTiles].lit) {
					destroy = false;
					break;
				}
				destroy = true;
			}
			if (destroy) {
				for (int d = 0; d < 5; d++) {
					for (int i = 0; i < fallenBlocks.size(); i++) {
						if ((int)fallenBlocks.get(i).y == y) {
							fallenBlockColors.remove(fallenBlocks.get(i));
							fallenBlocks.remove(i);
							score += 10;
						}
					}
				}
				for (Vector2 v : fallenBlocks) {
					if (v.y < y) {
						v.y += 1;
					}
				}
				destroy = false;
				Debug.println(score);
			}
		}
	}
	
	private boolean checkCollision(Vector2[] tetromino, int x, int y, int angle) {
		Vector2[] pos = calculateTetrominoPos(tetromino, x, y, angle);
		for (Vector2 v : pos) {
			int tileX = (int)v.x;
			int tileY = (int)v.y;
			
			
			if(tileY < yTiles && tileY > 0 && tileX < xTiles && tiles[tileX+(tileY)*xTiles].lit) {
				return true;
			}
			if (tileX >= xTiles || tileX < 0 || tileY > yTiles-1) {
				return true;
			}
		}
		return false;
	}
	
	private Vector2[] calculateTetrominoPos(Vector2[] tetromino, int x, int y, int angle) {
		List<Vector2> translated = new ArrayList<Vector2>();

		for (Vector2 v : tetromino) {
			Vector2 center = new Vector2(0, 0);
			if (tetromino.equals(straight) || tetromino.equals(square)) {
				center = new Vector2(0.5f, 0.5f);
			}
			if (tetromino.equals(L)) {
				center = new Vector2(0.5f, -1f);
			}
			if (tetromino.equals(reverseL)) {
				center = new Vector2(-0.5f, -1f);
			}
 			Vector2 rotated = new Vector2(v.x, v.y);
			if (angle == 0 || angle == 90 || angle == 180 || angle == 270) {
				rotated = rotatePoint(v, center, angle);
			}	
			translated.add(new Vector2(x+rotated.x, y+rotated.y));
		}
		return translated.toArray(new Vector2[translated.size()]);
	}
	 
	private Vector2 rotatePoint(Vector2 point, Vector2 pivot, float angle) {
		angle *= Mathf.Deg2Rad;
		float xTrans = point.x - pivot.x;
		float yTrans = point.y - pivot.y;
		float x = (float) (xTrans*Math.cos(angle) - yTrans*Math.sin(angle));
		float y = (float) (xTrans*Math.sin(angle) + yTrans*Math.cos(angle));
		x += pivot.x;
		y += pivot.y;
		x = Math.round(x);
		y = Math.round(y);
		return new Vector2(x, y);
	}
	
	private Color getRandomColor() {
		Color[] colors = new Color[]{Color.blue, Color.red, Color.green, Color.pink, Color.yellow, Color.cyan, Color.gray};
		return colors[new Random().nextInt(0, colors.length)];
	}
	
	private void SetTile(int x, int y, Color tint) {
		if (x >= xTiles || y >= yTiles || x < 0 || y < 0) {
			return;
		}
		tiles[x + y*xTiles].lit = true;
		tiles[x + y*xTiles].sprite.tint = tint;
		tiles[x + y*xTiles].DrawTile();
	}
	
	double moveInterval = 0.1f;
	double lastMove = 0;
	double fallInterval = 0.75f;
	double lastFall = 0;
	private void GetInput() {
		float div = Input.OnKeyHeld(Keycode.DownArrow) ? 10 : 1;
		if (time > lastFall) {
			lastFall = time+fallInterval/div;
			
			if (!checkCollision(selectedTetromino, tx, ty+1, theta)) {
				ty += 1;
			}
			else {
				changeTetromino(calculateTetrominoPos(selectedTetromino, tx, ty, theta));
				ty = 0;
				tx = 5;
				return;
			}
			clearRow();
		}
		
		if (time > lastMove) {
			lastMove = time + moveInterval;
			if (Input.OnKeyHeld(Keycode.LeftArrow)) {
				tx -= !checkCollision(selectedTetromino, tx-1, ty, theta) ? 1 : 0;
			}
			if (Input.OnKeyHeld(Keycode.RightArrow)) {
				tx += !checkCollision(selectedTetromino, tx+1, ty, theta) ? 1 : 0;
			}
		}
		if (Input.OnKeyPressed(Keycode.UpArrow) && !Input.OnKeyHeld(Keycode.LeftArrow) && !Input.OnKeyHeld(Keycode.RightArrow)) {
			if (!checkCollision(selectedTetromino, tx, ty, theta+90) && theta <= 270) {
				theta = theta >= 270 ? 0 : theta+90;
			}
		}
	}
	private void gameOver(boolean end) {
		gameIsOver = end;
		fallenBlocks.clear();
		if (end) Debug.println("GAME OVER! SCORE: " + score);
	}
}

public class Main {
	public static void main(String[] args) {
		Tetris g = new Tetris();
		g.Construct(16*11, 16*16, 2);
	}
}