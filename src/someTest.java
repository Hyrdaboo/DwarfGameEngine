import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Pipeline;
import Renderer3D.TriangleRenderer.ColorBuffer;
import Renderer3D.TriangleRenderer.TriangleRasterizer;

import static DwarfEngine.Core.DisplayRenderer.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Taskbar.State;
import java.util.Arrays;
import java.util.Comparator;

class app extends Application {

	TriangleRasterizer tr;
	ColorBuffer colorBuffer;
	@Override
	public void OnStart() {
		title = "Tests";
		
		tr = new TriangleRasterizer();
		colorBuffer = new ColorBuffer(GetPixels(), (int)getFrameSize().x, (int)getFrameSize().y);
		tr.bindBuffer(colorBuffer);	
	}
	
	void printBuffer() {
		int[] buffer = GetPixels();
		for (int y = 0; y < getFrameSize().y; y++) {
			for (int x = 0; x < getFrameSize().x; x++) {
				printCol(buffer[x + y*(int)getFrameSize().x], Color.gray);
			}
			log("\n");
		}
	}
	void printCol(int rgb, Color bc) {
		if (rgb == bc.getRGB()) return;
		Color c = new Color(rgb);
		System.out.print("(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
	}

	void transformVerts(Vector3[] verts, Matrix3x3 mat) {
		for (int i = 0; i < 3; i++) {
			Vector2 p2 = new Vector2(verts[i].x, verts[i].y);
			p2 = Matrix3x3.matrixMultiplyVector(mat, p2);
			verts[i].x = p2.x;
			verts[i].y = p2.y;
		}
	}
	
	
	Vector2 p = Vector2.zero();
	float angle = 88;
	float s = 500;
	Vector2 offset = Vector2.zero();
	public void OnUpdate() {
		clear(Color.gray);
		Vector3[] t1 = new Vector3[] {
				new Vector3(0, 0, 0),
				new Vector3(0, 1, 0),
				new Vector3(1, 1, 0)
		};
		Vector3[] c1 = new Vector3[] {
				new Vector3(0, 0, 0),
				new Vector3(0, 1, 0),
				new Vector3(1, 1, 0)
		};
		Vector3[] t2 = new Vector3[] {
				new Vector3(1, 1, 0),
				new Vector3(1, 0, 0),
				new Vector3(0, 0, 0)
		};
		Vector3[] c2 = new Vector3[] {
				new Vector3(1, 1, 0),
				new Vector3(1, 0, 0),
				new Vector3(0, 0, 0)
		};
		
		Matrix3x3 matFinal = Matrix3x3.identityMatrix();
		Matrix3x3 scale = Matrix3x3.scaleMatrix(new Vector2(s, s));
		Matrix3x3 rotation = Matrix3x3.rotationMatrix(angle);
		Matrix3x3 translation = Matrix3x3.translationMatrix(new Vector2(getFrameSize().x/2, getFrameSize().y/2));
		
		if (Input.OnKeyHeld(Keycode.A)) angle += getDeltaTime()*40;
		if (Input.OnKeyHeld(Keycode.D)) angle -= getDeltaTime()*40;
		s += -Input.getMouseWheel()*1000*getDeltaTime();
		
		if (Input.OnKeyHeld(Keycode.Insert)) Debug.log("pressing");
		
		if (Input.MouseButtonHeld(1)) {
			offset.addTo(Vector2.mulVecFloat(Input.GetMouseDelta(), 200));
		}
		if (Input.MouseButtonClicked(1)) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
			Input.setMouseConfined(true);
		}
		if (Input.MouseButtonReleased(1)) {
			setCursor(null);
			Input.setMouseConfined(false);
		}
		
		matFinal = Matrix3x3.matrixMultiplyMatrix(Matrix3x3.translationMatrix(new Vector2(-0.5f, -0.5f)), matFinal);
		matFinal = Matrix3x3.matrixMultiplyMatrix(scale, matFinal);
		matFinal = Matrix3x3.matrixMultiplyMatrix(rotation, matFinal);
		matFinal = Matrix3x3.matrixMultiplyMatrix(translation, matFinal);
		matFinal = Matrix3x3.matrixMultiplyMatrix(Matrix3x3.translationMatrix(offset), matFinal);
		
		transformVerts(t1, matFinal);
		transformVerts(t2, matFinal);
		
		tr.DrawTriangle(t1, 0xff0000);
		tr.DrawTriangle(t2, 0xff00ff);
		
		/*
		Vector3[] verts = new Vector3[] {
				new Vector3(0, -0.4f, 0),
				new Vector3(0, 1, 0),
				new Vector3(3, 1, 0),
		};
		Vector3[] colors = new Vector3[] {
				new Vector3(1, 0, 0),
				new Vector3(0, 1, 0),
				new Vector3(0, 0, 1),
		};
		tr.DrawTriangle(verts, colors, 0);
		*/
		
		if (Input.OnKeyPressed(Keycode.P)) {
			printBuffer();
		}
	}
}

public class someTest {
	
	public static void main(String[] args) {
		app a = new app();
		//a.Initialize(1280, 720, 1);		
		a.Initialize(144, 81, 7);
		//a.Initialize(10, 10, 40);
	}
	
}
