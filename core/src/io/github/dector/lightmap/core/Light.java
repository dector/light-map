package io.github.dector.lightmap.core;

/**
 * @author dector
 */
public class Light {

	public static final int RADIUS_OFF = -1;

	public final int radius;

	public Light(int radius) {
		this.radius = (radius >= RADIUS_OFF) ? radius : RADIUS_OFF;
	}

	public final boolean isOn() {
		return radius > RADIUS_OFF;
	}
}
