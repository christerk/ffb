package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.PutridRegurgitationBlockLogicModule;

import java.util.HashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStatePutridRegurgitationBlock extends AbstractClientStateBlock<PutridRegurgitationBlockLogicModule> {
	public ClientStatePutridRegurgitationBlock(FantasyFootballClientAwt pClient) {
		super(pClient, new PutridRegurgitationBlockLogicModule(pClient));
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
		}};
	}
}
