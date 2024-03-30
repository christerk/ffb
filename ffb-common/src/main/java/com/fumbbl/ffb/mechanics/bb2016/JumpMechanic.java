package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2016)
public class JumpMechanic extends com.fumbbl.ffb.mechanics.JumpMechanic {
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
