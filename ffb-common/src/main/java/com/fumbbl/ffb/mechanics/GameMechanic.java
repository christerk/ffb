package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
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

	public abstract ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract boolean allowsTeamReRoll(TurnMode turnMode);

	public abstract int mvpSpp();

	public abstract String[] concessionDialogMessages(boolean legalConcession);

	public abstract boolean isFoulActionAllowed(TurnMode turnMode);

	public abstract boolean isBombActionAllowed(TurnMode turnMode);

	public abstract boolean isGazeActionAllowed(TurnMode turnMode, PlayerAction playerAction);

	public abstract boolean declareGazeActionAtStart();

	public abstract boolean isKickTeamMateActionAllowed(TurnMode turnMode);

	public abstract boolean isBlockActionAllowed(TurnMode turnMode);

	public abstract PlayerStats zappedPlayerStats();

	public abstract boolean touchdownEndsGame(Game game);

	public abstract RosterPosition riotousRookiesPosition(Roster roster);

	public abstract boolean isLegalConcession(Game game, Team team);

	public abstract String fanModificationName();

	public abstract int fanModification(TeamResult teamResult);

	public abstract int fans(Team team);

	public abstract String audienceName();

	public abstract int audience(TeamResult teamResult);

	public abstract String weatherDescription(Weather weather);

	public abstract Set<String> enhancementsToRemoveAtEndOfTurn(SkillFactory skillFactory);

	public abstract Set<String> enhancementsToRemoveAtEndOfTurnWhenNotSettingActive(SkillFactory skillFactory);

	public abstract boolean rollForChefAtStartOfHalf();

	public abstract boolean allowMovementInEndZone();
}
