package com.fumbbl.ffb.client.state.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Influences {
	BALL_ACTIONS_DUE_TO_TREACHEROUS(ClientAction.PASS, ClientAction.HAND_OVER, ClientAction.SHOT_TO_NOTHING),
	CAN_PLACE_CARRIED_PLAYER(ClientAction.ILL_CARRY_YOU),
	HAS_ACTED(ClientAction.END_MOVE),
	HANDS_OVER_TO_ANYONE(ClientAction.HAND_OVER),
	INCORPOREAL_ACTIVE(ClientAction.INCORPOREAL, ClientAction.END_MOVE),
	IS_THROWING_HAIL_MARY(ClientAction.HAIL_MARY_BOMB, ClientAction.HAIL_MARY_PASS),
	IS_JUMPING(ClientAction.JUMP),
	MUST_PLACE_CARRIED_PLAYER(ClientAction.END_MOVE),
	VOMIT_DUE_TO_PUTRID_REGURGITATION(ClientAction.PROJECTILE_VOMIT);

	private final List<ClientAction> influencedActions = new ArrayList<>();

	Influences(ClientAction... influencedActions) {
		this.influencedActions.addAll(Arrays.asList(influencedActions));
	}

	public List<ClientAction> getInfluencedActions() {
		return influencedActions;
	}
}
