package io.github.dector.lightmap.visualiser;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * @author dector
 */
public class DesktopLauncher {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Lightmap visualiser";
		config.width = 800;
		config.height = 600;
		config.resizable = false;
		config.useGL20 = false;

		new LwjglApplication(new BasicVisualiser(), config);
	}
}
