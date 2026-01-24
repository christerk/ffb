package com.fumbbl.ffb.client.state.logic.plugin;

import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;

import java.util.Set;

public abstract class MoveLogicPlugin implements LogicPlugin {

	public abstract Set<ClientAction> availableActions();

	public abstract void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
		MoveLogicModule logicModule, ClientCommunication communication);

	public abstract ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
		MoveLogicModule logicModule);
}
