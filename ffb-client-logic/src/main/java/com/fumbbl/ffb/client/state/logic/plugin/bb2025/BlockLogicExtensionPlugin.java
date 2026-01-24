package com.fumbbl.ffb.client.state.logic.plugin.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BlockLogicExtensionPlugin extends com.fumbbl.ffb.client.state.logic.plugin.BlockLogicExtensionPlugin {

	@Override
	public Type getType() {
		return Type.BLOCK;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	public void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
		BlockLogicExtension logicModule, ClientCommunication communication) {

	}

	@Override
	public ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
		BlockLogicExtension logicModule) {
		return actionContext;
	}
}
