package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.InfluencingAction;

import java.util.List;

public class ActionContext {
	private final List<ClientAction> actions;
	private final List<InfluencingAction> influencingActions;
	private final List<String> blockAlternatives;

	public ActionContext(List<ClientAction> actions, List<InfluencingAction> influencingActions, List<String> blockAlternatives) {
		this.actions = actions;
		this.influencingActions = influencingActions;
		this.blockAlternatives = blockAlternatives;
	}

	public List<ClientAction> getActions() {
		return actions;
	}

	public List<InfluencingAction> getInfluencingActions() {
		return influencingActions;
	}

	public List<String> getBlockAlternatives() {
		return blockAlternatives;
	}
}
