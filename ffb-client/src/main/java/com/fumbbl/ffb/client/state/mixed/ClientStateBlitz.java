package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateBlitz extends AbstractClientStateBlitz<BlitzLogicModule> {
	public ClientStateBlitz(FantasyFootballClientAwt client) {
		super(client, new BlitzLogicModule(client));
	}

	@Override
	protected void evaluateClick(InteractionResult result, Player<?> player) {
		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForPlayer(player, result.getActionContext());
				break;
			default:
				super.evaluateClick(result, player);
				break;
		}
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = super.influencedItemConfigs();
		influences.putAll(extension.influencedItemConfigs());
		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = super.itemConfigs(actionContext);
		itemConfigs.putAll(extension.itemConfigs());
		return itemConfigs;
	}
}
