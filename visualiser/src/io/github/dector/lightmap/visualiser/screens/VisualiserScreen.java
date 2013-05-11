package io.github.dector.lightmap.visualiser.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.dector.lightmap.core.Light;
import io.github.dector.lightmap.core.LightMap;
import io.github.dector.lightmap.core.Position;
import io.github.dector.lightmap.visualiser.assets.AssetsLoader;

import java.util.Random;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author dector
 */
public class VisualiserScreen extends AbstractScreen {

	private static final int TILE_W = 32;
	private static final int TILE_H = 32;

	private static final float MIN_ZOOM = 0.5f;
	private static final float MAX_ZOOM = Math.min(TILE_W, TILE_H);

	public static final Position FIRST_TILE_POS = new Position();
	public static final Position LAST_TILE_POS = new Position();

	private LightMap map;

	private SpriteBatch batch;
	private SpriteBatch hudBatch;
	private OrthographicCamera cam;
	private TextureRegion lightSourceOnTex;
	private TextureRegion lightSourceOffTex;
	private TextureRegion tileTex;
	private TextureRegion playerTex;
	private TextureRegion darkTex;

	private BitmapFont font;

	private boolean affectLights;

	private int dynamicLightId;
	private int otherDynamicLightsCount;
	private Position playerPos;

	public VisualiserScreen() {
		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		cam = new OrthographicCamera();

		lightSourceOnTex = AssetsLoader.loadImageFileAsRegion("lightSource_on.png", 32, 32);
		lightSourceOffTex = AssetsLoader.loadImageFileAsRegion("lightSource_off.png", 32, 32);
		tileTex = AssetsLoader.loadImageFileAsRegion("tile.png", 32, 32);
		playerTex = AssetsLoader.loadImageFileAsRegion("player.png", 32, 32);
		darkTex = AssetsLoader.loadImageFileAsRegion("dark.png", 32, 32);

		font = AssetsLoader.loadFont("visitor.ttf", 18);

		affectLights = true;

		playerPos = new Position();

		if (LightMap.MEASURE_UPDATE) {
			// Update time test
			int w = 50;
			int h = 50;

			map = new LightMap(w, h);

			Random rnd = new Random();

			for (int i = 0; i < 10; i++) {
				map.addStaticLight(new Light(rnd.nextInt(20)), rnd.nextInt(w), rnd.nextInt(h));
			}

			playerPos.set(10, 10);
			dynamicLightId = map.addDynamicLight(new Light(3), new Position(playerPos));

			/*otherDynamicLightsCount = 100;
			for (int i = 0; i < otherDynamicLightsCount; i++) {
				map.addDynamicLight(new Light(rnd.nextInt(50)), new Position(rnd.nextInt(w), rnd.nextInt(h)));
			}*/
		} else {
			// TODO mockup
			int w = 25;
			int h = 25;

			map = new LightMap(w, h);
			map.addStaticLight(new Light(3), 5, 5);
			map.addStaticLight(new Light(4), 9, 5);
			map.addStaticLight(new Light(6), 16, 8);
			map.addStaticLight(new Light(5), 6, 15);

			map.addStaticLight(new Light(2, 5), 20, 18);
			map.addStaticLight(Light.lightCircle(3), 5, 20);
			map.addStaticLight(Light.lightSquare(3), 13, 20);

			playerPos.set(10, 10);
			dynamicLightId = map.addDynamicLight(new Light(3), new Position(playerPos));
		}

		FIRST_TILE_POS.set(0, 0);
		LAST_TILE_POS.set(map.getWidth() - 1, map.getHeight() - 1);

		centerMap();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		cam.viewportWidth = width;
		cam.viewportHeight = height;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		map.step();

		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.setColor(1, 1, 1, 1);

		Position fromTile = getTilePositionAtOrFirst(0, 0);
		Position toTile = getTilePositionAtOrLast(getWidth() + TILE_W, getHeight() + TILE_H);

		// Draw tiles
		for (int i = fromTile.x; i < toTile.x; i++) {
			for (int j = fromTile.y; j < toTile.y; j++) {
				draw(tileTex, i, j);
			}
		}

		// Draw lights
		for (Position p : map.getStaticLightsPositions()) {
			if (isTileOutOfRange(p, fromTile, toTile)) continue;

			if (map.getStaticLightAt(p).isOn()) {
				draw(lightSourceOnTex, p.x, p.y);
			} else {
				draw(lightSourceOffTex, p.x, p.y);
			}
		}

		// Draw player
		if (! isTileOutOfRange(playerPos, fromTile, toTile)) {
			draw(playerTex, playerPos.x, playerPos.y);
		}

		// Draw darkness
		if (affectLights) {
			for (int i = fromTile.x; i < toTile.x; i++) {
				for (int j = fromTile.y; j < toTile.y; j++) {
					batch.setColor(1, 1, 1, 1 - map.getLightValueAt(i, j));
					draw(darkTex, i, j);
				}
			}
		}

		batch.end();

		hudBatch.begin();
		font.drawMultiLine(hudBatch, getInfoString(), 10, getHeight() - 10);
		hudBatch.end();
	}

