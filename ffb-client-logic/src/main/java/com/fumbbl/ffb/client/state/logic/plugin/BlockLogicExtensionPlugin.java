package com.fumbbl.ffb.client.state.logic.plugin;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Set;

public abstract class BlockLogicExtensionPlugin implements LogicPlugin {

  @Override
  public Type getType() {
    return Type.BLOCK;
  }

  public abstract Set<ClientAction> availableActions();

  public abstract void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
    BlockLogicExtension logicModule, ClientCommunication communication, Player<?> defender);

  public abstract ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
    BlockLogicExtension logicModule);

  public abstract boolean playerCanNotMove(PlayerState playerState);
}
