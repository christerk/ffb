package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.util.Set;

public abstract class OnTheBallMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.ON_THE_BALL;
	}

	public abstract Set<Player<?>> findPassBlockers(Game game, Team pTeam, boolean pCheckCanReach);

	public abstract boolean validPassBlockMove(Game game, ActingPlayer actingPlayer, FieldCoordinate fromCoordinate, FieldCoordinate toCoordinate,
	                                           Set<FieldCoordinate> validPassBlockCoordinates, boolean canStillJump, int distance);

	public abstract String displayStringPassInterference();

	public abstract String[] passInterferenceDialogDescription();

	public abstract String passInterferenceStatusDescription();

	public abstract String displayStringKickOffInterference();
}
