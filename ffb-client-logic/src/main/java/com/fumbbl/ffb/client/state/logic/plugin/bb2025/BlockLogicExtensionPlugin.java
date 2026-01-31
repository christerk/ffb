package com.fumbbl.ffb.client.state.logic.plugin.bb2025;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BlockLogicExtensionPlugin extends com.fumbbl.ffb.client.state.logic.plugin.BlockLogicExtensionPlugin {

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.CHOMP);
	}

	@Override
	public void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
		BlockLogicExtension logicModule, ClientCommunication communication, Player<?> defender) {
		switch (action) {
			case CHOMP:
				if (logicModule.isChompAvailable(actingPlayer.getPlayer(), defender)) {
					logicModule.block(actingPlayer.getPlayerId(), defender, false, false, false, false, true);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
		BlockLogicExtension logicModule) {
		return actionContext;
	}

	@Override
	public boolean playerCanNotMove(PlayerState playerState) {
		return playerState.isPinned();
	}

	@Override
	public ActionContext blockActionContext(ActingPlayer actingPlayer, boolean multiBlock, ActionContext actionContext,
		BlockLogicExtension logicModule) {

		if (logicModule.isChompAvailable(actingPlayer.getPlayer())) {
			actionContext.add(ClientAction.CHOMP);
		}

		return actionContext;
	}
}
