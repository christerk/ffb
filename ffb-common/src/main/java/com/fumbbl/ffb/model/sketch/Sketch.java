package com.fumbbl.ffb.model.sketch;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.LinkedList;

public class Sketch {
	private int rgb;
	private final LinkedList<FieldCoordinate> path;

	public Sketch(int rgb) {
		this.rgb = rgb;
		path = new LinkedList<>();
	}

	public int getRgb() {
		return rgb;
	}

	public LinkedList<FieldCoordinate> getPath() {
		return path;
	}

	public void setRgb(int rgb) {
		this.rgb = rgb;
	}

	public void addCoordinate(FieldCoordinate coordinate) {
		if (path.size() != 1 || path.peekLast() != coordinate ) {
			path.addLast(coordinate);
		}
	}
}
