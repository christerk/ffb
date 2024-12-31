package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public class ActionContext {
	private final List<ClientAction> actions;
	private final List<Influences> influences;
	private final List<Skill> blockAlternatives;

	public ActionContext() {
		this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	private ActionContext(List<ClientAction> actions, List<Influences> influences, List<Skill> blockAlternatives) {
		this.actions = actions;
		this.influences = influences;
		this.blockAlternatives = blockAlternatives;
	}

	public List<ClientAction> getActions() {
		return actions;
	}

	public List<Influences> getInfluences() {
		return influences;
	}

	public List<Skill> getBlockAlternatives() {
		return blockAlternatives;
	}

	public void add(ClientAction action) {
		actions.add(action);
	}

	public void add(Influences influence) {
		influences.add(influence);
	}

	public void add(Skill blockAlternative) {
		blockAlternatives.add(blockAlternative);
	}
}
