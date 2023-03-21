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

	private final int fieldSquareSize = 30;

	public DimensionProvider(ClientLayout layout) {
		this.layout = layout;
	}

	public Dimension dimension(Component component) {
		return component.dimension(layout);
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
		FIELD(new Dimension(782, 452), new Dimension(452, 782)),
		CHAT(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343)),
		LOG(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343)),
		REPLAY_CONTROL(new Dimension(389, 26), new Dimension(389, 26), new Dimension(260, 26)),
		TURN_DICE_STATUS(new Dimension(SIDEBAR_WIDTH_L, 92), new Dimension(SIDEBAR_WIDTH_P, 101)),
		RESOURCE(new Dimension(SIDEBAR_WIDTH_L, 168), new Dimension(SIDEBAR_WIDTH_P, 185)),
		BUTTON_BOX(new Dimension(SIDEBAR_WIDTH_L, 22), new Dimension(SIDEBAR_WIDTH_P, 24)),
		BOX(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472)),
		PLAYER_DETAIL(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472)),
		SIDEBAR(new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(ClientLayout.LANDSCAPE)), new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(ClientLayout.PORTRAIT))),
		PLAYER_PORTRAIT(new Dimension(121, 147), new Dimension(133, 162)),
		PLAYER_PORTRAIT_OFFSET(new Dimension(3, 32), new Dimension(13, 32)),
		PLAYER_STAT_OFFSET(new Dimension(3, 179), new Dimension(4, 198)),
		PLAYER_STAT_BOX(new Dimension(28, 29), new Dimension(31, 30)),
		PLAYER_STAT_BOX_MISC(new Dimension(0, 14), new Dimension(0, 15)),
		PLAYER_SPP_OFFSET(new Dimension(8, 222), new Dimension(10, 245)),
		PLAYER_SKILL_OFFSET(new Dimension(8, 246), new Dimension(10, 270)),
		BOX_BUTTON(new Dimension(72, 22), new Dimension(82, 22)),
		END_TURN_BUTTON(new Dimension(143, 31), new Dimension(163, 34)),
		SCORE_BOARD(new Dimension(782, 32), new Dimension(782, 32), new Dimension(260, 96)),
		REPLAY_ICON_GAP(new Dimension(10, 0), new Dimension(10, 0), new Dimension(0, 0)),
		REPLAY_ICON(new Dimension(36, 0), new Dimension(36, 0), new Dimension(30, 0));

		private final Map<ClientLayout, Dimension> dimensions = new HashMap<>();

		Component(Dimension landscape, Dimension portrait, Dimension square) {
			dimensions.put(ClientLayout.LANDSCAPE, landscape);
			dimensions.put(ClientLayout.PORTRAIT, portrait);
			dimensions.put(ClientLayout.SQUARE, square);
		}

		Component(Dimension landscape, Dimension portrait) {
			this(landscape, portrait, portrait);
		}

		private static int sidebarHeight(ClientLayout layout) {
			return (int) Arrays.stream(new Component[]{Component.TURN_DICE_STATUS, Component.RESOURCE, Component.BOX, Component.BUTTON_BOX})
				.map(comp -> comp.dimension(layout)).mapToDouble(Dimension::getHeight).sum();
		}

		private Dimension dimension(ClientLayout layout) {
			return dimensions.get(layout);
		}
	}
}
