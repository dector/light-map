package io.github.dector.lightmap.visualiser.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.dector.lightmap.core.Light;
import io.github.dector.lightmap.core.LightMap;
import io.github.dector.lightmap.core.Position;
import io.github.dector.lightmap.visualiser.assets.AssetsLoader;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author dector
 */
public class VisualiserScreen extends AbstractScreen {

	private LightMap map;

	private SpriteBatch sb;
	private TextureRegion lightSourceTex;
	private TextureRegion tileTex;
	private TextureRegion darkTex;

	private BitmapFont font;

	private boolean affectLights;

	public VisualiserScreen() {
		sb = new SpriteBatch();

		lightSourceTex = AssetsLoader.loadImageFileAsRegion("lightSource.png", 32, 32);
		tileTex = AssetsLoader.loadImageFileAsRegion("tile.png", 32, 32);
		darkTex = AssetsLoader.loadImageFileAsRegion("dark.png", 32, 32);

		font = AssetsLoader.loadFont("visitor.ttf", 18);

		affectLights = true;

		// TODO mockup
		{
			int w = 8;
			int h = 8;

			map = new LightMap(w, h);
			map.addStaticLight(new Light(3), 5, 5);
			map.step();
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		sb.begin();

		sb.setColor(1, 1, 1, 1);

		// Draw tiles
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				sb.draw(tileTex, i * 32, j * 32, 32, 32);
			}
		}

		// Draw lights
		for (Position p : map.getStaticLightsPositions()) {
			sb.draw(lightSourceTex, p.x * 32, p.y * 32, 32, 32);
		}

		// Draw darkness
		if (affectLights) {
			for (int i = 0; i < map.getWidth(); i++) {
				for (int j = 0; j < map.getHeight(); j++) {
					sb.setColor(1, 1, 1, 1 - map.getLightValueAt(i, j));
					sb.draw(darkTex, i * 32, j * 32, 32, 32);
				}
			}
		}

		font.drawMultiLine(sb, getInfoString(), 10, getHeight() - 10);

		sb.end();
	}

	private String getInfoString() {
		StringBuilder sbuilder = new StringBuilder();

		sbuilder.append("Map size: ")
				.append(map.getWidth())
				.append("x")
				.append(map.getHeight())
				.append("\n");

		sbuilder.append("Static lights: ")
				.append(map.getStaticLightsCount())
				.append("\n");

		sbuilder.append("[F2] to toggle darkness\n");

		return sbuilder.toString();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.ESCAPE:
				Gdx.app.exit();
				break;
			case Keys.F2:
				affectLights = !affectLights;
				break;
		}

		return true;
	}
}
