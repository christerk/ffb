package com.fumbbl.ffb.client.overlay.sketch;

import java.util.ArrayList;
import java.util.List;

public class TriangleCoords {
	private final List<Integer> xCoords;
	private final List<Integer> yCoords;

	public TriangleCoords() {
		xCoords = new ArrayList<>();
		yCoords = new ArrayList<>();
	}

	public int[] getxCoords() {
		return xCoords.stream().mapToInt(value -> value).toArray();
	}

	public int[] getyCoords() {
		return yCoords.stream().mapToInt(value -> value).toArray();
	}

	public void addX(int value) {
		xCoords.add(value);
	}

	public void addY(int value) {
		yCoords.add(value);
	}

	public static TriangleCoords calculate(int previousX, int previousY, int finalX, int finalY, int legLength, int legAngle) {
		double angleRad = angle(previousX, previousY, finalX, finalY);

		TriangleCoords triangleCoords = new TriangleCoords();
		triangleCoords.addX(finalX);
		triangleCoords.addY(finalY);

		double angle1 = angleRad - Math.toRadians(legAngle);
		double angle2 = angleRad + Math.toRadians(legAngle);

		triangleCoords.addX((int) (finalX - Math.cos(angle1) * legLength));
		triangleCoords.addY((int) (finalY + Math.sin(angle1) * legLength));

		triangleCoords.addX((int) (finalX - Math.cos(angle2) * legLength));
		triangleCoords.addY((int) (finalY + Math.sin(angle2) * legLength));

		return triangleCoords;
	}

	private static double angle(int xFrom, int yFrom, int xTo, int yTo) {
		int xDiff = xTo - xFrom;
		int yDiff = yFrom - yTo; // y==0 is the top line of the component
		return Math.atan2(yDiff, xDiff);
	}
}
