package DwarfEngine;

public enum Keycode {
	 Q(81),W(87),E(69),R(82),T(84),Y(89),U(85),I(73),O(79),P(80),
	 A(65),S(83),D(68),F(70),G(71),H(72),J(74),K(75),L(76),
	 Z(90),X(88),C(67),V(86),B(66),N(78),M(77),
	 LeftArrow(37),RightArrow(39),UpArrow(38),DownArrow(40),
	 Space(32),
	 LeftShift(16),
	 LeftCtrl(17),
	 LefrAlt(18),
	 F1(112),
	 F3(114),
	 F11(122),
	 Escape(27);
	 
	private final int num;
	Keycode (int num) {
		this.num = num;
	}
	public int GetKeyCode() {
		return num;
	}
}
