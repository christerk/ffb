package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DimensionProvider {

	private static final int SIDEBAR_WIDTH_L = 145;
	private static final int SIDEBAR_WIDTH_P = 165;
	private final Map<Component, Dimension> portraitDimensions = new HashMap<>();
	private final Map<Component, Dimension> landscapeDimensions = new HashMap<>();
	private final int fieldSquareSize = 30;
	private boolean portrait;

	public DimensionProvider(boolean portrait) {
		this.portrait = portrait;

		portraitDimensions.put(Component.FIELD, new Dimension(452, 782));
		landscapeDimensions.put(Component.FIELD, new Dimension(782, 452));

		portraitDimensions.put(Component.CHAT, new Dimension(389, 153));
		landscapeDimensions.put(Component.CHAT, new Dimension(389, 226));

		portraitDimensions.put(Component.LOG, new Dimension(389, 153));
		landscapeDimensions.put(Component.LOG, new Dimension(389, 226));

		portraitDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));
		landscapeDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));

		portraitDimensions.put(Component.TURN_DICE_STATUS, new Dimension(SIDEBAR_WIDTH_P, 101));
		landscapeDimensions.put(Component.TURN_DICE_STATUS, new Dimension(SIDEBAR_WIDTH_L, 92));

		portraitDimensions.put(Component.RESOURCE, new Dimension(SIDEBAR_WIDTH_P, 185));
		landscapeDimensions.put(Component.RESOURCE, new Dimension(SIDEBAR_WIDTH_L, 168));

		portraitDimensions.put(Component.BOX_BUTTON, new Dimension(SIDEBAR_WIDTH_P, 24));
		landscapeDimensions.put(Component.BOX_BUTTON, new Dimension(SIDEBAR_WIDTH_L, 22));

		portraitDimensions.put(Component.BOX, new Dimension(SIDEBAR_WIDTH_P, 472));
		landscapeDimensions.put(Component.BOX, new Dimension(SIDEBAR_WIDTH_L, 430));

		portraitDimensions.put(Component.PLAYER_DETAIL, new Dimension(SIDEBAR_WIDTH_P, 472));
		landscapeDimensions.put(Component.PLAYER_DETAIL, new Dimension(SIDEBAR_WIDTH_L, 430));

		portraitDimensions.put(Component.SIDEBAR, new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(portraitDimensions)));
		landscapeDimensions.put(Component.SIDEBAR, new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(landscapeDimensions)));

	}

	public Dimension dimension(Component component) {
		return (portrait ? portraitDimensions : landscapeDimensions).get(component);
	}

	public boolean isPortrait() {
		return portrait;
	}

	public void setPortrait(boolean portrait) {
		this.portrait = portrait;
	}

	public int fieldSquareSize() {
		return fieldSquareSize;
	}

	public int imageOffset() {
		return fieldSquareSize / 2;
	}

	private int sidebarHeight(Map<Component, Dimension> dimensions) {
		return (int) Arrays.stream(new Component[]{Component.TURN_DICE_STATUS, Component.RESOURCE, Component.BOX, Component.BOX_BUTTON})
			.map(dimensions::get).mapToDouble(Dimension::getHeight).sum();
	}

	public FieldCoordinate normalize(FieldCoordinate fieldCoordinate) {
		if (portrait) {
			return new FieldCoordinate(25 - fieldCoordinate.getY(), fieldCoordinate.getX());
		}

		return fieldCoordinate;
	}

	public Dimension map(FieldCoordinate fieldCoordinate) {
		return map(fieldCoordinate, false);
	}

	public Dimension map(FieldCoordinate fieldCoordinate, boolean addImageOffset) {
		return map(fieldCoordinate.getX(), fieldCoordinate.getY(), addImageOffset);
	}

	public Dimension map(int x, int y, boolean addImageOffset) {
		int offset = addImageOffset ? fieldSquareSize / 2 : 0;


		if (portrait) {
			return new Dimension(y * fieldSquareSize + offset, (25 - x) * fieldSquareSize + offset);
		}
		return new Dimension(x * fieldSquareSize + offset, y * fieldSquareSize + offset);

	}

	public enum Component {
		FIELD, CHAT, LOG, REPLAY_CONTROL, TURN_DICE_STATUS, RESOURCE, BOX_BUTTON, BOX, PLAYER_DETAIL, SIDEBAR
	}
}
