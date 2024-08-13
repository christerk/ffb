package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Set;

public class HitAndRunLogicModule extends LogicModule {
  public HitAndRunLogicModule(FantasyFootballClient client) {
    super(client);
  }

  @Override
  public Set<ClientAction> availableActions() {
    return null;
  }

  @Override
  protected void performAvailableAction(Player<?> player, ClientAction action) {

  }
}
