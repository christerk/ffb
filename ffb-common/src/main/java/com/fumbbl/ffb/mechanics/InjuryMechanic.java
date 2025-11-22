package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;

public abstract class InjuryMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.INJURY;
	}

	public abstract SendToBoxReason raisedByNurgleReason();

	public abstract String raisedByNurgleMessage();

	public abstract boolean canRaiseInfectedPlayers(Team team, TeamResult teamResult);

	public abstract boolean infectedGoesToReserves();

	public abstract boolean canRaiseDead(Team team);

	public abstract PlayerType raisedNurgleType();

	public abstract boolean canUseApo(Game game, Player<?> defender, PlayerState playerState);

}
