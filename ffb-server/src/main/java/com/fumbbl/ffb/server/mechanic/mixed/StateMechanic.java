package com.fumbbl.ffb.server.mechanic.mixed;

import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.report.ReportLeader;
import com.fumbbl.ffb.server.step.IStep;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class StateMechanic extends com.fumbbl.ffb.server.mechanic.StateMechanic {
	@Override
	public void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel, IStep pStep) {
		if (!LeaderState.USED.equals(pTurnData.getLeaderState())) {
			if (teamHasLeaderOnField(pTeam, pFieldModel)) {
				if (LeaderState.NONE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.AVAILABLE);
					pTurnData.setReRolls(pTurnData.getReRolls() + 1);
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			} else {
				if (LeaderState.AVAILABLE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.NONE);
					pTurnData.setReRolls(Math.max(pTurnData.getReRolls() - 1, 0));
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			}
		}
	}

}
