package io.github.dector.lightmap.core;

/**
 * @author dector
 */
public class Light {

	public final int radius;

	public Light(int radius) {
		this.radius = (radius >= -1) ? radius : -1;
	}

	public final boolean isOn() {
		return radius >= 0;
	}
}
