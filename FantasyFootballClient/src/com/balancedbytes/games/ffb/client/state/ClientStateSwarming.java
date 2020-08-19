package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;

public class ClientStateSwarming extends ClientStateSetup {

  public ClientStateSwarming(FantasyFootballClient pClient) {
    super(pClient);
  }

  @Override
  public ClientStateId getId() {
    return ClientStateId.SWARMING;
  }

  @Override
  public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      Player player = getClient().getGame().getFieldModel().getPlayer(pCoordinate);
      return player != null && UtilCards.hasSkill(getClient().getGame(), player, Skill.SWARMING);
    }

    return false;
  }
}
