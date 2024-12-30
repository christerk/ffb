package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;

/**
 * @author Kalimar
 */
public class ClientStateBlock extends AbstractClientStateBlock<BlockLogicModule> {

  protected ClientStateBlock(FantasyFootballClientAwt pClient) {
    super(pClient, new BlockLogicModule(pClient));
  }

}
