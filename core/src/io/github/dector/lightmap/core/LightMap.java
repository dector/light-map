package io.github.dector.lightmap.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dector
 */
public class LightMap {

	private Map<Position, Light> staticLights;

	private float[][] staticLightsValues;
	private float[][] lightValues;

	private int width;
	private int height;

	public LightMap(int width, int height) {
		this.width = width;
		this.height = height;

		lightValues = new float[width][height];
		staticLightsValues = new float[width][height];

		staticLights = new HashMap<Position, Light>();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getLightValueAt(int x, int y) {
		return lightValues[x][y];
	}

	public float[][] getLightValues() {
		return lightValues;
	}

	public void addStaticLight(Light light, int x, int y) {
		addStaticLight(light, new Position(x, y));
	}

	public void addStaticLight(Light light, Position pos) {
		staticLights.put(pos, light);

		recountStaticLights();
	}

	public Position[] getStaticLightsPositions() {
		Position[] pos = new Position[staticLights.size()];
		staticLights.keySet().toArray(pos);
		return pos;
	}

	public int getStaticLightsCount() {
		return staticLights.size();
	}

	private void recountStaticLights() {
		clearArray(staticLightsValues);

		for (Position p : staticLights.keySet()) {
			Light l = staticLights.get(p);

			int x = p.x;
			int y = p.y;

			int r = l.getRadius();
			int fromX 	= Math.max(x - r, 0);
			int toX 	= Math.min(x + r, width - 1);
			int fromY 	= Math.max(y - r, 0);
			int toY 	= Math.min(y + r, height - 1);

			for (int i = fromX; i <= toX; i++) {
				for (int j = fromY; j <= toY; j++) {
					int dx = x - i;
					int dy = y - j;

					float dd = (float) Math.sqrt(dx * dx + dy * dy);

					if (dd <= r) {
						float lightVal = (float) Math.pow(1 - dd / r, 1.4f);
						staticLightsValues[i][j] += lightVal;
					}
				}
			}
		}

		setMaxOneInArray(staticLightsValues);
	}

	public void step() {
		clearArray(lightValues);
		applyStaticLights();
		setMaxOneInArray(lightValues);
	}

	private void applyStaticLights() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightValues[x][y] += staticLightsValues[x][y];
			}
		}
	}

	private void clearArray(float[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				a[i][j] = 0;
			}
		}
	}

	private void setMaxOneInArray(float[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (a[i][j] > 1) {
					a[i][j] = 1;
				}
			}
		}
	}

	public void print() {
		StringBuilder sb = new StringBuilder();

		for (int y = height - 1; y >= 0; y--) {
			sb.append("| ");
			for (int x = 0; x < width; x++) {
				sb.append(String.format("%.2f | ", lightValues[x][y]));
			}

			sb.append("\n");
		}

		System.out.println(sb.toString());
	}
}
