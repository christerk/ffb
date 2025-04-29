package com.fumbbl.ffb.model.sketch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SketchManager {
	private Map<String, List<Sketch>> sketchesByCoach = new HashMap<>();

	public List<Sketch> getSketches(String coach) {
		return sketchesByCoach.computeIfAbsent(coach, s -> new ArrayList<>());
	}


}
