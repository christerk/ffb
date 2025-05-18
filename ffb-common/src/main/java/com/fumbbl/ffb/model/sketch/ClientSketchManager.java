package com.fumbbl.ffb.model.sketch;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.List;
import java.util.Optional;

public class ClientSketchManager extends SketchManager {
	private final String coach;

	private Sketch activeSketch;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private final List<Sketch> sketches;

	public ClientSketchManager(String coach) {
		this.coach = coach;
		sketches = super.getSketches(coach);
	}

	public Optional<Sketch> activeSketch() {
		return Optional.ofNullable(activeSketch);
	}

	public void create(FieldCoordinate coordinate, int rgb) {
		activeSketch = new Sketch(rgb);
		activeSketch.addCoordinate(coordinate);
		sketches.add(activeSketch);
	}

	public void add(FieldCoordinate coordinate) {
		if (activeSketch != null) {
			activeSketch.addCoordinate(coordinate);
		}
	}

	public void finishSketch(FieldCoordinate coordinate) {
		if (activeSketch != null) {
			activeSketch.addCoordinate(coordinate);
		}
		activeSketch = null;
	}
}
