package com.fumbbl.ffb.server;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.sketch.Sketch;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSketchManager {

	private final Map<Session, List<Sketch>> sketchesBySession = new HashMap<>();

	public synchronized List<Sketch> getSketches(Session session) {
		return sketchesBySession.computeIfAbsent(session, s -> new ArrayList<>());
	}

	public synchronized void addPathCoordinate(Session session, String id, FieldCoordinate coordinate) {
		getSketches(session).stream().filter(sketch -> sketch.getId().equals(id)).findFirst()
			.ifPresent(sketch -> sketch.addCoordinate(coordinate));
	}

	public synchronized void setLabel(Session session, String id, String label) {
		getSketches(session).stream().filter(sketch -> sketch.getId().equals(id)).findFirst()
			.ifPresent(sketch -> sketch.setLabel(label));
	}

	public synchronized void setRgb(Session session, String id, int rgb) {
		getSketches(session).stream().filter(sketch -> sketch.getId().equals(id)).findFirst()
			.ifPresent(sketch -> sketch.setRgb(rgb));
	}

	public synchronized void addSketch(Session session, Sketch sketch) {
		getSketches(session).add(sketch);
	}

	public synchronized void removeSketches(Session session, List<String> ids) {
		List<Sketch> sketches = getSketches(session);
		sketches.removeIf(sketch -> ids.contains(sketch.getId()));
	}

	public synchronized void remove(Session session) {
		sketchesBySession.remove(session);
	}


}