	private void draw(TextureRegion reg, int x, int y) {
		batch.draw(reg, x * TILE_W, y * TILE_H, TILE_W, TILE_H);
	}

	private void centerMap() {
		cam.position.set(map.getWidth() * TILE_W / 2, map.getHeight() * TILE_H / 2, 0);
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
		sbuilder.append("Scroll on light to change radius\n");
		sbuilder.append("Scroll elsewhere to zoom\n");
		sbuilder.append("[F2] to toggle darkness\n");
		sbuilder.append("[F3] to center map\n");
		sbuilder.append("[Arrows] to move player\n");
		sbuilder.append("[R] to put player in random position\n");
		sbuilder.append("[T] to put dynamic lights in random positions\n");

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
			case Keys.F3:
				centerMap();
				break;
			case Keys.RIGHT:
				playerPos.x += 1;
				movePlayer();
				break;
			case Keys.LEFT:
				playerPos.x -= 1;
				movePlayer();
				break;
			case Keys.UP:
				playerPos.y += 1;
				movePlayer();
				break;
			case Keys.DOWN:
				playerPos.y -= 1;
				movePlayer();
				break;
			case Keys.R:
				Random rnd = new Random();

				playerPos.set(rnd.nextInt(map.getWidth()),
						rnd.nextInt(map.getHeight()));

				movePlayer();
				break;
			case Keys.T:
				rnd = new Random();

				for (int i = 0; i < otherDynamicLightsCount; i++) {
					map.setDynamicLightTo(i + 1, rnd.nextInt(map.getWidth()), rnd.nextInt(map.getHeight()));
				}
				break;
			case Keys.F10:
				if (LightMap.MEASURE_UPDATE) {
					System.out.printf("Avg. `hard` lightmap update time: %.5f s\n", map.getAvgHardUpdateTime());
				}
				break;
		}

		return true;
	}

	private void movePlayer() {
		if (playerPos.x < 0)
			playerPos.x = 0;
		else if (playerPos.x >= map.getWidth())
			playerPos.x = map.getWidth() - 1;

		if (playerPos.y < 0)
			playerPos.y = 0;
		else if (playerPos.y >= map.getHeight())
			playerPos.y = map.getHeight() - 1;

		map.setDynamicLightTo(dynamicLightId, playerPos.x, playerPos.y);
	}

	private long lastClickTime;
	private Vector2 dragPointStart = new Vector2();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		long time = System.currentTimeMillis();
		long diffTime = time - lastClickTime;

		tmpVec3.set(screenX, screenY, 0);
		cam.unproject(tmpVec3);

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

	private final Vector3 tmpVec3 = new Vector3();

	private boolean isTileOutOfRange(Position tilePos, Position from, Position to) {
		return tilePos.x < from.x || tilePos.y < from.y
				|| tilePos.x > to.x || tilePos.y > to.y;
	}

	private Position getTilePositionAtOrFirst(int screenX, int screenY) {
		Position p = getTilePositionAt(screenX, screenY, false);

		if (p != null) {
			return p;
		} else {
			return FIRST_TILE_POS;
		}
	}

	private Position getTilePositionAtOrLast(int screenX, int screenY) {
		Position p = getTilePositionAt(screenX, screenY, false);

		if (p != null) {
			return p;
		} else {
			return LAST_TILE_POS;
		}
	}

	private Position getTilePositionAt(int screenX, int screenY) {
		return getTilePositionAt(screenX, screenY, true);
	}

	private Position getTilePositionAt(int screenX, int screenY, boolean mouseCoords) {
		tmpVec3.x = screenX;
		tmpVec3.y = (mouseCoords) ? screenY : getHeight() - screenY - 1;

		cam.unproject(tmpVec3);

		int tileX = (int) tmpVec3.x / TILE_W;
		int tileY = (int) tmpVec3.y / TILE_H;

		if (0 <= tileX && tileX < map.getWidth()
				&& 0 <= tileY && tileY < map.getHeight()) {
			return new Position(tileX, tileY);
		} else {
			return null;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		cam.position.x -= (screenX - dragPointStart.x) * cam.zoom;
		cam.position.y += (screenY - dragPointStart.y) * cam.zoom;

		dragPointStart.set(screenX, screenY);

		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();

		Position tilePos = getTilePositionAt(mouseX, mouseY);

		boolean changeInner = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
				|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);

		boolean changeOuter = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
				|| Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);

		if (changeInner || changeOuter) {
			if (tilePos != null) {
				Light l = map.getStaticLightAt(tilePos);

				if (l != null) {
					if (changeInner) {
						map.changeStaticLightAt(tilePos, l.innerRadius - amount, l.outerRadius);
					} else if (changeOuter) {
						map.changeStaticLightAt(tilePos, l.innerRadius, l.outerRadius - amount);
					}
				}
			}
		} else {
			if (amount < 0) {
				cam.zoom /= 2;
			} else {
				cam.zoom *= 2;
			}

			if (cam.zoom < MIN_ZOOM)
				cam.zoom = MIN_ZOOM;
			else if (cam.zoom > MAX_ZOOM)
				cam.zoom = MAX_ZOOM;
		}

		return true;
	}
}
