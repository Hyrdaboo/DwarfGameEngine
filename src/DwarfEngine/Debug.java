package DwarfEngine;

import DwarfEngine.MathTypes.Matrix4x4;
import DwarfEngine.MathTypes.Vector2;
import DwarfEngine.MathTypes.Vector3;

public class Debug {
	public static void Log(Vector2 v) {
		if (v == null) {
			println(null);
			return;
		}
		println(v.x + ", " + v.y);
	}
	public static void Log(Vector3 v) {
		if (v == null) {
			println(null);
			return;
		}
		println(v.x + ", " + v.y + ", " + v.z);
	}
	public static void Log(Matrix4x4 m) {
		if (m == null) {
			println(null);
			return;
		}
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				System.out.print(m.getElement(x, y) + ", ");
			}
			System.out.println("\n");
		}
	}
	public static void println(Object o) {
		System.out.println(o);
	}
	public static void print(Object o) {
		System.out.print(o);
	}
}
