package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DimensionProvider {

	private ClientLayout layout;

	private static final int SIDEBAR_WIDTH_L = 145;
	private static final int SIDEBAR_WIDTH_P = 165;

	private final Map<ClientLayout, Map<Component, Dimension>> dimensions = new HashMap<>();
	private final int fieldSquareSize = 30;

	public DimensionProvider(ClientLayout layout) {
		this.layout = layout;
		Map<Component, Dimension> portraitDimensions = new HashMap<>();
		Map<Component, Dimension> squareDimensions = new HashMap<>();
		Map<Component, Dimension> landscapeDimensions = new HashMap<>();

		dimensions.put(ClientLayout.LANDSCAPE, landscapeDimensions);
		dimensions.put(ClientLayout.PORTRAIT, portraitDimensions);
		dimensions.put(ClientLayout.SQUARE, squareDimensions);

		portraitDimensions.put(Component.FIELD, new Dimension(452, 782));
		squareDimensions.put(Component.FIELD, new Dimension(452, 782));
		landscapeDimensions.put(Component.FIELD, new Dimension(782, 452));

		portraitDimensions.put(Component.CHAT, new Dimension(389, 153));
		squareDimensions.put(Component.CHAT, new Dimension(260, 343));
		landscapeDimensions.put(Component.CHAT, new Dimension(389, 226));

		portraitDimensions.put(Component.LOG, new Dimension(389, 153));
		squareDimensions.put(Component.LOG, new Dimension(260, 343));
		landscapeDimensions.put(Component.LOG, new Dimension(389, 226));

		portraitDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));
		squareDimensions.put(Component.REPLAY_CONTROL, new Dimension(260, 26));
		landscapeDimensions.put(Component.REPLAY_CONTROL, new Dimension(389, 26));

		portraitDimensions.put(Component.TURN_DICE_STATUS, new Dimension(SIDEBAR_WIDTH_P, 101));
		squareDimensions.put(Component.TURN_DICE_STATUS, new Dimension(SIDEBAR_WIDTH_P, 101));
		landscapeDimensions.put(Component.TURN_DICE_STATUS, new Dimension(SIDEBAR_WIDTH_L, 92));

		portraitDimensions.put(Component.RESOURCE, new Dimension(SIDEBAR_WIDTH_P, 185));
		squareDimensions.put(Component.RESOURCE, new Dimension(SIDEBAR_WIDTH_P, 185));
		landscapeDimensions.put(Component.RESOURCE, new Dimension(SIDEBAR_WIDTH_L, 168));

		portraitDimensions.put(Component.BUTTON_BOX, new Dimension(SIDEBAR_WIDTH_P, 24));
		squareDimensions.put(Component.BUTTON_BOX, new Dimension(SIDEBAR_WIDTH_P, 24));
		landscapeDimensions.put(Component.BUTTON_BOX, new Dimension(SIDEBAR_WIDTH_L, 22));

		portraitDimensions.put(Component.BOX, new Dimension(SIDEBAR_WIDTH_P, 472));
		squareDimensions.put(Component.BOX, new Dimension(SIDEBAR_WIDTH_P, 472));
		landscapeDimensions.put(Component.BOX, new Dimension(SIDEBAR_WIDTH_L, 430));

		portraitDimensions.put(Component.PLAYER_DETAIL, new Dimension(SIDEBAR_WIDTH_P, 472));
		squareDimensions.put(Component.PLAYER_DETAIL, new Dimension(SIDEBAR_WIDTH_P, 472));
		landscapeDimensions.put(Component.PLAYER_DETAIL, new Dimension(SIDEBAR_WIDTH_L, 430));

		portraitDimensions.put(Component.SIDEBAR, new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(portraitDimensions)));
		squareDimensions.put(Component.SIDEBAR, new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(portraitDimensions)));
		landscapeDimensions.put(Component.SIDEBAR, new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(landscapeDimensions)));

		portraitDimensions.put(Component.PLAYER_PORTRAIT, new Dimension(133, 162));
		squareDimensions.put(Component.PLAYER_PORTRAIT, new Dimension(133, 162));
		landscapeDimensions.put(Component.PLAYER_PORTRAIT, new Dimension(121, 147));

		portraitDimensions.put(Component.PLAYER_PORTRAIT_OFFSET, new Dimension(13, 32));
		squareDimensions.put(Component.PLAYER_PORTRAIT_OFFSET, new Dimension(13, 32));
		landscapeDimensions.put(Component.PLAYER_PORTRAIT_OFFSET, new Dimension(3, 32));

		portraitDimensions.put(Component.PLAYER_STAT_OFFSET, new Dimension(4, 198));
		squareDimensions.put(Component.PLAYER_STAT_OFFSET, new Dimension(4, 198));
		landscapeDimensions.put(Component.PLAYER_STAT_OFFSET, new Dimension(3, 179));

		portraitDimensions.put(Component.PLAYER_STAT_BOX, new Dimension(31, 30));
		squareDimensions.put(Component.PLAYER_STAT_BOX, new Dimension(31, 30));
		landscapeDimensions.put(Component.PLAYER_STAT_BOX, new Dimension(28, 29));

		portraitDimensions.put(Component.PLAYER_STAT_BOX_MISC, new Dimension(0, 15));
		squareDimensions.put(Component.PLAYER_STAT_BOX_MISC, new Dimension(0, 15));
		landscapeDimensions.put(Component.PLAYER_STAT_BOX_MISC, new Dimension(0, 14));

		portraitDimensions.put(Component.PLAYER_SPP_OFFSET, new Dimension(10, 245));
		squareDimensions.put(Component.PLAYER_SPP_OFFSET, new Dimension(10, 245));
		landscapeDimensions.put(Component.PLAYER_SPP_OFFSET, new Dimension(8, 222));

		portraitDimensions.put(Component.PLAYER_SKILL_OFFSET, new Dimension(10, 270));
		squareDimensions.put(Component.PLAYER_SKILL_OFFSET, new Dimension(10, 270));
		landscapeDimensions.put(Component.PLAYER_SKILL_OFFSET, new Dimension(8, 246));

		portraitDimensions.put(Component.BOX_BUTTON, new Dimension(82, 22));
		squareDimensions.put(Component.BOX_BUTTON, new Dimension(82, 22));
		landscapeDimensions.put(Component.BOX_BUTTON, new Dimension(72, 22));

		portraitDimensions.put(Component.END_TURN_BUTTON, new Dimension(163, 34));
		squareDimensions.put(Component.END_TURN_BUTTON, new Dimension(163, 34));
		landscapeDimensions.put(Component.END_TURN_BUTTON, new Dimension(143, 31));

		portraitDimensions.put(Component.SCORE_BOARD, new Dimension(782, 32));
		squareDimensions.put(Component.SCORE_BOARD, new Dimension(260, 96));
		landscapeDimensions.put(Component.SCORE_BOARD, new Dimension(782, 32));

		portraitDimensions.put(Component.REPLAY_ICON_GAP, new Dimension(10, 0));
		squareDimensions.put(Component.REPLAY_ICON_GAP, new Dimension(0, 0));
		landscapeDimensions.put(Component.REPLAY_ICON_GAP, new Dimension(10, 0));

		portraitDimensions.put(Component.REPLAY_ICON_WIDTH, new Dimension(36, 0));
		squareDimensions.put(Component.REPLAY_ICON_WIDTH, new Dimension(30, 0));
		landscapeDimensions.put(Component.REPLAY_ICON_WIDTH, new Dimension(36, 0));
	}

	public Dimension dimension(Component component) {
		return dimensions.get(layout).get(component);
	}

	public boolean isPitchPortrait() {
		return layout != ClientLayout.LANDSCAPE;
	}

	public ClientLayout getLayout() {
		return layout;
	}

	public void setLayout(ClientLayout layout) {
		this.layout = layout;
	}

	public FieldCoordinate mapToGlobal(FieldCoordinate fieldCoordinate) {
		if (isPitchPortrait()) {
			return new FieldCoordinate(25 - fieldCoordinate.getY(), fieldCoordinate.getX());
		}

		return fieldCoordinate;
	}

	public int fieldSquareSize() {
		return fieldSquareSize;
	}

	public int imageOffset() {
		return fieldSquareSize / 2;
	}

	private int sidebarHeight(Map<Component, Dimension> dimensions) {
		return (int) Arrays.stream(new Component[]{Component.TURN_DICE_STATUS, Component.RESOURCE, Component.BOX, Component.BUTTON_BOX})
			.map(dimensions::get).mapToDouble(Dimension::getHeight).sum();
	}

	public Dimension mapToLocal(int x, int y, boolean addImageOffset) {
		int offset = addImageOffset ? fieldSquareSize / 2 : 0;


		if (isPitchPortrait()) {
			return new Dimension(y * fieldSquareSize + offset, (25 - x) * fieldSquareSize + offset);
		}
		return new Dimension(x * fieldSquareSize + offset, y * fieldSquareSize + offset);

	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate) {
		return mapToLocal(fieldCoordinate, false);
	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate, boolean addImageOffset) {
		return mapToLocal(fieldCoordinate.getX(), fieldCoordinate.getY(), addImageOffset);
	}

	public Direction mapToLocal(Direction direction) {
		if (isPitchPortrait()) {
			switch (direction) {
				case NORTHEAST:
					return Direction.NORTHWEST;
				case EAST:
					return Direction.NORTH;
				case SOUTHEAST:
					return Direction.NORTHEAST;
				case SOUTHWEST:
					return Direction.SOUTHEAST;
				case WEST:
					return Direction.SOUTH;
				case NORTHWEST:
					return Direction.SOUTHWEST;
				case NORTH:
					return Direction.WEST;
				case SOUTH:
					return Direction.EAST;
			}
		}

		return direction;
	}

	public enum ClientLayout {
		LANDSCAPE, PORTRAIT, SQUARE
	}

	public enum Component {
		FIELD, CHAT, LOG, REPLAY_CONTROL, TURN_DICE_STATUS, RESOURCE, BUTTON_BOX, BOX, PLAYER_DETAIL, SIDEBAR,
		PLAYER_PORTRAIT, PLAYER_PORTRAIT_OFFSET, PLAYER_STAT_OFFSET, PLAYER_STAT_BOX, PLAYER_STAT_BOX_MISC,
		PLAYER_SPP_OFFSET, PLAYER_SKILL_OFFSET, BOX_BUTTON, END_TURN_BUTTON, SCORE_BOARD, REPLAY_ICON_GAP,
		REPLAY_ICON_WIDTH
	}
}
