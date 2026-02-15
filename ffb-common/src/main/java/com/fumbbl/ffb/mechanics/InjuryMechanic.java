package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.util.RaiseType;

import java.util.List;

public abstract class InjuryMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.INJURY;
	}

	public abstract SendToBoxReason raisedByNurgleReason();

	public abstract String raisedByNurgleMessage();

	public abstract boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult, Player<?> attacker, Player<?> deadPlayer);

	public abstract boolean infectedGoesToReserves();

	public abstract boolean canRaiseDead(Team team, TeamResult teamResult, Player<?> deadPlayer);

	public abstract PlayerType raisedNurgleType();

	public abstract boolean canUseApo(Game game, Player<?> defender, PlayerState playerState);

	public abstract List<RosterPosition> raisePositions(Team team);

	// Only call when you are sure the team can raise dead players
	public abstract RaiseType raiseType(Team team);
}
