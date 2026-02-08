package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.LinkedList;

public class FieldCoordinateBounds implements IJsonSerializable {

	public static final FieldCoordinateBounds FIELD = new FieldCoordinateBounds(new FieldCoordinate(0, 0),
			new FieldCoordinate(25, 14));

	public static final FieldCoordinateBounds HALF_HOME = new FieldCoordinateBounds(new FieldCoordinate(0, 0),
			new FieldCoordinate(12, 14));

	public static final FieldCoordinateBounds HALF_AWAY = new FieldCoordinateBounds(new FieldCoordinate(13, 0),
			new FieldCoordinate(25, 14));

	public static final FieldCoordinateBounds UPPER_HALF = new FieldCoordinateBounds(new FieldCoordinate(0, 0),
			new FieldCoordinate(25, 7));

	public static final FieldCoordinateBounds CENTER_FIELD_HOME = new FieldCoordinateBounds(new FieldCoordinate(0, 4),
			new FieldCoordinate(11, 10));

	public static final FieldCoordinateBounds CENTER_FIELD_AWAY = new FieldCoordinateBounds(new FieldCoordinate(14, 4),
			new FieldCoordinate(25, 10));

	public static final FieldCoordinateBounds LOS_HOME = new FieldCoordinateBounds(new FieldCoordinate(12, 4),
			new FieldCoordinate(12, 10));

	public static final FieldCoordinateBounds LOS_AWAY = new FieldCoordinateBounds(new FieldCoordinate(13, 4),
			new FieldCoordinate(13, 10));

	public static final FieldCoordinateBounds UPPER_WIDE_ZONE_HOME = new FieldCoordinateBounds(new FieldCoordinate(0, 0),
			new FieldCoordinate(12, 3));

	public static final FieldCoordinateBounds UPPER_WIDE_ZONE_AWAY = new FieldCoordinateBounds(new FieldCoordinate(13, 0),
			new FieldCoordinate(25, 3));

	public static final FieldCoordinateBounds LOWER_WIDE_ZONE_HOME = new FieldCoordinateBounds(new FieldCoordinate(0, 11),
			new FieldCoordinate(12, 14));

	public static final FieldCoordinateBounds LOWER_WIDE_ZONE_AWAY = new FieldCoordinateBounds(
			new FieldCoordinate(13, 11), new FieldCoordinate(25, 14));

	public static final FieldCoordinateBounds ENDZONE_HOME = new FieldCoordinateBounds(new FieldCoordinate(0, 0),
			new FieldCoordinate(0, 14));

	public static final FieldCoordinateBounds ENDZONE_AWAY = new FieldCoordinateBounds(new FieldCoordinate(25, 0),
			new FieldCoordinate(25, 14));

	public static final FieldCoordinateBounds SIDELINE_UPPER = new FieldCoordinateBounds(new FieldCoordinate(1, 0),
			new FieldCoordinate(24, 0));

	public static final FieldCoordinateBounds SIDELINE_LOWER = new FieldCoordinateBounds(new FieldCoordinate(1, 14),
			new FieldCoordinate(24, 14));

	private FieldCoordinate fTopLeftCorner;
	private FieldCoordinate fBottomRightCorner;

	public FieldCoordinateBounds() {
		super();
	}

	public FieldCoordinateBounds(FieldCoordinate pTopLeftCorner, FieldCoordinate pBottomRightCorner) {
		fTopLeftCorner = pTopLeftCorner;
		fBottomRightCorner = pBottomRightCorner;
	}

	public FieldCoordinate getTopLeftCorner() {
		return fTopLeftCorner;
	}

	public FieldCoordinate getBottomRightCorner() {
		return fBottomRightCorner;
	}

	public boolean isInBounds(FieldCoordinate pCoordinate) {
		boolean result = true;
		if (pCoordinate == null) {
			result = false;
		} else {
			if (pCoordinate.getX() < fTopLeftCorner.getX()) {
				result = false;
			}
			if (pCoordinate.getY() < fTopLeftCorner.getY()) {
				result = false;
			}
			if (pCoordinate.getX() > fBottomRightCorner.getX()) {
				result = false;
			}
			if (pCoordinate.getY() > fBottomRightCorner.getY()) {
				result = false;
			}
		}
		return result;
	}

	public FieldCoordinate[] fieldCoordinates() {
		LinkedList<FieldCoordinate> fieldCoordinates = new LinkedList<>();
		for (int i = getTopLeftCorner().getX(); i <= getBottomRightCorner().getX(); i++) {
			for (int k = getTopLeftCorner().getY(); k <= getBottomRightCorner().getY(); k++) {
				fieldCoordinates.add(new FieldCoordinate(i, k));
			}
		}
		return fieldCoordinates.toArray(new FieldCoordinate[0]);
	}

	public int width() {
		return getBottomRightCorner().getX() - getTopLeftCorner().getX() + 1;
	}

	public int height() {
		return getBottomRightCorner().getY() - getTopLeftCorner().getY() + 1;
	}

	public int size() {
		return width() * height();
	}

	// ByteArraySerialization

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fBottomRightCorner == null) ? 0 : fBottomRightCorner.hashCode());
		result = prime * result + ((fTopLeftCorner == null) ? 0 : fTopLeftCorner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldCoordinateBounds other = (FieldCoordinateBounds) obj;
		if (fBottomRightCorner == null) {
			if (other.fBottomRightCorner != null)
				return false;
		} else if (!fBottomRightCorner.equals(other.fBottomRightCorner))
			return false;
		if (fTopLeftCorner == null) {
			return other.fTopLeftCorner == null;
		} else return fTopLeftCorner.equals(other.fTopLeftCorner);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.TOP_LEFT.addTo(jsonObject, fTopLeftCorner);
		IJsonOption.BOTTOM_RIGHT.addTo(jsonObject, fBottomRightCorner);
		return jsonObject;
	}

	public FieldCoordinateBounds initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fTopLeftCorner = IJsonOption.TOP_LEFT.getFrom(source, jsonObject);
		fBottomRightCorner = IJsonOption.BOTTOM_RIGHT.getFrom(source, jsonObject);
		return this;
	}

}
