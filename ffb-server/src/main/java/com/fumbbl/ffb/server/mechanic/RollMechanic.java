package com.fumbbl.ffb.server.mechanic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;

import java.util.List;
import java.util.Optional;

public abstract class RollMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.ROLL;
	}

	public abstract int[] rollCasualty(DiceRoller diceRoller);

	public abstract PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext);

	public abstract PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext,
		Player<?> player, boolean useDecayRoll);

	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext);

	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, boolean useDecay);

	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int[] roll);

	public abstract int multiBlockAttackerModifier();

	public abstract int multiBlockDefenderModifier();

	public abstract int minimumLonerRoll(Player<?> player);

	public abstract int minimumProRoll();

	public abstract boolean askForReRollIfAvailable(GameState gameState, Player<?> player,
		ReRolledAction reRolledAction,
		int minimumRoll, boolean fumble, Skill modificationSkill,
		Skill reRollSkill,
		CommonProperty menuProperty, String defaultValueKey,
		List<String> messages);

	public abstract boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer);

	public abstract ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract boolean allowsTeamReRoll(TurnMode turnMode);

	public abstract Optional<ReRollProperty> findAdditionalReRollProperty(TurnData turnData);

	public boolean isProReRollAvailable(Player<?> player, Game game, PassState passState) {
		String originalBomberId = null;
		if (passState != null) {
			originalBomberId = passState.getOriginalBombardier();
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		SkillMechanic mechanic =
			(SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());
		return (mechanic.eligibleForPro(game, player, originalBomberId) &&
			player.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
			&& !playerState.hasUsedPro());
	}

	public boolean isSingleUseReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		return isTeamReRollAvailable(pGameState, pPlayer, game.getTurnData().getSingleUseReRolls());
	}

	public boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		return isTeamReRollAvailable(pGameState, pPlayer, game.getTurnData().getReRolls());
	}

	protected boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer, int amount) {
		Game game = pGameState.getGame();
		Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		TurnMode turnMode = game.getTurnMode();
		boolean homeHasPlayer = game.getTeamHome().hasPlayer(pPlayer);
		boolean awayHasPlayer = game.getTeamAway().hasPlayer(pPlayer);
		return (actingTeam.hasPlayer(pPlayer) && !game.getTurnData().isReRollUsed() && (amount > 0)
			&& allowsTeamReRoll(turnMode)
			&& ((turnMode != TurnMode.BOMB_HOME) || homeHasPlayer)
			&& ((turnMode != TurnMode.BOMB_HOME_BLITZ) || homeHasPlayer)
			&& ((turnMode != TurnMode.BOMB_AWAY) || awayHasPlayer)
			&& ((turnMode != TurnMode.BOMB_AWAY_BLITZ) || awayHasPlayer));
	}

}
