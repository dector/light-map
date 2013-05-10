package io.github.dector.lightmap.core;

/**
 * @author dector
 */
public class Light {

	public static final int RADIUS_OFF = -1;

	public final int outerRadius;
	public final int innerRadius;

	public Light(int outerRadius) {
		this(RADIUS_OFF, outerRadius);
	}

	public Light(int innerRadius, int outerRadius) {
		this.innerRadius = (innerRadius >= RADIUS_OFF) ? innerRadius : RADIUS_OFF;
		this.outerRadius = (outerRadius >= RADIUS_OFF) ? outerRadius : RADIUS_OFF;
	}

	public static Light lightSquare(int radius) {
		return new Light((int) (1.4f * radius + 1), radius);
	}

	public static Light lightCircle(int radius) {
		return new Light(radius, radius);
	}

	public final boolean isOn() {
		return outerRadius > RADIUS_OFF
				|| innerRadius > RADIUS_OFF;
	}
}
