package com.fumbbl.ffb.server.mechanic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.step.IStep;

public abstract class StateMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.STATE;
	}

	public abstract void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel, IStep pStep);

	protected boolean teamHasLeaderOnField(Team pTeam, FieldModel pFieldModel) {
		for (Player<?> player : pTeam.getPlayers()) {
			if (playerOnField(player, pFieldModel)
				&& player.hasSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean playerOnField(Player<?> pPlayer, FieldModel pFieldModel) {
		FieldCoordinate fieldCoordinate = pFieldModel.getPlayerCoordinate(pPlayer);
		return ((fieldCoordinate != null) && !fieldCoordinate.isBoxCoordinate());
	}
}
