package com.fumbbl.ffb.model.sketch;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientSketchManager extends SketchManager {

	private Sketch activeSketch;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private final List<Sketch> sketches;

	public ClientSketchManager(String coach) {
		sketches = super.getSketches(coach);
	}

	public Optional<Sketch> activeSketch() {
		return Optional.ofNullable(activeSketch);
	}

	public void clear() {
		sketches.clear();
		activeSketch = null;
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

	public boolean hasSketches() {
		return !sketches.isEmpty();
	}

	public Set<Sketch> getSketches(FieldCoordinate coordinate) {
		return sketches.stream()
				.filter(sketch -> sketch.getPath().contains(coordinate)).collect(Collectors.toSet());
	}

	public void setActive(Sketch sketch) {
		activeSketch = sketch;
	}

	public void remove(Sketch sketch) {
		sketches.remove(sketch);
		if (activeSketch == sketch) {
			activeSketch = null;
		}
	}
}
