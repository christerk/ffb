package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class FieldCoordinate implements IJsonSerializable {

	public static final int FIELD_WIDTH = 26;
	public static final int FIELD_HEIGHT = 15;

	public static final int RSV_HOME_X = -1;
	public static final int KO_HOME_X = -2;
	public static final int BH_HOME_X = -3;
	public static final int SI_HOME_X = -4;
	public static final int RIP_HOME_X = -5;
	public static final int BAN_HOME_X = -6;
	public static final int MNG_HOME_X = -7;

	public static final int RSV_AWAY_X = 30;
	public static final int KO_AWAY_X = 31;
	public static final int BH_AWAY_X = 32;
	public static final int SI_AWAY_X = 33;
	public static final int RIP_AWAY_X = 34;
	public static final int BAN_AWAY_X = 35;
	public static final int MNG_AWAY_X = 36;

	private int fX;
	private int fY;

	public FieldCoordinate(int pX, int pY) {
		fX = pX;
		fY = pY;
	}

	public FieldCoordinate() {
		this(0, 0);
	}

	public int getX() {
		return fX;
	}

	public int getY() {
		return fY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldCoordinate that = (FieldCoordinate) o;

		if (fX != that.fX) return false;
		return fY == that.fY;
	}

	@Override
	public int hashCode() {
		int result = fX;
		result = 31 * result + fY;
		return result;
	}

	public FieldCoordinate add(int pDeltaX, int pDeltaY) {
		return new FieldCoordinate(getX() + pDeltaX, getY() + pDeltaY);
	}

	public int distanceInSteps(FieldCoordinate pOtherCoordinate) {
		int result = -1;
		if (pOtherCoordinate != null) {
			result = Math.max(Math.abs(getX() - pOtherCoordinate.getX()), Math.abs(getY() - pOtherCoordinate.getY()));
		}
		return result;
	}

	public boolean isAdjacent(FieldCoordinate pOtherCoordinate) {
		return (pOtherCoordinate != null) && (distanceInSteps(pOtherCoordinate) == 1);
	}

	public FieldCoordinate transform() {
		switch (getX()) {
		case RSV_HOME_X:
			return new FieldCoordinate(RSV_AWAY_X, getY());
		case KO_HOME_X:
			return new FieldCoordinate(KO_AWAY_X, getY());
		case BH_HOME_X:
			return new FieldCoordinate(BH_AWAY_X, getY());
		case SI_HOME_X:
			return new FieldCoordinate(SI_AWAY_X, getY());
		case RIP_HOME_X:
			return new FieldCoordinate(RIP_AWAY_X, getY());
		case BAN_HOME_X:
			return new FieldCoordinate(BAN_AWAY_X, getY());
		case MNG_HOME_X:
			return new FieldCoordinate(MNG_AWAY_X, getY());
		case RSV_AWAY_X:
			return new FieldCoordinate(RSV_HOME_X, getY());
		case KO_AWAY_X:
			return new FieldCoordinate(KO_HOME_X, getY());
		case BH_AWAY_X:
			return new FieldCoordinate(BH_HOME_X, getY());
		case SI_AWAY_X:
			return new FieldCoordinate(SI_HOME_X, getY());
		case RIP_AWAY_X:
			return new FieldCoordinate(RIP_HOME_X, getY());
		case BAN_AWAY_X:
			return new FieldCoordinate(BAN_HOME_X, getY());
		case MNG_AWAY_X:
			return new FieldCoordinate(MNG_HOME_X, getY());
		default:
			return new FieldCoordinate(FIELD_WIDTH - 1 - getX(), getY());
		}
	}

	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}

	@SuppressWarnings("unused")
	public int compareTo(FieldCoordinate pAnotherFc) {
		if (pAnotherFc == null) {
			return -1;
		}
		if (pAnotherFc.getX() < getX()) {
			return 1;
		}
		if (pAnotherFc.getX() > getX()) {
			return -1;
		}
		return (getY() - pAnotherFc.getY());
	}

	public boolean isBoxCoordinate() {
		switch (getX()) {
		case RSV_HOME_X:
		case RSV_AWAY_X:
		case KO_HOME_X:
		case KO_AWAY_X:
		case BH_HOME_X:
		case BH_AWAY_X:
		case SI_HOME_X:
		case SI_AWAY_X:
		case RIP_HOME_X:
		case RIP_AWAY_X:
		case BAN_HOME_X:
		case BAN_AWAY_X:
		case MNG_HOME_X:
		case MNG_AWAY_X:
			return true;
		default:
			return false;
		}
	}

	public static FieldCoordinate transform(FieldCoordinate pFieldCoordinate) {
		return (pFieldCoordinate != null) ? pFieldCoordinate.transform() : null;
	}

	public static boolean equals(FieldCoordinate pCoordinate1, FieldCoordinate pCoordinate2) {
		if (pCoordinate1 != null) {
			return pCoordinate1.equals(pCoordinate2);
		} else return pCoordinate2 == null;
	}

	public static Direction getDirection(FieldCoordinate from, FieldCoordinate to) {
		int dx = to.getX() - from.getX();
		int dy = to.getY() - from.getY();

		if (dx < 0) {
			if (dy < 0) {
				return Direction.NORTHWEST;
			} else if (dy > 0) {
				return Direction.SOUTHWEST;
			} else {
				return Direction.WEST;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return Direction.NORTHEAST;
			} else if (dy > 0) {
				return Direction.SOUTHEAST;
			} else {
				return Direction.EAST;
			}
		} else {
			if (dy < 0) {
				return Direction.NORTH;
			} else if (dy > 0) {
				return Direction.SOUTH;
			}
		}
		return null;
	}

	public FieldCoordinate move(Direction d, int distance) {
		FieldCoordinate result = new FieldCoordinate(this.fX, this.fY);

		int dy = 0;
		int dx = 0;

		switch (d) {
		case NORTH:
			dy = -1;
			break;
		case NORTHEAST:
			dx = 1;
			dy = -1;
			break;
		case EAST:
			dx = 1;
			break;
		case SOUTHEAST:
			dx = 1;
			dy = 1;
			break;
		case SOUTH:
			dy = 1;
			break;
		case SOUTHWEST:
			dx = -1;
			dy = 1;
			break;
		case WEST:
			dx = -1;
			break;
		case NORTHWEST:
			dx = -1;
			dy = -1;
			break;
		}

		for (int i = 0; i < distance; i++) {
			result.fX += dx;
			result.fY += dy;
		}

		return result;
	}

	@Override
	public FieldCoordinate initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fX = IJsonOption.FIELD_COORDINATE_X.getFrom(source, jsonObject);
		fY = IJsonOption.FIELD_COORDINATE_Y.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.FIELD_COORDINATE_X.addTo(jsonObject, fX);
		IJsonOption.FIELD_COORDINATE_Y.addTo(jsonObject, fY);
		return jsonObject;
	}
}
