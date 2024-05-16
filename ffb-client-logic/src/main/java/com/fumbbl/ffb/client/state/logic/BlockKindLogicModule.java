package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.Set;

public class BlockKindLogicModule extends LogicModule {
	
	public BlockKindLogicModule(FantasyFootballClient client) {
		super(client);
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
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			Game game = client.getGame();
			if (!game.isHomePlaying()) {
				return;
			}
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (action) {
				case BLOCK:
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, false);
					break;
				case STAB:
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, true, false, false);
					break;
				case CHAINSAW:
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, true, false);
					break;
				case PROJECTILE_VOMIT:
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, true);
					break;
				case GORED_BY_THE_BULL:
					Skill goredSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie);
					if (UtilClientStateBlocking.isGoredAvailable(game) && goredSkill != null) {
						client.getCommunication().sendUseSkill(goredSkill, true, actingPlayer.getPlayerId());
					}
					client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, false);
					break;
				default:
					break;
			}
		}
	}
}
