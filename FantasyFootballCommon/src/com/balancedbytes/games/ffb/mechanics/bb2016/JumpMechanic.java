package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.COMMON)
public class JumpMechanic extends com.balancedbytes.games.ffb.mechanics.JumpMechanic {
	@Override
	public boolean isAvailableAsNextMove(Game game, ActingPlayer player, boolean jumping) {
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canLeap)
			&& UtilPlayer.isNextMovePossible(game, true);
	}

	@Override
	public boolean canStillJump(ActingPlayer actingPlayer) {
		return UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap);
	}

	@Override
	public boolean canJump(Player<?> player) {
		return player.hasSkillProperty(NamedProperties.canLeap);
	}
}
