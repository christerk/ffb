package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
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
      return player != null && UtilCards.hasSkillWithProperty( player, NamedProperties.canSneakExtraPlayersOntoPitch);
    }

    return false;
  }

  @Override
  public boolean isDragAllowed(FieldCoordinate pCoordinate) {
    Game game = getClient().getGame();
    return ((pCoordinate != null) &&
      ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate) && !FieldCoordinateBounds.LOS_HOME.isInBounds(pCoordinate) &&
        !FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME.isInBounds(pCoordinate) && !FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME.isInBounds(pCoordinate)
        || pCoordinate.isBoxCoordinate()) &&
        (game.getFieldModel().getPlayer(pCoordinate) == null)));

  }
}
