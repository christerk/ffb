package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.logic.mixed.BlockLogicModule;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateBlock extends AbstractClientStateBlock<BlockLogicModule> {

  public ClientStateBlock(FantasyFootballClientAwt pClient) {
    super(pClient, new BlockLogicModule(pClient));
  }

}
