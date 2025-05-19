package com.fumbbl.ffb.model.sketch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SketchManager {
	private final Map<String, List<Sketch>> sketchesByCoach = new HashMap<>();

	public List<Sketch> getSketches(String coach) {
		return sketchesByCoach.computeIfAbsent(coach, s -> new ArrayList<>());
	}

	public List<Sketch> getAllSketches() {
		return sketchesByCoach.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}


}
