package Demos;

import DwarfEngine.Core.Application;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import DwarfEngine.MathTypes.Vector3;
import Renderer3D.Transform;

public class FpsControls {
	/**
	 * WASD move<br>
	 * Hold right click and look around with mouse or IJKL to look around
	 *
	 * @param deltaTime use {@link Application#getDeltaTime()}
	 * @param transform the transform to move (Camera transform)
	 */
	public static void GetInput(float deltaTime, Transform transform) {
		float mul = 0.1f;
		if (Input.OnKeyHeld(Keycode.Shift)) {
			mul = 2;
		}
		float speed = 15 * mul;
		float lookSpeed = 100;

		if (Input.OnKeyHeld(Keycode.W)) {
			Vector3 forward = Vector3.mulVecFloat(transform.forward, speed * deltaTime);
			transform.position = Vector3.add2Vecs(transform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.S)) {
			Vector3 forward = Vector3.mulVecFloat(transform.forward, -speed * deltaTime);
			transform.position = Vector3.add2Vecs(transform.position, forward);
		}
		if (Input.OnKeyHeld(Keycode.A)) {
			Vector3 right = Vector3.mulVecFloat(transform.right, -speed * deltaTime);
			transform.position = Vector3.add2Vecs(transform.position, right);
		}
		if (Input.OnKeyHeld(Keycode.D)) {
			Vector3 right = Vector3.mulVecFloat(transform.right, speed * deltaTime);
			transform.position = Vector3.add2Vecs(transform.position, right);
		}

		if (Input.OnKeyHeld(Keycode.Q)) {
			transform.position.y -= deltaTime * speed;
		}
		if (Input.OnKeyHeld(Keycode.E)) {
			transform.position.y += deltaTime * speed;
		}

		if (Input.MouseButtonClicked(3)) {
			Input.setMouseConfined(true);
		}
		if (Input.MouseButtonReleased(3)) {
			Input.setMouseConfined(false);
		}
		if (Input.isMouseConfined()) {
			transform.rotation.y += Input.GetMouseDelta().x * 200;
			transform.rotation.x += Input.GetMouseDelta().y * 200;
		}

		// rotate camera
		if (Input.OnKeyHeld(Keycode.I)) {
			transform.rotation.x -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.J)) {
			transform.rotation.y -= deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.K)) {
			transform.rotation.x += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.L)) {
			transform.rotation.y += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.U)) {
			transform.rotation.z += deltaTime * lookSpeed;
		}
		if (Input.OnKeyHeld(Keycode.O)) {
			transform.rotation.z -= deltaTime * lookSpeed;
		}
	}
}