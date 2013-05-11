package io.github.dector.lightmap.core;

import java.util.*;

/**
 * Hear-and-soul of this library. Usage is simple.
 * Just create new map, add static and dynamic lights.
 *
 * Sample code:
 *
 * <code>
 *	int w = 25;
 *	int h = 25;
 *	// 1. Create new map
 *	map = new LightMap(w, h);
 *	// 2. Add static lights
 *	map.addStaticLight(new Light(3), 5, 5);		// Simple circle light with outer radius only
 *	map.addStaticLight(new Light(4), 9, 5);
 *	map.addStaticLight(new Light(6), 16, 8);
 *	map.addStaticLight(new Light(5), 6, 15);
 *
 *	map.addStaticLight(new Light(2, 5), 20, 18);		// Simple circle light with outer and inner radiuses
 *	map.addStaticLight(Light.lightCircle(3), 5, 20);	// Simple circle light, which is fully light on each in-radius tile
 *	map.addStaticLight(Light.lightSquare(3), 13, 20);	// Simple square light, which is fully light on each in-radius tile
 *
 *	// 3. Add dynamic lights
 *	dynamicLightId = map.addDynamicLight(new Light(3), new Position(playerPos));	// Simple dynamic light
 *
 * 	// 4. Update map (each frame)
 * 	map.step();		// Update dynamic lights
 * </code>
 *
 * You can change static lights in runtime, but it's not recommended yet
 *
 * @author dector
 */
public class LightMap {

	private static final int GRID_WIDTH = 10;
	private static final int GRID_HEIGHT = 10;

	public static final boolean MEASURE_UPDATE = true;

	private Map<Position, Light> staticLights;
	private Map<Integer, Pair<Position, Light>> dynamicLightsById;
	private Map<Position, List<Integer>> dynamicLightsByGridPosition;
	private int lastDynamicId = 0;

	private float[][] staticLightsValues;
	private float[][] dynamicLightsValues;
	private float[][] lightValues;

	private int width;
	private int height;

	private boolean staticDirty;
	private boolean dynamicDirty;
//	private int[] dynamicDirtyRects;

	private int dynamicGridWidth;
	private int dynamicGridHeight;
	private int dynamicGridXCount;
	private int dynamicGridYCount;
	private boolean[][] dynamicDirtyGrid;
//	private int dynamicLastGridX;
//	private int dynamicLastGridY;
//	private int dynamicLastGridW;
//	private int dynamicLastGridH;

	public LightMap(int width, int height) {
		this.width = width;
		this.height = height;

		dynamicGridWidth = Math.min(width, GRID_WIDTH);
		dynamicGridHeight = Math.min(height, GRID_HEIGHT);
		dynamicGridXCount = width / dynamicGridWidth;
		dynamicGridYCount = height / dynamicGridHeight;
		dynamicDirtyGrid = new boolean[dynamicGridXCount][dynamicGridYCount];

//		dynamicLastGridX = width / dynamicGridWidth;
//		dynamicLastGridY = height / dynamicGridHeight;
//		dynamicLastGridW = width - dynamicLastGridX * dynamicGridWidth;
//		dynamicLastGridH = height - dynamicLastGridY * dynamicGridHeight;

		lightValues = new float[width][height];
		staticLightsValues = new float[width][height];
		dynamicLightsValues = new float[width][height];

		staticLights = new HashMap<Position, Light>();
		dynamicLightsById = new HashMap<Integer, Pair<Position, Light>>();
		dynamicLightsByGridPosition = new HashMap<Position, List<Integer>>();
		for (int i = 0; i < dynamicGridXCount; i++) {
			for (int j = 0; j < dynamicGridYCount; j++) {
				dynamicLightsByGridPosition.put(Position.from(i, j), new ArrayList<Integer>());
			}
		}
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

		markStaticDirty();
	}

	public int addDynamicLight(Light light, Position pos) {
		Pair<Position, Light> pair = new Pair<Position, Light>(pos, light);

		dynamicLightsById.put(lastDynamicId, pair);

		int[] usedRects = getUsedRectsFor(light, pos);

		for (int i = 0; i < usedRects.length; i += 2) {
			Position gridPos = Position.tmp.setAndReturn(usedRects[i], usedRects[i+1]);
			System.out.println("adding dynamic light to: " + gridPos + " (" + pos + ")");

			List<Integer> idsMap = dynamicLightsByGridPosition.get(gridPos);
			idsMap.add(lastDynamicId);
		}

		markDynamicDirty(usedRects);

//		markDynamicDirty(lastDynamicId);

		return lastDynamicId++;
	}

	private int[] getUsedRectsFor(Light light, Position pos) {
		int r = light.outerRadius;
		int fromX = Math.max(0, pos.x - r);
		int fromY = Math.max(0, pos.y - r);
		int toX = Math.min(width - 1, pos.x + r);
		int toY = Math.min(height - 1, pos.y + r);

		int lX = fromX / dynamicGridWidth;
		int rX = toX / dynamicGridWidth;
		int lY = fromY / dynamicGridHeight;
		int rY = toY / dynamicGridHeight;

		return getDifferent(lX, rX, lY, rY);
	}

	private int[] getDifferent(int x1, int x2, int y1, int y2) {
		int length = 0;

		if (x1 == x2 && y1 == y2) {
			length = 1;
		} else {
			length += x2 - x1 + 1;
			length += y2 - y1 + 1;
		}

		int[] result = new int[2 * length];

		if (x1 == x2 && y1 == y2) {
			result[0] = x1;
			result[1] = y1;
		} else if (x1 != x2 && y1 == y2) {
			result[0] = x1;
			result[1] = y1;
			result[2] = x1;
			result[3] = y2;
		} else if (x1 == x2 && y1 != y2) {
			result[0] = x1;
			result[1] = y1;
			result[2] = x2;
			result[3] = y1;
		} else {
			result[0] = x1;
			result[1] = y1;
			result[2] = x2;
			result[3] = y1;
			result[4] = x1;
			result[5] = y2;
			result[6] = x2;
			result[7] = y2;
		}

		return result;
	}

