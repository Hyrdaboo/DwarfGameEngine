import Demos.LightsDemo;
import DwarfEngine.Core.Application;
import DwarfEngine.Core.Input;
import DwarfEngine.Core.Keycode;
import Renderer3D.SceneManager;

class SequenceRenderer extends Application {

	@Override
	public void OnStart() {
		SceneManager.AddScene(LightsDemo.class, "lights");
		SceneManager.LoadScene("lights");
	}

	int i = 0;

	@Override
	public void OnUpdate() {
		SceneManager.renderActiveScene();

		saveImage("C:\\Users\\USER\\OneDrive\\Desktop\\Sequence", "frame" + i);
		i++;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Input.OnKeyHeld(Keycode.Backspace)) {
			Exit();
		}
	}

}

public class test {
	public static void main(String[] args) {
		SequenceRenderer renderer = new SequenceRenderer();
		renderer.Initialize(1280, 720, 0);
	}
}
