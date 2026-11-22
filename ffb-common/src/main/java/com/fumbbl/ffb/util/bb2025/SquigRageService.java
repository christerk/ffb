package com.fumbbl.ffb.util.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

/**
 * Evaluates the Squig Rage trait which has to be rolled for on activation before an action is declared, unless a
 * team-mate with Squig Restraint is close enough.
 */
public class SquigRageService {

	private static final int RESTRAINT_RANGE = 3;

	public Skill squigRageSkill(Player<?> player) {
		return player == null ? null : player.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction);
	}

	public boolean requiresRoll(Game game, Player<?> player) {
		Skill skill = squigRageSkill(player);
		TurnMode turnMode = game.getTurnMode();
		if (skill == null || turnMode == null || !turnMode.checkNegatraits()) {
			return false;
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player.getId().equals(actingPlayer.getPlayerId())) {
			if (actingPlayer.isSkillUsed(skill)) {
				return false;
			}
			// the roll is only made at the start of the activation, a player activated without it keeps acting as normal
			PlayerAction playerAction = actingPlayer.getPlayerAction();
			if (playerAction != null && playerAction != PlayerAction.SQUIG_RAGE) {
				return false;
			}
		}
		return !isRestrained(game, player);
	}

	/**
	 * Whether a Standing team-mate with Squig Restraint is within 3 squares of the player.
	 */
	public boolean isRestrained(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(player);
		if (player.getTeam() == null || !FieldCoordinateBounds.FIELD.isInBounds(coordinate)) {
			return false;
		}
		return Arrays.stream(UtilPlayer.findStandingOrPronePlayers(game, player.getTeam(), coordinate, RESTRAINT_RANGE))
			.filter(teamMate -> teamMate.hasSkillProperty(NamedProperties.preventsRageRollOfNearbyTeamMates))
			.anyMatch(teamMate -> fieldModel.getPlayerState(teamMate).isStanding());
	}

	/**
	 * Whether the acting player has already rolled for Squig Rage during this activation.
	 */
	public boolean hasRolled(ActingPlayer actingPlayer) {
		Skill skill = squigRageSkill(actingPlayer.getPlayer());
		return skill != null && actingPlayer.isSkillUsed(skill);
	}

	/**
	 * Whether the acting player rolled for Squig Rage, the resulting sequence is resolved without ending the turn and an
	 * action still has to be declared. Until the activation is ended no other player may be activated.
	 */
	public boolean isAwaitingActionDeclaration(ActingPlayer actingPlayer) {
		return actingPlayer.getPlayer() != null && actingPlayer.getPlayerAction() == PlayerAction.SQUIG_RAGE
			&& hasRolled(actingPlayer) && actingPlayer.isAwaitingActionDeclaration();
	}

	/**
	 * Whether the given player may not be activated because the acting player still has to declare an action after
	 * rolling for Squig Rage.
	 */
	public boolean isActivationBlocked(ActingPlayer actingPlayer, Player<?> player) {
		return player != null && player != actingPlayer.getPlayer() && isAwaitingActionDeclaration(actingPlayer);
	}

	/**
	 * Whether the acting player has done anything apart from rolling for Squig Rage.
	 */
	public boolean hasActedApartFromRoll(ActingPlayer actingPlayer) {
		Skill skill = squigRageSkill(actingPlayer.getPlayer());
		if (skill == null) {
			return actingPlayer.hasActed();
		}
		return actingPlayer.hasActedIgnoring(skill);
	}

	/**
	 * Whether a prone acting player has only been activated by the Squig Rage roll and never stood up.
	 */
	public boolean isOnlyActivatedWhileProne(ActingPlayer actingPlayer) {
		return hasRolled(actingPlayer) && actingPlayer.isStandingUp() && !actingPlayer.hasActedIgnoringNegativeTraits();
	}

	/**
	 * Whether the acting player has been forced by Squig Rage to block and has not blocked yet.
	 */
	public boolean isBlockForced(ActingPlayer actingPlayer) {
		return actingPlayer.isMustCompleteAction() && actingPlayer.getPlayerAction() == PlayerAction.BLOCK
			&& !actingPlayer.hasBlocked();
	}

	/**
	 * Opponents the player is able to block. Ball &amp; Chain does not prevent this block.
	 */
	public Player<?>[] blockTargets(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		PlayerState playerState = fieldModel.getPlayerState(player);
		if (playerState == null || !playerState.isActive()) {
			return new Player<?>[0];
		}
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		if (!mechanic.isBlockActionAllowed(game.getTurnMode())) {
			return new Player<?>[0];
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean prone = playerState.getBase() == PlayerState.PRONE
			|| (player.getId().equals(actingPlayer.getPlayerId()) && actingPlayer.isStandingUp());
		if (prone && !player.hasSkillProperty(NamedProperties.canStandUpForFree)) {
			return new Player<?>[0];
		}
		FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(player);
		return UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(player.getTeam()), coordinate);
	}

	public Player<?>[] teamMateTargets(Game game, Player<?> player) {
		FieldCoordinate coordinate = game.getFieldModel().getPlayerCoordinate(player);
		return UtilPlayer.findAdjacentBlockablePlayers(game, player.getTeam(), coordinate);
	}
}
