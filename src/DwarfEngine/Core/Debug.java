package DwarfEngine.Core;

public class Debug {
	public static void log(Object o) {
		if (o == null) {
			System.out.println("Null");
			return;
		}
		System.out.println(o.toString());
	}
}