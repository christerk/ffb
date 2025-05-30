package com.fumbbl.ffb.client.overlay.sketch;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.model.sketch.SketchManager;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientSketchManager extends SketchManager {

	private Sketch activeSketch;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private final List<Sketch> sketches;
	private final PitchDimensionProvider pitchDimensionProvider;

	public ClientSketchManager(String coach, PitchDimensionProvider pitchDimensionProvider) {
		sketches = super.getSketches(coach);
		this.pitchDimensionProvider = pitchDimensionProvider;
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

	public Set<Sketch> getSketches(int x, int y) {
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

	public void setActive(Sketch sketch) {
		activeSketch = sketch;
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

}
