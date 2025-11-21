package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateBlock extends AbstractClientStateBlock<BlockLogicModule> {

  public ClientStateBlock(FantasyFootballClientAwt pClient) {
    super(pClient, new BlockLogicModule(pClient));
  }

}
