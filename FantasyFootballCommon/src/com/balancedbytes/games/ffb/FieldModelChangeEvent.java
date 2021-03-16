package com.balancedbytes.games.ffb;

import java.util.EventObject;

/**
 * 
 * @author Kalimar
 */
public class FieldModelChangeEvent extends EventObject {

	public static final int TYPE_BLOODSPOT = 1;
	public static final int TYPE_PUSHBACK_SQUARE = 2;
	public static final int TYPE_PLAYER_POSITION = 3;
	public static final int TYPE_PLAYER_STATE = 4;
	public static final int TYPE_BALL_MOVING = 5;
	public static final int TYPE_BALL_COORDINATE = 6;
	public static final int TYPE_TRACK_NUMBER = 7;
	public static final int TYPE_WEATHER = 8;
	public static final int TYPE_DICE_DECORATION = 9;
	public static final int TYPE_MOVE_SQUARE = 10;
	public static final int TYPE_RANGE_RULER = 11;
	public static final int TYPE_FIELD_MARKER = 12;
	public static final int TYPE_PLAYER_MARKER = 13;
	public static final int TYPE_BOMB_COORDINATE = 14;
	public static final int TYPE_BOMB_MOVING = 15;

	private int fType;
	private Object fOldValue;
	private Object fNewValue;
	private Object fProperty;

	public FieldModelChangeEvent(Object pSource, int pType, Object pProperty, Object pOldValue, Object pNewValue) {
		super(pSource);
		fType = pType;
		fProperty = pProperty;
		fOldValue = pOldValue;
		fNewValue = pNewValue;
	}

	public int getType() {
		return fType;
	}

	public Object getOldValue() {
		return fOldValue;
	}

	public Object getNewValue() {
		return fNewValue;
	}

	public Object getProperty() {
		return fProperty;
	}

	public boolean isAdded() {
		return ((fNewValue != null) && (fOldValue == null));
	}

	public boolean isRemoved() {
		return ((fOldValue != null) && (fNewValue == null));
	}

	public boolean isUpdated() {
		return ((fNewValue != null) && (fOldValue != null));
	}

}
