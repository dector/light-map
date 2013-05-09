package io.github.dector.lightmap.core.test;

import io.github.dector.lightmap.core.Light;
import io.github.dector.lightmap.core.LightMap;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author dector
 */
public class LightMapTest {

	@Test
	public void testBasic() {
		int w = 5;
		int h = 5;

		LightMap map = new LightMap(w, h);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Assert.assertEquals("Light value at " + x + ":" + y,
						0f, map.getLightValueAt(x, y));
			}
		}
	}

	@Test
	public void drawStatic() {
		int w = 8;
		int h = 8;

		LightMap map = new LightMap(w, h);
		map.addStaticLight(new Light(3), 5, 5);
		map.step();
		map.print();
	}
}
