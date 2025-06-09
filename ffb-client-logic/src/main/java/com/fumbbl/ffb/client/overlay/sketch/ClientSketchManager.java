package com.fumbbl.ffb.client.overlay.sketch;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.model.sketch.Sketch;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientSketchManager {

	private Sketch activeSketch;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<Sketch> sketches;
	private final PitchDimensionProvider pitchDimensionProvider;
	private final String coach;
	private final Set<String> coachesPreventedFromSketching = new HashSet<>();
	private final Set<String> hiddenCoaches = new HashSet<>();

	private final Map<String, List<Sketch>> sketchesByCoach = new HashMap<>();

	public ClientSketchManager(String coach, PitchDimensionProvider pitchDimensionProvider) {
		this.coach = coach;
		sketches = getSketches(coach);
		this.pitchDimensionProvider = pitchDimensionProvider;
	}

	public synchronized List<Sketch> getSketches(String coach) {
		if (coachesPreventedFromSketching.contains(coach)) {
			return new ArrayList<>();
		}
		return sketchesByCoach.computeIfAbsent(coach, s -> new ArrayList<>());
	}

	public synchronized List<Sketch> getAllSketches() {
		return sketchesByCoach.entrySet().stream()
			.filter(entry -> !coachesPreventedFromSketching.contains(entry.getKey()))
			.flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
	}

	public synchronized Optional<Sketch> getSketch(String id) {
		return sketches.stream()
			.filter(sketch -> sketch.getId().equals(id))
			.findFirst();
	}

	public synchronized Optional<Sketch> activeSketch() {
		return Optional.ofNullable(activeSketch);
	}

	public synchronized void clearOwn() {
		sketches.clear();
		activeSketch = null;
	}

	public synchronized void clearAll() {
		sketchesByCoach.clear();
		sketches = getSketches(coach);
		activeSketch = null;
	}

	public void create(FieldCoordinate coordinate, int rgb) {
		Sketch sketch = new Sketch(rgb);
		sketch.addCoordinate(coordinate);
		add(sketch);
	}

	public synchronized void add(Sketch sketch) {
		activeSketch = sketch;
		sketches.add(activeSketch);
	}

	public synchronized void add(String coach, Sketch sketch) {
		List<Sketch> coachSketches = getSketches(coach);
		coachSketches.add(sketch);
	}

	public synchronized void add(String coach, String sketchId, FieldCoordinate coordinate) {
		getSketches(coach).stream().filter(sketch -> sketch.getId().equals(sketchId))
			.findFirst()
			.ifPresent(sketch -> sketch.addCoordinate(coordinate));
	}

	public synchronized void add(FieldCoordinate coordinate) {
		if (activeSketch != null) {
			activeSketch.addCoordinate(coordinate);
		}
	}

	public synchronized void setColor(String coach, String sketchId, int rgb) {
		getSketches(coach).stream().filter(sketch -> sketch.getId().equals(sketchId))
			.findFirst()
			.ifPresent(sketch -> sketch.setRgb(rgb));
	}

	public synchronized void setLabel(String coach, String sketchId, String label) {
		getSketches(coach).stream().filter(sketch -> sketch.getId().equals(sketchId))
			.findFirst()
			.ifPresent(sketch -> sketch.setLabel(label));
	}

	public synchronized void finishSketch(FieldCoordinate coordinate) {
		if (activeSketch != null) {
			activeSketch.addCoordinate(coordinate);
		}
		activeSketch = null;
	}

	public synchronized boolean hasAnySketches() {
		return !sketchesByCoach.isEmpty() && sketchesByCoach.values().stream().anyMatch(list -> !list.isEmpty());
	}

	public synchronized boolean hasOwnSketches() {
		return !sketches.isEmpty();
	}

	public synchronized Set<Sketch> getSketches(int x, int y) {
		return sketches.stream()
			.filter(sketch -> intersect(sketch, x, y)).collect(Collectors.toSet());
	}

	private boolean intersect(Sketch sketch, int x, int y) {
		double sketchWidth = pitchDimensionProvider.dimension(Component.SKETCH_STROKE).width;
		Iterator<FieldCoordinate> iterator = sketch.getPath().iterator();
		FieldCoordinate currentNode = iterator.next();

		if (inStartDecoration(pitchDimensionProvider.mapToLocal(currentNode, true), x, y)) {
			return true;
		}

		while (iterator.hasNext()) {
			FieldCoordinate previousNode = currentNode;
			currentNode = iterator.next();
			if (withinSpannedRectangle(
				pitchDimensionProvider.mapToLocal(previousNode, true),
				pitchDimensionProvider.mapToLocal(currentNode, true),
				sketchWidth,
				x, y
			)) {
				return true;
			}
		}

		return inEndDecoration(pitchDimensionProvider.mapToLocal(currentNode, true),
			pitchDimensionProvider.mapToLocal(sketch.getPath().getLast(), true), x, y);
	}

	public synchronized void removeAll(String coach) {
		getSketches(coach).clear();
	}

	public void remove(String coach, String id) {
		List<Sketch> sketches = sketchesByCoach.get(coach);
		if (sketches != null) {
			sketches.stream()
				.filter(sketch -> sketch.getId().equals(id))
				.findFirst()
				.ifPresent(sketches::remove);
		}
	}

	public void remove(String id) {
		getSketch(id).ifPresent(this::remove);
	}

	public void remove(Sketch sketch) {
		sketches.remove(sketch);
		if (activeSketch == sketch) {
			activeSketch = null;
		}
	}

	private boolean inStartDecoration(Dimension start, int x, int y) {
		Dimension circleDimension = pitchDimensionProvider.dimension(Component.SKETCH_CIRCLE);
		Ellipse2D circle = new Ellipse2D.Double(
			start.getWidth() - (double) circleDimension.width / 2,
			start.getHeight() - (double) circleDimension.height / 2,
			circleDimension.width,
			circleDimension.height
		);
		return circle.contains(x, y);
	}

	private boolean inEndDecoration(Dimension previous, Dimension current, int x, int y) {
		int legLength = pitchDimensionProvider.dimension(Component.SKETCH_TRIANGLE).width;
		int legAngle = pitchDimensionProvider.unscaledDimension(Component.SKETCH_TRIANGLE).height;

		TriangleCoords triangle = TriangleCoords.calculate(previous.width, previous.height, current.width, current.height, legLength, legAngle);

		Polygon polygon = new Polygon(triangle.getxCoords(), triangle.getyCoords(), 3);

		return polygon.contains(x, y);
	}

	private boolean withinSpannedRectangle(Dimension start, Dimension end, double rectWidth, int x, int y) {
		double endX = end.getWidth() - start.getWidth();
		double endY = end.getHeight() - start.getHeight();
		double checkX = x - start.getWidth();
		double checkY = y - start.getHeight();
		double checkToEnd = ((endX - checkX) * (endX - checkX)) + ((endY - checkY) * (endY - checkY));
		double startToCheck = (checkX * checkX) + (checkY * checkY);
		double startToEnd = (endX * endX) + (endY * endY);
		double minNumerator = Math.abs((endY * checkX) - (endX * checkY));
		double perpendicular = minNumerator / Math.sqrt(startToEnd);
		return startToEnd >= startToCheck && startToEnd >= checkToEnd && rectWidth > (2 * perpendicular);
	}

	public void preventedFromSketching(String coach) {
		coachesPreventedFromSketching.add(coach);
	}

	public void allowSketching(String coach) {
		coachesPreventedFromSketching.remove(coach);
	}

	public Set<String> preventedCoaches() {
		return new HashSet<>(coachesPreventedFromSketching);
	}

	public boolean isCoachPreventedFromSketching(String coach) {
		return coachesPreventedFromSketching.contains(coach);
	}

	public void hideSketches(String coach) {
		hiddenCoaches.add(coach);
	}

	public void showSketches(String coach) {
		hiddenCoaches.remove(coach);
	}

	public Set<String> hiddenCoaches() {
		return new HashSet<>(hiddenCoaches);
	}

	public boolean areSketchesHidden(String coach) {
		return hiddenCoaches.contains(coach);
	}

	public boolean displaySketches(String coach) {
		return !isCoachPreventedFromSketching(coach) && !areSketchesHidden(coach);
	}
}
