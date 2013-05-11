package io.github.dector.lightmap.core;

/**
 * @author dector
 */
public class Position {

	public int x;
	public int y;

	public Position() {
	}

	public Position(Position pos) {
		this.x = pos.x;
		this.y = pos.y;
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Position from(int x, int y) {
		return new Position(x, y);
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position position = (Position) o;

		if (x != position.x) return false;
		if (y != position.y) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	@Override
	public String toString() {
		return x + ":" + y;
	}
}
