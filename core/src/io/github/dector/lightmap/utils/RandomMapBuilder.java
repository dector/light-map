package io.github.dector.lightmap.utils;

import io.github.dector.lightmap.core.Light;
import io.github.dector.lightmap.core.LightMap;
import io.github.dector.lightmap.core.Pair;
import io.github.dector.lightmap.core.Position;

import java.util.Random;

/**
 * @author dector
 */
public class RandomMapBuilder {

	private int width = 1;
	private int height = 1;

	private Position playerPos = new Position();

	private int staticLightsCount;
	private int dynamicLightsCount;

	private int staticLightMaxRadius = 20;
	private int dynamicLightMaxRadius = 50;
	private int playerLightMaxRadius = 3;

	private LightMap map;
	private int playerLightId;

	public RandomMapBuilder width(int width) {
		if (width > 0) {
			this.width = width;
		}

		return this;
	}

	public RandomMapBuilder height(int height) {
		if (height > 0) {
			this.height = height;
		}

		return this;
	}

	public RandomMapBuilder player(int x, int y) {
		if (x >= 0 || y >= 0) {
			playerPos.set(x, y);
		}

		return this;
	}

	public RandomMapBuilder staticCount(int count) {
		if (count >= 0) {
			staticLightsCount = count;
		}

		return this;
	}

	public RandomMapBuilder dynamicCount(int count) {
		if (count >= 0) {
			dynamicLightsCount = count;
		}

		return this;
	}

	public RandomMapBuilder staticMaxRadius(int radius) {
		if (radius >= 0) {
			staticLightMaxRadius = radius + 1;
		}

		return this;
	}

	public RandomMapBuilder dynamicMaxRadius(int radius) {
		if (radius >= 0) {
			dynamicLightMaxRadius = radius + 1;
		}

		return this;
	}

	public RandomMapBuilder playerMaxRadius(int radius) {
		if (radius >= 0) {
			playerLightMaxRadius = radius + 1;
		}

		return this;
	}

	public RandomMapBuilder build() {
		map = new LightMap(width, height);

		Random rnd = new Random();

		for (int i = 0; i < staticLightsCount; i++) {
			map.addStaticLight(new Light(rnd.nextInt(staticLightMaxRadius)), rnd.nextInt(width), rnd.nextInt(height));
		}

		playerLightId = map.addDynamicLight(new Light(playerLightMaxRadius), new Position(playerPos));

		for (int i = 0; i < dynamicLightsCount; i++) {
			map.addDynamicLight(new Light(rnd.nextInt(dynamicLightMaxRadius)),
					new Position(rnd.nextInt(width), rnd.nextInt(height)));
		}

		return this;
	}

	public LightMap getMap() {
		return map;
	}

	public int getPlayerLightId() {
		return playerLightId;
	}
}
