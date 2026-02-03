package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class TtmMechanic extends com.fumbbl.ffb.mechanics.TtmMechanic {

	public Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower) {

		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);

		return Arrays.stream(fieldModel.findAdjacentCoordinates(throwerCoordinate, FieldCoordinateBounds.FIELD,
			1, false))
			.map(fieldModel::getPlayer)
			.filter(Objects::nonNull)
			.filter(player -> canBeThrown(pGame, player)).toArray(Player[]::new);
	}

	@Override
	public boolean canBeThrown(Game game, Player<?> player) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return player.canBeThrown()
			&& !playerState.isPinned()
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public boolean canBeKicked(Game game, Player<?> player) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return player.canBeThrown()
			&& !playerState.isPinned()
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public int minimumRoll(PassingDistance distance, Set<PassModifier> modifiers) {
		return 2 + modifierSum(distance, modifiers);
	}

	@Override
	public int modifierSum(PassingDistance distance, Set<PassModifier> modifiers) {
		return calculateModifiers(modifiers) + distance.getModifier2020();
	}

	@Override
	public boolean isValidEndScatterCoordinate(Game game, FieldCoordinate coordinate) {
		return true;
	}

	@Override
	public boolean handleKickLikeThrow() {
		return true;
	}

	@Override
	public boolean isKtmAvailable(TurnData turnData) {
		return !turnData.isKtmUsed();
	}

	@Override
	public boolean canThrow(Game game, Player<?> player) {
		boolean canDeclare = game.getFieldModel().getPlayerState(player).getBase() != PlayerState.PRONE
			|| UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_SPECIAL_ACTIONS_FROM_PRONE);

		return canDeclare && player.hasSkillProperty(NamedProperties.canThrowTeamMates) && player.getStrengthWithModifiers() >= 5;
	}

	private int calculateModifiers(Collection<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return modifierTotal;
	}

	@Override
	public boolean isTtmAvailable(TurnData turnData) {
		return !turnData.isTtmUsed();
	}

	@Override
	public Player<?>[] findKickableTeamMates(Game pGame, Player<?> pKicker) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate kickerCoordinate = fieldModel.getPlayerCoordinate(pKicker);

		return Arrays.stream(fieldModel.findAdjacentCoordinates(kickerCoordinate, FieldCoordinateBounds.FIELD, 1, false))
			.map(fieldModel::getPlayer)
			.filter(Objects::nonNull)
			.filter(player -> canBeKicked(pGame, player))
			.toArray(Player[]::new);
	}
}
