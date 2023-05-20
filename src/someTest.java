import DwarfEngine.Sprite;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Debug;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Matrix3x3;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Shader;
import Renderer3D.TriangleRasterizer;
import Renderer3D.Vertex;

import static DwarfEngine.Core.DisplayRenderer.*;

import java.awt.Color;
import java.awt.Cursor;

@SuppressWarnings("serial")
class app extends Application {

	TriangleRasterizer tr;
	@Override
	public void OnStart() {
		title = "Tests";
		
		tr = new TriangleRasterizer();
		
		spr = new Sprite();
		spr.LoadFromFile("./res/Textures/uvtest.png");
	}
	static Sprite spr;
	
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
	float angle = 0;
	float s = 500;
	Vector2 offset = Vector2.zero();
	public void OnUpdate() {
		clear(Color.gray);
		Vector3[] t1 = new Vector3[] {
				new Vector3(0, 0, 0),
				new Vector3(0, 1, 0),
				new Vector3(1, 1, 0),
		};
		Vector2[] c1 = new Vector2[] {
				new Vector2(0, 1),
				new Vector2(0, 0),
				new Vector2(1, 0),
		};
		Vector3[] t2 = new Vector3[] {
				new Vector3(1, 1, 0),
				new Vector3(1, 0, 0),
				new Vector3(0, 0, 0)
		};
		Color[] c2 = new Color[] {
				new Color(1.0f, 1.0f, 0.0f),
				new Color(1.0f, 0.0f, 0.0f),
				new Color(0.0f, 0.0f, 0.0f)
		};
		
		Matrix3x3 matFinal = Matrix3x3.identityMatrix();
		Matrix3x3 scale = Matrix3x3.scaleMatrix(new Vector2(s, s));
		Matrix3x3 rotation = Matrix3x3.rotationMatrix(angle);
		Matrix3x3 translation = Matrix3x3.translationMatrix(new Vector2(getFrameSize().x/2, getFrameSize().y/2));
		
		if (Input.OnKeyHeld(Keycode.A)) angle += getDeltaTime()*40;
		if (Input.OnKeyHeld(Keycode.D)) angle -= getDeltaTime()*40;
		s += -Input.getMouseWheel()*1000*getDeltaTime();
		
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
		
		Vertex[] verts1 = new Vertex[3];
		Vertex[] verts2 = new Vertex[3];
		
		for (int i = 0; i < 3; i++) {
			verts1[i] = new Vertex();
			verts1[i].position = t1[i];
			verts1[i].texcoord = c1[i];
			
			verts2[i] = new Vertex();
			verts2[i].position = t2[i];
			verts2[i].color = c2[i];
		}
		
		tr.DrawTriangle(verts1, tex);
		//tr.DrawTriangle(verts2, f);
	}
	frag f = new frag();
	Tex tex = new Tex();
}

public class someTest {
	
	public static void main(String[] args) {
		app a = new app();
		//a.Initialize(1280, 720, 1);		
		a.Initialize(720, 405, 1);		
		//a.Initialize(144, 81, 7);
		//a.Initialize(10, 10, 40);
	}
	
}
