package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;

public class ClientStateSwarming extends ClientStateSetup {

  public ClientStateSwarming(FantasyFootballClient pClient) {
    super(pClient);
  }

  @Override
  public ClientStateId getId() {
    return ClientStateId.SWARMING;
  }
}