	public void removeStaticLightAt(Position p) {
		staticLights.remove(p);

		markStaticDirty();
	}

	public Position[] getStaticLightsPositions() {
		Position[] pos = new Position[staticLights.size()];
		staticLights.keySet().toArray(pos);
		return pos;
	}

	public void changeStaticLightAt(Position p, int innerRadius, int outerRadius) {
		removeStaticLightAt(p);
		addStaticLight(new Light(innerRadius, outerRadius), p);
	}

	public boolean hasStaticLightAt(int x, int y) {
		return hasStaticLightAt(new Position(x, y));
	}

	public boolean hasStaticLightAt(Position pos) {
		boolean found = false;

		for (Position p : getStaticLightsPositions()) {
			if (p.equals(pos)) {
				found = true;
			}
		}

		return found;
	}

	public Light getStaticLightAt(Position p) {
		if (staticLights.containsKey(p)) {
			return staticLights.get(p);
		} else {
			return null;
		}
	}

	public int getStaticLightsCount() {
		return staticLights.size();
	}

	public void setDynamicLightTo(int id, int x, int y) {
		if (! dynamicLightsById.containsKey(id)) return;

		Pair<Position, Light> pair = dynamicLightsById.get(id);

		markDynamicDirty(getUsedRectsFor(pair.second, pair.first));

		pair.first.x = x;
		pair.first.y = y;

		markDynamicDirty(getUsedRectsFor(pair.second, pair.first));
	}

	private long measureStartTime;
	private double hardMeasureTimeSum;
	private int hardMeasureCount;

	public void step() {
		if (MEASURE_UPDATE)
			measureStartTime = System.currentTimeMillis();

		boolean dirty = false;

		if (staticDirty) {
			recountStaticLights();

			dirty = true;
		}

		if (dynamicDirty) {
			recountDynamicLights();

			dirty = true;
		}

		if (dirty) {
//			clearArray(lightValues);
//			applyStaticLights();
			applyDynamicLights();
//			setMaxOneInArray(lightValues);
		}

		if (MEASURE_UPDATE) {
			float measureTime = (float) (System.currentTimeMillis() - measureStartTime) / 1000;

			if (measureTime >= 0.001f) {
				hardMeasureTimeSum += measureTime;
				hardMeasureCount++;

				System.out.printf("%d. Update time: %.3f s\n", hardMeasureCount, measureTime);
			}
		}
	}

	private void markStaticDirty() {
		staticDirty = true;
	}

	private void markDynamicDirty(int[] usedRects) {
		dynamicDirty = true;

		for (int i = 0; i < usedRects.length; i += 2) {
			dynamicDirtyGrid[usedRects[i]][usedRects[i+1]] = true;
			System.out.println("marking dirty: " + usedRects[i] + ":" + usedRects[i+1]);
		}
	}

	private void recountStaticLights() {
		clearArray(staticLightsValues);

		for (Position p : staticLights.keySet()) {
			Light l = staticLights.get(p);

			recountLight(staticLightsValues, p, l);
		}

		staticDirty = false;
	}

	private void recountDynamicLights() {
		clearArray(dynamicLightsValues);

		for (int i = 0; i < dynamicGridXCount; i++) {
			for (int j = 0; j < dynamicGridYCount; j++) {
				if (dynamicDirtyGrid[i][j]) {
					List<Integer> list = dynamicLightsByGridPosition.get(Position.tmp.setAndReturn(i, j));

					for (int id : list) {
						Pair<Position, Light> p = dynamicLightsById.get(id);
						recountLight(dynamicLightsValues, p.first, p.second);
					}
				}
			}
		}

		dynamicDirty = false;
	}

	private void recountLight(float[][] lightValues, Position p, Light l) {
		int x = p.x;
		int y = p.y;

		int inR = l.innerRadius;
		int outR = l.outerRadius;
		int fromX 	= Math.max(x - outR, 0);
		int toX 	= Math.min(x + outR, width - 1);
		int fromY 	= Math.max(y - outR, 0);
		int toY 	= Math.min(y + outR, height - 1);

		for (int i = fromX; i <= toX; i++) {
			for (int j = fromY; j <= toY; j++) {
				int dx = x - i;
				int dy = y - j;

				float dd = (float) Math.sqrt(dx * dx + dy * dy);

				if (dd <= inR) {
					lightValues[i][j] = 1;
				} else if (dd <= outR) {
					float lightVal = (float) Math.pow(1 - dd / outR, 1.4f);
					lightValues[i][j] += lightVal;
				}
			}
		}
	}

	private void applyStaticLights() {
		for (int x = 0; x < width; x++) {
			System.arraycopy(staticLightsValues[x], 0, lightValues[x], 0, height);
		}

		/*for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightValues[x][y] += staticLightsValues[x][y];
			}
		}*/
	}

	private void applyDynamicLights() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightValues[x][y] = staticLightsValues[x][y] + dynamicLightsValues[x][y];

				if (lightValues[x][y] > 1)
					lightValues[x][y] = 1;
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

	public float getAvgHardUpdateTime() {
		if (MEASURE_UPDATE) {
			return (float) (hardMeasureTimeSum / hardMeasureCount);
		} else {
			return 0;
		}
	}
}
