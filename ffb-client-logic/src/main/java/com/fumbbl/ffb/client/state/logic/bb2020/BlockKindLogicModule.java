package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.Set;

public class BlockKindLogicModule extends LogicModule {
	protected final BlockLogicExtension extension;

	public BlockKindLogicModule(FantasyFootballClient client) {
		super(client);
		extension = new BlockLogicExtension(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SELECT_BLOCK_KIND;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.BLOCK);
			add(ClientAction.STAB);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.CHAINSAW);
			add(ClientAction.GORED_BY_THE_BULL);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		return null;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			Game game = client.getGame();
			if (!game.isHomePlaying()) {
				return;
			}
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (action) {
				case GORED_BY_THE_BULL:
					Skill goredSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie);
					if (extension.isGoredAvailable(game) && goredSkill != null) {
						client.getCommunication().sendUseSkill(goredSkill, true, actingPlayer.getPlayerId());
					}
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, false, false);
					break;
				default:
					extension.perform(player, action);
					break;
			}
		}
	}
}
