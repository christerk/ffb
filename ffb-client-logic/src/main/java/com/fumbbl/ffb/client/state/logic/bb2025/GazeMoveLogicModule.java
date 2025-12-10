package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;

public class GazeMoveLogicModule extends GazeLogicModule {
  public GazeMoveLogicModule(FantasyFootballClient client) {
    super(client);
  }

  @Override
  public ClientStateId getId() {
    return ClientStateId.GAZE_MOVE;
  }

}
