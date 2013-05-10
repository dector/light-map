package io.github.dector.lightmap.visualiser.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.dector.lightmap.core.Light;
import io.github.dector.lightmap.core.LightMap;
import io.github.dector.lightmap.core.Position;
import io.github.dector.lightmap.visualiser.assets.AssetsLoader;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author dector
 */
public class VisualiserScreen extends AbstractScreen {

	private static final int MIN_TILE_W = 4;
	private static final int MIN_TILE_H = 4;
	private static final int MAX_TILE_W = 64;
	private static final int MAX_TILE_H = 64;

	private LightMap map;

	private SpriteBatch sb;
	private TextureRegion lightSourceOnTex;
	private TextureRegion lightSourceOffTex;
	private TextureRegion tileTex;
	private TextureRegion darkTex;

	private BitmapFont font;

	private boolean affectLights;
	private int startX;
	private int startY;
	private int tileW;
	private int tileH;

	public VisualiserScreen() {
		sb = new SpriteBatch();

		lightSourceOnTex = AssetsLoader.loadImageFileAsRegion("lightSource_on.png", 32, 32);
		lightSourceOffTex = AssetsLoader.loadImageFileAsRegion("lightSource_off.png", 32, 32);
		tileTex = AssetsLoader.loadImageFileAsRegion("tile.png", 32, 32);
		darkTex = AssetsLoader.loadImageFileAsRegion("dark.png", 32, 32);

		font = AssetsLoader.loadFont("visitor.ttf", 18);

		affectLights = true;
		tileW = 32;
		tileH = 32;

		// TODO mockup
		{
			int w = 25;
			int h = 25;

			map = new LightMap(w, h);
			map.addStaticLight(new Light(3), 5, 5);
			map.addStaticLight(new Light(4), 9, 5);
			map.addStaticLight(new Light(6), 16, 8);
			map.addStaticLight(new Light(5), 6, 15);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		map.step();

		sb.begin();
		sb.setColor(1, 1, 1, 1);

		// Draw tiles
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				draw(tileTex, i, j);
			}
		}

		// Draw lights
		for (Position p : map.getStaticLightsPositions()) {
			if (map.getStaticLightAt(p).isOn()) {
				draw(lightSourceOnTex, p.x, p.y);
			} else {
				draw(lightSourceOffTex, p.x, p.y);
			}
		}

		// Draw darkness
		if (affectLights) {
			for (int i = 0; i < map.getWidth(); i++) {
				for (int j = 0; j < map.getHeight(); j++) {
					sb.setColor(1, 1, 1, 1 - map.getLightValueAt(i, j));
					draw(darkTex, i, j);
				}
			}
		}

		font.drawMultiLine(sb, getInfoString(), 10, getHeight() - 10);

		sb.end();
	}

	private void draw(TextureRegion reg, int x, int y) {
		sb.draw(reg, startX + x * tileW, startY + y * tileH, tileW, tileH);
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

		sbuilder.append("\n");
		sbuilder.append("Drag map with mouse\n");
		sbuilder.append("Doubleclick to add/remove light\n");
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

	private long lastClickTime;
	private Vector2 dragPointStart = new Vector2();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		long time = System.currentTimeMillis();
		long diffTime = time - lastClickTime;

		if (diffTime < 200) {
			Position tilePos = getTilePositionAt(screenX, screenY);

			if (tilePos != null) {
				if (map.hasStaticLightAt(tilePos)) {
					map.removeStaticLightAt(tilePos);
				} else {
					map.addStaticLight(new Light(3), tilePos);
				}
			}
		}

		lastClickTime = time;
		dragPointStart.set(screenX, screenY);

		return true;
	}

	private Position getTilePositionAt(int screenX, int screenY) {
		int tileX = (screenX - startX) / tileH;
		int tileY = (getHeight() - screenY - 1 - startY) / tileH;

		if (0 <= tileX && tileX < map.getWidth()
				&& 0 <= tileY && tileY < map.getHeight()) {
			return new Position(tileX, tileY);
		} else {
			return null;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		startX += screenX - dragPointStart.x;
		startY -= screenY - dragPointStart.y;

		dragPointStart.set(screenX, screenY);

		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		Position tilePos = getTilePositionAt(Gdx.input.getX(), Gdx.input.getY());

		if (tilePos != null) {
			Light l = map.getStaticLightAt(tilePos);

			if (l != null) {
				map.removeStaticLightAt(tilePos);
				map.addStaticLight(new Light(l.radius - amount), tilePos);
			} else {
				if (amount < 0) {
					tileW *= 2;
					tileH *= 2;
				} else {
					tileW /= 2;
					tileH /= 2;
				}

				if (tileW < MIN_TILE_W)
					tileW = MIN_TILE_W;
				if (tileW > MAX_TILE_W)
					tileW = MAX_TILE_W;

				if (tileH < MIN_TILE_H)
					tileH = MIN_TILE_H;
				if (tileH > MAX_TILE_H)
					tileH = MAX_TILE_H;
			}
		}

		return true;
	}
}
