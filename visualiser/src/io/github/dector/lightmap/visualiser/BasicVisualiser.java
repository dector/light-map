package io.github.dector.lightmap.visualiser;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import io.github.dector.lightmap.visualiser.screens.VisualiserScreen;

/**
 * @author dector
 */
public class BasicVisualiser extends Game {

	@Override
	public void create() {
		VisualiserScreen screen = new VisualiserScreen();

		setScreen(screen);
		Gdx.input.setInputProcessor(screen);
	}
}
