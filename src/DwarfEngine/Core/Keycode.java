package DwarfEngine.Core;

public enum Keycode {
	 Q(81),W(87),E(69),R(82),T(84),Y(89),U(85),I(73),O(79),P(80),
	 A(65),S(83),D(68),F(70),G(71),H(72),J(74),K(75),L(76),
	 Z(90),X(88),C(67),V(86),B(66),N(78),M(77),
	 Space(32),
	 LeftArrow(37),RightArrow(39),UpArrow(38),DownArrow(40),
	 F1(112),
	 F2(113),
	 F3(114),
	 F4(115),
	 F5(116),
	 F6(117),
	 F7(118),
	 F8(119),
	 F9(120),
	 F10(121),
	 F11(122),
	 F12(123),	 
	 Enter(10),
	 Backspace(8),
	 Delete(127),
	 Insert(155),
	 Escape(27),
	 Backquote(192),
	 Slash(47),
	 AnyKey(-100);
	 
	private final int num;
	Keycode (int num) {
		this.num = num;
	}
	public int GetKeyCode() {
		return num;
	}
}
