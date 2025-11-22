package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TeamStatus;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.TEAM_STATUS)
@RulesCollection(Rules.COMMON)
public class TeamStatusFactory implements INamedObjectFactory {

	public TeamStatus forId(int pId) {
		switch (pId) {
		case 0:
			return TeamStatus.NEW;
		case 1:
			return TeamStatus.ACTIVE;
		case 2:
			return TeamStatus.PENDING_APPROVAL;
		case 3:
			return TeamStatus.BLOCKED;
		case 4:
			return TeamStatus.RETIRED;
		case 5:
			return TeamStatus.WAITING_FOR_OPPONENT;
		case 6:
			return TeamStatus.SKILL_ROLLS_PENDING;
		default:
			return null;
		}
	}

	public TeamStatus forName(String pName) {
		for (TeamStatus status : TeamStatus.values()) {
			if (status.getName().equalsIgnoreCase(pName)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
