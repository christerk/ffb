package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.PutridRegurgitationBlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.Map;

public class ClientStatePutridRegurgitationBlock extends AbstractClientStateBlock<PutridRegurgitationBlockLogicModule> {
	public ClientStatePutridRegurgitationBlock(FantasyFootballClientAwt pClient) {
		super(pClient, new PutridRegurgitationBlockLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.PUTRID_REGURGITATION_BLOCK;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SUPER:
				super.clickOnPlayer(pPlayer);
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
		}};
	}
}
