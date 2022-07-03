package DwarfEngine;

public enum Keycode {
	 W(87),
	 S(83),
	 A(65),
	 D(68),
	 R(82),
	 LeftArrow(37),
	 RightArrow(39),
	 UpArrow(38),
	 DownArrow(40),
	 Space(32),
	 LeftShift(16),
	 LeftCtrl(17),
	 F11(122),
	 F3(114),
	 Escape(27);
	 
	private final int num;
	Keycode (int num) {
		this.num = num;
	}
	public int GetKeyCode() {
		return num;
	}
}
