package com.fumbbl.ffb.client;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.fumbbl.ffb.client.LayoutSettings.SIDEBAR_WIDTH_L;
import static com.fumbbl.ffb.client.LayoutSettings.SIDEBAR_WIDTH_P;

public enum Component {

	FIELD_SQUARE(new Dimension(30, 30), new Dimension(57, 57)),
	FIELD(fieldDimension(ClientLayout.LANDSCAPE), fieldDimension(ClientLayout.PORTRAIT), fieldDimension(ClientLayout.WIDE)),
	CHAT(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343), new Dimension(741, 139)),
	LOG(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343), new Dimension(741, 139)),
	REPLAY_CONTROL(new Dimension(389, 26), new Dimension(389, 26), new Dimension(260, 26), new Dimension(389, 26)),
	TURN_DICE_STATUS(new Dimension(SIDEBAR_WIDTH_L, 92), new Dimension(SIDEBAR_WIDTH_P, 101), new Dimension(SIDEBAR_WIDTH_L, 92)),
	RESOURCE(new Dimension(SIDEBAR_WIDTH_L, 168), new Dimension(SIDEBAR_WIDTH_P, 185), new Dimension(SIDEBAR_WIDTH_L, 484)),
	BUTTON_BOX(new Dimension(SIDEBAR_WIDTH_L, 22), new Dimension(SIDEBAR_WIDTH_P, 24), new Dimension(SIDEBAR_WIDTH_L, 22)),
	BOX(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472), new Dimension(SIDEBAR_WIDTH_L, 430)),
	PLAYER_DETAIL(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472), new Dimension(SIDEBAR_WIDTH_L, 430)),
	SIDEBAR(new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(ClientLayout.LANDSCAPE)), new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(ClientLayout.PORTRAIT)), new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(ClientLayout.WIDE))),
	PLAYER_PORTRAIT(new Dimension(121, 147), new Dimension(133, 162), new Dimension(121, 147)),
	PLAYER_PORTRAIT_OFFSET(new Dimension(3, 32), new Dimension(13, 32), new Dimension(3, 32)),
	PLAYER_STAT_OFFSET(new Dimension(3, 179), new Dimension(4, 198), new Dimension(3, 179)),
	PLAYER_STAT_BOX(new Dimension(28, 29), new Dimension(31, 30), new Dimension(28, 29)),
	PLAYER_STAT_BOX_MISC(new Dimension(0, 14), new Dimension(0, 15), new Dimension(0, 14)),
	PLAYER_SPP_OFFSET(new Dimension(8, 222), new Dimension(10, 245), new Dimension(8, 222)),
	PLAYER_SKILL_OFFSET(new Dimension(8, 246), new Dimension(10, 270), new Dimension(8, 246)),
	BOX_BUTTON(new Dimension(72, 22), new Dimension(82, 22), new Dimension(72, 22)),
	END_TURN_BUTTON(new Dimension(143, 31), new Dimension(163, 34), new Dimension(143, 31)),
	SCORE_BOARD(new Dimension(782, 32), new Dimension(782, 32), new Dimension(260, 96), new Dimension(1486, 32)),
	REPLAY_ICON_GAP(new Dimension(10, 0), new Dimension(10, 0), new Dimension(0, 0), new Dimension(10, 0)),
	REPLAY_ICON(new Dimension(36, 0), new Dimension(36, 0), new Dimension(30, 0), new Dimension(36, 0)),
	INDUCEMENT_COUNTER_SIZE(new Dimension(15, 15)),
	RESOURCE_SLOT(new Dimension(46, 40)),
	MAX_ICON(new Dimension(40, 40), new Dimension(76, 76)),
	ABOUT_DIALOG(new Dimension(813, 542)),
	CLIENT_SIZE(new Dimension(1078, 762), new Dimension(788, 1019), new Dimension(1050, 834), new Dimension(1920, 1080)),
	BOX_SQUARE(new Dimension(39, 39));
	private final Map<ClientLayout, Dimension> dimensions = new HashMap<>();

	Component(Dimension landscape, Dimension portrait, Dimension square, Dimension wide) {
		dimensions.put(ClientLayout.LANDSCAPE, landscape);
		dimensions.put(ClientLayout.PORTRAIT, portrait);
		dimensions.put(ClientLayout.SQUARE, square);
		dimensions.put(ClientLayout.WIDE, wide);
	}

	Component(Dimension landscape, Dimension portrait, Dimension wide) {
		this(landscape, portrait, portrait, wide);
	}

	Component(Dimension landscape, Dimension wide) {
		this(landscape, landscape, landscape, wide);
	}

	Component(Dimension landscape) {
		this(landscape, landscape, landscape, landscape);
	}

	private static int sidebarHeight(ClientLayout layout) {
		return (int) Arrays.stream(new Component[]{Component.TURN_DICE_STATUS, Component.RESOURCE, Component.BOX, Component.BUTTON_BOX})
			.map(comp -> comp.dimension(layout)).mapToDouble(Dimension::getHeight).sum();
	}

	private static int fieldLongSide(ClientLayout layout) {
		return FIELD_SQUARE.dimension(layout).width * 26 + 2;
	}

	private static int fieldShortSide(ClientLayout layout) {
		return FIELD_SQUARE.dimension(layout).width * 15 + 2;
	}

	private static int fieldWidth(ClientLayout layout) {
		return layout.isPortrait() ? fieldShortSide(layout) : fieldLongSide(layout);
	}

	private static int fieldHeight(ClientLayout layout) {
		return layout.isPortrait() ? fieldLongSide(layout) : fieldShortSide(layout);
	}

	private static Dimension fieldDimension(ClientLayout layout) {
		return new Dimension(fieldWidth(layout), fieldHeight(layout));
	}

	public Dimension dimension(ClientLayout layout) {
		return dimensions.get(layout);
	}
}
