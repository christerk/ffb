package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FieldCoordinate;


public class GeometryService {

	public static GeometryService INSTANCE = new GeometryService();

	/**
	 * Checks if the given coordinate is within a rectangle defined by the start and end coordinates
	 * and the specified width. The rectangle is spanned perpendicular to the line between start and end.
	 *
	 * @param start                The starting coordinate of the rectangle.
	 * @param end                  The ending coordinate of the rectangle.
	 * @param rectWidth            The width of the rectangle, spanned symmetrically to both sides of the
	 *                             line defined by the centers start and end.
	 * @param coordinateToCheck    The coordinate to check against the rectangle.
	 * @return true if any corner the field coordinate is within the rectangle, false otherwise.
	 */
	public boolean withinSpannedRectangle(FieldCoordinate start, FieldCoordinate end,
																				double rectWidth, FieldCoordinate coordinateToCheck) {
		double perpendicular = getPerpendicular(start, end, coordinateToCheck);
		return rectWidth > (2 * perpendicular);
	}

	private double getPerpendicular(FieldCoordinate throwerCoordinate, FieldCoordinate targetCoordinate, FieldCoordinate interceptorCoordinate) {
		int receiverX = targetCoordinate.getX() - throwerCoordinate.getX();
		int receiverY = targetCoordinate.getY() - throwerCoordinate.getY();
		int interceptorX = interceptorCoordinate.getX() - throwerCoordinate.getX();
		int interceptorY = interceptorCoordinate.getY() - throwerCoordinate.getY();
		int c1 = (receiverX * receiverX) + (receiverY * receiverY);
		double minNumerator = getMinNumerator(receiverX, receiverY, interceptorX, interceptorY);
		return minNumerator / Math.sqrt(c1);
	}

	private double getMinNumerator(int receiverX, int receiverY, int interceptorX, int interceptorY) {
		double d1 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY + 0.5)));
		double d2 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY - 0.5)));
		double d3 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY + 0.5)));
		double d4 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY - 0.5)));
		return Math.min(Math.min(Math.min(d1, d2), d3), d4);
	}
}
