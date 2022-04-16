package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerStats;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;

import java.util.Set;

public abstract class GameMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.GAME;
	}

	/**
	 * @return true if the re-roll used was only available in the current drive
	 */
	public abstract ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract int minimumLonerRoll(Player<?> player);

	public abstract int minimumProRoll();

	public abstract boolean eligibleForPro(Game game, Player<?> player);

	public abstract SendToBoxReason raisedByNurgleReason();

	public abstract String raisedByNurgleMessage();

	public abstract boolean allowsTeamReRoll(TurnMode turnMode);

	public abstract int mvpSpp();

	public abstract String[] concessionDialogMessages(boolean legalConcession);

	public abstract boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player);

	public abstract boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate);

	public abstract boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult);

	public abstract boolean infectedGoesToReserves();

	public abstract boolean canRaiseDead(Team team);

	public abstract boolean canPreventStripBall(PlayerState playerState);

	public abstract boolean isFoulActionAllowed(TurnMode turnMode);

	public abstract boolean isBombActionAllowed(TurnMode turnMode);

	public abstract boolean isGazeActionAllowed(TurnMode turnMode, PlayerAction playerAction);

	public abstract boolean declareGazeActionAtStart();

	public abstract boolean isKickTeamMateActionAllowed(TurnMode turnMode);

	public abstract boolean areSpecialBlockActionsAllowed(TurnMode turnMode);

	public abstract boolean allowsCancellingGuard(TurnMode turnMode);

	public abstract boolean isBlockActionAllowed(TurnMode turnMode);

	public abstract PlayerStats zappedPlayerStats();

	public abstract String calculatePlayerLevel(Game game, Player<?> player);

	public abstract boolean touchdownEndsGame(Game game);

	public abstract RosterPosition riotousRookiesPosition(Roster roster);

	public abstract boolean isLegalConcession(Game game, Team team);

	public abstract boolean starPairCountsAsTwo();

	public abstract String fanModificationName();

	public abstract int fanModification(TeamResult teamResult);

	public abstract int fans(Team team);

	public abstract String audienceName();

	public abstract int audience(TeamResult teamResult);

	public abstract PlayerType raisedNurgleType();

	public abstract boolean canUseApo(Game game, Player<?> defender);

	public abstract String weatherDescription(Weather weather);

	public abstract Set<String> enhancementsToRemoveAtEndOfTurn(SkillFactory skillFactory);
}
