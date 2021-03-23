package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2016)
public class JumpMechanic extends com.balancedbytes.games.ffb.mechanics.JumpMechanic {
	@Override
	public boolean isAvailableAsNextMove(Game game, ActingPlayer actingPlayer, boolean jumping) {
		return canStillJump(game, actingPlayer) && UtilPlayer.isNextMovePossible(game, jumping);
	}

	@Override
	public boolean canStillJump(Game game, ActingPlayer actingPlayer) {
		return UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap);
	}

	@Override
	public boolean canJump(Game game, Player<?> player, FieldCoordinate coordinate) {
		return player.hasSkillProperty(NamedProperties.canLeap);
	}

	@Override
	public boolean isValidJump(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to) {
		return !to.equals(from) && to.distanceInSteps(from) < 3;

	}
}
