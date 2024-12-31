package com.fumbbl.ffb.client.state.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Influences {
	BALL_ACTIONS_DUE_TO_TREACHEROUS(ClientAction.PASS, ClientAction.HAND_OVER),
	IS_JUMPING(ClientAction.JUMP),
	VOMIT_DUE_TO_PUTRID_REGURGITATION(ClientAction.PROJECTILE_VOMIT);

	private final List<ClientAction> influencedActions = new ArrayList<>();

	Influences(ClientAction... influencedActions) {
		this.influencedActions.addAll(Arrays.asList(influencedActions));
	}

	public List<ClientAction> getInfluencedActions() {
		return influencedActions;
	}
}
