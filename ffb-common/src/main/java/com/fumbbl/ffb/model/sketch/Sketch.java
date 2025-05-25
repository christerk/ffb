package com.fumbbl.ffb.model.sketch;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.LinkedList;

public class Sketch {
	private int rgb;
	private String label;
	private final LinkedList<FieldCoordinate> path;

	public Sketch(int rgb) {
		this.rgb = rgb;
		path = new LinkedList<>();
	}

	public LinkedList<FieldCoordinate> getPath() {
		return path;
	}

	public int getRgb() {
		return rgb;
	}

	public void setRgb(int rgb) {
		this.rgb = rgb;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addCoordinate(FieldCoordinate coordinate) {
		if (!coordinate.equals(path.peekLast())) {
			path.addLast(coordinate);
		}
	}
}
